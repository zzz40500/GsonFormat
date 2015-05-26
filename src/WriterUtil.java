import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private JDialog jDialog;
    private JLabel jLabel;


    public WriterUtil(JDialog jDialog, JLabel jLabel,
                      String jsonStr, PsiFile mFile, Project project, PsiClass mClass, PsiFile... files) {
        super(project, files);

        mFactory = JavaPsiFacade.getElementFactory(project);
        this.mFile = mFile;
        this.jLabel = jLabel;
        this.jDialog = jDialog;
        this.jsonStr = jsonStr;
        this.project = project;
        this.mClass = mClass;
    }

    @Override
    protected void run() {



        String s1=jsonStr.replaceAll("/\\*\\*" +
                "[\\S\\s]*?" +
                "\\*/","");
        String jsonStr=s1.replaceAll("//\\S+","");
        try {
            JSONObject json = new JSONObject(jsonStr);
            Set<String> set = json.keySet();
            List<String> list = new ArrayList<String>(set);
            createField(json, list, mClass);
            createSetMethod(json, list, mClass);
            createGetMethod(json, list, mClass);
            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
            styleManager.optimizeImports(mFile);
            styleManager.shortenClassReferences(mClass);
            jDialog.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            jLabel.setText("json 格式有误");

        }
    }

    private void createField(JSONObject json, List<String> list, PsiClass mClass) {
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
            String typeStr = typeByValue(mClass, key, type,true);
            String filedStr = "private  " + typeStr + key + " ; ";
            if (i == 0) {
                filedStr = sb.toString() + filedStr;
            }
            mClass.add(mFactory.createFieldFromText(filedStr, mClass));
        }

    }


    @NotNull
    private String typeByValue(PsiClass mClass, String key, Object type ) {

        return  typeByValue(mClass,key,type,false);

    }
    @NotNull
    private String typeByValue(PsiClass mClass, String key, Object type,boolean createClassSub ) {
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
            typeStr = " " + createClassSubName(mClass, key, type, mClass,createClassSub) + " ";
            if(createClassSub) {
                createClassSub(typeStr, type, mClass);
            }
        } else if (type instanceof JSONArray) {
            typeStr = " java.util.List<" + createClassSubName(mClass,key, type, mClass,createClassSub) + "> ";
        } else {
            typeStr = " String ";
        }
        return typeStr;
    }


    private void createClassSub(String className, Object o, PsiClass mClass) {


        if (o instanceof JSONObject) {


            JSONObject jsonObject = (JSONObject) o;
            createClassSub(className, jsonObject, mClass);
        }

    }

    private void createClassSub(String className, JSONObject json, PsiClass mClass) {

        String classContent = "/** */\n " +
                "public  " + className + "(){" +
                "}";
        PsiClass subClass = mFactory.createClass(className.trim());
        subClass.setName(className.trim());

        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);
        createField(json, list, subClass);
        createSetMethod(json, list, subClass);
        createGetMethod(json, list, subClass);
        mClass.add(subClass);

    }

    private String createClassSubName(PsiClass aClass, String key, Object o, PsiClass mClass,boolean createClassSUb) {


        String name="";
        if (o instanceof JSONObject) {
            name= key.substring(0, 1).toUpperCase() + key.substring(1) + "Entity";
        } else if (o instanceof JSONArray) {
            JSONArray jsonArray=(JSONArray)o;
            if(jsonArray.length()>0){
                Object item=jsonArray.get(0);
                name=typeByValue(mClass,key,item,createClassSUb);
            }else{
                name="?";
            }
        }
        return name;

    }

    private void createSetMethod(JSONObject json, List<String> list, PsiClass mClass) {


        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Object type = json.get(key);
            String typeStr;
            typeStr = typeByValue(mClass, key, type);

            String method = "public void  set" + captureName(key) + "( " + typeStr + " " + key + ") {   this." + key + " = " + key + ";} ";
            mClass.add(mFactory.createMethodFromText(method, mClass));


        }

    }

    public String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return name;
    }

    private void createGetMethod(JSONObject json, List<String> list, PsiClass mClass) {


        for (int i = 0; i < list.size(); i++) {
            String key = list.get(i);
            Object type = json.get(key);
            String typeStr;
            typeStr = typeByValue(mClass, key, type);


            if (type instanceof Boolean) {

                String method = "public " + typeStr + "   is" + captureName(key) + "() {   return " + key + " ;} ";
                mClass.add(mFactory.createMethodFromText(method, mClass));
            } else {


                String method = "public " + typeStr + "   get" + captureName(key) + "() {   return " + key + " ;} ";
                mClass.add(mFactory.createMethodFromText(method, mClass));
            }


        }

    }
}
