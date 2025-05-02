import java.io.PrintStream;

public interface ChatInterface
{
    PrintStream getOutput();
    void updateParticipants(String[] participants);
}
