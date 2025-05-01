import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuForm extends JFrame
{
    private JPanel mainPanel;
    private JRadioButton hostRadio;
    private JRadioButton joinRadio;
    private JTextField displayNameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton startChatButton;

    private void launchChat() throws IOException
    {
        String displayName = displayNameField.getText();
        String host = hostField.getText();
        int port = Integer.parseInt(portField.getText());

        ChatOperator chatOperator;
        if (hostRadio.isSelected())
            chatOperator = new ChatServer(displayName, port);
        else
            chatOperator = new ChatClient(displayName, host, port);

        ChatForm chatForm = new ChatForm(chatOperator);
        chatForm.pack();
        chatForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chatForm.setVisible(true);

        dispose();
    }

    public MenuForm()
    {
        setContentPane(mainPanel);
        setResizable(false);

        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                hostField.setEnabled(joinRadio.isSelected());
            }
        };
        joinRadio.addActionListener(listener);
        hostRadio.addActionListener(listener);

        startChatButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                try {
                    launchChat();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
