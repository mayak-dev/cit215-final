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
    }

    private final ID id;
    private final String clientId;

    protected ChatPacket(ID _packetId, String _clientId)
    {
        id = _packetId;
        clientId = _clientId;
    }

    public ID getId()
    {
        return id;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void write(DataOutputStream out) throws IOException
    {
        out.writeInt(id.ordinal());
        out.writeUTF(clientId);
    }

    public static ChatPacket read(DataInputStream in) throws IOException
    {
        int idOrdinal = in.readInt();
        String clientId = in.readUTF();

        ID packetId = ID.values()[idOrdinal];
        return new ChatPacket(packetId, clientId);
    }
}
