package org.gsonformat.intellij.process;

import com.intellij.psi.*;
import org.apache.http.util.TextUtils;
import org.gsonformat.intellij.common.PsiClassUtil;
import org.gsonformat.intellij.config.Config;
import org.gsonformat.intellij.entity.ConvertLibrary;
import org.gsonformat.intellij.entity.FieldEntity;
import org.gsonformat.intellij.entity.ClassEntity;

import java.util.HashMap;

import static org.gsonformat.intellij.common.StringUtils.captureName;

/**
 * Created by dim on 16/11/7.
 */
public abstract class Processor {

    private static HashMap<ConvertLibrary, Processor> sProcessorMap = new HashMap<>();

    static {
        sProcessorMap.put(ConvertLibrary.Gson, new GsonProcessor());
        sProcessorMap.put(ConvertLibrary.Jack, new JackProcessor());
        sProcessorMap.put(ConvertLibrary.FastJson, new FastJsonProcessor());
        sProcessorMap.put(ConvertLibrary.AutoValue, new AutoValueProcessor());
        sProcessorMap.put(ConvertLibrary.LoganSquare, new LoganSquareProcessor());
        sProcessorMap.put(ConvertLibrary.Other, new OtherProcessor());
    }

    public static Processor getProcessor(ConvertLibrary convertLibrary) {
        return sProcessorMap.get(convertLibrary);
    }


    public void process(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {
        onStarProcess(classEntity, factory, cls);
        for (FieldEntity fieldEntity : classEntity.getFields()) {
            generateField(factory, fieldEntity, cls, classEntity);
        }
        for (ClassEntity innerClass : classEntity.getInnerClasss()) {
            generateClass(factory, innerClass, cls);
        }
        generateGetterAndSetter(factory, cls, classEntity);
        generateConvertMethod(factory, cls, classEntity);
        onEndProcess(classEntity, factory, cls);
    }

    public void onEndProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {

    }

    public void onStarProcess(ClassEntity classEntity, PsiElementFactory factory, PsiClass cls) {

    }

    public void generateConvertMethod(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {
        if (Config.getInstant().isObjectFromData()) {
            createMethod(factory, Config.getInstant().getObjectFromDataStr().replace("$ClassName$", cls.getName()).trim(), cls);
        }
        if (Config.getInstant().isObjectFromData1()) {
            createMethod(factory, Config.getInstant().getObjectFromDataStr1().replace("$ClassName$", cls.getName()).trim(), cls);
        }
        if (Config.getInstant().isArrayFromData()) {
            createMethod(factory, Config.getInstant().getArrayFromDataStr().replace("$ClassName$", cls.getName()).trim(), cls);
        }
        if (Config.getInstant().isArrayFromData1()) {
            createMethod(factory, Config.getInstant().getArrayFromData1Str().replace("$ClassName$", cls.getName()).trim(), cls);
        }
    }

    public void generateGetterAndSetter(PsiElementFactory factory, PsiClass cls, ClassEntity classEntity) {

        if (Config.getInstant().isFieldPrivateMode()) {
            for (FieldEntity field : classEntity.getFields()) {
                createGetAndSetMethod(factory, cls, field);
            }
        }
    }

    protected void createMethod(PsiElementFactory mFactory, String method, PsiClass cla) {
        cla.add(mFactory.createMethodFromText(method, cla));
    }

    protected void createGetAndSetMethod(PsiElementFactory factory, PsiClass cls, FieldEntity field) {
        if (field.isGenerate()) {
            String fieldName = field.getGenerateFieldName();
            String typeStr = field.getRealType();
            if (Config.getInstant().isUseFieldNamePrefix()) {
                String temp = fieldName.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");
                if (!TextUtils.isEmpty(temp)) {
                    fieldName = temp;
                }
            }
            if (typeStr.equals("boolean") || typeStr.equals("Boolean")) {
                String method = "public ".concat(typeStr).concat("   is").concat(
                        captureName(fieldName)).concat("() {   return ").concat(
                        field.getGenerateFieldName()).concat(" ;} ");
                cls.add(factory.createMethodFromText(method, cls));
            } else {
                String method = "public ".concat(typeStr).concat(
                        "   get").concat(
                        captureName(fieldName)).concat(
                        "() {   return ").concat(
                        field.getGenerateFieldName()).concat(" ;} ");
                cls.add(factory.createMethodFromText(method, cls));
            }

            String arg = fieldName;
            if (Config.getInstant().isUseFieldNamePrefix()) {
                String temp = fieldName.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");
                if (!TextUtils.isEmpty(temp)) {
                    fieldName = temp;
                    arg = fieldName;
                    if (arg.length() > 0) {

                        if (arg.length() > 1) {
                            arg = (arg.charAt(0) + "").toLowerCase() + arg.substring(1);
                        } else {
                            arg = arg.toLowerCase();
                        }
                    }
                }
            }

            String method = "public void  set".concat(captureName(fieldName)).concat("( ").concat(typeStr).concat(" ").concat(arg).concat(") {   ");
            if (field.getGenerateFieldName().equals(arg)) {
                method = method.concat("this.").concat(field.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
            } else {
                method = method.concat(field.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
            }
            cls.add(factory.createMethodFromText(method, cls));
        }
    }

    public void generateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass) {

        onStartGenerateClass(factory, classEntity, parentClass);
        PsiClass generateClass = null;
        if (classEntity.isGenerate()) {
            if (Config.getInstant().isSplitGenerate()) {
                try {
                    generateClass = PsiClassUtil.getPsiClass(
                            parentClass.getContainingFile(), parentClass.getProject(), classEntity.getQualifiedName());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                String classContent =
                        "public static class " + classEntity.getClassName() + "{}";
                generateClass = factory.createClassFromText(classContent, null).getInnerClasses()[0];
            }

            if (generateClass != null) {
                for (FieldEntity fieldEntity : classEntity.getFields()) {
                    generateField(factory, fieldEntity, generateClass, classEntity);
                }

                for (ClassEntity innerClass : classEntity.getInnerClasss()) {

                    generateClass(factory, innerClass, generateClass);
                }
                generateGetterAndSetter(factory, generateClass, classEntity);
                generateConvertMethod(factory, generateClass, classEntity);
                if (!Config.getInstant().isSplitGenerate()) {
                    parentClass.add(generateClass);
                }
            }

        }
        onEndGenerateClass(factory, classEntity, parentClass, generateClass);

    }

    protected void onStartGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass) {

    }

    protected void onEndGenerateClass(PsiElementFactory factory, ClassEntity classEntity, PsiClass parentClass, PsiClass generateClass) {

    }

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
            if (!filedName.equals(fieldEntity.getKey()) || Config.getInstant().isUseSerializedName()) {
                fieldSb.append(Config.getInstant().geFullNameAnnotation().replaceAll("\\{filed\\}", fieldEntity.getKey()));
            }

            if (Config.getInstant().isFieldPrivateMode()) {
                fieldSb.append("private  ").append(fieldEntity.getFullNameType()).append(" ").append(filedName).append(" ; ");
            } else {
                fieldSb.append("public  ").append(fieldEntity.getFullNameType()).append(" ").append(filedName).append(" ; ");
            }
            cls.add(factory.createFieldFromText(fieldSb.toString(), cls));
        }

    }
}
