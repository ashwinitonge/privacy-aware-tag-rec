/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package featuresimilarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Ashwini
 */
public class FeatureSimilarity {

    /**
     * @param args the command line arguments
     */
    static List<String> TrainIdList = new ArrayList<String>();
    static List<String> TestIdList = new ArrayList<>();
    static HashMap<String, String> PrivacySettings = new HashMap<>();
    
    static class Config {
            public static String WordNetDir;
            public static int ISC;
            public static int TSC;
            public static int c;
            public static String r;
            public static int s;
            public static String name;
            public static String TargetFolder;
            public static String DBFolder;
            public static int IsWordNet;

            public static void getConfig(String configFile)
            {
                    Scanner sc = null;
                    String line = null;
                    String[] parts = null;
                    try
                    {
                            sc = new Scanner(new File(configFile));

                            while(sc.hasNextLine())
                            {
                                    String[] values = null;
                                    String temp = null;

                                    line = sc.nextLine();
                                    parts = line.split(":");
                                    
                                    //System.out.println(line);

                                    temp = parts[1];
                                    temp = temp.replaceAll("\\s+", "");
                                    values = temp.split(",");

                                    if(parts[0].equalsIgnoreCase("W"))
                                    {
                                            WordNetDir = values[0];
                                    }
                                    else if(parts[0].equalsIgnoreCase("ISC"))
                                    {
                                            ISC = Integer.parseInt(values[0]);
                                    } 
                                    else if(parts[0].equalsIgnoreCase("TSC"))
                                    {
                                            TSC = Integer.parseInt(values[0]);
                                    }                                        
                                    else if(parts[0].equalsIgnoreCase("C"))
                                    {
                                            c = Integer.parseInt(values[0]); 						
                                    }
                                    else if(parts[0].equalsIgnoreCase("r"))
                                    {
                                            r = values[0]; 						
                                    }
                                    else if(parts[0].equalsIgnoreCase("s"))
                                    {
                                            s = Integer.parseInt(values[0]); 						
                                    } 
                                    else if(parts[0].equalsIgnoreCase("name"))
                                    {
                                            name = values[0]; 						
                                    } 
                                    else if(parts[0].equalsIgnoreCase("tname"))
                                    {
                                            TargetFolder = values[0]; 						
                                    }                                   
                                    else if(parts[0].equalsIgnoreCase("dname"))
                                    {
                                            DBFolder = values[0]; 						
                                    }                                   
                                    else if(parts[0].equalsIgnoreCase("WN"))
                                    {
                                            IsWordNet = Integer.parseInt(values[0]); 						
                                    } 

                            }

                    } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                    finally
                    {
                            if (sc!=null) 
                            {
                                    sc.close();
                            }
                    }
            }
    }    
    
    public static void main(String[] args) {
        try
        {
        
            Config.getConfig(args[0]);
                    
            System.setProperty("wordnet.database.dir", Config.WordNetDir);
            
//            if (w == 1)
//            {
//                System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\3.0\\dict");
//            }
//            else
//            {
//                System.setProperty("wordnet.database.dir", "/data1/akt0092/PiCalertDB/Experiments/TagReco/WordNet-3.0/dict");
//            }
        
            //Locations..
            //String TrainId = ""; //DB1
            
//            if (w == 1)
//            {
//                DB1 = "F:\\RA-ML\\TagReco\\ICDE-17\\dataset-1\\PiCalertDB_8k"; // 8K
//                DB2 = "F:\\RA-ML\\TagReco\\ICDE-17\\dataset-1\\" + Config.Filename; // 2K
//            }
//            else
//            {
//                DB1 = "/data1/akt0092/PiCalertDB/Experiments/TagReco/CollaborativeFiltering/dataset/PiCalertDB_8k"; // 8K
//                DB2 = "/data1/akt0092/PiCalertDB/Experiments/TagReco/CollaborativeFiltering/dataset/PiCalertDB_16k"; // 2K
//            }
            
            String DB1, DB2;
            DB1 = Config.DBFolder;
            DB2 = Config.TargetFolder;
            
       
            String TrainId = DB1 + File.separator + "filenames.txt";
            String TestId = DB2 + File.separator + "filenames.txt";

            String TrainImFeatures = DB1 + File.separator + "pool5.csv"; //Train Pool5 Features
            String TestImFeatures = DB2 + File.separator + "pool5.csv";  //Test Pool5 Features

            String PrivacyTrain = DB1 + File.separator + "privacysetting.csv";
            String PrivacyTest = DB2 + File.separator + "privacysetting.csv";        

            String TrainDeepTagFolder = DB1 + File.separator + "Tags" + File.separator + "All-VisibleDT";      
            String TestDeepTagFolder = DB2 + File.separator + "Tags" + File.separator + "All-VisibleDT";
        
            //String OutDir = "E:\\Projects\\TagReco\\CollaborativeFiltering\\dataset\\PiCalertDB\\train\\DB2\\Experiments\\ImageView\\TagEx-5-10-similarity-pool5";
            String FSimOutDir = DB2 + File.separator + "Experiments" + File.separator + "FeatureSim";
            
            String TrainUserTagVisibleFolder = DB1 + File.separator + "Tags" + File.separator + "All-VisibleUT";
            String TrainUserTagHiddenFolder = "";
            String TestUserTagVisibleFolder = "";
            String TestUserTagHiddenFolder = DB2 + File.separator + "Tags" + File.separator + "All-HiddenUT";
        
            LoadIds(TrainId, TrainIdList);
            LoadIds(TestId, TestIdList);

            LoadPrivacySetting(PrivacyTrain, PrivacySettings);
            LoadPrivacySetting(PrivacyTest, PrivacySettings);
        
            int ViewFlag = Config.c; 

            Pool5Features TrainPool5Features = new Pool5Features();
            Pool5Features TestPool5Features = new Pool5Features();
            BagOfWords TrainTags = new BagOfWords();
            BagOfWords TestTags = new BagOfWords();
          
            String OutFile = "";
            switch(ViewFlag)
            {
                case 1: 
                    OutFile = FSimOutDir + File.separator + Config.name  + "_ImSimilarity_" + Config.ISC + ".csv";
                    break;
                case 2:
                    OutFile = FSimOutDir + File.separator + Config.name + "_TagSimilarity_" + Config.TSC + "_" + Config.r + "_" + Config.s + ".csv";
                    break;                    
            }        

            switch(ViewFlag)
            {
                case 1:
                    //Load Image Features
                    TrainPool5Features.LoadDeepFeatures(TrainImFeatures);
                    TestPool5Features.LoadDeepFeatures(TestImFeatures);
                    
                    SaveImSimilarity(TrainIdList, TestIdList, PrivacySettings, OutFile, TrainPool5Features, TestPool5Features, Config.ISC);
                  
                    break;
                case 2:
                    //Train UserTags 
                    HashMap<String, String> TrainUserTagsVisible = TrainTags.GetUserTagsVisible();
                    TrainTags.LoadTags(TrainUserTagVisibleFolder, TrainUserTagsVisible);

                    // Test UserTags
                    HashMap<String, String> TestUserTagsVisible = TestTags.GetUserTagsVisible();
                    if (TestUserTagVisibleFolder.compareTo("") != 0)
                    {
                        TestTags.LoadTags(TestUserTagVisibleFolder, TestUserTagsVisible);
                    }

                    HashMap<String, String> TestUserTagsHidden = TestTags.GetUserTagsHidden();
                    TestTags.LoadTags(TestUserTagHiddenFolder, TestUserTagsHidden);

                    //Deep Tags
                    HashMap<String, String> TrainDT = TrainTags.GetDeepTags();
                    TrainTags.LoadTags(TrainDeepTagFolder, TrainDT);
                    
                    HashMap<String, String> TestDT = TestTags.GetDeepTags();                      
                    TestTags.LoadTags(TestDeepTagFolder, TestDT);
                    
                    if (Config.r.compareToIgnoreCase("all") == 0)
                    {
                        SaveTagSimilarity(TestIdList, OutFile, Config.r, Config.s, TrainTags, TestTags, Config.TSC, Config.IsWordNet);
                    }
                    else
                    {
                        String TestVisibleTag = DB2 + File.separator + "Tags" + File.separator + Config.r + "-s" + Config.s + "-VisibleUT";
                        TestTags.GetUserTagsVisible().clear();
                        TestTags.LoadTags(TestVisibleTag, TestTags.GetUserTagsVisible());
                        SaveTagSimilarity(TestIdList, OutFile, Config.r, Config.s, TrainTags, TestTags, Config.TSC, Config.IsWordNet);
                    }                   
                    
                    
                    break;
            }
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
        }
        
    }
    
    static void LoadIds(String Filepath, List<String> IdList)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Filepath)));
            String Line = "";
            
            while((Line = reader.readLine()) != null)
            {
                IdList.add(Line.trim());
            }
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }  
    
    static void LoadPrivacySetting(String Filename, HashMap<String, String> PrivacySettings) 
    {
        try
        {
            BufferedReader Reader = new BufferedReader(new FileReader(new File(Filename)));
            String Line = "";

            while ((Line = Reader.readLine()) != null)
            {
                String Split[] = Line.split(",");

                PrivacySettings.put(Split[0], Split[1]);
            }

            Reader.close();            
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }        

    } 
    
    static void LoadPrivacyWeights(String IdList, String wtFile, HashMap<String, Double> PrivacySettings)
    {
        try
        {
            BufferedReader Wtreader = new BufferedReader(new FileReader(new File(wtFile)));
            BufferedReader IdReader = new BufferedReader(new FileReader(new File(IdList)));
            String PLine, wtLine;
            
            IdReader.readLine();
            
            while ((PLine = IdReader.readLine()) != null)
            {
                wtLine = Wtreader.readLine();
                
                String Split[] = wtLine.split("\\s+");
                String weight = Split[0].replace("*", "");
                
                String name = PLine.split(",")[0];
                
                PrivacySettings.put(name, Double.parseDouble(weight));                
                
            }
            
            Wtreader.close();
            
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }
    
    static void SaveImSimilarity(List<String> TrainIdList, List<String> TestIdList, HashMap<String, String> PrivacySettings, String OutDir,  
            Pool5Features TrainPool5Features, Pool5Features TestPool5Features, int SC) throws IOException
    {        
        HashMap<String, String> TrainPool5 = TrainPool5Features.GetImFeatures();
        HashMap<String, String> TestPool5 = TestPool5Features.GetImFeatures();         
        //Similarity S = new Similarity();
     
        int Imcnt = 0;
        
        BufferedWriter Simwriter = null;
        
        Simwriter = new BufferedWriter(new FileWriter(new File(OutDir)));
        
        Similarity S = new Similarity();
        
        for (int i = 0; i < TestIdList.size(); i++)
        {

            String name = TestIdList.get(i);
            Imcnt++;
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }
            
            String d1 = TestPool5.get(name);
            if (d1 == null)
                continue;

            //Similarity Score
            S.SaveDocImSimilarity(Simwriter, TrainPool5, d1, name, SC);
          
            Simwriter.flush();
        }
        Simwriter.close();        
    }
    
    static void SaveTagSimilarity(List<String> TestIdList, String OutDir, String r, int s, BagOfWords TrainTags, BagOfWords TestTags, int SC, int IsWordNet) throws IOException
    {
        
        HashMap<String, String> TrainDT = TrainTags.GetDeepTags();
        HashMap<String, String> TrainUserTagsVisible = TrainTags.GetUserTagsVisible();
        HashMap<String, String> TestUserTagsVisible = TestTags.GetUserTagsVisible();        
        HashMap<String, String> TestDT = TestTags.GetDeepTags();
        
        Similarity S = new Similarity(); 
               
        int Imcnt = 0;
        
        BufferedWriter Simwriter = null;
        
        Simwriter = new BufferedWriter(new FileWriter(new File(OutDir)));
        
        //Get Weighted User Tags and Calculate Performance      
        for (int i = 0; i < TestIdList.size(); i++)
        {

            String name = TestIdList.get(i);            
            //name = "4319604926";
            Imcnt++;
            //System.out.println(name);
            
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }
            
            String d1 = "";
            if (TestUserTagsVisible.containsKey(name))
            {
                d1 = TestUserTagsVisible.get(name);
            }
            
            String dt1 = TestDT.get(name);
            if (d1 == null)
            {
                System.out.println(name);
                continue;
            }

            S.SaveDocTagSimilarity(Simwriter, TrainUserTagsVisible, TrainDT, d1, dt1, name, SC, IsWordNet);
            Simwriter.flush();
            
        }
        
        Simwriter.close();
       
    }    
    
}
