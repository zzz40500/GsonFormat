package org.gsonformat.intellij.common;

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

    private static CheckUtil mCheckUtil;
    private List<String> mKeyWordList = new ArrayList<String>();
    private List<String> mSimpleTypeList = new ArrayList<String>();
    private Set<String> declareClassNameList = new HashSet();
    private Set<String> declareFieldNameList = new HashSet();

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

        mSimpleTypeList.add("String");
        mSimpleTypeList.add("boolean");
        mSimpleTypeList.add("Boolean");
        mSimpleTypeList.add("int");
        mSimpleTypeList.add("Integer");
        mSimpleTypeList.add("Float");
        mSimpleTypeList.add("float");
        mSimpleTypeList.add("Double");
        mSimpleTypeList.add("double");
        mSimpleTypeList.add("Long");
        mSimpleTypeList.add("long");
    }

    public static CheckUtil getInstant() {
        if (mCheckUtil == null) {
            mCheckUtil = new CheckUtil();
        }
        return mCheckUtil;
    }

    public void cleanDeclareData() {
        declareClassNameList.clear();
        declareFieldNameList.clear();
    }


    public boolean containsDeclareClassName(String name) {
        return declareClassNameList.contains(name);
    }

    public void addDeclareClassName(String name) {
        declareClassNameList.add(name.replace(".java",""));
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

        return mSimpleTypeList.contains(s);
    }

    public boolean checkKeyWord(String key) {
        return mKeyWordList.contains(key);
    }

    public String handleArg(String arg) {
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(arg);
        if (matcher.find()) {
            return "value" + arg;
        } else {
            if (CheckUtil.getInstant().checkKeyWord(arg)) {
                return arg + "X";
            }
            return arg;
        }
    }
}
