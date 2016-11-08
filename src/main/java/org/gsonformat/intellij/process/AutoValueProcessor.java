package org.gsonformat.intellij.process;

import com.intellij.psi.*;
import org.apache.http.util.TextUtils;
import org.gsonformat.intellij.config.Config;
import org.gsonformat.intellij.config.Constant;
import org.gsonformat.intellij.entity.FieldEntity;
import org.gsonformat.intellij.entity.ClassEntity;

import java.util.regex.Pattern;

/**
 * Created by dim on 16/11/7.
 */
class AutoValueProcessor extends Processor {

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls,IProcessor visitor) {
        super.onStarProcess(classEntity, factory, cls, visitor);
        injectAutoAnnotation(factory, cls);
    }

    private void injectAutoAnnotation(PsiElementFactory factory, PsiClass cls) {
        PsiModifierList modifierList = cls.getModifierList();
        PsiElement firstChild = modifierList.getFirstChild();
        Pattern pattern = Pattern.compile("@.*?AutoValue");
        if (firstChild != null && !pattern.matcher(firstChild.getText()).find()) {
            PsiAnnotation annotationFromText = factory.createAnnotationFromText("@com.google.auto.value.AutoValue", cls);
            modifierList.addBefore(annotationFromText, firstChild);
        }

        if (!modifierList.hasModifierProperty(PsiModifier.ABSTRACT)) {
            modifierList.setModifierProperty(PsiModifier.ABSTRACT, true);
        }
    }

    @Override
    public void generateField(PsiElementFactory factory, FieldEntity fieldEntity, PsiClass cls, ClassEntity classEntity) {

        if (fieldEntity.isGenerate()) {
            StringBuilder fieldSb = new StringBuilder();
            String filedName = fieldEntity.getGenerateFieldName();
            if (!TextUtils.isEmpty(classEntity.getExtra())) {
                fieldSb.append(classEntity.getExtra()).append("\n");
                classEntity.setExtra(null);
            }
            if (fieldEntity.getTargetClass() != null) {
                fieldEntity.getTargetClass().setGenerate(true);
            }

            fieldSb.append(String.format("public abstract %s %s() ; ", fieldEntity.getFullNameType(), filedName));
            cls.add(factory.createMethodFromText(fieldSb.toString(), cls));
        }
    }

    @Override
    public void generateGetterAndSetter(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
    }

    @Override
    public void generateConvertMethod(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
        super.generateConvertMethod(factory, cls, classEntity);
        createMethod(factory, Constant.autoValueMethodTemplate.replace("$className$", cls.getName()).trim(), cls);
    }

    @Override
    protected void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor) {
        super.onEndGenerateClass(factory, classEntity, parentClass, generateClass, visitor);
        injectAutoAnnotation(factory, generateClass);
    }
}
