import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuForm extends JFrame
{
    private JPanel mainPanel;
    private JRadioButton hostRadio;
    private JRadioButton joinRadio;
    private JTextField displayNameField;
    private JTextField ipv4Field;
    private JTextField portField;
    private JButton startChatButton;

    private void launchChat()
    {
        String displayName = displayNameField.getText();
        String ipv4 = ipv4Field.getText();
        int port = Integer.parseInt(portField.getText());

        ChatForm chatForm;
        if (hostRadio.isSelected())
            chatForm = new ChatForm(displayName, port);
        else
            chatForm = new ChatForm(displayName, ipv4, port);

        chatForm.pack();
        chatForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chatForm.setVisible(true);

        dispose();
    }

    public MenuForm()
    {
        setContentPane(mainPanel);

        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                ipv4Field.setEnabled(joinRadio.isSelected());
            }
        };
        joinRadio.addActionListener(listener);
        hostRadio.addActionListener(listener);

        startChatButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                launchChat();
            }
        });
    }
}
