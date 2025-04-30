import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer extends ChatOperator
{
    private static final int MAX_CLIENTS = 20;

    private final String displayName;

    private final ServerSocket socket;
    private final HashMap<String, ChatClient> clients = new HashMap<String, ChatClient>();

    public ChatServer(String _displayName, int port) throws IOException
    {
        displayName = _displayName;
        socket = new ServerSocket(port);
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Socket clientSocket = socket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                if (packetId == ChatPacket.ID.JOIN_REQUEST)
                {
                    JoinRequestPacket joinRequestPacket = JoinRequestPacket.read(in);
                    String clientId = joinRequestPacket.getClientId();

                    ChatClient client = new ChatClient(clientId, clientSocket);
                    if (clients.size() >= MAX_CLIENTS)
                    {
                        client.reject("The chat is full. Please try again later.");
                    }
                    else if (clientId.equals(displayName) || clients.containsKey(clientId))
                    {
                        client.reject(String.format("Name '%s' already in use. Please choose another.", clientId));
                    }
                    else
                    {
                        clients.put(clientId, client);
                        client.sendPacket(new JoinResponsePacket(true, null));
                        new Thread(() -> listen(client)).start();
                    }
                }
                else
                {
                    socket.close();
                }
            }
            catch (IOException e)
            {
            }
        }
    }

    private void disconnect(ChatClient client)
    {
        clients.remove(client.getClientId());
        client.closeConnection();
    }

    private void listen(ChatClient client)
    {
        DataInputStream in = client.getInputStream();

        try
        {
            while (true)
            {
                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                switch (packetId)
                {
                    case MESSAGE_SEND:
                    {
                        break;
                    }
                    default:
                        disconnect(client);
                }
            }
        }
        catch (IOException e)
        {
            disconnect(client);
        }
    }

    public void broadcast(ChatPacket packet) throws IOException
    {
        for (ChatClient client : clients.values())
            client.sendPacket(packet);
    }

    @Override
    public void sendChatMessage(String message)
    {
        ChatForm.getOutput().printf("%s: %s\n", displayName, message);
    }
}
