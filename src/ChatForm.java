import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class ChatForm extends JFrame implements ChatInterface
{
    private JPanel mainPanel;
    private JTextArea chatTextArea;
    private JList participantList;
    private JScrollPane chatPanel;
    private JPanel participantPanel;
    private JTextField messageField;
    private JButton sendButton;
    private JPanel messagePanel;

    private ChatOperator chatOperator;

    private PrintStream output;

    @Override
    public PrintStream getOutput()
    {
        return output;
    }

    @Override
    public void updateParticipants(String[] participants)
    {
        participantList.setListData(participants);
    }

    private void sendChatMessage()
    {
        chatOperator.sendChatMessage(messageField.getText());
        messageField.setText("");
    }

    public ChatForm(ChatOperator _chatOperator)
    {
        setContentPane(mainPanel);

        output = new PrintStream(new TextAreaOutputStream(chatTextArea));

        chatOperator = _chatOperator;
        chatOperator.setChatInterface(this);
        new Thread(chatOperator).start();

        sendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                sendChatMessage();
            }
        });
    }
}
