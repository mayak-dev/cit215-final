import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageReceivePacket extends ChatPacket
{
    private final String sender;
    private final String message;

    public MessageReceivePacket(String _sender, String _message)
    {
        super(ID.MESSAGE_RECEIVE);
        sender = _sender;
        message = _message;
    }

    public String getSender()
    {
        return sender;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        super.write(out);
        out.writeUTF(sender);
        out.writeUTF(message);
    }

    public static MessageReceivePacket read(DataInputStream in) throws IOException
    {
        String sender = in.readUTF();
        String message = in.readUTF();
        return new MessageReceivePacket(sender, message);
    }
}
