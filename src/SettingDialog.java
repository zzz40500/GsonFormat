import config.Config;
import config.Strings;

import javax.swing.*;
import java.awt.event.*;

public class SettingDialog extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton fieldPublicRadioButton;
    private JRadioButton fieldPrivateRadioButton;
    private JCheckBox useSerializedNameCheckBox;
    private JTextPane example;


    public SettingDialog() {
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        fieldPublicRadioButton.addActionListener(new MActionListener());
        fieldPrivateRadioButton.addActionListener(new MActionListener());
        useSerializedNameCheckBox.addActionListener(new MActionListener());
        if (Config.getInstant().isFieldPrivateMode()) {
            fieldPrivateRadioButton.setSelected(true);
        } else {
            fieldPublicRadioButton.setSelected(true);
        }
        useSerializedNameCheckBox.setSelected(Config.getInstant().isUseSerializedName());
        setText();
    }

    private void onOK() {


        Config.getInstant().setFieldPrivateMode(fieldPrivateRadioButton.isSelected());
        Config.getInstant().setUseSerializedName(useSerializedNameCheckBox.isSelected());

        Config.getInstant().save();
        dispose();
    }

    class MActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            setText();

        }
    }

    public void setText() {
        if (useSerializedNameCheckBox.isSelected()) {

            if (fieldPublicRadioButton.isSelected()) {
                example.setText(Strings.publicUseSerializedNameStr);
            } else {
                example.setText(Strings.privateUseSerializedNameStr);
            }

        } else {

            if (fieldPublicRadioButton.isSelected()) {
                example.setText(Strings.publicStr);
            } else {
                example.setText(Strings.privateStr);
            }


        }
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        SettingDialog dialog = new SettingDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
