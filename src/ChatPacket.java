import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatPacket
{
    public enum ID
    {
        JOIN_REQUEST,
        JOIN_RESPONSE,

        MESSAGE_SEND,
        MESSAGE_RECEIVE,

        PARTICIPANTS_RECEIVE,
    }

    private final ID id;

    protected ChatPacket(ID _packetId)
    {
        id = _packetId;
    }

    public ID getId()
    {
        return id;
    }

    public void write(DataOutputStream out) throws IOException
    {
        out.writeByte(id.ordinal());
    }

    public static ChatPacket read(DataInputStream in) throws IOException
    {
        ID packetId = ID.values()[in.readByte()];
        return new ChatPacket(packetId);
    }
}
