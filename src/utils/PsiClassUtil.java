package utils;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import org.apache.http.util.TextUtils;
import java.io.File;

/**
 * Created by dim on 15/8/22.
 */
public class PsiClassUtil {


    public static  PsiClass exist(PsiFile psiFile, String generateClass){
        PsiClass psiClass = null;
        PsiDirectory psiDirectory =null;
        if(psiFile instanceof PsiJavaFileImpl){
            String packageName=((PsiJavaFileImpl) psiFile).getPackageName();
            String[] arg=packageName.split("\\.");
            psiDirectory=  psiFile.getContainingDirectory();

            for (int i = 0; i < arg.length; i++) {
                psiDirectory=  psiDirectory.getParent();
                if(psiDirectory==null){
                    break;
                }
            }
        }
        if(psiDirectory ==null || psiDirectory.getVirtualFile().getCanonicalPath()==null){
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(generateClass.length() - className.length(), generateClass.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length-1; i++) {
                psiDirectory= psiDirectory.findSubdirectory(strArray[i]);
                if(psiDirectory == null){
                    return  null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if((psiFile1 instanceof  PsiJavaFile )&& ((PsiJavaFile) psiFile1).getClasses().length>0 ){
                psiClass=((PsiJavaFile) psiFile1).getClasses()[0];
            }
        }
        return psiClass;
    }


    public static PsiClass getPsiClass(PsiFile psiFile,Project project, String generateClass) throws  Throwable{

        PsiClass psiClass = null;
        PsiDirectory psiDirectory =null;
        if(psiFile instanceof PsiJavaFileImpl){
         String packageName=((PsiJavaFileImpl) psiFile).getPackageName();
            String[] arg=packageName.split("\\.");
             psiDirectory=  psiFile.getContainingDirectory();

            for (int i = 0; i < arg.length; i++) {
                psiDirectory=  psiDirectory.getParent();
                if(psiDirectory==null){
                    break;
                }
            }
        }

        if(psiDirectory ==null || psiDirectory.getVirtualFile().getCanonicalPath()==null){
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(0,generateClass.length() - className.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length-1; i++) {
                psiDirectory= psiDirectory.findSubdirectory(strArray[i]);
                if(psiDirectory == null){
                    return  null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if((psiFile1 instanceof  PsiJavaFile )&& ((PsiJavaFile) psiFile1).getClasses().length>0 ){
                psiClass=((PsiJavaFile) psiFile1).getClasses()[0];
            }
            if(psiClass != null) {
                FileEditorManager manager = FileEditorManager.getInstance(project);
                manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
            }

        } else {
            if (!file.getParentFile().exists() && !TextUtils.isEmpty(packName)) {
                psiDirectory = createPackageInSourceRoot(packName, psiDirectory);

            } else {
                for (int i = 0; i < strArray.length-1; i++) {
                    psiDirectory= psiDirectory.findSubdirectory(strArray[i]);
                    if(psiDirectory == null){
                        return  null;
                    }
                }
            }
              psiClass = JavaDirectoryService.getInstance().createClass(psiDirectory, className);

                FileEditorManager manager = FileEditorManager.getInstance(project);
                manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
        }

        return psiClass;
    }
    public static PsiDirectory createPackageInSourceRoot(String packageName, PsiDirectory sourcePackageRoot) {
        return DirectoryUtil.createSubdirectories(packageName, sourcePackageRoot, ".");
    }


}
