package org.gsonformat.intellij.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import org.gsonformat.intellij.entity.ClassEntity;

/**
 * Created by dim on 16/11/7.
 */
class JackProcessor extends Processor {
    @Override
    protected void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass) {
        super.onEndGenerateClass(factory, classEntity, parentClass, generateClass);
//        subClass = parentClass.findInnerClassByName(className, false);
//        if (subClass != null) {
//
//            PsiModifierList modifierList = subClass.getModifierList();
//            PsiAnnotation annotationFromText =
//                    factory.createAnnotationFromText("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)", subClass);
//
//            PsiElement firstChild = modifierList.getFirstChild();
//            modifierList.addBefore(annotationFromText, firstChild);
//
//        }
    }
}
