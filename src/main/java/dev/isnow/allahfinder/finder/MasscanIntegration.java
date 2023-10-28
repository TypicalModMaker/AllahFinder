package dev.isnow.allahfinder.finder;

import dev.isnow.allahfinder.AllahFinderImpl;
import dev.isnow.allahfinder.checker.connection.CheckerIntegration;
import dev.isnow.allahfinder.checker.protocol.ProtocolVersion;
import dev.isnow.allahfinder.checker.protocol.json.data.FinalResponse;
import dev.isnow.allahfinder.util.CheckUtil;
import dev.isnow.allahfinder.util.MessageUtil;
import dev.isnow.allahfinder.util.StringUtil;
import lombok.Getter;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MasscanIntegration
        implements Runnable {

    @Getter
    public Process process;

    public String ipString, portString, extraString;

    public final ArrayList<String> last200Lines = new ArrayList<>();
    public MasscanIntegration(String ips, String ports, String extra) {
        ipString = ips;
        portString = ports;
        extraString = extra;
        MessageUtil.info("Masscan started!");
        startMasscan();
    }

    @Override
    public void run() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (process != null && process.isAlive()) {
            try {
                final String line = bufferedReader.readLine();
                if (line == null) continue;
                if(line.startsWith("Discovered")) {
                    final String ip = line.split("on ")[1].split(" ")[0];
                    final int port = Integer.parseInt(line.split("port ")[1].split("/")[0]);

                    // Retardation fix
                    if(last200Lines.contains(ip + ":" + port)) {
                        continue;
                    }
                    if(last200Lines.size() >= 200) {
                        last200Lines.remove(0); // last one
                    }
                    last200Lines.add(ip + ":" + port);

                    Thread t = new Thread(() -> {
                        try {
                            final FinalResponse response = CheckUtil.check(ip, port);
                            if(response == null) {
                                return;
                            }
                            String players = "0/0";
                            if(response.getPlayers() != null) {
                                players = response.getPlayers().getOnline() + "/" + response.getPlayers().getMax();
                            }
                            String version = "NULL";
                            if(response.getVersion() != null) {
                                version = response.getVersion().getName();
                                // Try to fetch the version from engine if it couldn't be parsed from the json (also fuck the protocol 4 shit)
                                if(response.getVersion().getProtocol() <= 4) {
                                    final Pattern pattern = Pattern.compile("\\d+(\\.\\d+)*");
                                    final Matcher matcher = pattern.matcher(version);
                                    if (matcher.find()) {
                                        final String versionString = matcher.group();
                                        final ProtocolVersion protocol = ProtocolVersion.getProtocolByName(versionString);
                                        response.getVersion().setProtocol(protocol.getVersion());
                                    }
                                }
                            }
                            String description = "Failed to parse json";
                            if(response.getDescription() != null) {
                                description = response.getDescription();
                            }
                            final String outputLine = "(" + ip + ":" + port + ")(" + players + ")(" + version + ")(" + StringUtil.clean(description) + ")";
                            if(description.contains("BlocksMC") || version.equals("Aegis") || outputLine.contains("The ||CubeCraft|| Network ") || outputLine.contains("FalixNodes.net/start") || outputLine.contains("www.MineHost.pl") || outputLine.contains("Ochrona DDoS:") || outputLine.contains("Craftserve.pl - wydajny hosting Minecraft!Testuj za darmo przez 24h!") || outputLine.contains("Serwer jest wylaczony") || outputLine.contains("start.falix.cc") || outputLine.contains("start.Falix.cc") || outputLine.contains("Powered by FalixNodes.net") || outputLine.contains("Ochrona DDoS") || outputLine.contains("Blad pobierania statusu. Polacz sie bezposrednio") || outputLine.contains("Please refer to our documentation at docs.tcpshield.com")) {
                                return;
                            }


                            System.out.println(outputLine);

                            Thread.sleep(1250);

                            String joinResponse = "FAILED - DIDNTCHECK";
                            if(response.getPlayers() != null && response.getPlayers().getOnline() > 10) {
                                Thread.sleep(10000);
                                try {
                                    joinResponse = new CheckerIntegration().connect(ip, (short) port, response.getVersion().getProtocol(), 0, new ArrayList<>());
                                } catch (SocketTimeoutException ignored) {
                                    joinResponse = "FAILED - SOCKETTIMEOUT" + ip + ":" + port;
                                }
                                MessageUtil.debug("Response for", ip + ":" + port + ":", joinResponse);

                                final Document oldServer = AllahFinderImpl.getInstance().getDatabase().getServer(ip, port);
                                if(oldServer != null) {
                                    if(!oldServer.getString("joinResult").contains("SUCCESS")) {
                                        AllahFinderImpl.getInstance().getBot().broadcastHit(response, ip, port, CheckUtil.valid(joinResponse, response, port));
                                    }
                                } else {
                                    AllahFinderImpl.getInstance().getBot().broadcastHit(response, ip, port, CheckUtil.valid(joinResponse, response, port));
                                }
                            }

                            AllahFinderImpl.getInstance().getDatabase().addHit(ip, (short) port, response, joinResponse);
                        } catch (NullPointerException | IOException | InterruptedException exception) {
                            MessageUtil.error("Something went wrong!");
                            exception.printStackTrace();
                        }
                    });
                    t.start();
                }
                if(line.contains("waiting")) {
                    this.process.destroy();
                    startMasscan();
                }
            } catch (Exception e) {
                MessageUtil.error("Something went wrong!");
                e.printStackTrace();
            }
        }
        this.process.destroy();
        startMasscan();
    }

    private void startMasscan() {
        MessageUtil.debug("NEW SCAN!");
        try {
            final ProcessBuilder ps = new ProcessBuilder("bash", "-c", "masscan " + extraString + " --range=" + ipString + " --ports " + portString);
            ps.redirectErrorStream(true);
            process = ps.start();
            run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}