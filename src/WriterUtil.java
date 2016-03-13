import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import entity.InnerClassEntity;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: dim
 * Date: 14-7-4
 * Time: 下午3:58
 */
public class WriterUtil extends WriteCommandAction.Simple {

    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project mProject;
    private PsiFile mFile;

    public InnerClassEntity  mInnerClassEntity;

    public WriterUtil(JsonUtilsDialog mJsonUtilsDialog, JLabel jLabel,
                       PsiFile mFile, Project project, PsiClass mClass) {
        super(project, null);

        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.mProject = project;
        this.mClass = mClass;

    }

    @Override
    protected void run() {

        mInnerClassEntity.generateField(mProject, mFactory, mClass);
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mProject);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
    }

}
