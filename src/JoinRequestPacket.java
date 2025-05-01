import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinRequestPacket extends ChatPacket
{
    private final String clientId;

    public JoinRequestPacket(String _clientId)
    {
        super(ID.JOIN_REQUEST);
        clientId = _clientId;
    }

    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeUTF(clientId);
    }

    public static JoinRequestPacket read(DataInputStream in) throws IOException
    {
        String clientId = in.readUTF();
        return new JoinRequestPacket(clientId);
    }
}
