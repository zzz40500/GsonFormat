package config;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Created by zzz40500 on 15/5/31.
 */
public class Config {

    private boolean fieldPrivateMode = true;

    private boolean useSerializedName = false;


    private Config() {

    }

    private static Config config;


    public static Config getInstant() {


        if (config == null) {
            config = new Config();
            config.setFieldPrivateMode(PropertiesComponent.getInstance().getBoolean("fieldPrivateMode", true));
            config.setUseSerializedName(PropertiesComponent.getInstance().getBoolean("useSerializedName", false));
        }
        return config;
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

    }
}
