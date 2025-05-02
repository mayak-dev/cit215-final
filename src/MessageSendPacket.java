import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageSendPacket extends ChatPacket
{
    private final String message;

    public MessageSendPacket(String _message)
    {
        super(ID.MESSAGE_SEND);
        message = _message;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeUTF(message);
    }

    public static MessageSendPacket read(DataInputStream in) throws IOException
    {
        String message = in.readUTF();
        return new MessageSendPacket(message);
    }
}
