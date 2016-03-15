import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import config.Config;
import entity.FieldEntity;
import entity.InnerClassEntity;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.CheckUtil;
import utils.PsiClassUtil;
import utils.Toast;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dim on 2015/8/21.
 * 把 json 转成 实体类
 */
public class ConvertBridge {

    protected PsiClass mGeneratClass;
    protected PsiClass currentClass;
    private PsiElementFactory mFactory;
    private Project project;

    private PsiFile mFile;
    private String jsonStr;
    private JsonUtilsDialog mJsonUtilsDialog;
    private JLabel errorInfoLb;

    private List<String> mFilterFields;

    private List<InnerClassEntity> mFilterClass;
    private String generateClassName;
    private InnerClassEntity mGenerateEntity = new InnerClassEntity();

    private StringBuilder fullFilterRegex = null;
    private StringBuilder briefFilterRegex = null;
    private String filterRegex = null;


    public ConvertBridge(JsonUtilsDialog mJsonUtilsDialog, JLabel errorInfoLb,
                         String jsonStr, PsiFile mFile, Project project,
                         PsiClass generateClass,
                         PsiClass currentClass, String generateClassName, PsiFile... files) {

        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.errorInfoLb = errorInfoLb;
        this.generateClassName = generateClassName;
        this.mJsonUtilsDialog = mJsonUtilsDialog;
        this.jsonStr = jsonStr;
        this.project = project;
        this.mGeneratClass = generateClass;
        this.currentClass = currentClass;
        mFilterFields = new ArrayList<String>();
        mFilterClass = new ArrayList<InnerClassEntity>();

        fullFilterRegex = new StringBuilder();
        briefFilterRegex = new StringBuilder();
        String[] arg = Config.getInstant().getAnnotationStr().replace("{filed}", "(\\w+)").split("\\.");

        for (int i = 0; i < arg.length; i++) {
            String s = arg[i];
            if (i == arg.length - 1) {
                briefFilterRegex.append(s);
                fullFilterRegex.append(s);
                Matcher matcher = Pattern.compile("\\w+").matcher(s);
                if (matcher.find()) {
                    filterRegex = matcher.group();
                }

            } else {
                fullFilterRegex.append(s).append("\\s*\\.\\s*");
            }
        }


    }


    public void run() {

        JSONObject json = null;
        mJsonUtilsDialog.mErrorInfo = null;
        try {
            json = new JSONObject(jsonStr);
        } catch (Exception e) {
            String jsonTS = filterComment(jsonStr);

            jsonTS = jsonTS.replaceAll("^[\\s\\S]*?\\{", "{");
            try {
                json = new JSONObject(jsonTS);
            } catch (Exception e2) {
                e2.printStackTrace();
                errorInfoLb.setText("data err !!");
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e2.printStackTrace(printWriter);
                printWriter.close();
                mJsonUtilsDialog.mErrorInfo = writer.toString();
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorInfoLb, MessageType.ERROR, "click to see details");
                }
            }
        }
        if (json != null) {

            try {

                mFilterFields = initFilterFieldStr(mGeneratClass);
                if (Config.getInstant().isReuseEntity()) {
                    initFilterClass();
                }
                parseJson(json);
            } catch (Exception e2) {
                e2.printStackTrace();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e2.printStackTrace(printWriter);
                printWriter.close();
                mJsonUtilsDialog.mErrorInfo = writer.toString();
                errorInfoLb.setText("parse err !!");
                if (Config.getInstant().isToastError()) {
                    Toast.make(project, errorInfoLb, MessageType.ERROR, "click to see details");
                }

            }
        }


        mFilterFields = null;
        mFilterClass = null;
    }


    private void initFilterClass() {


        if (mGeneratClass == null) {
            return;
        }

        PsiClass[] psiClasses = this.mGeneratClass.getAllInnerClasses();
        for (PsiClass psiClass : psiClasses) {

            InnerClassEntity item = new InnerClassEntity();
            item.setClassName(psiClass.getName());
            item.setAutoCreateClassName(psiClass.getName());
            item.setFields(initFilterField(psiClass));
            item.setPsiClass(psiClass);
            item.setType(mGeneratClass.getName()+"."+psiClass.getName());
            mFilterClass.add(item);

            recursionInnerClass(item);
        }
    }

    /**
     * 遍历所有的内部类.
     *
     * @param innerClassEntity
     */
    private void recursionInnerClass(InnerClassEntity innerClassEntity) {

        PsiClass[] innerClassArray = innerClassEntity.getPsiClass().getInnerClasses();

        for (PsiClass psiClass : innerClassArray) {
            InnerClassEntity item = new InnerClassEntity();
            item.setClassName(psiClass.getName());
            item.setAutoCreateClassName(psiClass.getName());
            item.setFields(initFilterField(psiClass));
            item.setPsiClass(psiClass);
            item.setPackName(innerClassEntity.getFieldPackName());
            item.setType("%s");
            mFilterClass.add(item);
            recursionInnerClass(item);
        }
    }

    /**
     * 过滤掉// 和/** 注释
     *
     * @param str
     * @return
     */
    public String filterComment(String str) {

        String temp = str.replaceAll("/\\*" +
                "[\\S\\s]*?" +
                "\\*/", "");
        return temp.replaceAll("//[\\S\\s]*?\n", "");
    }

    /**
     * 收集类的所有属性.
     */
    public List<String> initFilterFieldStr(PsiClass mClass) {

        ArrayList<String> filterFieldList = new ArrayList<String>();
        if (mClass != null) {
            PsiField[] psiFields = mClass.getAllFields();
            for (PsiField psiField : psiFields) {
                String psiFieldText = filterComment(psiField.getText());
                if (filterRegex != null && psiFieldText.contains(filterRegex)) {
                    boolean isSerializedName = false;
                    psiFieldText = psiFieldText.trim();

                    Pattern pattern = Pattern.compile(fullFilterRegex.toString());
                    Matcher matcher = pattern.matcher(psiFieldText);
                    if (matcher.find()) {
                        filterFieldList.add(matcher.group(1));
                        isSerializedName = true;
                    }
                    pattern = Pattern.compile(briefFilterRegex.toString());
                    matcher = pattern.matcher(psiFieldText);
                    if (matcher.find()) {
                        filterFieldList.add(matcher.group(1));
                        isSerializedName = true;
                    }
                    if (!isSerializedName) {
                        filterFieldList.add(psiField.getName());
                    }
                } else {
                    filterFieldList.add(psiField.getName());
                }
            }
        }

        return filterFieldList;


    }


    /**
     * 收集类的属性
     */
    public List<FieldEntity> initFilterField(PsiClass mClass) {

        PsiField[] psiFields = mClass.getAllFields();
        ArrayList<FieldEntity> filterFields = new ArrayList<FieldEntity>();

        for (PsiField psiField : psiFields) {

            String psiFieldText = filterComment(psiField.getText());
            String key = null;
            if (filterRegex != null && psiFieldText.contains(filterRegex)) {

                boolean isSerializedName = false;
                psiFieldText = psiFieldText.trim();
                Pattern pattern = Pattern.compile(fullFilterRegex.toString());
                Matcher matcher = pattern.matcher(psiFieldText);
                if (matcher.find()) {
                    key = matcher.group(1);
                    isSerializedName = true;
                }

                pattern = Pattern.compile(briefFilterRegex.toString());
                matcher = pattern.matcher(psiFieldText);
                if (matcher.find()) {
                    key = matcher.group(1);
                    isSerializedName = true;
                }
                if (!isSerializedName) {
                    key = psiField.getName();
                }
            } else {
                key = psiField.getName();

            }
            if (key != null) {
                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setKey(key);
                filterFields.add(fieldEntity);
            }

        }
        return filterFields;
    }


    public void parseJson(JSONObject json) {

        Set<String> set = json.keySet();
        List<String> fieldList = new ArrayList<String>();
        for (String key : set) {
            if (!mFilterFields.contains(key)) {
                fieldList.add(key);
            }
        }
        if (Config.getInstant().isVirgoMode()) {
            mGenerateEntity.setClassName("");
            mGenerateEntity.setAutoCreateClassName("");
            mGenerateEntity.setPsiClass(mGeneratClass);
            mGenerateEntity.setFields(createFields(json, fieldList, mGenerateEntity));
            FieldsDialog fieldsDialog = new FieldsDialog(mJsonUtilsDialog, mGenerateEntity, mFactory,
                    mGeneratClass, currentClass, mFile, project, generateClassName);
            fieldsDialog.setSize(800, 500);
            fieldsDialog.setLocationRelativeTo(null);
            fieldsDialog.setVisible(true);
            mJsonUtilsDialog.setVisible(false);
        } else {
            if (mGeneratClass == null) {
                try {
                    mGeneratClass = PsiClassUtil.getPsiClass(mFile, project, generateClassName);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    mJsonUtilsDialog.errorLB.setText("data err !!");
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    throwable.printStackTrace(printWriter);
                    printWriter.close();
                    mJsonUtilsDialog.mErrorInfo = writer.toString();
                    mJsonUtilsDialog.setVisible(true);
                    Toast.make(project, mJsonUtilsDialog.generateClassP, MessageType.ERROR, "the path is not allowed");
                }

            }
            if (mGeneratClass != null) {
                mGenerateEntity.setPsiClass(mGeneratClass);
                String[] arg = generateClassName.split("\\.");
                if (arg.length > 1) {
                    Config.getInstant().setEntityPackName(generateClassName.substring(0, generateClassName.length() - arg[arg.length - 1].length()));
                    Config.getInstant().save();
                }
                Config.getInstant().setEntityPackName(generateClassName);
                try {
                    mGenerateEntity.setFields(createFields(json, fieldList, mGenerateEntity));
                    WriterUtil writerUtil = new WriterUtil(null, null, mFile, project, mGeneratClass);
                    writerUtil.mInnerClassEntity = mGenerateEntity;
                    writerUtil.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    mJsonUtilsDialog.errorLB.setText("parse err !!");
                    Writer writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                    mJsonUtilsDialog.mErrorInfo = writer.toString();
                    mJsonUtilsDialog.setVisible(true);
                    if (Config.getInstant().isToastError()) {
                        Toast.make(project, errorInfoLb, MessageType.ERROR, "click to see details");
                    }
                    return;
                }
            }
            //消失
            mJsonUtilsDialog.dispose();


        }


    }


    private List<FieldEntity> createFields(JSONObject json, List<String> list, InnerClassEntity parentClass) {

        List<FieldEntity> fieldEntityList = new ArrayList<FieldEntity>();
        StringBuilder sb = new StringBuilder();
        sb.append("/** \n");
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            sb.append("* ").append(key).append(" : ");
            sb.append(json.get(key).toString().replaceAll("\r", "")
                    .replaceAll("\t ", "").replaceAll("\f", ""));
            sb.append("\n");
        }
        sb.append("*/ \n");

        List<String> listEntityList = new ArrayList<String>();
        boolean writeExtra = Config.getInstant().isGenerateComments();

        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Object type = json.get(key);
            if (type instanceof JSONArray) {
                listEntityList.add(key);
                continue;
            }

            FieldEntity fieldEntity = createFiled(parentClass, key, type);

            fieldEntityList.add(fieldEntity);
            if (writeExtra) {
                writeExtra = false;

                parentClass.setExtra(sb.toString());
            }
        }

        for (int i = 0; i < listEntityList.size(); i++) {
            String key = listEntityList.get(i);
            Object type = json.get(key);

            FieldEntity fieldEntity = createFiled(parentClass, key, type);
            fieldEntityList.add(fieldEntity);
        }

        return fieldEntityList;
    }

    private FieldEntity createFiled(InnerClassEntity parentClass, String key, Object type) {

        String filedName = CheckUtil.getInstant().handleArg(key);
        if (CheckUtil.getInstant().checkKeyWord(filedName)) {
            filedName = filedName + "X";
        }

        if (Config.getInstant().isUseSerializedName()) {
            if (Config.getInstant().isUseFiledNamePrefix() && !TextUtils.isEmpty(Config.getInstant().getFiledNamePreFixStr())) {
                filedName = Config.getInstant().getFiledNamePreFixStr() + "_" + filedName;
            }
            filedName = captureStringLeaveUnderscore(filedName);
        }


        FieldEntity fieldEntity = typeByValue(parentClass, key, type);
        fieldEntity.setFieldName(filedName);
        fieldEntity.setAutoCreateFiledName(filedName);


        return fieldEntity;

    }


    private FieldEntity typeByValue(InnerClassEntity parentClass, String key, Object type) {

        FieldEntity nodeBean = null;
        String typeStr;

        if (type instanceof JSONObject) {

            InnerClassEntity classEntity = checkInnerClass((JSONObject) type);
            if (classEntity == null) {
                typeStr = createSubClassName(key, type, parentClass);
                InnerClassEntity innerClassEntity = createJSonObjectClassSub(typeStr, (JSONObject) type, parentClass);
                innerClassEntity.setKey(key);
                innerClassEntity.setType("%s");
                nodeBean = innerClassEntity;

            } else {

                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setKey(key);
                fieldEntity.setTargetClass(classEntity);
                fieldEntity.setType("%s");
                nodeBean = fieldEntity;
            }
        } else if (type instanceof JSONArray) {

            FieldEntity fieldEntity = handJSONArray(parentClass, (JSONArray) type, key, listStr);
            nodeBean = fieldEntity;
        } else {

            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setKey(key);
            if (type instanceof Boolean) {
                typeStr = "boolean";

            } else if (type instanceof Integer) {
                typeStr = "int";
            } else if (type instanceof Double) {
                typeStr = "double";
            } else if (type instanceof Long) {
                typeStr = "long";
            } else if (type instanceof String) {
                typeStr = "String";

            } else {
                typeStr = "Object";
            }

            fieldEntity.setType(typeStr);
            nodeBean = fieldEntity;
            if (type != null && !(nodeBean instanceof InnerClassEntity)) {
                nodeBean.setValue(type.toString());
            }

        }

        nodeBean.setKey(key);

        return nodeBean;
    }

    private InnerClassEntity checkInnerClass(JSONObject jsonObject) {

        for (InnerClassEntity innerClassEntity : mFilterClass) {
            Iterator<String> keys = jsonObject.keys();

            boolean had = false;
            while (keys.hasNext()) {
                String key = keys.next();
                had = false;

                for (FieldEntity fieldEntity : innerClassEntity.getFields()) {
                    if (fieldEntity.getKey().equals(key)) {
                        had = true;
                        break;
                    }
                }
                if (!had) {
                    break;
                }
            }
            if (had) {
//
                return innerClassEntity;
            }
        }
        return null;
    }

    private InnerClassEntity createJSonObjectClassSub(String className, JSONObject json, InnerClassEntity parentClass) {

        InnerClassEntity subClassEntity = new InnerClassEntity();
        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);
        List<FieldEntity> fields = createFields(json, list, subClassEntity);
        subClassEntity.setFields(fields);
        subClassEntity.setClassName(className);
        subClassEntity.setAutoCreateClassName(className);
        if (Config.getInstant().isReuseEntity()) {
            mFilterClass.add(subClassEntity);
        }
        return subClassEntity;
    }

    private String createSubClassName(String key, Object o, InnerClassEntity parentClass) {
        String name = "";
        if (o instanceof JSONObject) {
            if (TextUtils.isEmpty(key)) {
                return key;
            }
            String[] strings = key.split("_");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                stringBuilder.append(captureName(strings[i]));
            }

            name = stringBuilder.toString() + Config.getInstant().getSuffixStr();

        }
        return name;

    }

    private FieldEntity handJSONArray(InnerClassEntity parentClass, JSONArray jsonArray, String key, String preListType) {

        FieldEntity fieldEntity = null;
        if (jsonArray.length() > 0) {

            Object item = jsonArray.get(0);
            fieldEntity = listTypeByValue(parentClass, key, item, preListType);
        } else {

            fieldEntity = new FieldEntity();
            fieldEntity.setKey(key);
            fieldEntity.setType(String.format(preListType, "?"));
        }

        return fieldEntity;
    }

    public static final String listStr = "java.util.List<%s>";


    private FieldEntity listTypeByValue(InnerClassEntity parentClass, String key, Object type, String s) {

        FieldEntity noteBean = null;
        String typeStr;
        if (type instanceof JSONObject) {
            InnerClassEntity classEntity = checkInnerClass((JSONObject) type);
            if (classEntity == null) {
                typeStr = s;
                InnerClassEntity innerClassEntity = createJSonObjectClassSub(typeStr, (JSONObject) type, parentClass);
                innerClassEntity.setType(typeStr);
                innerClassEntity.setKey(key);
                innerClassEntity.setClassName(createSubClassName(key, type, parentClass));
                innerClassEntity.setAutoCreateClassName(innerClassEntity.getClassName());
                noteBean = innerClassEntity;
            } else {

                typeStr = classEntity.getFieldPackName();
                typeStr = String.format(s, typeStr);
                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setKey(key);
                fieldEntity.setTargetClass(classEntity);
                fieldEntity.setType(typeStr);
                noteBean = fieldEntity;
            }

        } else if (type instanceof JSONArray) {
            typeStr = String.format(s, listStr);
            FieldEntity fieldEntity = handJSONArray(parentClass, (JSONArray) type, key, typeStr);
            fieldEntity.setKey(key);
            noteBean = fieldEntity;
        } else {
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setKey(key);

            if (type instanceof Boolean) {
                typeStr = String.format(s, type.getClass().getSimpleName());

            } else if (type instanceof Integer) {
                typeStr = String.format(s, type.getClass().getSimpleName());

            } else if (type instanceof Double) {
                typeStr = String.format(s, type.getClass().getSimpleName());

            } else if (type instanceof Long) {
                typeStr = String.format(s, type.getClass().getSimpleName());
            } else if (type instanceof String) {
                typeStr = String.format(s, type.getClass().getSimpleName());

            } else {
                typeStr = String.format(s, "?");
            }
            fieldEntity.setType(typeStr);
            noteBean = fieldEntity;
        }
        return noteBean;
    }


    public String captureName(String text) {

        if (text.length() > 0) {
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    /**
     * 转成驼峰
     *
     * @param str
     * @return
     */
    public String captureStringLeaveUnderscore(String str) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String temp = str.replaceAll("^_+", "");

        if (!TextUtils.isEmpty(temp)) {
            str = temp;
        }

        String[] strings = str.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            stringBuilder.append(captureName(strings[i]));
        }
        return stringBuilder.toString();
    }
}

