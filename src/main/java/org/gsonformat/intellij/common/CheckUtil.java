package org.gsonformat.intellij.common;

import org.gsonformat.intellij.config.Constant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dim on 2015/8/21.
 */
public class CheckUtil {

    private static CheckUtil sCheckUtil;
    private List<String> keyWordList = new ArrayList<String>();
    private List<String> simpleTypeList = new ArrayList<String>();
    private Set<String> declareClassNameList = new HashSet();
    private Set<String> declareFieldNameList = new HashSet();
    private static Pattern sPattern = Pattern.compile("^\\d+");

    private CheckUtil() {
        keyWordList.add("abstract");
        keyWordList.add("assert");
        keyWordList.add("boolean");
        keyWordList.add("break");
        keyWordList.add("byte");
        keyWordList.add("case");
        keyWordList.add("catch");
        keyWordList.add("char");
        keyWordList.add("class");
        keyWordList.add("const");
        keyWordList.add("continue");
        keyWordList.add("default");
        keyWordList.add("do");
        keyWordList.add("double");
        keyWordList.add("else");
        keyWordList.add("enum");
        keyWordList.add("extends");
        keyWordList.add("final");
        keyWordList.add("finally");
        keyWordList.add("float");
        keyWordList.add("for");
        keyWordList.add("goto");
        keyWordList.add("if");
        keyWordList.add("implements");
        keyWordList.add("import");
        keyWordList.add("instanceof");
        keyWordList.add("int");
        keyWordList.add("interface");
        keyWordList.add("long");
        keyWordList.add("native");
        keyWordList.add("new");
        keyWordList.add("package");
        keyWordList.add("private");
        keyWordList.add("protected");
        keyWordList.add("public");
        keyWordList.add("return");
        keyWordList.add("strictfp");
        keyWordList.add("short");
        keyWordList.add("static");
        keyWordList.add("super");
        keyWordList.add("switch");
        keyWordList.add("synchronized");
        keyWordList.add("this");
        keyWordList.add("throw");
        keyWordList.add("throws");
        keyWordList.add("transient");
        keyWordList.add("try");
        keyWordList.add("abstract");
        keyWordList.add("void");
        keyWordList.add("volatile");
        keyWordList.add("while");

        simpleTypeList.add("String");
        simpleTypeList.add("boolean");
        simpleTypeList.add("Boolean");
        simpleTypeList.add("int");
        simpleTypeList.add("Integer");
        simpleTypeList.add("Float");
        simpleTypeList.add("float");
        simpleTypeList.add("Double");
        simpleTypeList.add("double");
        simpleTypeList.add("Long");
        simpleTypeList.add("long");
    }

    public static CheckUtil getInstant() {
        if (sCheckUtil == null) {
            sCheckUtil = new CheckUtil();
        }
        return sCheckUtil;
    }

    public void cleanDeclareData() {
        declareClassNameList.clear();
        declareFieldNameList.clear();
    }


    public boolean containsDeclareClassName(String name) {
        return declareClassNameList.contains(name);
    }

    public void addDeclareClassName(String name) {
        declareClassNameList.add(name.replace(".java", ""));
    }

    public void removeDeclareClassName(String name) {
        declareClassNameList.remove(name);
    }

    public boolean containsDeclareFieldName(String name) {
        return declareFieldNameList.contains(name);
    }

    public void addDeclareFieldName(String name) {
        declareFieldNameList.add(name);
    }

    public void removeDeclareFieldName(String name) {
        declareFieldNameList.remove(name);
    }

    public boolean checkSimpleType(String s) {

        return simpleTypeList.contains(s);
    }

    public boolean checkKeyWord(String key) {
        return keyWordList.contains(key);
    }

    public String handleArg(String arg) {

        arg = arg.replaceAll("-", "");
        Matcher matcher = sPattern.matcher(arg);
        if (matcher.find()) {
            return Constant.DEFAULT_PREFIX + arg;
        } else {
            if (CheckUtil.getInstant().checkKeyWord(arg)) {
                return arg + "X";
            }
            return arg;
        }
    }
}
