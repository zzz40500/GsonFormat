package entity;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import config.Config;
import org.apache.http.util.TextUtils;
import utils.CheckUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzz40500 on 2015/7/15.
 */
public class FieldEntity {

    private String key;
    private String type;
    private String fieldName;
    private String value;
    private String extra;

    private InnerClassEntity targetClass;


    public InnerClassEntity getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(InnerClassEntity targetClass) {
        this.targetClass = targetClass;
    }

    private boolean generate = true;

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {

        this.generate = generate;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
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


    public String getRealType(){
        if(targetClass != null){
            return  String.format(type, targetClass.getClassName());
        }
        return type;
    }


    public void setType(String type1) {

        this.type = type1;
    }

    public void checkAndSetType(String s) {





        if(targetClass  == null){

        }else{
            String regex = getType().replaceAll("%s", "(\\w+)").replaceAll(".", "\\.");
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()&&matcher.groupCount()>0) {
                targetClass.setClassName(matcher.group(1));
            }
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


    public void generateFiled(PsiElementFactory mFactory, PsiClass mClass) {

        if (generate) {

            StringBuilder filedSb = new StringBuilder();
            String filedName = null;
            if (CheckUtil.getInstant().checkKeyWord(getFieldName())) {
                filedName = getFieldName() + "X";
            } else {
                filedName = getFieldName();
            }
            if (!TextUtils.isEmpty(getExtra())) {
                filedSb.append(getExtra()).append("\n");
            }
            if (!filedName.equals(getKey()) || Config.getInstant().isUseSerializedName()) {

                filedSb.append(Config.getInstant().geFullNametAnnotation().replaceAll("\\{filed\\}", getKey()));
//                filedSb.append("@com.google.gson.annotations.SerializedName(\"").append(getKey()).append("\")\n");
            }

            if (Config.getInstant().isFieldPrivateMode()) {
                filedSb.append("private  ").append(getRealType()).append(" ").append(filedName).append(" ; ");
            } else {
                filedSb.append("public  ").append(getRealType()).append(" ").append(filedName).append(" ; ");
            }
            mClass.add(mFactory.createFieldFromText(filedSb.toString(), mClass));
        }
    }


}
