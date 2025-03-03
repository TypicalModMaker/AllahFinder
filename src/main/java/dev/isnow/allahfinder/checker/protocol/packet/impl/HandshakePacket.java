package dev.isnow.allahfinder.checker.protocol.packet.impl;

import dev.isnow.allahfinder.checker.connection.ConnectAtributes;
import dev.isnow.allahfinder.checker.protocol.packet.Packet;
import dev.isnow.allahfinder.util.PacketUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HandshakePacket extends Packet {

    private final String ip;
    private final int protocol;
    private final int port;

    public HandshakePacket(DataOutputStream wrappedStream, ArrayList<ConnectAtributes> connectAtributes, String name, String ip, int protocol, int port) {
        super("Handshake", wrappedStream, connectAtributes);
        this.ip = ip;
        this.protocol = protocol;
        this.port = port;
    }

    @Override
    public void run() throws IOException {
        PacketUtil.writeByte(outputStream, 0x00);

        PacketUtil.writeVarInt(outputStream, protocol);

        if(!connectAtributes.contains(ConnectAtributes.PREMIUM) || (connectAtributes.contains(ConnectAtributes.PREMIUM) && connectAtributes.contains(ConnectAtributes.COCKED))) {
            PacketUtil.writeString(outputStream, "\u0000"+ ip + "\u0000cb3e2894-5bcf-3efa-a538-3708ef163e8f");
        } else {
            PacketUtil.writeString(outputStream, ip);
        }

        PacketUtil.writeShort(outputStream, (short) port);
        PacketUtil.writeVarInt(outputStream, 2);
    }
}
