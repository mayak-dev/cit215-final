import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ChatPacket
{
    public enum ID
    {
        REQUEST_JOIN,
        ACCEPT_JOIN,

        SEND_MESSAGE,
        RECEIVE_MESSAGE,
    }

    private final ID packetId;

    protected ChatPacket(ID _packetId)
    {
        packetId = _packetId;
    }

    public ID getPacketId()
    {
        return packetId;
    }

    void write(DataOutputStream out) throws IOException
    {
        out.writeInt(packetId.ordinal());
    }
}
