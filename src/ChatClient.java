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
        chatInterface.getOutput().printf("Connecting to chat room %s:%d\n", socket.getInetAddress(), socket.getLocalPort());

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
                            chatInterface.getOutput().printf("Welcome, %s!\n", clientId);
                        }
                        else
                        {
                            chatInterface.getOutput().println(joinResponsePacket.getMessage());
                            closeConnection();
                        }
                    }
                    break;
                    case MESSAGE_RECEIVE:
                    {
                        MessageReceivePacket messageReceivePacket = MessageReceivePacket.read(in);
                        chatInterface.getOutput().printf("%s: %s\n",
                                messageReceivePacket.getSender(), messageReceivePacket.getMessage());
                    }
                    break;
                    case PARTICIPANTS_RECEIVE:
                    {
                        ParticipantsReceivePacket participantsReceivePacket = ParticipantsReceivePacket.read(in);
                        chatInterface.updateParticipants(participantsReceivePacket.getParticipants());
                    }
                    break;
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
        try
        {
            sendPacket(new MessageSendPacket(message));
        }
        catch (IOException e)
        {
        }
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
