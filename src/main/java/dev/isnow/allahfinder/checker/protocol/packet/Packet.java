package dev.isnow.allahfinder.checker.protocol.packet;


import dev.isnow.allahfinder.checker.connection.ConnectAtributes;
import dev.isnow.allahfinder.util.PacketUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Data;

@Data
public abstract class Packet implements IPacket {
    public final String name;
    public final DataOutputStream outputStream;
    public final ArrayList<ConnectAtributes> connectAtributes;

    public void flush(DataOutputStream socketStream, ByteArrayOutputStream byteOutStream) throws IOException {
        PacketUtil.writeVarInt(socketStream, byteOutStream.size());
        socketStream.write(byteOutStream.toByteArray());
    }
}
