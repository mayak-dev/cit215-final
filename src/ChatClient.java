import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.*;

public class ChatClient extends ChatOperator
{
    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());

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
        chatInterface.getOutput().println("Connecting to chat room...");
        logger.log(Level.INFO, "Started chat client");

        try
        {
            logger.log(Level.INFO, "Sending JoinRequestPacket to server");
            sendPacket(new JoinRequestPacket(clientId));

            while (true)
            {
                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                logger.log(Level.INFO, String.format("Packet received from server with ID %s", packetId));

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
                    {
                        logger.log(Level.WARNING,
                                String.format("Received unexpected packet ID %s from server", packetId));
                        closeConnection();
                    }
                }
            }
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Transmission error occurred while processing packets from the server", e);
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
            logger.log(Level.WARNING, "Failed to send MessageSendPacket to server", e);
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
            logger.log(Level.INFO, "Something weird happened", e);
        }
    }

    public void reject(String message) throws IOException
    {
        logger.log(Level.INFO, String.format("Rejected connection with message: %s", message));

        sendPacket(new JoinResponsePacket(false, message));
        closeConnection();
    }

    public DataInputStream getInputStream()
    {
        return in;
    }
}
