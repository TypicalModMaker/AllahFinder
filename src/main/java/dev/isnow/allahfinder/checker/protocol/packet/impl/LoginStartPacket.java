package dev.isnow.allahfinder.checker.protocol.packet.impl;

import dev.isnow.allahfinder.checker.connection.ConnectAtributes;
import dev.isnow.allahfinder.checker.protocol.packet.Packet;
import dev.isnow.allahfinder.util.PacketUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LoginStartPacket extends Packet {
    private final int protocol;
    private final String name;
    public LoginStartPacket(DataOutputStream stream, ArrayList<ConnectAtributes> connectAtributes, int protocol, String name) {
        super("LoginStart", stream, connectAtributes);
        this.protocol = protocol;
        this.name = name;
    }

    @Override
    public void run() throws IOException {
        PacketUtil.writeByte(outputStream, 0x00);
        PacketUtil.writeString(outputStream, name);
        if(protocol >= 759 && connectAtributes.contains(ConnectAtributes.PREMIUM)) {
            outputStream.writeBoolean(false);
        }
    }
}
