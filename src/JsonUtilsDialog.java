import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import config.Config;
import org.apache.http.util.TextUtils;
import utils.PsiClassUtil;
import utils.Toast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JsonUtilsDialog extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPane2;
    private JButton okButton;
    private JButton cancelButton;
    public JLabel errorLB;
    private JTextPane editTP;
    private JButton settingButton;
    private JLabel generateClassLB;
    private JTextField generateClassTF;
    public JPanel generateClassP;


    protected PsiClass mClass;
    protected PsiElementFactory mFactory;
    protected PsiFile mFile;
    protected Project mProject;

    public String mErrorInfo = null;
    public String currentClass = null;

    public JsonUtilsDialog(PsiClass mClass, PsiElementFactory factory, PsiFile file, Project project) throws HeadlessException {
        this.mClass = mClass;
        this.mFactory = factory;
        this.mFile = file;
        this.mProject = project;
        setContentPane(contentPane2);
        setTitle("GsonFormat 1.2.1 ");
        getRootPane().setDefaultButton(okButton);
        this.setAlwaysOnTop(true);
        initGeneratePanel(file);

        initListener();
    }

    private void initListener() {


        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (generateClassTF.isFocusOwner()) {
                    editTP.requestFocus(true);
                } else {
                    onOK();
                }
            }
        });
        editTP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                super.keyReleased(keyEvent);
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER ) {

                        onOK();
                }


            }
        });
        generateClassP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                super.keyReleased(keyEvent);
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                        editTP.requestFocus(true);
                }

            }
        });
        errorLB.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (mErrorInfo != null) {
                    ErrorDialog errorDialog = new ErrorDialog(mErrorInfo);
                    errorDialog.setSize(800, 600);
                    errorDialog.setLocationRelativeTo(null);
                    errorDialog.setVisible(true);
                }

            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        settingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSettingDialog();
            }
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane2.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initGeneratePanel(PsiFile file) {
        cardLayout = (CardLayout) generateClassP.getLayout();
        generateClassTF.setBackground(errorLB.getBackground());
        currentClass = ((PsiJavaFileImpl) file).getPackageName() + "." + file.getName().split("\\.")[0];
        generateClassLB.setText(currentClass);
        generateClassTF.setText(currentClass);
        generateClassTF.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                cardLayout.next(generateClassP);
                if (TextUtils.isEmpty(generateClassTF.getText())) {
                    generateClassLB.setText(currentClass);
                    generateClassTF.setText(currentClass);
                } else {
                    generateClassLB.setText(generateClassTF.getText());
                }
            }
        });

        generateClassLB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                cardLayout.next(generateClassP);


                if(generateClassLB.getText().equals(currentClass) &&! TextUtils.isEmpty( Config.getInstant().getEntityPackName() )&& !Config.getInstant().getEntityPackName().equals("null")){
                    generateClassLB.setText( Config.getInstant().getEntityPackName());
                    generateClassTF.setText( Config.getInstant().getEntityPackName());
                }

                generateClassTF.requestFocus(true);
            }

        });
    }

    private void onOK() {
        this.setAlwaysOnTop(false);
        String jsonSTR = editTP.getText();
        if(TextUtils.isEmpty(jsonSTR)){
            return;
        }

        String generateClassName=generateClassTF.getText().replaceAll(" ","").replaceAll(".java$", "");

        if(TextUtils.isEmpty(generateClassName)|| generateClassName.endsWith(".")){
            Toast.make(mProject,generateClassP, MessageType.ERROR,"the path is not allowed");
            return;
        }
        PsiClass generateClass=null;
        if (!currentClass.equals(generateClassName)) {
            generateClass = PsiClassUtil.exist(mFile, generateClassTF.getText());
        }else{
            generateClass=mClass;
        }

        new ConvertBridge(
                this, errorLB, jsonSTR, mFile, mProject,generateClass,
                mClass, generateClassName).run();
    }

    private void onCancel() {
        dispose();
    }


    public PsiClass getmClass() {
        return mClass;
    }

    public void setmClass(PsiClass mClass) {
        this.mClass = mClass;
    }

    public PsiElementFactory getmFactory() {
        return mFactory;
    }

    public void setmFactory(PsiElementFactory mFactory) {
        this.mFactory = mFactory;
    }


    public void setmProject(Project mProject) {
        this.mProject = mProject;
    }

    public void setmFile(PsiFile mFile) {
        this.mFile = mFile;
    }

    private void createUIComponents() {
    }


    public void openSettingDialog() {

        SettingDialog settingDialog = new SettingDialog(mProject);
        settingDialog.setSize(800, 700);
        settingDialog.setLocationRelativeTo(null);
        settingDialog.setResizable(false);
        settingDialog.setVisible(true);

    }

    public static void main(String[] args) {

    }
}
