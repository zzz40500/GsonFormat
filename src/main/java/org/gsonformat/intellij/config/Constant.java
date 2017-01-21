package org.gsonformat.intellij.config;

/**
 * Created by dim on 15/5/31.
 */
public class Constant {


    public static final String DEFAULT_PREFIX = "_$";
    public static String FIXME="// FIXME check this code";


    public static final String privateStr = "   private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";
    public static final String publicStr = "    public String name;";

    public static final String privateUseSerializedNameStr = "    @SerializedName(\"name\")\n" +
            "    private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";

    public static final String publicUseSerializedNameStr = "    @SerializedName(\"name\")\n" +
            "    public String name;";

    public static final String objectFromObject = "    public  static $ClassName$ objectFromData(String str){\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,$ClassName$.class);\n" +
            "    }";

    public static final String objectFromObject1 = "    public  static $ClassName$ objectFromData(String str, String key){\n" +
            "\n" +
            "        try {\n" +
            "            org.json.JSONObject jsonObject=new org.json.JSONObject(str);\n" +
            "\n" +
            "            return new com.google.gson.Gson().fromJson(jsonObject.getString(str),$ClassName$.class);\n" +
            "        } catch (org.json.JSONException e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "\n" +
            "        return null;\n" +
            "    }";

    public static final String arrayFromData = "    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str){\n" +
            "\n" +
            "        java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<java.util.ArrayList<$ClassName$>>(){}.getType();\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,listType);\n" +
            "    }";

    public static final String arrayFromData1 = "    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str,String key){\n" +
            "\n" +
            "        try {\n" +
            "            org.json.JSONObject jsonObject=new org.json.JSONObject(str);\n" +
            "            java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<java.util.ArrayList<$ClassName$>>(){}.getType();\n" +
            "\n" +
            "            return new com.google.gson.Gson().fromJson(jsonObject.getString(str),listType);\n" +
            "\n" +
            "        } catch (org.json.JSONException e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "\n" +
            "        return new java.util.ArrayList();\n" +
            "\n" +
            "\n" +
            "    }";

    public static final String autoValueMethodTemplate = "public static com.google.gson.TypeAdapter<$className$> typeAdapter(com.google.gson.Gson gson)" +
            " {\n" +
            "    return new AutoValue_$AdapterClassName$.GsonTypeAdapter(gson);\n" +
            "}";

    public static final String gsonAnnotation = "@com.google.gson.annotations.SerializedName\\s*\\(\\s*\"{filed}\"\\s*\\)";

    public static final String gsonFullNameAnnotation = "@com.google.gson.annotations.SerializedName(\"{filed}\")";

    public static final String fastFullNameAnnotation = "@com.alibaba.fastjson.annotation.JSONField(name=\"{filed}\")";

    public static final String fastAnnotation = "@com.alibaba.fastjson.annotation.JSONField\\s*\\(\\s*name\\s*=\\s*\"{filed}\"\\s*\\)";

    public static final String jackAnnotation = "@com.fasterxml.jackson.annotation.JsonProperty\\s*\\(\\s*\"{filed}\"\\s*\\)";

    public static final String loganSquareAnnotation = "@com.bluelinelabs.logansquare.annotation.JsonField\\s*\\(\\s*name\\s*=\\s*\"{filed}\"\\s*\\)";

    public static final String jackFullNameAnnotation = "@com.fasterxml.jackson.annotation.JsonProperty(\"{filed}\")";

    public static final String autoValueAnnotation = "autoValue";
    public static final String lombokAnnotation = "lombok";

    public static final String loganSquareFullNameAnnotation = "@com.bluelinelabs.logansquare.annotation.JsonField(name=\"{filed}\")";

}
