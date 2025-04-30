import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClient extends ChatOperator
{
    private String clientId;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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

    @Override
    public void run()
    {
        try
        {
            sendPacket(new JoinRequestPacket(clientId));

            // THIS WILL BE MOVED LATER
            while (true)
            {
                ChatPacket packet = ChatPacket.read(in);
                ChatPacket.ID packetId = packet.getId();

                if (packetId == ChatPacket.ID.JOIN_RESPONSE)
                {
                    JoinResponsePacket joinResponsePacket = JoinResponsePacket.read(in);

                    if (!joinResponsePacket.getAccepted())
                    {
                        JOptionPane.showMessageDialog(null, joinResponsePacket.getMessage());
                        return;
                    }
                }
            }
        }
        catch (IOException e)
        {
        }
    }

    @Override
    public void sendPacket(ChatPacket packet) throws IOException
    {
        packet.write(out);
        out.flush();
    }

    public void closeConnection() throws IOException
    {
        socket.close();
    }
}
