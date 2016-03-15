import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import config.Config;
import entity.FieldEntity;
import entity.InnerClassEntity;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import src.cn.vearn.checktreetable.FiledTreeTableModel;
import src.org.jdesktop.swingx.ux.CheckTreeTableManager;
import utils.PsiClassUtil;
import utils.Toast;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class FieldsDialog extends JFrame {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JPanel filedPanel;
    private JScrollPane sp;
    private PsiClass mGenerateClass;
    private InnerClassEntity mInnerClassEntity;
    private JsonUtilsDialog mJsonUtilsDialog;
    public PsiElementFactory mFactory;
    public PsiClass mClass;
    public PsiFile mFile;
    public Project project;
    private JLabel generateClass;
    private String generateClassStr;
    private ArrayList<DefaultMutableTreeTableNode> defaultMutableTreeTableNodeList;


    public FieldsDialog(JsonUtilsDialog jsonUtilsDialog, InnerClassEntity innerClassEntity,
                        PsiElementFactory mFactory, PsiClass mGenerateClass, PsiClass mClass, PsiFile mFile, Project project
            , String generateClassStr
    )  {

        mJsonUtilsDialog = jsonUtilsDialog;
        this.mFactory = mFactory;
        this.mClass = mClass;
        this.mFile = mFile;
        this.project = project;
        this.mGenerateClass = mGenerateClass;
        this.generateClassStr = generateClassStr;
        this.setAlwaysOnTop(true);

        setTitle("Virgo Model");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        mInnerClassEntity=innerClassEntity;
        defaultMutableTreeTableNodeList=new ArrayList<DefaultMutableTreeTableNode>();
        JXTreeTable treetable = new JXTreeTable(new FiledTreeTableModel(createData(innerClassEntity)));
        CheckTreeTableManager manager = new CheckTreeTableManager(treetable);
        manager.getSelectionModel().addPathsByNodes(defaultMutableTreeTableNodeList);
        treetable.getColumnModel().getColumn(0).setPreferredWidth(150);
//        treetable.setSelectionBackground(treetable.getBackground());
        treetable.expandAll();
        treetable.setCellSelectionEnabled(false);
        final DefaultListSelectionModel  defaultListSelectionModel=  new DefaultListSelectionModel();
        treetable.setSelectionModel(defaultListSelectionModel);

        defaultListSelectionModel.setSelectionMode(SINGLE_SELECTION);
        defaultListSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                defaultListSelectionModel.clearSelection();
            }
        });

        defaultMutableTreeTableNodeList=null;
        treetable.setRowHeight(30);
        sp.setViewportView(treetable);
        generateClass.setText(generateClassStr);
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

    }

    private void onOK() {

        this.setAlwaysOnTop(false);
        if(mGenerateClass ==null){
            try {
                mGenerateClass= PsiClassUtil.getPsiClass(mFile, project, generateClassStr);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                mJsonUtilsDialog.errorLB.setText("path err !!");
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                throwable.printStackTrace(printWriter);
                printWriter.close();
                mJsonUtilsDialog.mErrorInfo = writer.toString();
                mJsonUtilsDialog.setVisible(true);
                Toast.make(project,mJsonUtilsDialog.generateClassP, MessageType.ERROR,"the path is not allowed");
            }
        }
        if(mGenerateClass!= null){

            String[] arg=generateClassStr.split("\\.");
            if(arg.length>1){
                Config.getInstant().setEntityPackName(generateClassStr.substring(0,generateClassStr.length()-arg[arg.length-1].length()));
                Config.getInstant().save();
            }
            try {
                WriterUtil writerUtil=  new WriterUtil(null, null , mFile,project, mGenerateClass);
                writerUtil.mInnerClassEntity=mInnerClassEntity;
                writerUtil.execute() ;
            }catch (Exception e){
                e.printStackTrace();
                mJsonUtilsDialog.errorLB.setText("parse err !!");
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                printWriter.close();
                mJsonUtilsDialog.mErrorInfo = writer.toString();
                mJsonUtilsDialog.setVisible(true);
//                Toast.make(project, mJsonUtilsDialog.generateClassP, MessageType.ERROR, "the path is not allowed");
                dispose();
            }

        }

        dispose();
    }

    private void onCancel() {

        mJsonUtilsDialog.setVisible(true);
        dispose();
    }

    @Override
    public void dispose() {

        if(mJsonUtilsDialog != null && !mJsonUtilsDialog.isVisible()){
            mJsonUtilsDialog.dispose();
        }
        super.dispose();
    }

    private DefaultMutableTreeTableNode createData(InnerClassEntity innerClassEntity) {

        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode(innerClassEntity);
        createDataNode(root, innerClassEntity);
        return root;
    }

    private void createDataNode(DefaultMutableTreeTableNode root, InnerClassEntity innerClassEntity) {
        for (FieldEntity field: innerClassEntity.getFields()){
            if(field instanceof  InnerClassEntity){
                DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(field);
                root.add(node);
                createDataNode(node, (InnerClassEntity) field);
            }else {
                DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(field);
                root.add(node);
                defaultMutableTreeTableNodeList.add(node);
            }
        }
    }

}
