package config;

/**
 * Created by zzz40500 on 15/5/31.
 */
public class Strings {


    public static final String privateStr="   private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";
    public static final String publicStr="    public String name;";
    public static final String privateUseSerializedNameStr="    @SerializedName(\"name\")\n" +
            "    private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";
    public static final String publicUseSerializedNameStr="    @SerializedName(\"name\")\n" +
            "    public String name;";



    public static  final String objectFromObject="    public  static $ClassName$ objectFromData(String str){\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,$ClassName$.class);\n" +
            "    }";
    public static  final String objectFromObject1="    public  static $ClassName$ objectFromData(String str, String key){\n" +
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
    public static  final String arrayFromData="    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str){\n" +
            "\n" +
            "        java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<java.util.ArrayList<$ClassName$>>(){}.getType();\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,listType);\n" +
            "    }";
    public static  final String arrayFromData1="    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str,String key){\n" +
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
}
