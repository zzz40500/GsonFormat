import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.event.*;

public class JsonUtilsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel label22;
    private JTextPane textPane1;


    protected PsiClass mClass;
    protected  PsiElementFactory mFactory    ;
    protected  PsiFile mFile    ;
    protected  Project project    ;



    public JsonUtilsDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle("GsonFormat");
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        textPane1.addKeyListener(new KeyListener() {
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


        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {

        String  jsonSTR=   textPane1.getText().toString();
        new WriterUtil(this, label22,jsonSTR ,  mFile,  project    , mClass).execute() ;
//        dispose();
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


    public void setProject(Project project) {
        this.project = project;
    }

    public void setmFile(PsiFile mFile) {
        this.mFile = mFile;
    }

    private void createUIComponents() {
    }
}
