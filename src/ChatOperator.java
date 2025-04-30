import java.io.IOException;
import java.net.Socket;
import java.net.SocketImpl;

public abstract class ChatOperator implements Runnable
{
    public abstract void run();
    public abstract void sendPacket(ChatPacket packet) throws IOException;
}
