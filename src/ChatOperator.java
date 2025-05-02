import java.io.PrintStream;

public abstract class ChatOperator implements Runnable
{
    protected ChatInterface chatInterface;

    public void setChatInterface(ChatInterface _chatInterface)
    {
        chatInterface = _chatInterface;
    }

    public abstract void run();
    public abstract void sendChatMessage(String message);
}
