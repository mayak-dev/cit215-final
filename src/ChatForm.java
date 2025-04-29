import javax.swing.*;

public class ChatForm extends JFrame
{
    private JPanel mainPanel;
    private JTextArea chatTextArea;
    private JList participantList;
    private JScrollPane chatPanel;
    private JPanel participantPanel;

    private void output(String message)
    {
        chatTextArea.append(message + "\n");
    }

    private void formInit()
    {
        setContentPane(mainPanel);
    }

    public ChatForm(String displayName, int port)
    {
        formInit();

        output("Starting chat server on port " + port + "...");
    }

    public ChatForm(String displayName, String ipv4, int port)
    {
        formInit();

        output("Joining chat room " + ipv4 + ":" + port + "...");
    }
}
