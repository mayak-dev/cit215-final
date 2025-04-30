import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JoinResponsePacket extends ChatPacket
{
    private final boolean accepted;
    private final String message;

    public JoinResponsePacket(boolean _accepted, String _message)
    {
        super(ID.JOIN_RESPONSE, "");
        accepted = _accepted;
        message = _message;
    }

    public boolean getAccepted()
    {
        return accepted;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeBoolean(accepted);
        if (!accepted)
            out.writeUTF(message);
    }

    public static JoinResponsePacket read(DataInputStream in) throws IOException
    {
        boolean accepted = in.readBoolean();
        String message = null;
        if (!accepted)
            message = in.readUTF();
        return new JoinResponsePacket(accepted, message);
    }
}
