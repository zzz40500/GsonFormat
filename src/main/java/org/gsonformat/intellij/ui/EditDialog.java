package org.gsonformat.intellij.ui;

import org.gsonformat.intellij.config.Config;
import org.gsonformat.intellij.config.Constant;

import javax.swing.*;
import java.awt.event.*;

public class EditDialog extends JFrame {

    private JPanel contentPane;
    private JButton okButton;
    private JButton cancelButton;
    private JTextPane editTP;
    private JButton resetButton;
    private JLabel titleLB;
    private String titleName;
    private String editStr;
    private Type type;

    public EditDialog(Type type) {
        this.type = type;
        setContentPane(contentPane);
//        setModal(true);
        this.setAlwaysOnTop(true);
        getRootPane().setDefaultButton(okButton);
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
        setTitle("Convert method");

        switch (type) {
            case OBJECT_FROM_DATA:
                titleName = "objectFromData(Object data)";
                editStr = Config.getInstant().getObjectFromDataStr();
                break;
            case OBJECT_FROM_DATA1:
                titleName = "objectFromData(Object data,String key)";
                editStr = Config.getInstant().getObjectFromDataStr1();
                break;
            case ARRAY_FROM_DATA:
                titleName = "arrayFromData(Object data)";
                editStr = Config.getInstant().getArrayFromDataStr();
                break;
            case ARRAY_FROM_DATA1:
                titleName = "arrayFromData(Object data,String key)";
                editStr = Config.getInstant().getArrayFromData1Str();
                break;
        }
        titleLB.setText(titleName);
        editTP.setText(editStr);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                resetAction();
            }
        });


    }

    private void resetAction() {

        switch (type) {
            case OBJECT_FROM_DATA:
                editTP.setText(Constant.objectFromObject);
                break;
            case OBJECT_FROM_DATA1:
                System.out.println(Constant.objectFromObject1);
                editTP.setText(Constant.objectFromObject1);
                break;
            case ARRAY_FROM_DATA:
                editTP.setText(Constant.arrayFromData);
                break;
            case ARRAY_FROM_DATA1:
                editTP.setText(Constant.arrayFromData1);
                break;
        }
    }

    private void onOK() {

        switch (type) {
            case OBJECT_FROM_DATA:
                Config.getInstant().saveObjectFromDataStr(editTP.getText());
                break;
            case OBJECT_FROM_DATA1:
                Config.getInstant().saveObjectFromDataStr1(editTP.getText());
                break;
            case ARRAY_FROM_DATA:
                Config.getInstant().saveArrayFromDataStr(editTP.getText());
                break;
            case ARRAY_FROM_DATA1:
                Config.getInstant().saveArrayFromData1Str(editTP.getText());
                break;
        }

        dispose();
    }

    private void onCancel() {

        dispose();
    }

    public enum Type {
        OBJECT_FROM_DATA, OBJECT_FROM_DATA1, ARRAY_FROM_DATA, ARRAY_FROM_DATA1;
    }

}
