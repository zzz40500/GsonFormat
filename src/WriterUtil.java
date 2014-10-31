import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
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
    protected void run()  {


        try {
            JSONObject json = new JSONObject(jsonStr);
            Set<String> set = json.keySet();
            List<String>  list = new ArrayList<String>(set);
            createField(json,list, mClass);
            createSetMethod(json,list,mClass);
            createGetMethod(json,list,mClass);
            JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
            styleManager.optimizeImports(mFile);
            styleManager.shortenClassReferences(mClass);
            jDialog.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            jLabel.setText("json 格式有误");

        }
    }

    private void createField( JSONObject json, List<String> list ,PsiClass mClass){
        StringBuilder sb = new StringBuilder();
        sb.append("/** \n");
        for(int i=0;i<list.size();i++){
            String key = list.get(i) ;
            sb.append("* " + key + " : " + json.get(key) + "\n");
        }
        sb.append("*/ \n");

        for(int i=0;i<list.size();i++){
            String key = list.get(i) ;

            String type=json.get(key).getClass().getSimpleName();


            String typeStr  ;
            if(type.equals("Boolean")){
                typeStr=" boolean ";
            }else if(type.equals("Integer"))   {
                typeStr=" int ";
            }else if(type.equals("Double"))   {
                typeStr=" double ";
            }     else if(type.equals("JSONObject")){
                typeStr=" "+createClassSubName(key,mClass)+" ";
                createClassSub(typeStr,json.getJSONObject(key),mClass);
            }    else if(type.equals("JSONArray")){

                typeStr=" java.util.List<"+createClassSubName(key,mClass)+"> " ;
                createClassSub(createClassSubName(key,mClass),json.getJSONArray(key).get(0),mClass);
            }    else{
                typeStr=" String "      ;
            }
            String filedStr=  "private  " +typeStr+ key + " ; "     ;


            if(i==0){
                filedStr=sb.toString()+filedStr;
            }
            mClass.add(mFactory.createFieldFromText(filedStr, mClass));
        }

    }


    private void createClassSub(String className,Object o,PsiClass mClass){


        System.out.println(o.getClass().toString());
        if(o instanceof JSONObject ){

            JSONObject jsonObject=(JSONObject)o;

            createClassSub(className,jsonObject,mClass);
        }

    }

    private void createClassSub(String className,JSONObject json,PsiClass mClass){

        String classContent="/** */\n " +
                "public  "+className+"(){" +
                "}";
        PsiClass subClass=    mFactory.createClass(className.trim())   ;
        subClass.setName(className.trim());

        Set<String> set = json.keySet();
        List<String>  list = new ArrayList<String>(set);
        createField(json,list, subClass);
        createSetMethod(json,list,subClass);
        createGetMethod(json,list,subClass);
        mClass.add(subClass);

    }
    private String createClassSubName(String key,PsiClass mClass){




        return key.substring(0,1).toUpperCase()+key.substring(1)+"Entity";
    }

    private void createSetMethod(JSONObject json, List<String> list,PsiClass mClass){


        for(int i=0;i<list.size();i++){
            String key = list.get(i) ;
            String type=json.get(key).getClass().getSimpleName();
            String typeStr  ;
            if(type.equals("Boolean")){
                typeStr=" boolean ";
            }else if(type.equals("Integer"))   {
                typeStr=" int ";
            }else if(type.equals("Double"))   {
                typeStr=" double ";
            }    else if(type.equals("JSONObject")){
                typeStr=" "+createClassSubName(key,mClass)+" ";     ;
            }    else if(type.equals("JSONArray")){
                typeStr=" List<"+createClassSubName(key,mClass)+"> " ;
            }    else{
                typeStr=" String "      ;
            }




                String method=  "public void  set"+captureName(key) + "( "+typeStr+" " +key+") {   this."+key+" = "+key+";} "     ;

                mClass.add(mFactory.createMethodFromText( method, mClass));







        }

    }

    public  String captureName(String name) {
             name = name.substring(0, 1).toUpperCase() + name.substring(1);

             return  name;
//        char[] cs=name.toCharArray();
//        cs[0]-=32;
//        return String.valueOf(cs);

    }
    private void createGetMethod(JSONObject json, List<String> list,PsiClass mClass){



        for(int i=0;i<list.size();i++){
            String key = list.get(i) ;
            String type=json.get(key).getClass().getSimpleName();
            String typeStr  ;
            if(type.equals("Boolean")){
                typeStr=" boolean ";
            }else if(type.equals("Integer"))   {
                typeStr=" int ";
            }else if(type.equals("Double"))   {
                typeStr=" double ";
            }     else if(type.equals("JSONObject")){
                typeStr=" "+createClassSubName(key,mClass)+" ";    ;
            }    else if(type.equals("JSONArray")){
                typeStr=" List<"+createClassSubName(key,mClass)+"> " ;
            } else{
                typeStr=" String "      ;
            }


            if(type.equals("Boolean")){



                String method=  "public " +typeStr+ "   is"+captureName(key) +"() {   return "+key+" ;} "   ;
                mClass.add(mFactory.createMethodFromText( method, mClass));

            } else{
                String method=  "public " +typeStr+ "   get"+captureName(key) +"() {   return "+key+" ;} "   ;
                mClass.add(mFactory.createMethodFromText( method, mClass));

            }



        }

    }
}
