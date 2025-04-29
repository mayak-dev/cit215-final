import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        MenuForm menuForm = new MenuForm();
        menuForm.pack();
        menuForm.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuForm.setVisible(true);
    }
}
