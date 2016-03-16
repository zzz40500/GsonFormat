package entity;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import config.Config;
import config.Strings;
import org.apache.http.util.TextUtils;
import utils.CheckUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dim on 2015/7/15.
 */
public class InnerClassEntity extends FieldEntity {

    private PsiClass psiClass;
    private String fieldTypeSuffix;
    private String className;
    private List<? extends FieldEntity> fields;
    private String packName;
    /**
     * 存储 comment
     */
    private String extra;
    private String autoCreateClassName;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getAutoCreateClassName() {
        return autoCreateClassName;
    }

    public void setAutoCreateClassName(String autoCreateClassName) {
        this.autoCreateClassName = autoCreateClassName;
    }

    public <T extends FieldEntity> void setFields(List<T> fields) {
        this.fields = fields;
    }


    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getRealType() {
        return String.format(super.getType(), className);
    }

    public void checkAndSetType(String s) {

        String regex = getType().replaceAll("%s", "(\\\\w+)").replaceAll("\\.", "\\\\.");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.find() && matcher.groupCount() > 0) {

            String temp = matcher.group(1);
            if (TextUtils.isEmpty(temp)) {
                setClassName(getAutoCreateClassName());
            } else {
                setClassName(matcher.group(1));
            }

        }

    }

    public String getFieldTypeSuffix() {
        return fieldTypeSuffix;
    }

    public void setFieldTypeSuffix(String fieldTypeSuffix) {
        this.fieldTypeSuffix = fieldTypeSuffix;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<? extends FieldEntity> getFields() {
        return fields;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public String getClassFieldType() {
        String fullClassName;
        if (!TextUtils.isEmpty(getFieldTypeSuffix())) {
            fullClassName = getFieldTypeSuffix() + "." + getClassName();
        } else {
            fullClassName = getClassName();
        }

        if (TextUtils.isEmpty(getType())) {
            return fullClassName;
        }

        String string=getType().replaceAll("List<","java.util.List<");

        return String.format(string, fullClassName);
    }

    private PsiClass getPsiClassByName(Project project, String cls) {
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        return javaPsiFacade.findClass(cls, searchScope);
    }

//    public void addImportClass(PsiClass mClass,PsiElementFactory mFactory,Project project){
//
//        mClass.addBefore(mFactory.createImportStatement(getPsiClassByName(project, "java.util.List")),mClass);
//    }

    public void generateField(Project project, PsiElementFactory mFactory, PsiClass mClass) {

        try {

            if (Config.getInstant().getAnnotationStr().equals(Strings.fastAnnotation)) {

//                PsiModifierList modifierList = mClass.getModifierList();
//                PsiElement firstChild = modifierList.getFirstChild();
//                Pattern pattern = Pattern.compile("@.*?JsonIgnoreProperties");

//                if (!pattern.matcher(firstChild.getText()).find()) {
//                    PsiAnnotation annotationFromText =
//                            mFactory.createAnnotationFromText("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)", mClass);
//                    modifierList.addBefore(annotationFromText, firstChild);
//                }
            } else if (Config.getInstant().getAnnotationStr().equals(Strings.loganSquareAnnotation)) {
                PsiModifierList modifierList = mClass.getModifierList();
                PsiElement firstChild = modifierList.getFirstChild();
                Pattern pattern = Pattern.compile("@.*?JsonObject");
                if (firstChild != null && !pattern.matcher(firstChild.getText()).find()) {
                    PsiAnnotation annotationFromText = mFactory.createAnnotationFromText("@com.bluelinelabs.logansquare.annotation.JsonObject", mClass);
                    modifierList.addBefore(annotationFromText, firstChild);
                }
            }
        } catch (Throwable e) {
        }

        if (Config.getInstant().isObjectFromData()) {
            createMethod(mFactory, Config.getInstant().getObjectFromDataStr().replace("$ClassName$", mClass.getName()).trim(), mClass);
        }
        if (Config.getInstant().isObjectFromData1()) {
            createMethod(mFactory, Config.getInstant().getObjectFromDataStr1().replace("$ClassName$", mClass.getName()).trim(), mClass);

        }
        if (Config.getInstant().isArrayFromData()) {
            createMethod(mFactory, Config.getInstant().getArrayFromDataStr().replace("$ClassName$", mClass.getName()).trim(), mClass);

        }
        if (Config.getInstant().isArrayFromData1()) {
            createMethod(mFactory, Config.getInstant().getArrayFromData1Str().replace("$ClassName$", mClass.getName()).trim(), mClass);
        }

        for (FieldEntity fieldEntity : getFields()) {
            if (fieldEntity instanceof InnerClassEntity) {
                ((InnerClassEntity) fieldEntity).generateSupperFiled(mFactory, mClass);
                ((InnerClassEntity) fieldEntity).generateClass(mFactory, mClass);
            } else {
                fieldEntity.generateFiled(mFactory, mClass, this);
            }
        }

        if (Config.getInstant().isFieldPrivateMode()) {
            createGetAndSetMethod(mFactory, getFields(), mClass);
        }


    }

    public void generateClass(PsiElementFactory mFactory, PsiClass parentClass) {

        if (isGenerate()) {
            String classContent =
                    "public static class " + className + "{}";
            PsiClass subClass = mFactory.createClassFromText(classContent, null).getInnerClasses()[0];

            if (Config.getInstant().isObjectFromData()) {
                createMethod(mFactory, Config.getInstant().getObjectFromDataStr().replace("$ClassName$", subClass.getName()).trim(), subClass);
            }
            if (Config.getInstant().isObjectFromData1()) {
                createMethod(mFactory, Config.getInstant().getObjectFromDataStr1().replace("$ClassName$", subClass.getName()).trim(), subClass);
            }
            if (Config.getInstant().isArrayFromData()) {
                createMethod(mFactory, Config.getInstant().getArrayFromDataStr().replace("$ClassName$", subClass.getName()).trim(), subClass);

            }
            if (Config.getInstant().isArrayFromData1()) {
                createMethod(mFactory, Config.getInstant().getArrayFromData1Str().replace("$ClassName$", subClass.getName()).trim(), subClass);

            }


            for (FieldEntity fieldEntity : getFields()) {

                if (fieldEntity instanceof InnerClassEntity) {
                    ((InnerClassEntity) fieldEntity).generateSupperFiled(mFactory, subClass);
                    ((InnerClassEntity) fieldEntity).setFieldTypeSuffix(getClassFieldType());
                    ((InnerClassEntity) fieldEntity).generateClass(mFactory, subClass);
                } else {

                    fieldEntity.generateFiled(mFactory, subClass, this);
                }
            }
            if (Config.getInstant().isFieldPrivateMode()) {

                createGetAndSetMethod(mFactory, getFields(), subClass);
            }
            parentClass.add(subClass);

            if (Config.getInstant().getAnnotationStr().equals(Strings.jackAnnotation)) {
//                subClass = parentClass.findInnerClassByName(className, false);
//                if (subClass != null) {
//
//                    PsiModifierList modifierList = subClass.getModifierList();
//                    PsiAnnotation annotationFromText =
//                            mFactory.createAnnotationFromText("@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)", subClass);
//
//                    PsiElement firstChild = modifierList.getFirstChild();
//                    modifierList.addBefore(annotationFromText, firstChild);
//
//                }

            } else if (Config.getInstant().getAnnotationStr().equals(Strings.loganSquareAnnotation)) {
                subClass = parentClass.findInnerClassByName(className, false);
                if (subClass != null) {
                    PsiModifierList modifierList = subClass.getModifierList();
                    PsiAnnotation annotationFromText =
                            mFactory.createAnnotationFromText("@com.bluelinelabs.logansquare.annotation.JsonObject", subClass);
                    PsiElement firstChild = modifierList.getFirstChild();
                    modifierList.addBefore(annotationFromText, firstChild);
                }
            }
        }
    }

    public void generateSupperFiled(PsiElementFactory mFactory, PsiClass prentClass) {

        if (isGenerate()) {

            StringBuilder filedSb = new StringBuilder();
            String filedName = getGenerateFieldName();
            if (CheckUtil.getInstant().checkKeyWord(filedName)) {
                filedName = filedName + "X";
            }
            if (!TextUtils.isEmpty(this.getExtra())) {
                filedSb.append(this.getExtra()).append("\n");
                this.setExtra(null);
            }
            if (!filedName.equals(getKey()) || Config.getInstant().isUseSerializedName()) {

                filedSb.append(Config.getInstant().geFullNameAnnotation().replaceAll("\\{filed\\}", getKey()));
            }

            if (Config.getInstant().isFieldPrivateMode()) {

                filedSb.append("private  ");

            } else {
                filedSb.append("public  ");
            }

            filedSb.append(getClassFieldType()).append(" ").append(filedName).append(" ; ");
            prentClass.add(mFactory.createFieldFromText(filedSb.toString(), prentClass));
        }
    }

    private void createMethod(PsiElementFactory mFactory, String method, PsiClass cla) {
        cla.add(mFactory.createMethodFromText(method, cla));
    }

    public String captureName(String name) {

        if (name.length() > 0) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }

    private void createGetAndSetMethod(PsiElementFactory mFactory, List<? extends FieldEntity> fields, PsiClass mClass) {

        for (FieldEntity field1 : fields) {
            if (field1.isGenerate()) {
                String field = field1.getGenerateFieldName();
                String typeStr = field1.getRealType();
                if (Config.getInstant().isUseFiledNamePrefix()) {
                    String temp = field.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");
                    if (!TextUtils.isEmpty(temp)) {

                        field = temp;
                    }
                }
                if (typeStr.equals("boolean") || typeStr.equals("Boolean")) {
                    String method = "public ".concat(typeStr).concat("   is").concat(
                            captureName(field)).concat("() {   return ").concat(
                            field1.getGenerateFieldName()).concat(" ;} ");
                    mClass.add(mFactory.createMethodFromText(method, mClass));
                } else {

                    String method = "public "
                            .concat(typeStr).concat(
                                    "   get").concat(
                                    captureName(field)).concat(
                                    "() {   return ").concat(
                                    field1.getGenerateFieldName()).concat(" ;} ");
                    mClass.add(mFactory.createMethodFromText(method, mClass));
                }

                String arg = field;
                if (Config.getInstant().isUseFiledNamePrefix()) {

                    String temp = field.replaceAll("^" + Config.getInstant().getFiledNamePreFixStr(), "");

                    if (!TextUtils.isEmpty(temp)) {
                        field = temp;
                        arg = field;
                        if (arg.length() > 0) {

                            if (arg.length() > 1) {
                                arg = (arg.charAt(0) + "").toLowerCase() + arg.substring(1);
                            } else {
                                arg = arg.toLowerCase();
                            }
                        }
                    }
                }

                String method = "public void  set".concat(captureName(field)).concat("( ").concat(typeStr).concat(" ").concat(arg).concat(") {   ");
                if (field1.getGenerateFieldName().equals(arg)) {
                    method = method.concat("this.").concat(field1.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
                } else {
                    method = method.concat(field1.getGenerateFieldName()).concat(" = ").concat(arg).concat(";} ");
                }

                mClass.add(mFactory.createMethodFromText(method, mClass));
            }
        }

    }


}
