package org.gsonformat.intellij.entity;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import org.apache.http.util.TextUtils;
import org.gsonformat.intellij.utils.CheckUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dim on 2015/7/15.
 */
public class FieldEntity {

    private String key;
    private String type;
    private String fieldName;
    private String value;
    private String autoCreateFiledName;
    private InnerClassEntity targetClass;
    private boolean generate = true;

    public String getAutoCreateFiledName() {
        return autoCreateFiledName;
    }

    public void setAutoCreateFiledName(String autoCreateFiledName) {
        this.autoCreateFiledName = autoCreateFiledName;
    }

    public InnerClassEntity getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(InnerClassEntity targetClass) {
        this.targetClass = targetClass;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getGenerateFieldName() {

        String field = CheckUtil.getInstant().handleArg(fieldName);
        if (CheckUtil.getInstant().checkKeyWord(field)) {
            return field + "X";
        } else {
            return field;
        }
    }

    public void setFieldName(String fieldName) {
        if (TextUtils.isEmpty(fieldName)) {
            return;
        }
        this.fieldName = fieldName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public String getRealType() {
        if (targetClass != null) {
            return String.format(type, targetClass.getClassName());
        }
        return type;
    }


    public String getFullNameType() {
        if (targetClass != null) {

            String typeStr = null;
            if (TextUtils.isEmpty(targetClass.getPackName())) {
                typeStr = targetClass.getClassName();
            } else {
                typeStr = targetClass.getPackName() + "." + targetClass.getClassName();

            }
            String string = type.replaceAll("List<", "java.util.List<");
            return String.format(string, typeStr);
        }
        return type;
    }

    public void setType(String type1) {

        this.type = type1;
    }

    public void checkAndSetType(String s) {

        if (CheckUtil.getInstant().checkSimpleType(type.trim())) {
            //基本类型
            if (CheckUtil.getInstant().checkSimpleType(s.trim())) {
                this.type = s;
            }
        } else {
            //实体类:
            if (targetClass != null) {
                String regex = getType().replaceAll("%s", "(\\w+)").replaceAll(".", "\\.");
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s);
                if (matcher.find() && matcher.groupCount() > 0) {
                    String temp = matcher.group(1);
                    if (TextUtils.isEmpty(temp)) {
                        targetClass.setClassName(targetClass.getAutoCreateClassName());
                    } else {
                        targetClass.setClassName(temp);
                    }
                }
            }
        }

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void generateFiled(PsiElementFactory mFactory, PsiClass mClass, InnerClassEntity classEntity) {

        if (generate) {

            StringBuilder filedSb = new StringBuilder();
            String filedName = getGenerateFieldName();

            if (!TextUtils.isEmpty(classEntity.getExtra())) {
                filedSb.append(classEntity.getExtra()).append("\n");
                classEntity.setExtra(null);
            }
            if (!filedName.equals(getKey()) || Config.getInstant().isUseSerializedName()) {
                filedSb.append(Config.getInstant().geFullNameAnnotation().replaceAll("\\{filed\\}", getKey()));
            }

            if (Config.getInstant().getAnnotationStr().equals(Strings.autoValueAnnotation)) {
                filedSb.append(String.format("public abstract %s %s() ; ", getFullNameType(), filedName));
                mClass.add(mFactory.createMethodFromText(filedSb.toString(), mClass));
            } else {
                if (Config.getInstant().isFieldPrivateMode()) {
                    filedSb.append("private  ").append(getFullNameType()).append(" ").append(filedName).append(" ; ");
                } else {
                    filedSb.append("public  ").append(getFullNameType()).append(" ").append(filedName).append(" ; ");
                }
                mClass.add(mFactory.createFieldFromText(filedSb.toString(), mClass));
            }
        }
    }


}
