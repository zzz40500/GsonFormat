package org.gsonformat.intellij.process;

import com.intellij.psi.*;
import org.apache.http.util.TextUtils;
import org.gsonformat.intellij.common.FieldHelper;
import org.gsonformat.intellij.common.Try;
import org.gsonformat.intellij.config.Config;
import org.gsonformat.intellij.config.Constant;
import org.gsonformat.intellij.entity.ClassEntity;
import org.gsonformat.intellij.entity.FieldEntity;

import java.util.regex.Pattern;

/**
 * Created by dim on 16/11/7.
 */
class AutoValueProcessor extends Processor {

    @Override
    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls, IProcessor visitor) {
        super.onStarProcess(classEntity, factory, cls, visitor);
        injectAutoAnnotation(factory, cls);
    }

    @Override
    public void generateField(PsiElementFactory factory, FieldEntity fieldEntity, PsiClass cls, ClassEntity classEntity) {

        if (fieldEntity.isGenerate()) {

            Try.run(new Try.TryListener() {
                @Override
                public void run() {
                    cls.add(factory.createMethodFromText(generateFieldText(classEntity, fieldEntity, null), cls));
                }

                @Override
                public void runAgain() {
                    fieldEntity.setFieldName(FieldHelper.generateLuckyFieldName(fieldEntity.getFieldName()));
                    cls.add(factory.createMethodFromText(generateFieldText(classEntity, fieldEntity, Constant.FIXME), cls));
                }

                @Override
                public void error() {
                    cls.addBefore(factory.createCommentFromText("// FIXME generate failure  field " + fieldEntity.getFieldName(), cls), cls.getChildren()[0]);
                }
            });

        }
    }

    @Override
    public void generateGetterAndSetter(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
    }

    @Override
    public void generateConvertMethod(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
        super.generateConvertMethod(factory, cls, classEntity);
//        if (PsiClassUtil.isClassAvailableForProject(cls.getProject(), "com.ryanharter.auto.value.gson.AutoValueGsonAdapterFactoryProcessor")) {
//            String qualifiedName = cls.getQualifiedName();
//            String autoAdapter = qualifiedName.substring(mainPackage.length()+1, qualifiedName.length());
//            createMethod(factory, Constant.autoValueMethodTemplate.replace("$className$", classEntity.getClassName()).replace("$AdapterClassName$", getAutoAdpaterClass(autoAdapter)).trim(), cls);
//        }
    }

    public static String getAutoAdpaterClass(String className) {
        return String.join("_", className.split("\\."));
    }

    @Override
    protected void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass, IProcessor visitor) {
        super.onEndGenerateClass(factory, classEntity, parentClass, generateClass, visitor);
        injectAutoAnnotation(factory, generateClass);
    }

    private void injectAutoAnnotation(PsiElementFactory factory, PsiClass cls) {
        PsiModifierList modifierList = cls.getModifierList();
        if (modifierList != null) {
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
    }

    private String generateFieldText(ClassEntity classEntity, FieldEntity fieldEntity, String fixme) {
        fixme = fixme == null ? "" : fixme;
        StringBuilder fieldSb = new StringBuilder();
        String fieldName = fieldEntity.getGenerateFieldName();
        if (!TextUtils.isEmpty(classEntity.getExtra())) {
            fieldSb.append(classEntity.getExtra()).append("\n");
            classEntity.setExtra(null);
        }
        if (!fieldName.equals(fieldEntity.getKey()) || Config.getInstant().isUseSerializedName()) {
            fieldSb.append(Constant.gsonFullNameAnnotation.replaceAll("\\{filed\\}", fieldEntity.getKey()));
        }
        if (fieldEntity.getTargetClass() != null) {
            fieldEntity.getTargetClass().setGenerate(true);
        }
        return fieldSb.append(String.format("public abstract %s %s(); " + fixme, fieldEntity.getFullNameType(), fieldName)).toString();
    }
}
