import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ErrorDialog extends JFrame {

    private JPanel contentPane;
    private JTextPane editTP;
    private JButton okButton;
    private JScrollPane scrollPane;

    public ErrorDialog(String errorInfo) {
        setContentPane(contentPane);

        setTitle("error info");
        getRootPane().setDefaultButton(okButton);
        this.setAlwaysOnTop(true);
        editTP.setText(errorInfo);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        editTP.setCaretPosition(0);

    }
}
