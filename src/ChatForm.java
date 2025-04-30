import javax.swing.*;

public class ChatForm extends JFrame
{
    private JPanel mainPanel;
    private JTextArea chatTextArea;
    private JList participantList;
    private JScrollPane chatPanel;
    private JPanel participantPanel;

    private ChatOperator chatOperator;

    private void output(String message)
    {
        chatTextArea.append(message + "\n");
    }

    public ChatForm(ChatOperator _chatOperator)
    {
        setContentPane(mainPanel);

        chatOperator = _chatOperator;
        new Thread(chatOperator).start();
    }
}
