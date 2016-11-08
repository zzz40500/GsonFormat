package org.gsonformat.intellij.action;

import com.intellij.openapi.application.RunResult;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.gsonformat.intellij.common.PsiClassUtil;

/**
 * Created by dim on 16/11/8.
 */
public class ClassProvider<T extends PsiClass> extends WriteCommandAction.Simple {


    private PsiFile file;
    private Project project;
    private String generateClassName;
    private PsiClass psiClass;

    public ClassProvider(Project project, PsiFile... files) {
        super(project, files);
    }

    public RunResult<T> execute(String generateClassName) {
        this.generateClassName = generateClassName;
        RunResult result = execute();
        result.setResult(psiClass);
        return result;
    }

    @Override
    protected void run() throws Throwable {
        psiClass = PsiClassUtil.getPsiClass(file, project, generateClassName);
    }
}
