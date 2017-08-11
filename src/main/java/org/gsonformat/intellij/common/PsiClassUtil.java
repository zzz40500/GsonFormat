package org.gsonformat.intellij.common;

import com.intellij.ide.util.DirectoryUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.http.util.TextUtils;

import java.io.File;

/**
 * Created by dim on 15/8/22.
 */
public class PsiClassUtil {


    public static PsiClass exist(PsiFile psiFile, String generateClass) {
        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
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
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
        }
        return psiClass;
    }

    public static PsiDirectory getJavaSrc(PsiFile psiFile) {
        PsiDirectory psiDirectory = null;
        if (psiFile instanceof PsiJavaFileImpl) {
            String packageName = ((PsiJavaFileImpl) psiFile).getPackageName();
            String[] arg = packageName.split("\\.");
            psiDirectory = psiFile.getContainingDirectory();

            for (int i = 0; i < arg.length; i++) {
                psiDirectory = psiDirectory.getParent();
                if (psiDirectory == null) {
                    break;
                }
            }
        }
        return psiDirectory;
    }

    public static File getPackageFile(PsiFile psiFile, String packageName) {
        PsiDirectory psiDirectory = getJavaSrc(psiFile);
        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        if (packageName == null) {
            return new File(psiDirectory.getVirtualFile().getCanonicalPath());
        }
        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(packageName.trim().replace(".", "/")));
        if (file.exists()) {
            return file;
        }
        return null;
    }


    public static PsiClass getPsiClass(PsiFile psiFile, Project project, String generateClass) throws Throwable {

        PsiClass psiClass = null;
        PsiDirectory psiDirectory = getJavaSrc(psiFile);

        if (psiDirectory == null || psiDirectory.getVirtualFile().getCanonicalPath() == null) {
            return null;
        }

        File file = new File(psiDirectory.getVirtualFile().getCanonicalPath().concat("/")
                .concat(generateClass.trim().replace(".", "/")).concat(".java"));

        String[] strArray = generateClass.replace(" ", "").split("\\.");
        if (TextUtils.isEmpty(generateClass)) {
            return null;
        }
        String className = strArray[strArray.length - 1];
        String packName = generateClass.substring(0, generateClass.length() - className.length());
        if (file.exists()) {
            for (int i = 0; i < strArray.length - 1; i++) {
                psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                if (psiDirectory == null) {
                    return null;
                }
            }
            PsiFile psiFile1 = psiDirectory.findFile(className + ".java");
            if ((psiFile1 instanceof PsiJavaFile) && ((PsiJavaFile) psiFile1).getClasses().length > 0) {
                psiClass = ((PsiJavaFile) psiFile1).getClasses()[0];
            }
            if (psiClass != null) {
                FileEditorManager manager = FileEditorManager.getInstance(project);
                manager.openFile(psiClass.getContainingFile().getVirtualFile(), true, true);
            }

        } else {
            if (!file.getParentFile().exists() && !TextUtils.isEmpty(packName)) {
                psiDirectory = createPackageInSourceRoot(packName, psiDirectory);

            } else {
                for (int i = 0; i < strArray.length - 1; i++) {
                    psiDirectory = psiDirectory.findSubdirectory(strArray[i]);
                    if (psiDirectory == null) {
                        return null;
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

    private PsiClass getPsiClassByName(Project project, String cls) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findClass(cls, searchScope);
    }


    public static String getPackage(PsiClass cls) {
        if (cls.getQualifiedName() == null) {
            return null;
        }
        int i = cls.getQualifiedName().lastIndexOf(".");
        if (i > -1) {
            return cls.getQualifiedName().substring(0, i);
        } else {
            return "";
        }
    }

    public static boolean isClassAvailableForProject(Project project, String className) {
        PsiClass classInModule = JavaPsiFacade.getInstance(project).findClass(className,
                new EverythingGlobalScope(project));
        return classInModule != null;
    }


//    public static  a(PsiElementFactory factory){
//        PsiElement psiElement = cls.addAfter(factory.createCommentFromText("// todo dim " + fieldEntity.getFieldName(), cls), add);
////                    CharTable charTableByTree = SharedImplUtil.findCharTableByTree(
////                            (ASTNode) psiElement);
////                    PsiWhiteSpace psiWhiteSpace = (PsiWhiteSpace) Factory.createSingleLeafElement(TokenType.WHITE_SPACE, "\n\n",
////                                    charTableByTree, PsiManager.getInstance(cls.getProject()));
////                    cls.addAfter(psiWhiteSpace, psiElement);
//    }

}
