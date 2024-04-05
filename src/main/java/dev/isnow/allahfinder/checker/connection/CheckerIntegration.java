package dev.isnow.allahfinder.checker.connection;

import com.google.gson.Gson;
import dev.isnow.allahfinder.checker.protocol.json.data.MCResponse;
import dev.isnow.allahfinder.checker.protocol.packet.impl.EncryptionPacket;
import dev.isnow.allahfinder.checker.protocol.packet.impl.HandshakePacket;
import dev.isnow.allahfinder.checker.protocol.packet.impl.LoginStartPacket;
import dev.isnow.allahfinder.checker.protocol.packet.impl.MotdHandshakePacket;
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

    public String connect(String ip, int port, int protocol, int tries, ArrayList<ConnectAtributes> connectAtributes) throws IOException, InterruptedException {
        if(socket != null) {
            socket.close();
            Thread.sleep(500);
        }
        // SOCKET
        socket = new Socket();
        socket.setSoTimeout(5000);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(ip, port), 30000);
        in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        // MOTD HANDSHAKE
        if(connectAtributes.contains(ConnectAtributes.VERSION)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream packetStream = new DataOutputStream(outputStream);
            MotdHandshakePacket motdHandshakePacket = new MotdHandshakePacket(packetStream, connectAtributes, ip, port);
            motdHandshakePacket.run();
            motdHandshakePacket.flush(out, outputStream);

            PacketUtil.readVarInt(this.in);
            int id = PacketUtil.readVarInt(this.in);
            int length = PacketUtil.readVarInt(this.in);
            if (id >= 0 || length > 0) {
                byte[] array = new byte[length];
                this.in.readFully(array);
                String json = new String(array);
                if (json.contains("{")) {
                    MCResponse response = new Gson().fromJson(json, MCResponse.class);
                    protocol = response.getVersion().getProtocol();
                }
            }
        }

        // NORMAL HANDSHAKE
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream packetStream = new DataOutputStream(outputStream);
        HandshakePacket handshakePacket = new HandshakePacket(packetStream, connectAtributes, "TypicalModMaker", ip, protocol, port);
        handshakePacket.run();
        handshakePacket.flush(out, outputStream);

        // LOGIN START
        outputStream = new ByteArrayOutputStream();
        packetStream = new DataOutputStream(outputStream);
        LoginStartPacket loginStartPacket = new LoginStartPacket(packetStream, connectAtributes, protocol, "TypicalModMaker");
        loginStartPacket.run();
        loginStartPacket.flush(out, outputStream);

        // ENCRYPTION PACKET
        if (protocol >= 759 && !connectAtributes.contains(ConnectAtributes.BLACK)) {
            outputStream = new ByteArrayOutputStream();
            packetStream = new DataOutputStream(outputStream);
            EncryptionPacket encryptionPacket = new EncryptionPacket(packetStream, connectAtributes);
            encryptionPacket.run();
            encryptionPacket.flush(out, outputStream);
        }


        tries++;
        return readServerResponse(ip, port, protocol, tries, connectAtributes);
    }

    public void close() {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private String readServerResponse(String ip, int port, int protocol, int tries, ArrayList<ConnectAtributes> connectAtributes) throws IOException, InterruptedException {
        try {
            PacketUtil.readVarInt(in);
        } catch (IOException e) {
            if(tries > 5) {
                return "FAILED - EndOFLine At Reading Server Response";
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
                    Thread.sleep(5500);
                    if(tries > 3 && connectAtributes.contains(ConnectAtributes.THROTTLE)) {
                        return "FAILED - THROTTLE";
                    }
                    return connect(ip, port, protocol, tries, addAtribute(ConnectAtributes.THROTTLE, connectAtributes));
                case WHITELIST:
                    if(!connectAtributes.contains(ConnectAtributes.PREMIUM)) {
                        return "SUCCESS - Whitelist | (IPForwarding/Cracked)";
                    } else {
                        return "FAILED - Whitelist | Premium";
                    }
                case MODDED:
                    return "FAILED - Modded";
                case VERSION:
                    if(tries > 2) {
                        return "FAILED - VERSION | Report to Isnow";
                    }
                    return connect(ip, port, protocol, tries, addAtribute(ConnectAtributes.VERSION, connectAtributes));
                case IP_FORWARDING:
                    Thread.sleep(100);
                    if(tries > 10) {
                        return "SUCCESS - IPFORWARDING COULDN'T CHECK";
                    }
                    return connect(ip, port, protocol, tries, addAtribute(ConnectAtributes.COCKED, connectAtributes));
                case BYTE_BUF:
                    if(tries == 3) {
                        if(protocol >= 759) {
                            if(connectAtributes.contains(ConnectAtributes.PREMIUM) && connectAtributes.contains(ConnectAtributes.COCKED)) {
                                return "SUCCESS - >1.19 IPFORWARDING COULDN'T CHECK | ENCRYPTION";
                            }
                            return "FAILED - <1.19 Premium | BYTEBUF " + connectAtributes;
                        } else {
                            return "FAILED - Weird Packet Order | Report To Isnow!";
                        }
                    }
                    Thread.sleep(100);
                    return connect(ip, port, protocol, tries, addAtribute(ConnectAtributes.PREMIUM, connectAtributes));
                case KEY_PACKET:
                    if(connectAtributes.contains(ConnectAtributes.KEY_PACKET)) {
                        return "SUCCESS - <1.19 Cracked No IPForwarding";
                    }
                    return connect(ip, port, protocol, tries, addAtribute(ConnectAtributes.KEY_PACKET, addAtribute(ConnectAtributes.BLACK, connectAtributes)));
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
                        return "FAILED - Online Mode [KEY]";
                    } else if(message.contains("Outdated server")) {
                        return "FAILED - Report to Isnow";
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
    private ArrayList<ConnectAtributes> addAtribute(ConnectAtributes atribute, ArrayList<ConnectAtributes> atributes) {
        if(atributes.contains(atribute)) {
            return atributes;
        }
        atributes.add(atribute);
        return atributes;
    }
}
