package dev.isnow.allahfinder.checker.connection;

import dev.isnow.allahfinder.checker.protocol.packet.impl.EncryptionPacket;
import dev.isnow.allahfinder.checker.protocol.packet.impl.HandshakePacket;
import dev.isnow.allahfinder.checker.protocol.packet.impl.LoginStartPacket;
import dev.isnow.allahfinder.util.PacketUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class CheckerIntegration {
    private DataInputStream in;

    private Socket socket;

    public String connect(String ip, short port, int protocol, int tries, ArrayList<ConnectAtributes> connectAtributes) throws IOException, InterruptedException {
        if(socket != null) {
            socket.close();
            Thread.sleep(500);
        }
        socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(ip, port), 30000);
        in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        // NORMAL HANDSHAKE
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream packetStream = new DataOutputStream(outputStream);
        HandshakePacket handshakePacket = new HandshakePacket(packetStream, connectAtributes, "AllahFinder", ip, protocol, port);
        handshakePacket.run();
        handshakePacket.flush(out, outputStream);

        // LOGIN START
        outputStream = new ByteArrayOutputStream();
        packetStream = new DataOutputStream(outputStream);
        LoginStartPacket loginStartPacket = new LoginStartPacket(packetStream, connectAtributes, protocol, "AllahFinder");
        loginStartPacket.run();
        loginStartPacket.flush(out, outputStream);

        // ENCRYPTION PACKET
        if (protocol >= 759 && !connectAtributes.contains(ConnectAtributes.COCKED) && !connectAtributes.contains(ConnectAtributes.BLACK) ) {
            outputStream = new ByteArrayOutputStream();
            packetStream = new DataOutputStream(outputStream);
            EncryptionPacket encryptionPacket = new EncryptionPacket(packetStream, connectAtributes);
            encryptionPacket.run();
            encryptionPacket.flush(out, outputStream);
        }


        tries++;
        return readServerResponse(ip, port, protocol, tries, connectAtributes);
    }

    private String readServerResponse(String ip, short port, int protocol, int tries, ArrayList<ConnectAtributes> connectAtributes) throws IOException, InterruptedException {
        try {
            PacketUtil.readVarInt(in);
        } catch (IOException e) {
            if(tries > 5) {
                e.printStackTrace();
                return "FAILED - EndOFLine At Reading Server Response - Report to Isnow! IP: " + ip + " Port: " + port;
            }
            connectAtributes.add(ConnectAtributes.BLACK);
            Thread.sleep(100);
            return connect(ip, port, protocol, tries, connectAtributes);
        }
        final int code = PacketUtil.readVarInt(in);
        String message = "";
        if (code == 0) {
            final byte[] array = new byte[PacketUtil.readVarInt(in)];
            in.readFully(array);
            final String s = new String(array);
            DisconnectReason reason = DisconnectReason.getReason(s);
            switch (reason) {
                case IPWL:
                    return "FAILED - OnlyProxyJoin|IPWhitelist|IPWL";
                case BungeeGuard:
                    return "FAILED - BungeeGuard";
                case Protected:
                    return "FAILED - Protected";
                case VANILLA:
                    return "FAILED - VANILLA";
                case VPN:
                    return "FAILED - VPN";
                case DATA_HOSTNAME:
                case THROTTLE:
                    Thread.sleep(5250);
                    if(tries > 3) {
                        return "FAILED - THROTTLE";
                    }
                    connectAtributes.add(ConnectAtributes.PREMIUM);
                    return connect(ip, port, protocol, tries, connectAtributes);
                case WHITELIST:
                    if(!connectAtributes.contains(ConnectAtributes.PREMIUM)) {
                        return "SUCCESS - Whitelist | (IPForwarding/Cracked)";
                    } else {
                        return "FAILED - Whitelist | Premium";
                    }
                case MODDED:
                    return "FAILED - Modded";
                case VERSION:
                    return "FAILED - VERSION | Report to Isnow";
                case IP_FORWARDING:
                    connectAtributes.add(ConnectAtributes.COCKED);
                    Thread.sleep(100);
                    return connect(ip, port, protocol, tries, removeAtribute(ConnectAtributes.PREMIUM, connectAtributes));
                case BYTE_BUF:
                    if(tries == 3) {
                        if(protocol >= 759) {
                            return "FAILED - <1.19 Premium";
                        } else {
                            return "FAILED - Weird Packet Order | Report To Isnow!";
                        }
                    }
                    Thread.sleep(100);
                    return connect(ip, port, protocol, tries, removeAtribute(ConnectAtributes.PREMIUM, connectAtributes));
                case KEY_PACKET:
                    return "SUCCESS - <1.19 Cracked No IPForwarding";
                case VELOCITY:
                    return "FAILED - Velocity";
                case UNKNOWN:
                    message = s;
            }
        }
        switch (code) {
            case -1: {
                return "INVALID - Couldn't connect";
            }
            case 0: {
                if(!message.isEmpty()) {
                    if(message.contains("Release candidate")) {
                        return "FAILED - Snapshot";
                    } else if(message.contains("public_key")) {
                        return "Failed - Online Mode [KEY]";
                    } else if(message.contains("Outdated server")) {
                        return "Failed - Report to Isnow";
                    }
                    return "FAILED - Unknown Message - " + message;
                }
                return "FAILED - Unknown Message";
            }
            case 1: {
                return "FAILED - Online Mode [BYTE]";
            }
            case 2: {
                return "SUCCESS - Disable IPForwarding";
            }
            case 3: {
                return "SUCCESS - Could be IPForwarding";
            }
            default:
                return "INVALID - Weird Byte (Velocity)";
        }
    }

    private ArrayList<ConnectAtributes> removeAtribute(ConnectAtributes atribute, ArrayList<ConnectAtributes> atributes) {
        atributes.remove(atribute);
        return atributes;
    }
}
