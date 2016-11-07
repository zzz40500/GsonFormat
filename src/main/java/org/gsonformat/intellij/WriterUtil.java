package org.gsonformat.intellij;

import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.gsonformat.intellij.common.CheckUtil;
import org.gsonformat.intellij.entity.ClassEntity;
import org.gsonformat.intellij.process.ClassProcess;
import org.gsonformat.intellij.ui.Toast;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: dim
 * Date: 14-7-4
 * Time: 下午3:58
 */
public class WriterUtil extends WriteCommandAction.Simple {

    private PsiClass cls;
    private PsiElementFactory factory;
    private Project project;
    private PsiFile file;
    private ClassEntity targetClass;

    public WriterUtil(PsiFile file, Project project, PsiClass cls) {
        super(project, file);
        factory = JavaPsiFacade.getElementFactory(project);
        this.file = file;
        this.project = project;
        this.cls = cls;
    }

    public void execute(ClassEntity targetClass) {
        this.targetClass = targetClass;
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "GsonFormat") {

            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);
                long currentTimeMillis = System.currentTimeMillis();
                execute();
                progressIndicator.setIndeterminate(false);
                progressIndicator.setFraction(1.0);
                System.out.println("GsonFormat[" + (System.currentTimeMillis() - currentTimeMillis) + "ms]");
//                Toast.make(project, progressIndicator, MessageType.INFO, "click to see details");
            }
        });
    }

    @NotNull
    @Override
    @Deprecated()
    public RunResult execute() {
        return super.execute();
    }

    @Override
    protected void run() {
        if (targetClass == null) {
            return;
        }
        new ClassProcess(factory, cls).generate(targetClass);
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.optimizeImports(file);
        styleManager.shortenClassReferences(cls);
        targetClass = null;
    }

}
