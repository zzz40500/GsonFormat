import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.ui.awt.RelativePoint;
import config.Config;
import entity.FieldEntity;
import entity.InnerClassEntity;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-7-4
 * Time: 下午3:58
 * To change this template use File | Settings | File Templates.
 */
public class WriterUtil extends WriteCommandAction.Simple {

    protected PsiClass mClass;
    private PsiElementFactory mFactory;
    private Project project;

    private PsiFile mFile;
    private String jsonStr;
    private JsonUtilsDialog mJsonUtilsDialog;
    private JLabel jLabel;

    private List<String> mKeyWordList;
    private List<String> mFilterFields;

    private List<InnerClassEntity> mFilterClass;



    public WriterUtil(JsonUtilsDialog mJsonUtilsDialog, JLabel jLabel,
                      String jsonStr, PsiFile mFile, Project project, PsiClass mClass, PsiFile... files) {
        super(project, files);
        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.jLabel = jLabel;
        this.mJsonUtilsDialog = mJsonUtilsDialog;
        this.jsonStr = jsonStr;
        this.project = project;
        this.mClass = mClass;
        mKeyWordList = new ArrayList<String>();
        mFilterFields = new ArrayList<String>();
        mFilterClass = new ArrayList<InnerClassEntity>();

    }

    @Override
    protected void run() {

        JSONObject json = null;
        mJsonUtilsDialog.mErrorInfo=null;
        try {
            //直接解析
            json = new JSONObject(jsonStr);
        } catch (Exception e) {
            //删除注释代码再解析
            String jsonTS = filterAnnotation(jsonStr);
            try {
                json = new JSONObject(jsonTS);
            } catch (Exception e2) {
                e2.printStackTrace();
                jLabel.setText("data err !!");
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e2.printStackTrace(printWriter);
                printWriter.close();
                String result = writer.toString();
                mJsonUtilsDialog.mErrorInfo=result;
            }
        }

        if (json != null) {
            try {
                mFilterFields = initFilterField(mClass);

                if(Config.getInstant().isResuseEntity()) {
                    initFilterClass();
                }
                parseJson(json);
            } catch (Exception e2) {
                e2.printStackTrace();
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e2.printStackTrace(printWriter);
                printWriter.close();
                String result = writer.toString();
                mJsonUtilsDialog.mErrorInfo=result;
                jLabel.setText("parse err !!");
            }
        }

        if(mJsonUtilsDialog.mErrorInfo == null) {
            showNotification(project, MessageType.INFO, " Convert success ! ");
        }
        mKeyWordList = null;
        mFilterFields = null;
    }

    private void initFilterClass() {

        PsiClass[] psiClasses = this.mClass.getAllInnerClasses();
        for (PsiClass psiClass : psiClasses) {

            InnerClassEntity innerClassEntity1 = new InnerClassEntity();
            innerClassEntity1.setClassName(psiClass.getName());
            innerClassEntity1.setFields(initFilterField(psiClass));
            innerClassEntity1.setPackName("");
            innerClassEntity1.setPsiClass(psiClass);
            recursionInnerClass(innerClassEntity1);
        }
    }

    private void recursionInnerClass(InnerClassEntity innerClassEntity) {

        PsiClass[] innerClassｓ = innerClassEntity.getPsiClass().getInnerClasses();
        if (innerClassｓ.length == 0) {

            mFilterClass.add(innerClassEntity);
        } else {
            for (PsiClass psiClass : innerClassｓ) {
                InnerClassEntity innerClassEntity1 = new InnerClassEntity();
                innerClassEntity1.setClassName(psiClass.getName());
                innerClassEntity1.setFields(initFilterField(psiClass));
                innerClassEntity1.setPsiClass(psiClass);
                innerClassEntity1.setPackName(innerClassEntity.getPackName() + innerClassEntity.getClassName() + ".");
                recursionInnerClass(innerClassEntity1);
            }
        }


    }

    public String filterAnnotation(String str) {

        String temp = str.replaceAll("/\\*" +
                "[\\S\\s]*?" +
                "\\*/", "");
        return temp.replaceAll("//[^\"\\]\\}']*\\s+", "");

    }

    public List<String> initFilterField(PsiClass mClass) {

        PsiField[] psiFields = mClass.getAllFields();
        ArrayList<String> filterFields = new ArrayList<String>();
        for (PsiField psiField : psiFields) {
            String psiFieldText = filterAnnotation(psiField.getText());
            if (psiFieldText.contains("SerializedName")) {
                boolean isSerializedName = false;

                System.out.println("SerializedName");
                psiFieldText = psiFieldText.trim();

                Pattern pattern = Pattern.compile("@com\\s*\\.\\s*google\\s*\\.\\s*gson\\s*\\.\\s*annotations\\s*\\.\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                Matcher matcher = pattern.matcher(psiFieldText);
                if (matcher.find()) {
                    filterFields.add(matcher.group(1));
                    System.out.println("com");
                    isSerializedName = true;
                }
                Pattern pattern2 = Pattern.compile("@\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                Matcher matcher2 = pattern2.matcher(psiFieldText);
                if (matcher2.find()) {
                    System.out.println("@\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
                    filterFields.add(matcher.group(1));
                    isSerializedName = true;
                }
                if (!isSerializedName) {
                    filterFields.add(psiField.getName());
                }
            } else {
                filterFields.add(psiField.getName());
            }
        }

        return filterFields;


    }


    public void parseJson(JSONObject json) {

        /**
         * 关键词
         *
         * */
        mKeyWordList.add("default");
        mKeyWordList.add("public");
        mKeyWordList.add("abstract");
        mKeyWordList.add("null");
        mKeyWordList.add("final");
        mKeyWordList.add("void");
        mKeyWordList.add("implements");
        mKeyWordList.add("this");
        mKeyWordList.add("instanceof");
        mKeyWordList.add("native");
        mKeyWordList.add("new");
        mKeyWordList.add("goto");
        mKeyWordList.add("const");
        mKeyWordList.add("volatile");
        mKeyWordList.add("return");
        mKeyWordList.add("finally");

        Set<String> set = json.keySet();
        if(Config.getInstant().isObjectFromData()){
            createMethod(Config.getInstant().getObjectFromDataStr().replace("$ClassName$",mClass.getName()).trim(),mClass);
        }
        if(Config.getInstant().isObjectFromData1()){
            createMethod(Config.getInstant().getObjectFromDataStr1().replace("$ClassName$",mClass.getName()).trim(),mClass);

        }
        if(Config.getInstant().isArrayFromData()){
            createMethod(Config.getInstant().getArrayFromDataStr().replace("$ClassName$",mClass.getName()).trim(),mClass);

        }
        if(Config.getInstant().isArrayFromData1()){
            createMethod(Config.getInstant().getArrayFromData1Str().replace("$ClassName$",mClass.getName()).trim(),mClass);

        }


        List<String> fieldList = new ArrayList<String>();
        for (String key : set) {
            if (!mFilterFields.contains(key)) {
                fieldList.add(key);
            }
        }

        List<FieldEntity> fields = createField(json, fieldList, mClass);


        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, mClass);
            createGetMethod(fields, mClass);
        }

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.optimizeImports(mFile);
        styleManager.shortenClassReferences(mClass);
        mJsonUtilsDialog.dispose();
    }


    private List<FieldEntity> createField(JSONObject json, List<String> list, PsiClass mClass) {

        List<FieldEntity> fieldEntities = new ArrayList<FieldEntity>();
        StringBuilder sb = new StringBuilder();
        sb.append("/** \n");
        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            sb.append("* " + key + " : " + json.get(key) + "\n");
        }
        sb.append("*/ \n");

        for (int i = 0; i < list.size(); i++) {

            String key = list.get(i);
            Object type = json.get(key);
            StringBuilder filedSb = new StringBuilder();
            //检查是不是关键字

            if (Config.getInstant().isUseSerializedName()) {
                key = captureStringLeaveUnderscore(key);
            }
            if (checkKeyWord(key)) {
                //是关键字 使用注解
                filedSb.append("@com.google.gson.annotations.SerializedName(\"" + key + "\")\n");
                key = key + "X";
            } else {
                if (Config.getInstant().isUseSerializedName()) {
                    filedSb.append("@com.google.gson.annotations.SerializedName(\"" + key + "\")\n");
                }
            }
            String typeStr = typeByValue(mClass, key, type);

            if (Config.getInstant().isFieldPrivateMode()) {
                filedSb.append("private  ").append(typeStr).append(key).append(" ; ");
            } else {
                filedSb.append("public  ").append(typeStr).append(key).append(" ; ");
            }
            String filedStr = null;
            if (i == 0) {
                filedStr = sb.append(filedSb.toString()).toString();
            } else {
                filedStr = filedSb.toString();
            }
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setField(key);
            fieldEntity.setType(typeStr);
            fieldEntities.add(fieldEntity);
            mClass.add(mFactory.createFieldFromText(filedStr, mClass));
        }
        return fieldEntities;

    }

    public boolean checkKeyWord(String key) {

        return mKeyWordList.contains(key);
    }


    @NotNull
    private String typeByValue(PsiClass mClass, String key, Object type) {

        String typeStr;
        if (type instanceof Boolean) {
            typeStr = " boolean ";
        } else if (type instanceof Integer) {
            typeStr = " int ";
        } else if (type instanceof Double) {
            typeStr = " double ";
        } else if (type instanceof Long) {
            typeStr = " long ";
        } else if (type instanceof String) {
            typeStr = " String ";
        } else if (type instanceof Character) {
            typeStr = " char ";
        } else if (type instanceof JSONObject) {


            typeStr = checkInnerClass((JSONObject) type);

            if (typeStr == null) {
                typeStr = " " + createClassSubName(mClass, key, type, mClass) + " ";
                createClassSub(typeStr,(JSONObject) type, mClass);
            } else {
                typeStr = " " + typeStr + " ";
            }

        } else if (type instanceof JSONArray) {
            typeStr = " java.util.List<" + createClassSubName(mClass, key, type, mClass) + "> ";
        } else {
            typeStr = " String ";
        }
        return typeStr;
    }

    private String checkInnerClass(JSONObject jsonObject) {

        for (InnerClassEntity innerClassEntity : mFilterClass) {
            Iterator<String> keys = jsonObject.keys();

            boolean had = true;
            while (keys.hasNext()) {
                String key = keys.next();
                if (!innerClassEntity.getFields().contains(key)) {
                    had = false;
                    break;
                }
            }
            if (had) {
                return innerClassEntity.getPackName() + innerClassEntity.getClassName();
            }
        }
        return null;
    }


    private void createClassSub(String className, JSONObject json, PsiClass mClass) {

        String classContent =
                "public static class " + className + "{}";
        //重点
        PsiClass subClass = mFactory.createClassFromText(classContent, null).getInnerClasses()[0];
        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);
        if(Config.getInstant().isObjectFromData()){
            createMethod(Config.getInstant().getObjectFromDataStr().replace("$ClassName$", subClass.getName()).trim(),subClass);
        }
        if(Config.getInstant().isObjectFromData1()){
            createMethod(Config.getInstant().getObjectFromDataStr1().replace("$ClassName$", subClass.getName()).trim(),subClass);

        }
        if(Config.getInstant().isArrayFromData()){
            createMethod(Config.getInstant().getArrayFromDataStr().replace("$ClassName$", subClass.getName()).trim(),subClass);

        }
        if(Config.getInstant().isArrayFromData1()){
            createMethod(Config.getInstant().getArrayFromData1Str().replace("$ClassName$", subClass.getName()).trim(),subClass);

        }

        List<FieldEntity> fields = createField(json, list, subClass);
        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, subClass);
            createGetMethod(fields, subClass);
        }

        mClass.add(subClass);


        if(Config.getInstant().isResuseEntity()) {

            InnerClassEntity innerClassEntity=new InnerClassEntity();
            innerClassEntity.setClassName(subClass.getName());
            innerClassEntity.setPackName(mClass.getName()+".");
            innerClassEntity.setFields(list);



            mFilterClass.add(innerClassEntity);
        }

    }

    private String createClassSubName(PsiClass aClass, String key, Object o, PsiClass mClass) {

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

            name= stringBuilder.toString()+Config.getInstant().getSuffixStr();

        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) o;
            if (jsonArray.length() > 0) {
                Object item = jsonArray.get(0);
                name = typeByValue(mClass, key, item);
            } else {
                name = "?";
            }
        }
        return name;

    }

    private void createSetMethod(List<FieldEntity> fields, PsiClass mClass) {

        for (FieldEntity field1 : fields) {
            String field = field1.getField();
            String typeStr = field1.getType();
            String method = "public void  set" + captureName(field) + "( " + typeStr + " " + field + ") {   this." + field + " = " + field + ";} ";
            mClass.add(mFactory.createMethodFromText(method, mClass));


        }

    }

    public String captureName(String name) {

        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return name;
    }

    public String captureStringLeaveUnderscore(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String[] strings = str.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            stringBuilder.append(captureName(strings[i]));
        }

        return stringBuilder.toString();

    }

    private void createGetMethod(List<FieldEntity> fields, PsiClass mClass) {

        for (FieldEntity field1 : fields) {
            String field = field1.getField();

            String typeStr = field1.getType();

            if (typeStr.equals(" boolean ")) {

                String method = "public " + typeStr + "   is" + captureName(field) + "() {   return " + field + " ;} ";
                mClass.add(mFactory.createMethodFromText(method, mClass));
            } else {


                String method = "public " + typeStr + "   get" + captureName(field) + "() {   return " + field + " ;} ";
                mClass.add(mFactory.createMethodFromText(method, mClass));
            }


        }

    }
    private void createMethod(String method, PsiClass cla) {

        cla.add(mFactory.createMethodFromText(method, cla));

    }


    /**
     * Display simple notification of given type
     *
     * @param project
     * @param type
     * @param text
     */
    public static void showNotification(Project project, MessageType type, String text) {

        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(text, type, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }
}
