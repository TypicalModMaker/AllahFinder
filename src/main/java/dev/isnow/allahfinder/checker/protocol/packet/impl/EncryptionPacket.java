package dev.isnow.allahfinder.checker.protocol.packet.impl;

import dev.isnow.allahfinder.checker.connection.ConnectAtributes;
import dev.isnow.allahfinder.checker.protocol.packet.Packet;
import dev.isnow.allahfinder.util.PacketUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EncryptionPacket extends Packet {


    public EncryptionPacket(DataOutputStream dataOutputStream, ArrayList<ConnectAtributes> connectAtributes) {
        super("Encryption", dataOutputStream, connectAtributes);
    }

    @Override
    public void run() throws IOException {
        PacketUtil.writeByte(outputStream, 0x01);

        final byte[] sharedSecret = {
                (byte)0x63, (byte)0x6f, (byte)0x6e, (byte)0x73, (byte)0x65, (byte)0x63, (byte)0x72, (byte)0x65,
                (byte)0x74, (byte)0x21, (byte)0x23, (byte)0x24, (byte)0x25, (byte)0x26, (byte)0x27, (byte)0x28
        };
        final byte[] verifyToken = {
                (byte)0x61, (byte)0x62, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66, (byte)0x67, (byte)0x68
        };

        PacketUtil.writeByteArray(outputStream, sharedSecret);
        PacketUtil.writeByteArray(outputStream, verifyToken);
    }
}
