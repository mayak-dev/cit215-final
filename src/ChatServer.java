import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer extends ChatOperator
{
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
                // THIS WILL BE MOVED LATER
                Socket clientSocket = socket.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();
                String clientId = packet.getClientId();

                if (packetId == ChatPacket.ID.JOIN_REQUEST)
                {
                    ChatClient client = new ChatClient(clientId, clientSocket);
                    if (clientId.equals(displayName) || clients.containsKey(clientId))
                    {
                        client.sendPacket(new JoinResponsePacket(false, "Name already in use. Please choose another one."));
                        client.closeConnection();
                    }
                    else
                    {
                        clients.put(clientId, client);
                        client.sendPacket(new JoinResponsePacket(true, null));
                    }
                }
            }
            catch (IOException e)
            {
            }
        }
    }

    @Override
    public void sendPacket(ChatPacket packet) throws IOException
    {
        for (ChatClient client : clients.values())
            client.sendPacket(packet);
    }
}
