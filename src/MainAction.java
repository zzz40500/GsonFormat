import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;

/**
 * User: dim
 * Date: 14-7-4
 * Time: 下午1:44
 *
 */
public class MainAction extends BaseGenerateAction {
    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project mProject;

    @SuppressWarnings("unused")
    public MainAction() {
        super(null);
    }

    @SuppressWarnings("unused")
    public MainAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        return super.isValidForClass(targetClass)     ;
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {

        return super.isValidForFile(project, editor, file);
    }


    public void actionPerformed(AnActionEvent event) {

        mProject = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, mProject);
        mClass=getTargetClass(editor,mFile);
        JsonUtilsDialog jsonD=new JsonUtilsDialog(mClass,mFactory,mFile, mProject);
        jsonD.setClass(mClass);
        jsonD.setFactory(mFactory);
        jsonD.setFile(mFile);
        jsonD.setProject(mProject);
        jsonD.setSize(600, 400);
        jsonD.setLocationRelativeTo(null);
        jsonD.setVisible(true);

    }

}
