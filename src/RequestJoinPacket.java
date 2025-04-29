import java.io.DataOutputStream;
import java.io.IOException;

public class RequestJoinPacket extends ChatPacket
{
    private final String displayName;

    public RequestJoinPacket(String _displayName)
    {
        super(ID.REQUEST_JOIN);
        displayName = _displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeUTF(displayName);
    }
}
