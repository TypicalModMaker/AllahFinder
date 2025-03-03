package dev.isnow.allahfinder.checker.protocol.packet.impl;


import dev.isnow.allahfinder.checker.connection.ConnectAtributes;
import dev.isnow.allahfinder.checker.protocol.packet.Packet;
import dev.isnow.allahfinder.util.PacketUtil;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MotdHandshakePacket extends Packet {
    private final String ip;
    private final int port;
    public MotdHandshakePacket(DataOutputStream outputStream, ArrayList<ConnectAtributes> connectAtributes, String ip, int port) {
        super("MotdHandshake", outputStream, connectAtributes);
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() throws IOException {
        PacketUtil.writeByte(outputStream, 0x00);
        PacketUtil.writeVarInt(outputStream, 4);
        PacketUtil.writeString(outputStream, ip);
        PacketUtil.writeShort(outputStream, (short) port);
        PacketUtil.writeVarInt(outputStream, 1);
    }
}
