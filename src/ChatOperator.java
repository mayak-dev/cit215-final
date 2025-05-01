import java.io.PrintStream;

public abstract class ChatOperator implements Runnable
{
    protected PrintStream chatOutput = System.out;

    public void setOutput(PrintStream stream)
    {
        chatOutput = stream;
    }

    public abstract void run();
    public abstract void sendChatMessage(String message);
}
