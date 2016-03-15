import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.codeStyle.VariableKind;
import config.Config;
import config.Strings;
import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.event.*;

public class SettingDialog extends JFrame {

    private JPanel contentPane;
    private JRadioButton fieldPublicRadioButton;
    private JRadioButton fieldPrivateRadioButton;
    private JCheckBox useSerializedNameCheckBox;
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
    private JTextField filedPrefixTF;
    private JCheckBox filedPrefixCB;
    private JRadioButton gsonJRB;
    private JRadioButton jackRB;
    private JRadioButton fastJsonRB;
    private JRadioButton otherRB;
    private JTextField annotationFT;
    private JCheckBox virgoModelCB;
    private JCheckBox generateCommentsCT;
    private JRadioButton loganSquareCB;
    private String annotaionStr;

    public SettingDialog(Project project) {
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

        if (Config.getInstant().isFieldPrivateMode()) {
            fieldPrivateRadioButton.setSelected(true);
        } else {
            fieldPublicRadioButton.setSelected(true);
        }

        virgoModelCB.setSelected(Config.getInstant().isVirgoMode());
        generateCommentsCT.setSelected(Config.getInstant().isGenerateComments());
        filedPrefixCB.setSelected(Config.getInstant().isUseFiledNamePrefix());
        filedPrefixTF.setEnabled(Config.getInstant().isUseFiledNamePrefix());
        useSerializedNameCheckBox.setSelected(Config.getInstant().isUseSerializedName());
        objectFromDataCB.setSelected(Config.getInstant().isObjectFromData());
        objectFromData1CB.setSelected(Config.getInstant().isObjectFromData1());
        arrayFromDataCB.setSelected(Config.getInstant().isArrayFromData());
        arrayFromData1CB.setSelected(Config.getInstant().isArrayFromData1());
        reuseEntityCB.setSelected(Config.getInstant().isReuseEntity());
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
        filedPrefixCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                filedPrefixTF.setEnabled(filedPrefixCB.isSelected());
            }
        });
        otherRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                annotationFT.setText("@{filed}");
                annotationFT.setEnabled(otherRB.isSelected());
                objectFromDataCB.setEnabled(false);
                objectFromData1CB.setEnabled(false);
                arrayFromDataCB.setEnabled(false);
                arrayFromData1CB.setEnabled(false);
                objectFromDataCB.setSelected(false);
                objectFromData1CB.setSelected(false);
                arrayFromDataCB.setSelected(false);
                arrayFromData1CB.setSelected(false);
                objectButton.setEnabled(false);
                object1Button.setEnabled(false);
                arrayButton.setEnabled(false);
                array1Button.setEnabled(false);
            }
        });
        loganSquareCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (loganSquareCB.isSelected()) {
                    annotationFT.setText(Strings.loganSquareAnnotation);
                }
                annotationFT.setEnabled(otherRB.isSelected());
                objectFromDataCB.setEnabled(false);
                objectFromData1CB.setEnabled(false);
                arrayFromDataCB.setEnabled(false);
                arrayFromData1CB.setEnabled(false);
                objectFromDataCB.setSelected(false);
                objectFromData1CB.setSelected(false);
                arrayFromDataCB.setSelected(false);
                arrayFromData1CB.setSelected(false);
                objectButton.setEnabled(false);
                object1Button.setEnabled(false);
                arrayButton.setEnabled(false);
                array1Button.setEnabled(false);
            }
        });
        String filedPrefix = null;
        filedPrefix = Config.getInstant().getFiledNamePreFixStr();
        if (TextUtils.isEmpty(filedPrefix)) {
            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
            filedPrefix = styleManager.getPrefixByVariableKind(VariableKind.FIELD
            );
        }
        filedPrefixTF.setText(filedPrefix);
        gsonJRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (gsonJRB.isSelected()) {
                    annotationFT.setText(Strings.gsonAnnotation);
                }
                objectFromDataCB.setEnabled(true);
                objectFromData1CB.setEnabled(true);
                arrayFromDataCB.setEnabled(true);
                arrayFromData1CB.setEnabled(true);
                annotationFT.setEnabled(false);
            }
        });
        fastJsonRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (fastJsonRB.isSelected()) {
                    annotationFT.setText(Strings.fastAnnotation);
                }
                objectFromDataCB.setEnabled(false);
                objectFromData1CB.setEnabled(false);
                arrayFromDataCB.setEnabled(false);
                arrayFromData1CB.setEnabled(false);
                annotationFT.setEnabled(false);
                objectFromDataCB.setSelected(false);
                objectFromData1CB.setSelected(false);
                arrayFromDataCB.setSelected(false);
                arrayFromData1CB.setSelected(false);
                objectButton.setEnabled(false);
                object1Button.setEnabled(false);
                arrayButton.setEnabled(false);
                array1Button.setEnabled(false);
            }
        });
        jackRB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (jackRB.isSelected()) {
                    annotationFT.setText(Strings.jackAnnotation);
                }
                annotationFT.setEnabled(false);
                objectFromDataCB.setEnabled(false);
                objectFromData1CB.setEnabled(false);
                arrayFromDataCB.setEnabled(false);
                arrayFromData1CB.setEnabled(false);
                annotationFT.setEnabled(false);
                objectFromDataCB.setSelected(false);
                objectFromData1CB.setSelected(false);
                arrayFromDataCB.setSelected(false);
                arrayFromData1CB.setSelected(false);
                objectButton.setEnabled(false);
                object1Button.setEnabled(false);
                arrayButton.setEnabled(false);
                array1Button.setEnabled(false);
            }
        });

        annotaionStr = Config.getInstant().getAnnotationStr();
        if (annotaionStr.equals(Strings.gsonAnnotation)) {
            gsonJRB.setSelected(true);
            annotationFT.setEnabled(false);
        } else if (annotaionStr.equals(Strings.fastAnnotation)) {
            fastJsonRB.setSelected(true);
            annotationFT.setEnabled(false);
        } else if (annotaionStr.equals(Strings.jackAnnotation)) {
            jackRB.setSelected(true);
            annotationFT.setEnabled(false);
        } else if (annotaionStr.equals(Strings.loganSquareAnnotation)) {
            loganSquareCB.setSelected(true);
            annotationFT.setEnabled(false);
        } else {
            otherRB.setSelected(true);
            annotationFT.setEnabled(true);
        }
        annotationFT.setText(annotaionStr);
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
    }


    private void onOK() {

        Config.getInstant().setFieldPrivateMode(fieldPrivateRadioButton.isSelected());
        Config.getInstant().setUseSerializedName(useSerializedNameCheckBox.isSelected());
        Config.getInstant().setArrayFromData(arrayFromDataCB.isSelected());
        Config.getInstant().setArrayFromData1(arrayFromData1CB.isSelected());
        Config.getInstant().setObjectFromData(objectFromDataCB.isSelected());
        Config.getInstant().setObjectFromData1(objectFromData1CB.isSelected());
        Config.getInstant().setReuseEntity(reuseEntityCB.isSelected());
        Config.getInstant().setSuffixStr(suffixEdit.getText());
        Config.getInstant().setVirgoMode(virgoModelCB.isSelected());
        Config.getInstant().setGenerateComments(generateCommentsCT.isSelected());
        Config.getInstant().setFiledNamePreFixStr(filedPrefixTF.getText());
        Config.getInstant().setAnnotationStr(annotationFT.getText());
        Config.getInstant().setUseFiledNamePrefix(filedPrefixCB.isSelected());
        Config.getInstant().save();

        dispose();

    }

    private void createUIComponents() {
    }


    private void onCancel() {
        dispose();
    }


}
