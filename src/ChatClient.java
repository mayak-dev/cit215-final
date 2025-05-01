import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends ChatOperator
{
    private final String clientId;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ChatClient(String _clientId, String host, int port) throws IOException
    {
        this(_clientId, new Socket(host, port));
    }

    public ChatClient(String _clientId, Socket _socket) throws IOException
    {
        clientId = _clientId;

        socket = _socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void run()
    {
        chatOutput.printf("Connecting to chat room %s:%d\n", socket.getInetAddress(), socket.getLocalPort());

        try
        {
            sendPacket(new JoinRequestPacket(clientId));

            while (true)
            {
                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                switch (packetId)
                {
                    case JOIN_RESPONSE:
                    {
                        JoinResponsePacket joinResponsePacket = JoinResponsePacket.read(in);
                        if (joinResponsePacket.getAccepted())
                        {
                            chatOutput.printf("Welcome, %s!\n", clientId);
                        }
                        else
                        {
                            chatOutput.println(joinResponsePacket.getMessage());
                            closeConnection();
                        }

                        break;
                    }
                    default:
                        closeConnection();
                }
            }
        }
        catch (IOException e)
        {
            closeConnection();
        }
    }

    public void sendPacket(ChatPacket packet) throws IOException
    {
        packet.write(out);
        out.flush();
    }

    @Override
    public void sendChatMessage(String message)
    {
    }

    public void closeConnection()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
        }
    }

    public void reject(String message) throws IOException
    {
        sendPacket(new JoinResponsePacket(false, message));
        closeConnection();
    }

    public DataInputStream getInputStream()
    {
        return in;
    }
}
