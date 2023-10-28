package dev.isnow.allahfinder.util;

import lombok.experimental.UtilityClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class PacketUtil {
    public int readVarInt(DataInputStream input) throws IOException {
        int result = 0;
        int count = 0;
        int read;
        do {
            read = input.readByte();
            result |= (read & 0x7F) << (count * 7);
            count++;
        } while ((read & 0x80) == 0x80 && count < 5);
        return result;
    }

    public void writeVarInt(DataOutputStream output, int value) throws IOException {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            output.writeByte(temp);
        } while (value != 0);
    }

    public void writeByteArray(DataOutputStream output, byte[] bytes) throws IOException {
        writeVarInt(output, bytes.length);
        output.write(bytes);
    }

    public void writeByte(DataOutputStream output, int bytee) throws IOException {
        output.writeByte(bytee);
    }

    public void writeShort(DataOutputStream output, short shortt) throws IOException {
        output.writeShort(shortt);
    }
    public String readString(DataInputStream input, int length) throws IOException {
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeString(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(output, bytes.length);
        output.write(bytes);
    }
}
