import config.Config;
import config.Strings;

import javax.swing.*;
import java.awt.event.*;

public class SettingDialog extends JFrame {
    private JPanel contentPane;
    private JRadioButton fieldPublicRadioButton;
    private JRadioButton fieldPrivateRadioButton;
    private JCheckBox useSerializedNameCheckBox;
    private JTextPane exampleLB;
    private JButton objectButton;
    private JButton object1Button;
    private JButton arrayButton;
    private JButton array1Button;
    private JTextField suffixEdit;
    private JCheckBox objectFromDataCB;
    private JCheckBox objectFromData1CB;
    private JCheckBox arrayFromDataCB;
    private JCheckBox arrayFromData1CB;
    private JCheckBox reuseEntityCB;
    private JButton cancelButton;
    private JButton okButton;


    public SettingDialog() {
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(okButton);
        this.setAlwaysOnTop(true);
        setTitle("Setting");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
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
        objectFromDataCB.setSelected(Config.getInstant().isObjectFromData());
        objectFromData1CB.setSelected(Config.getInstant().isObjectFromData1());
        arrayFromDataCB.setSelected(Config.getInstant().isArrayFromData());
        arrayFromData1CB.setSelected(Config.getInstant().isArrayFromData1());
        reuseEntityCB.setSelected(Config.getInstant().isResuseEntity());
        objectButton.setEnabled(objectFromDataCB.isSelected());
        object1Button.setEnabled(objectFromData1CB.isSelected());
        arrayButton.setEnabled(arrayFromDataCB.isSelected());
        array1Button.setEnabled(arrayFromData1CB.isSelected());
        suffixEdit.setText(Config.getInstant().getSuffixStr());
        objectFromDataCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                objectButton.setEnabled(objectFromDataCB.isSelected());
            }
        });
        objectFromData1CB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                object1Button.setEnabled(objectFromData1CB.isSelected());
            }
        });
        arrayFromDataCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                arrayButton.setEnabled(arrayFromDataCB.isSelected());
            }
        });
        arrayFromData1CB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                array1Button.setEnabled(arrayFromData1CB.isSelected());
            }
        });

        objectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                EditDialog editDialog = new EditDialog(EditDialog.Type.OBJECT_FROM_DATA);
                editDialog.setSize(600, 360);
                editDialog.setLocationRelativeTo(null);
                editDialog.setResizable(false);
                editDialog.setVisible(true);
            }
        });
        object1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                EditDialog editDialog = new EditDialog(EditDialog.Type.OBJECT_FROM_DATA1);
                editDialog.setSize(600, 360);
                editDialog.setLocationRelativeTo(null);
                editDialog.setResizable(false);
                editDialog.setVisible(true);
            }
        });
        arrayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                EditDialog editDialog = new EditDialog(EditDialog.Type.ARRAY_FROM_DATA);
                editDialog.setSize(600, 600);
                editDialog.setLocationRelativeTo(null);
                editDialog.setResizable(false);
                editDialog.setVisible(true);
            }
        });
        array1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                EditDialog editDialog = new EditDialog(EditDialog.Type.ARRAY_FROM_DATA1);
                editDialog.setSize(600, 600);
                editDialog.setLocationRelativeTo(null);
                editDialog.setResizable(false);
                editDialog.setVisible(true);
            }
        });
        setText();


    }


    private void onOK() {
        Config.getInstant().setFieldPrivateMode(fieldPrivateRadioButton.isSelected());
        Config.getInstant().setUseSerializedName(useSerializedNameCheckBox.isSelected());
        Config.getInstant().setArrayFromData(arrayFromDataCB.isSelected());
        Config.getInstant().setArrayFromData1(arrayFromData1CB.isSelected());
        Config.getInstant().setObjectFromData(objectFromDataCB.isSelected());
        Config.getInstant().setObjectFromData1(objectFromData1CB.isSelected());
        Config.getInstant().setResuseEntity(reuseEntityCB.isSelected());
        Config.getInstant().setSuffixStr(suffixEdit.getText());

        Config.getInstant().save();
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
                exampleLB.setText(Strings.publicUseSerializedNameStr);
            } else {
                exampleLB.setText(Strings.privateUseSerializedNameStr);
            }

        } else {

            if (fieldPublicRadioButton.isSelected()) {
                exampleLB.setText(Strings.publicStr);
            } else {
                exampleLB.setText(Strings.privateStr);
            }


        }
        exampleLB.setCaretPosition(0);
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
