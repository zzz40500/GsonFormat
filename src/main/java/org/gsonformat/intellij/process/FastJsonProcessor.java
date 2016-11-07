package org.gsonformat.intellij.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import org.gsonformat.intellij.entity.ClassEntity;

/**
 * Created by dim on 16/11/7.
 */
class FastJsonProcessor extends Processor {

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        super.onEndProcess(classEntity, factory, cls);
//        PsiModifierList modifierList = cls.getModifierList();
//        PsiElement firstChild = modifierList.getFirstChild();
//        Pattern pattern = Pattern.compile("@.*?JsonIgnoreProperties");
//
//        if (!pattern.matcher(firstChild.getText()).find()) {
//            PsiAnnotation annotationFromText =
//                    factory.createAnnotationFromText("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)", cls);
//            modifierList.addBefore(annotationFromText, firstChild);
//        }
    }

}
