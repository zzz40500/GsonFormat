package config;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Created by zzz40500 on 15/5/31.
 */
public class Config {

    private boolean fieldPrivateMode = true;

    private boolean useSerializedName = false;

    private boolean objectFromData=false;
    private boolean objectFromData1=false;
    private boolean arrayFromData=false;
    private boolean arrayFromData1=false;

    private String objectFromDataStr;
    private String objectFromDataStr1;
    private String arrayFromDataStr;
    private String arrayFromData1Str;

    private String suffixStr;
    private boolean resuseEntity=false;


    private Config() {

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
            config.setSuffixStr(PropertiesComponent.getInstance().getValue("suffixStr", "Entity"));
            config.setResuseEntity(PropertiesComponent.getInstance().getBoolean("resuseEntity", false));

            config.setObjectFromDataStr(PropertiesComponent.getInstance().getValue("objectFromDataStr", Strings.objectFromObject));
            config.setObjectFromDataStr1(PropertiesComponent.getInstance().getValue("objectFromDataStr1", Strings.objectFromObject1));
            config.setArrayFromDataStr(PropertiesComponent.getInstance().getValue("arrayFromDataStr", Strings.arrayFromData));
            config.setArrayFromData1Str(PropertiesComponent.getInstance().getValue("arrayFromData1Str", Strings.arrayFromData1));
        }
        return config;
    }

    public boolean isObjectFromData() {
        return objectFromData;
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

    public boolean isResuseEntity() {
        return resuseEntity;
    }

    public void setResuseEntity(boolean resuseEntity) {
        this.resuseEntity = resuseEntity;
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
        PropertiesComponent.getInstance().setValue("resuseEntity", resuseEntity + "");
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


}
