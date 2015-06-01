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
}
