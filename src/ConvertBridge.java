import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import config.Config;
import entity.FieldEntity;
import entity.InnerClassEntity;
import org.apache.commons.lang.StringEscapeUtils;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzz40500 on 2015/8/21.
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
            String jsonTS = filterAnnotation(jsonStr);

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


    public static void main(String[] args) {

//        String s = "";
//        s = s.replaceAll("^[\\s\\S]*?\\{", "{");

    }

    private void initFilterClass() {


        if (mGeneratClass == null) {
            return;

        }
        PsiClass[] psiClasses = this.mGeneratClass.getAllInnerClasses();
        for (PsiClass psiClass : psiClasses) {

            InnerClassEntity innerClassEntity1 = new InnerClassEntity();
            innerClassEntity1.setClassName(psiClass.getName());
            innerClassEntity1.setAutoCreateClassName(psiClass.getName());
            innerClassEntity1.setFields(initFilterField(psiClass));
            innerClassEntity1.setPsiClass(psiClass);
            recursionInnerClass(innerClassEntity1);
        }
    }

    private void recursionInnerClass(InnerClassEntity innerClassEntity) {

        PsiClass[] innerClasss = innerClassEntity.getPsiClass().getInnerClasses();
        if (innerClasss.length == 0) {

            mFilterClass.add(innerClassEntity);
        } else {
            for (PsiClass psiClass : innerClasss) {
                InnerClassEntity innerClassEntity1 = new InnerClassEntity();
                innerClassEntity1.setClassName(psiClass.getName());
                innerClassEntity1.setAutoCreateClassName(psiClass.getName());
                innerClassEntity1.setFields(initFilterField(psiClass));
                innerClassEntity1.setPsiClass(psiClass);
                recursionInnerClass(innerClassEntity1);
            }
        }
    }

    public String filterAnnotation(String str) {

        String temp = str.replaceAll("/\\*" +
                "[\\S\\s]*?" +
                "\\*/", "");
        return temp.replaceAll("//[\\S\\s]*?\n", "");

    }

    public List<String> initFilterFieldStr(PsiClass mClass) {

        ArrayList<String> filterFields = new ArrayList<String>();
        if (mClass != null) {
            PsiField[] psiFields = mClass.getAllFields();
            for (PsiField psiField : psiFields) {
                String psiFieldText = filterAnnotation(psiField.getText());
                if (filterRegex != null && psiFieldText.contains(filterRegex)) {
                    boolean isSerializedName = false;

                    psiFieldText = psiFieldText.trim();

//                    Pattern pattern = Pattern.compile("@com\\s*\\.\\s*google\\s*\\.\\s*gson\\s*\\.\\s*annotations\\s*\\.\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
//                                                        @com\\s*\\.\\s*google\\s*\\.\\s*gson\\s*\\.\\s*annotations\\s*\\.\\s*SerializedName\\(\"(\\w+)\"\\)
                    Pattern pattern = Pattern.compile(fullFilterRegex.toString());
                    Matcher matcher = pattern.matcher(psiFieldText);
                    if (matcher.find()) {
                        filterFields.add(matcher.group(1));
                        isSerializedName = true;
                    }
//                    Pattern pattern2 = Pattern.compile("@\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                    Pattern pattern2 = Pattern.compile(briefFilterRegex.toString());
                    Matcher matcher2 = pattern2.matcher(psiFieldText);
                    if (matcher2.find()) {
                        filterFields.add(matcher2.group(1));
                        isSerializedName = true;
                    }
                    if (!isSerializedName) {
                        filterFields.add(psiField.getName());
                    }
                } else {
                    filterFields.add(psiField.getName());
                }
            }
        }

        return filterFields;


    }


    public List<FieldEntity> initFilterField(PsiClass mClass) {
        PsiField[] psiFields = mClass.getAllFields();
        ArrayList<FieldEntity> filterFields = new ArrayList<FieldEntity>();
        for (PsiField psiField : psiFields) {
            String psiFieldText = filterAnnotation(psiField.getText());
            String key = null;
            if (psiFieldText.contains("SerializedName")) {
                boolean isSerializedName = false;
                psiFieldText = psiFieldText.trim();
                Pattern pattern = Pattern.compile("@com\\s*\\.\\s*google\\s*\\.\\s*gson\\s*\\.\\s*annotations\\s*\\.\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                Matcher matcher = pattern.matcher(psiFieldText);
                if (matcher.find()) {
                    key = matcher.group(1);

                    isSerializedName = true;
                }
                Pattern pattern2 = Pattern.compile("@\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                Matcher matcher2 = pattern2.matcher(psiFieldText);
                if (matcher2.find()) {
                    key = matcher2.group(1);

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
                }catch (Exception e){
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

        List<FieldEntity> fieldEntities = new ArrayList<FieldEntity>();
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
        boolean writeExtra = true;


        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Object type = json.get(key);
            if (type instanceof JSONArray) {


                listEntityList.add(key);
                continue;
            }

            FieldEntity fieldEntity = createFiled(parentClass, key, type);

            fieldEntities.add(fieldEntity);
            if (writeExtra) {
                writeExtra = false;
                parentClass.setExtra(sb.toString());
            }
        }

        for (int i = 0; i < listEntityList.size(); i++) {
            String key = listEntityList.get(i);
            Object type = json.get(key);

            FieldEntity fieldEntity = createFiled(parentClass, key, type);

            fieldEntities.add(fieldEntity);
//            if (writeExtra) {
//                writeExtra = false;
//                fieldEntity.setExtra(sb.toString());
//            }

        }

        return fieldEntities;
    }

    private FieldEntity createFiled(InnerClassEntity parentClass, String key, Object type) {

        String filedName = key;
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

        FieldEntity noteBean = null;
        String typeStr;

        if (type instanceof JSONObject) {

            InnerClassEntity classEntity = checkInnerClass((JSONObject) type);
            if (classEntity == null) {
                typeStr = createSubClassName(key, type, parentClass);
                InnerClassEntity innerClassEntity = createJSonObjectClassSub(typeStr, (JSONObject) type, parentClass);
                innerClassEntity.setKey(key);
                innerClassEntity.setType("%s");
                noteBean = innerClassEntity;

            } else {
                typeStr = classEntity.getFiledPackName();

                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setKey(key);
                fieldEntity.setTargetClass(classEntity);
                fieldEntity.setType("%s");
                noteBean = fieldEntity;


            }
        } else if (type instanceof JSONArray) {

            FieldEntity fieldEntity = handJSONArray(parentClass, (JSONArray) type, key, listStr);
            noteBean = fieldEntity;
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
            noteBean = fieldEntity;
            if (type != null && !(noteBean instanceof InnerClassEntity)) {
                noteBean.setValue(type.toString());
            }

        }

        noteBean.setKey(key);

        return noteBean;
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
        if(jsonArray.length()>0){
            Object item = jsonArray.get(0);
            fieldEntity = listTypeByValue(parentClass, key, item, preListType);
        }else{
             fieldEntity = new FieldEntity();
            fieldEntity.setKey(key);

            fieldEntity.setType( String.format(preListType, "?"));

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

                typeStr = classEntity.getFiledPackName();
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


    public String captureName(String name) {

        if (name.length() > 0) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return name;
    }

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

