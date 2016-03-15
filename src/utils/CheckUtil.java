package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dim on 2015/8/21.
 */
public class CheckUtil {

    private static CheckUtil mCheckUtil;
    private List<String> mKeyWordList = new ArrayList<String>();
    private List<String> mSimpleType = new ArrayList<String>();

    private CheckUtil() {
        mKeyWordList.add("abstract");
        mKeyWordList.add("assert");
        mKeyWordList.add("boolean");
        mKeyWordList.add("break");
        mKeyWordList.add("byte");
        mKeyWordList.add("case");
        mKeyWordList.add("catch");
        mKeyWordList.add("char");
        mKeyWordList.add("class");
        mKeyWordList.add("const");
        mKeyWordList.add("continue");
        mKeyWordList.add("default");
        mKeyWordList.add("do");
        mKeyWordList.add("double");
        mKeyWordList.add("else");
        mKeyWordList.add("enum");
        mKeyWordList.add("extends");
        mKeyWordList.add("final");
        mKeyWordList.add("finally");
        mKeyWordList.add("float");
        mKeyWordList.add("for");
        mKeyWordList.add("goto");
        mKeyWordList.add("if");
        mKeyWordList.add("implements");
        mKeyWordList.add("import");
        mKeyWordList.add("instanceof");
        mKeyWordList.add("int");
        mKeyWordList.add("interface");
        mKeyWordList.add("long");
        mKeyWordList.add("native");
        mKeyWordList.add("new");
        mKeyWordList.add("package");
        mKeyWordList.add("private");
        mKeyWordList.add("protected");
        mKeyWordList.add("public");
        mKeyWordList.add("return");
        mKeyWordList.add("strictfp");
        mKeyWordList.add("short");
        mKeyWordList.add("static");
        mKeyWordList.add("super");
        mKeyWordList.add("switch");
        mKeyWordList.add("synchronized");
        mKeyWordList.add("this");
        mKeyWordList.add("throw");
        mKeyWordList.add("throws");
        mKeyWordList.add("transient");
        mKeyWordList.add("abstract");
        mKeyWordList.add("void");
        mKeyWordList.add("volatile");
        mKeyWordList.add("while");

        mSimpleType.add("String");
        mSimpleType.add("boolean");
        mSimpleType.add("Boolean");
        mSimpleType.add("int");
        mSimpleType.add("Integer");
        mSimpleType.add("Float");
        mSimpleType.add("float");
        mSimpleType.add("Double");
        mSimpleType.add("double");
        mSimpleType.add("Long");
        mSimpleType.add("long");
    }

    public static CheckUtil getInstant() {
        if (mCheckUtil == null) {
            mCheckUtil = new CheckUtil();
        }
        return mCheckUtil;
    }


    public boolean checkSimpleType(String s) {

        return mSimpleType.contains(s);
    }

    public boolean checkKeyWord(String key) {
        return mKeyWordList.contains(key);
    }

    public String handleArg(String arg){
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(arg);
        if (matcher.find()) {
            return "value"+arg;
        } else {
            return arg;
        }
    }
}
