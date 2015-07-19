import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.event.*;

public class JsonUtilsDialog extends JFrame {
    private JPanel contentPane2;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel errorLB;
    private JTextPane editTP;
    private JButton settingButton;


    protected PsiClass mClass;
    protected  PsiElementFactory mFactory    ;
    protected  PsiFile mFile    ;
    protected  Project mProject;

    public String mErrorInfo=null;

    public JsonUtilsDialog() {
        setContentPane(contentPane2);
//        setModal(true);
        setTitle("GsonFormat");
        getRootPane().setDefaultButton(okButton);
        this.setAlwaysOnTop(true);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        editTP.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }


            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onOK();
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        errorLB.addMouseListener(new MouseAdapter(){


            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if(mErrorInfo != null){
                    ErrorDialog errorDialog=new ErrorDialog(mErrorInfo);
                    errorDialog.setSize(800,600);
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

    private void onOK() {

        String  jsonSTR=   editTP.getText().toString();
        new WriterUtil(this, errorLB,jsonSTR ,  mFile, mProject, mClass).execute() ;
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        JsonUtilsDialog dialog = new JsonUtilsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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


   public void openSettingDialog(){

        SettingDialog settingDialog=new SettingDialog();
        settingDialog.setSize(600,600);
        settingDialog.setLocationRelativeTo(null);
        settingDialog.setResizable(false);
        settingDialog.setVisible(true);

    }
}
