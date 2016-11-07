package org.gsonformat.intellij.process;

import com.intellij.psi.*;
import org.gsonformat.intellij.entity.ClassEntity;

import java.util.regex.Pattern;

/**
 * Created by dim on 16/11/7.
 */
public class LoganSquareProcessor extends Processor{
    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        super.onStarProcess(classEntity, factory, cls);
        PsiModifierList modifierList = cls.getModifierList();
        PsiElement firstChild = modifierList.getFirstChild();
        Pattern pattern = Pattern.compile("@.*?JsonObject");
        if (firstChild != null && !pattern.matcher(firstChild.getText()).find()) {
            PsiAnnotation annotationFromText = factory.createAnnotationFromText("@com.bluelinelabs.logansquare.annotation.JsonObject", cls);
            modifierList.addBefore(annotationFromText, firstChild);
        }
    }

    @Override
    protected void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass) {
        super.onEndGenerateClass(factory, classEntity, parentClass, generateClass);
        generateClass = parentClass.findInnerClassByName(classEntity.getClassName(), false);
        if (generateClass != null) {
            PsiModifierList modifierList = generateClass.getModifierList();
            PsiAnnotation annotationFromText =
                    factory.createAnnotationFromText("@com.bluelinelabs.logansquare.annotation.JsonObject", generateClass);
            PsiElement firstChild = modifierList.getFirstChild();
            modifierList.addBefore(annotationFromText, firstChild);
        }
    }
}
