import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

public class ChatServer extends ChatOperator
{
    private static final int MAX_CLIENTS = 20;
    public static final int MAX_NAME_LENGTH = 12;

    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private final String displayName;

    private final ServerSocket socket;
    private final HashMap<String, ChatClient> clients = new HashMap<>();

    public ChatServer(String _displayName, int port) throws IOException
    {
        displayName = _displayName;
        socket = new ServerSocket(port);
    }

    private void updateParticipants()
    {
        logger.log(Level.INFO, "Updating chat participants");

        Set<String> clientIds = clients.keySet();
        String[] participants = clientIds.toArray(new String[clientIds.size() + 1]);
        participants[participants.length - 1] = displayName;

        chatInterface.updateParticipants(participants);

        broadcast(new ParticipantsReceivePacket(participants));
    }

    @Override
    public void run()
    {
        chatInterface.getOutput().printf("Running chat server on port %d\n", socket.getLocalPort());
        logger.log(Level.INFO, "Started chat server");

        updateParticipants();

        while (true)
        {
            try
            {
                Socket clientSocket = socket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                logger.log(Level.INFO,
                        String.format("Received new connection from %s", clientSocket.getLocalSocketAddress()));

                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                if (packetId == ChatPacket.ID.JOIN_REQUEST)
                {
                    logger.log(Level.INFO, "Received JoinRequestPacket from new client");

                    JoinRequestPacket joinRequestPacket = JoinRequestPacket.read(in);
                    String clientId = joinRequestPacket.getClientId();

                    ChatClient client = new ChatClient(clientId, clientSocket);
                    if (clients.size() >= MAX_CLIENTS)
                    {
                        client.reject("The chat is full. Please try again later.");
                    }
                    else if (clientId.isEmpty() || clientId.length() > MAX_NAME_LENGTH)
                    {
                        client.reject(String.format("Name must be between 1 and %d characters.", MAX_NAME_LENGTH));
                    }
                    else if (clientId.equals(displayName) || clients.containsKey(clientId))
                    {
                        client.reject(String.format("Name '%s' already in use. Please choose another.", clientId));
                    }
                    else
                    {
                        logger.log(Level.INFO, String.format("Connection accepted from '%s' (%s)",
                                        clientId, clientSocket.getLocalSocketAddress()));

                        clients.put(clientId, client);
                        client.sendPacket(new JoinResponsePacket(true, null));
                        new Thread(() -> listen(client)).start();

                        updateParticipants();
                    }
                }
                else
                {
                    logger.log(Level.INFO, "New client sent a packet that was not a JoinRequestPacket");
                    socket.close();
                }
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Transmission error occurred with a new client", e);
            }
        }
    }

    private void disconnect(ChatClient client)
    {
        clients.remove(client.getClientId());
        client.closeConnection();

        updateParticipants();
    }

    private void listen(ChatClient client)
    {
        logger.log(Level.INFO, "Listening for new packets from established client");

        DataInputStream in = client.getInputStream();

        try
        {
            while (true)
            {
                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                logger.log(Level.INFO, String.format("Packet received with ID %s", packetId));

                switch (packetId)
                {
                    case MESSAGE_SEND:
                    {
                        MessageSendPacket messageSendPacket = MessageSendPacket.read(in);
                        String sender = client.getClientId();
                        String message = messageSendPacket.getMessage();

                        chatInterface.getOutput().printf("%s: %s\n", sender, message);

                        broadcast(new MessageReceivePacket(sender, message));
                    }
                    break;
                    default:
                    {
                        logger.log(Level.WARNING,
                                String.format("Received unexpected packet ID %s while listening to established client",
                                        packetId));
                        disconnect(client);
                    }
                }
            }
        }
        catch (IOException e)
        {
            logger.log(Level.WARNING, "Transmission error occurred with an established client", e);
            disconnect(client);
        }
    }

    public void broadcast(ChatPacket packet)
    {
        ArrayList<ChatClient> toDisconnect = new ArrayList<>();

        for (ChatClient client : clients.values())
        {
            try
            {
                client.sendPacket(packet);
            }
            catch (IOException e)
            {
                logger.log(Level.WARNING, "Failed to broadcast to an established client", e);
                toDisconnect.add(client);
            }
        }

        for (ChatClient client : toDisconnect)
            disconnect(client);
    }

    @Override
    public void sendChatMessage(String message)
    {
        chatInterface.getOutput().printf("%s: %s\n", displayName, message);

        broadcast(new MessageReceivePacket(displayName, message));
    }
}
