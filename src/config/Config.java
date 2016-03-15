package config;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Created by dim on 15/5/31.
 */
public class Config {

    private boolean fieldPrivateMode = true;
    private boolean generateComments =true;
    private boolean useSerializedName = false;

    private boolean objectFromData=false;
    private boolean objectFromData1=false;
    private boolean arrayFromData=false;
    private boolean arrayFromData1=false;

    private String objectFromDataStr;
    private String objectFromDataStr1;
    private String arrayFromDataStr;
    private String arrayFromData1Str;

    /**
     * 注解语句
     */
    private  String annotationStr;

    /**
     * 字段前缀
     */
    private String filedNamePreFixStr;


    /**
     * 处女座模
     */
    private boolean virgoMode=true;

    /**
     * 创建实体类的包名.
     */
    private String entityPackName;

    /**
     * 错误次数,前两次提醒哪里查看错误日志.
     */
    private int errorCount;

    private  boolean useFiledNamePrefix=false;

    private String suffixStr;
    private boolean reuseEntity =false;

    private Config() {

    }
    public void save() {

        PropertiesComponent.getInstance().setValue("fieldPrivateMode", "" + isFieldPrivateMode());
        PropertiesComponent.getInstance().setValue("useSerializedName", isUseSerializedName() + "");
        PropertiesComponent.getInstance().setValue("objectFromData", objectFromData + "");
        PropertiesComponent.getInstance().setValue("objectFromData1", objectFromData1 + "");
        PropertiesComponent.getInstance().setValue("arrayFromData", arrayFromData + "");
        PropertiesComponent.getInstance().setValue("arrayFromData1", arrayFromData1 + "");
        PropertiesComponent.getInstance().setValue("objectFromDataStr", objectFromDataStr + "");
        PropertiesComponent.getInstance().setValue("objectFromDataStr1", objectFromDataStr1 + "");
        PropertiesComponent.getInstance().setValue("arrayFromData1Str", arrayFromData1Str + "");
        PropertiesComponent.getInstance().setValue("suffixStr", suffixStr + "");
        PropertiesComponent.getInstance().setValue("reuseEntity", reuseEntity + "");
        PropertiesComponent.getInstance().setValue("virgoMode", virgoMode + "");
        PropertiesComponent.getInstance().setValue("filedNamePreFixStr", filedNamePreFixStr + "");
        PropertiesComponent.getInstance().setValue("annotationStr", annotationStr + "");
        PropertiesComponent.getInstance().setValue("errorCount", errorCount + "");
        PropertiesComponent.getInstance().setValue("entityPackName", entityPackName + "");
        PropertiesComponent.getInstance().setValue("useFiledNamePrefix", useFiledNamePrefix + "");
        PropertiesComponent.getInstance().setValue("generateComments", generateComments + "");
    }

    private static Config config;
    public static Config getInstant() {
        if (config == null) {
            config = new Config();
            config.setFieldPrivateMode(PropertiesComponent.getInstance().getBoolean("fieldPrivateMode", true));
            config.setUseSerializedName(PropertiesComponent.getInstance().getBoolean("useSerializedName", false));
            config.setObjectFromData(PropertiesComponent.getInstance().getBoolean("objectFromData", false));
            config.setObjectFromData1(PropertiesComponent.getInstance().getBoolean("objectFromData1", false));
            config.setArrayFromData(PropertiesComponent.getInstance().getBoolean("arrayFromData", false));
            config.setArrayFromData1(PropertiesComponent.getInstance().getBoolean("arrayFromData1", false));
            config.setSuffixStr(PropertiesComponent.getInstance().getValue("suffixStr", "Bean"));
            config.setReuseEntity(PropertiesComponent.getInstance().getBoolean("reuseEntity", false));
            config.setObjectFromDataStr(PropertiesComponent.getInstance().getValue("objectFromDataStr", Strings.objectFromObject));
            config.setObjectFromDataStr1(PropertiesComponent.getInstance().getValue("objectFromDataStr1", Strings.objectFromObject1));
            config.setArrayFromDataStr(PropertiesComponent.getInstance().getValue("arrayFromDataStr", Strings.arrayFromData));
            config.setArrayFromData1Str(PropertiesComponent.getInstance().getValue("arrayFromData1Str", Strings.arrayFromData1));
            config.setAnnotationStr(PropertiesComponent.getInstance().getValue("annotationStr", Strings.gsonAnnotation));
            config.setEntityPackName(PropertiesComponent.getInstance().getValue("entityPackName"));
            config.setFiledNamePreFixStr(PropertiesComponent.getInstance().getValue("filedNamePreFixStr"));
            config.setErrorCount(PropertiesComponent.getInstance().getOrInitInt("errorCount", 0));
            config.setVirgoMode(PropertiesComponent.getInstance().getBoolean("virgoMode", true));
            config.setUseFiledNamePrefix(PropertiesComponent.getInstance().getBoolean("useFiledNamePrefix", false));
            config.setGenerateComments(PropertiesComponent.getInstance().getBoolean("generateComments", true));

        }
        return config;
    }

    public boolean isUseFiledNamePrefix() {
        return useFiledNamePrefix;
    }

    public void setUseFiledNamePrefix(boolean useFiledNamePrefix) {
        this.useFiledNamePrefix = useFiledNamePrefix;
    }

    public boolean isObjectFromData() {
        return objectFromData;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getEntityPackName() {
        return entityPackName;
    }
    public String geFullNameAnnotation(){

        if(annotationStr.equals(Strings.gsonAnnotation)){
            return Strings.gsonFullNameAnnotation;
        }
        if(annotationStr.equals(Strings.jackAnnotation)){
            return Strings.jackFullNameAnnotation;
        }
        if(annotationStr.equals(Strings.fastAnnotation)){
            return Strings.fastFullNameAnnotation;
        }
        if(annotationStr.equals(Strings.loganSquareAnnotation)){
            return Strings.loganSquareFullNameAnnotation;
        }


        return annotationStr.replaceAll("\\(", "(").replaceAll("\\)",")").replaceAll("\\s\\*","");
    }


    public boolean isGenerateComments() {
        return generateComments;
    }

    public void setGenerateComments(boolean generateComments) {
        this.generateComments = generateComments;
    }

    public void setEntityPackName(String entityPackName) {
        this.entityPackName = entityPackName;
    }

    public boolean isVirgoMode() {
        return virgoMode;
    }

    public void setVirgoMode(boolean virgoMode) {
        this.virgoMode = virgoMode;
    }

    public String getFiledNamePreFixStr() {
        return filedNamePreFixStr;
    }

    public void setFiledNamePreFixStr(String filedNamePreFixStr) {
        this.filedNamePreFixStr = filedNamePreFixStr;
    }

    public String getAnnotationStr() {
        return annotationStr;
    }

    public void setAnnotationStr(String annotationStr) {
        this.annotationStr = annotationStr;
    }

    public void setObjectFromData(boolean objectFromData) {
        this.objectFromData = objectFromData;
    }

    public boolean isObjectFromData1() {
        return objectFromData1;
    }

    public void setObjectFromData1(boolean objectFromData2) {
        this.objectFromData1 = objectFromData2;
    }

    public boolean isArrayFromData() {
        return arrayFromData;
    }

    public void setArrayFromData(boolean arrayFromData) {
        this.arrayFromData = arrayFromData;
    }

    public boolean isArrayFromData1() {
        return arrayFromData1;
    }

    public void setArrayFromData1(boolean arrayFromData1) {
        this.arrayFromData1 = arrayFromData1;
    }


    public void setObjectFromDataStr(String objectFromDataStr) {
        this.objectFromDataStr = objectFromDataStr;
    }

    public void setObjectFromDataStr1(String objectFromDataStr1) {
        this.objectFromDataStr1 = objectFromDataStr1;
    }

    public void setArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
    }

    public void setArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
    }

    public String getObjectFromDataStr() {
        return objectFromDataStr;
    }



    public String getObjectFromDataStr1() {
        return objectFromDataStr1;
    }


    public String getArrayFromDataStr() {
        return arrayFromDataStr;
    }



    public String getArrayFromData1Str() {
        return arrayFromData1Str;
    }



    public String getSuffixStr() {
        return suffixStr;
    }

    public void setSuffixStr(String suffixStr) {
        this.suffixStr = suffixStr;
    }

    public boolean isReuseEntity() {
        return reuseEntity;
    }

    public void setReuseEntity(boolean reuseEntity) {
        this.reuseEntity = reuseEntity;
    }

    public boolean isUseSerializedName() {
        return useSerializedName;
    }

    public void setUseSerializedName(boolean useSerializedName) {
        this.useSerializedName = useSerializedName;
    }

    public boolean isFieldPrivateMode() {
        return fieldPrivateMode;
    }

    public void setFieldPrivateMode(boolean fieldPrivateMode) {
        this.fieldPrivateMode = fieldPrivateMode;
    }


    public void saveObjectFromDataStr(String objectFromDataStr) {
        this.objectFromDataStr = objectFromDataStr;
        PropertiesComponent.getInstance().setValue("objectFromDataStr", objectFromDataStr + "");
    }

    public void saveObjectFromDataStr1(String objectFromDataStr1) {
        this.objectFromDataStr1 = objectFromDataStr1;
        PropertiesComponent.getInstance().setValue("objectFromDataStr1", objectFromDataStr1 + "");
    }
    public void saveArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
        PropertiesComponent.getInstance().setValue("arrayFromDataStr", arrayFromDataStr + "");
    }

    public void saveArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
        PropertiesComponent.getInstance().setValue("arrayFromData1Str", arrayFromData1Str + "");
    }


    public boolean isToastError() {

       if( Config.getInstant().getErrorCount()<3){
           Config.getInstant().setErrorCount(Config.getInstant().getErrorCount() + 1);
           Config.getInstant().save();
           return true;
       }
        return false;
    }
}
