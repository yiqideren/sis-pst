/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import com.pff.PSTFile;
import elasticSearch.CreateNode;
import gui.MainForm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pstanalyzer.PSTFileEmail;


/**
 *
 * @author Matej
 */
public class main{
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String fileName="D:"+"\\"+"FAKS"+"\\"+"4_CETVRTA_GODINA"+"\\"+"Sigurnost_informacijskih_sustava"+"\\"+"PST_dat"+"\\"+"goran_fazer@hotmail.com.pst";
        System.out.println(fileName);
        PSTFileEmail fileEmail=new PSTFileEmail();
        try{
            PSTFile pstFile=new PSTFile(fileName);
            fileEmail.proccessFolder(pstFile.getRootFolder());
        }catch (Exception e){
            System.out.println(e.toString());
        }
        CreateNode createNode = CreateNode.getInstance();
        //System.out.println("CLIENT DATA MOTHERFUCKER!!!!!: "+createNode.node.client().toString());
        /*for(Object i : fileEmail.GetJsonArray()){
            JSONObject jsonObject = (JSONObject) i;
            System.out.println(jsonObject.get("senderEmail"));
        }
        System.out.println("VELICINA POLJA: "+fileEmail.GetEmailJsonArray().size());*/
        
        /*for(String i : fileEmail.GetFolderList()){
            System.out.println(i);
        }*/
        
        MainForm form = new MainForm();
        form.setVisible(true);
    }
    
}