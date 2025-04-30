import javax.swing.*;
import java.io.OutputStream;

// https://docs.oracle.com/javase/8/docs/api/java/io/OutputStream.html
// this is so that an OutputStream representing a JTextArea can be
// used to create a PrintStream to "emulate" console output

public class TextAreaOutputStream extends OutputStream
{
    private final JTextArea textArea;

    public TextAreaOutputStream(JTextArea _textArea)
    {
        textArea = _textArea;
    }

    public void write(int b)
    {
        textArea.append(String.valueOf((char)b));
    }
}
