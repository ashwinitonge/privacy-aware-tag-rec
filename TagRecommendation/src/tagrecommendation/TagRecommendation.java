/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author Ashwini
 */
public class TagRecommendation {

    static List<String> TrainIdList = new ArrayList<String>();
    static List<String> TestIdList = new ArrayList<>();
    static HashMap<String, String> PrivacySettings = new HashMap<>();
    static List<HashMap<String, Double>> PrivacyWeight;

	static class Config {
            public static String WordNetDir;
            public static int ISC;
            public static int TSC;
            public static int TRC;
            public static int[] kValues;
            public static int[] tValues;
            public static int   c;
            public static int   st;
            public static String   r;
            public static int   s;
            public static String name;
            public static String TargetFolder;
            public static String DBFolder;
            public static String SFilename;
            public static int[][] skipPairs;
            

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
                                else if(parts[0].equalsIgnoreCase("TRC"))
                                {
                                        TRC = Integer.parseInt(values[0]);
                                }
                                else if(parts[0].equalsIgnoreCase("K"))
                                {
                                        kValues = new int[values.length];
                                        for(int i = 0; i < values.length; i++)
                                        {
                                                kValues[i] = Integer.parseInt(values[i]);
                                        }
                                }
                                else if(parts[0].equalsIgnoreCase("T"))
                                {
                                        tValues = new int[values.length];

                                        for(int i = 0; i< values.length; i++)
                                        {
                                                tValues[i]= Integer.parseInt(values[i]);
                                        }
                                }
                                else if(parts[0].equalsIgnoreCase("C"))
                                {
                                        c = Integer.parseInt(values[0]); 						
                                }
                                else if(parts[0].equalsIgnoreCase("save"))
                                {
                                        st = Integer.parseInt(values[0]); 						
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
                                else if(parts[0].equalsIgnoreCase("sfname"))
                                {
                                        SFilename = values[0]; 						
                                }                                  
                                else if(parts[0].equalsIgnoreCase("skip"))
                                {
                                        skipPairs = new int[values.length][2];
                                        for(int i = 0; i < values.length; i++)
                                        {
                                                String[] t = values[i].trim().split("-");
                                                skipPairs[i][0] = Integer.parseInt(t[0]);
                                                skipPairs[i][1] = Integer.parseInt(t[1]);
                                        }
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
            String TagRec = DB2 + File.separator + "Experiments" + File.separator + "TagRec";
            
            String TrainUserTagVisibleFolder = DB1 + File.separator + "Tags" + File.separator + "All-VisibleUT";
            String TrainUserTagHiddenFolder = "";
            String TestUserTagVisibleFolder = "";
            String TestUserTagHiddenFolder = DB2 + File.separator + "Tags" + File.separator + "All-HiddenUT";
        
        
            LoadIds(TrainId, TrainIdList);
            LoadIds(TestId, TestIdList);

            LoadPrivacySetting(PrivacyTrain, PrivacySettings);
            LoadPrivacySetting(PrivacyTest, PrivacySettings);
        
            int Kvalues[] = Config.kValues;
            int Tvalues[] = Config.tValues;
         
            // 1: Image View
            // 2: Tag View
            // 3: Visible tag exp
            // 4: Image+Tag view
            int ViewFlag = Config.c; 
            //int ViewFlag = 3; 

            BagOfWords TrainTags = new BagOfWords();
            BagOfWords TestTags = new BagOfWords();
            
            PrivacyWeight = new ArrayList<>();
            
            HashMap<String, Double> Wt1 = new HashMap<>();
            if (Config.TRC == 4)
            LoadPrivacyWeights(TestImFeatures, DB2 + File.separator + Config.SFilename, Wt1);
            
            if (Config.TRC == 5 || Config.TRC == 6 || Config.TRC == 8)
                LoadTagPrivacyWeights(DB2 + File.separator + Config.SFilename, Wt1);
            PrivacyWeight.add(Wt1);
            
            HashMap<String, Double> Wt2 = new HashMap<>();
            if (Config.TRC == 8)
                LoadTagPrivacyWeights(DB2 + File.separator + "privacyprob.csv", Wt2);
            
            PrivacyWeight.add(Wt2);
            
            String OutFile = "";
            switch(ViewFlag)
            {
                case 1: 
                    OutFile = TagRec + File.separator + Config.name + "_ImTagRec";
                    break;
                case 2:
                    OutFile = TagRec + File.separator + Config.name + "_TagTagRec";
                    break;
            }
        
        //BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutFile)));
        Similarity S = new Similarity();
        HashMap<String, HashMap<String, Double>> SimilarityMapSet = new HashMap<>();

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
        
        if (Config.r.compareToIgnoreCase("all") != 0)
        {
            String TestVisibleTag = DB2 + File.separator + "Tags" + File.separator + Config.r + "-s" + Config.s + "-VisibleUT";
            TestTags.GetUserTagsVisible().clear();
            TestTags.LoadTags(TestVisibleTag, TestTags.GetUserTagsVisible());
        }                   
        

        //Deep Tags
        HashMap<String, String> TrainDT = TrainTags.GetDeepTags();
        TrainTags.LoadTags(TrainDeepTagFolder, TrainDT);

        HashMap<String, String> TestDT = TestTags.GetDeepTags();                      
        TestTags.LoadTags(TestDeepTagFolder, TestDT);
        
        for (int K = 0; K < Kvalues.length; K++)
        {
            SimilarityMapSet.clear();
            
            switch(ViewFlag)
            {
                case 1: 
                    S.LoadTopKSimilarity(SimilarityMapSet, TagRec + File.separator + Config.name  + "_ImSimilarity_" + Config.ISC + "_" + Kvalues[K] + ".csv");
                    break;
                case 2:
                    S.LoadTopKSimilarity(SimilarityMapSet, TagRec + File.separator + Config.name + "_TagSimilarity_" + Config.TSC + "_" + Config.r + "_" + Config.s + "_" + Kvalues[K] + ".csv");
                    break;                    
            }            

            for (int T = 0; T < Tvalues.length; T++)
            {
                boolean isContinue = false;
                for (int i = 0; i < Config.skipPairs.length; i++)
                {
                    if (Config.skipPairs[i][0] == Kvalues[K] && Config.skipPairs[i][1] == Tvalues[T])
                    {
                        isContinue = true;
                        break;
                    }
                }
                
                if(isContinue)
                continue;                
                   
                System.out.println("K:" + Kvalues[K] + "T:" + Tvalues[T]);
                //writer.write(Kvalues[K] + "," + Tvalues[T]);

                switch(ViewFlag)
                {
                    case 1:
                        GenImageViewTagRec(TrainIdList, TestIdList, PrivacySettings, OutFile, Kvalues[K], Tvalues[T], TrainTags, TestTags, SimilarityMapSet, S, Config.TRC, Config.r, Config.s);
                        break;
                    case 2:
                        GenTagViewTagRec(TrainIdList, TestIdList, PrivacySettings, OutFile, Config.r, Config.s, Kvalues[K], Tvalues[T], TrainTags, TestTags, SimilarityMapSet, Config.TRC, S, Config.st);
                        break;
                }
                
            }
            
        }
        
        //writer.close();
        
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
    
    static void LoadTagPrivacyWeights(String wtFile, HashMap<String, Double> PrivacySettings)
    {
        try
        {
            BufferedReader Wtreader = new BufferedReader(new FileReader(new File(wtFile)));
            String wtLine;
            
            while ((wtLine = Wtreader.readLine()) != null)
            {
                String Split[] = wtLine.split(",");
                String tag = Split[0];
                String weight = Split[1];
                               
                PrivacySettings.put(tag, Double.parseDouble(weight));                
                
            }
            
            Wtreader.close();
            
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }    

    static void GenImageViewTagRec(List<String> TrainIdList, List<String> TestIdList, HashMap<String, String> PrivacySettings, String OutDir, int K, int T 
            ,BagOfWords TrainTags, BagOfWords TestTags, HashMap<String, HashMap<String, Double>> SimilarityMapSet, Similarity S, int TRC, String r, int s) throws IOException
    {        
        HashMap<String, String> TrainUserTagsVisible = TrainTags.GetUserTagsVisible();
        HashMap<String, String> TestUserTagsVisbile = TestTags.GetUserTagsVisible();
        
        HashMap<String, Double> WeightedUserTags = new LinkedHashMap<>();

        //Similarity S = new Similarity();
        Set<String> Vocabulary = new HashSet<>();
        Iterator<String> itr = TrainUserTagsVisible.values().iterator();
        while(itr.hasNext()){
            String Tags = itr.next();
            String SplitTags[] = Tags.split(",");
            for (int i = 0; i < SplitTags.length; i++){
                Vocabulary.add(SplitTags[i]);
            }
        }        
             
        int Imcnt = 0;
      
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutDir + "-" + "K" + K + "-T" + T + "-r" + r + "-s" + s + "_" + TRC + ".csv")));        
        
        for (int i = 0; i < TestIdList.size(); i++)
        {
            WeightedUserTags.clear();

            String name = TestIdList.get(i);
            Imcnt++;
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }
            
            HashMap<String, Double> SimilarityMap;
            SimilarityMap = SimilarityMapSet.get(name);
            
            //Recommend Tags based on top k doc
            List<String> TestVisibleTags;
            String vTag = "";
            if (TestUserTagsVisbile.containsKey(name))
            {
                vTag = TestUserTagsVisbile.get(name);
                TestVisibleTags = Arrays.asList(vTag.split(","));
            }
            else
            {
                TestVisibleTags = new ArrayList<>();
            }
            
            switch(TRC)
            {
                case 1:
                    TagReco.RecommendTTagsWithSynCheckWeighted(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 2:
                    TagReco.RecommendTTagsWithSynCheckRandom(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 3:
                    TagReco.RecommendTTagsWithSynCheckFrequency(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 4:
                    TagReco.RecommendTTagsWithSynCheckPrivacyWeighted(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings, PrivacyWeight.get(0).get(name));
                    break;
                case 5:
                    TagReco.RecommendTTagsWithSynCheckWeightedTagPrivacyProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings.get(name), PrivacyWeight.get(0));
                    break;
                case 6:
                    TagReco.RecommendTTagsWithSynCheckFrequencyTagPrivacyProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings.get(name), PrivacyWeight.get(0));
                    break;
                case 7:
                    TagReco.RecommendTTagsRandom(T, Vocabulary, TestIdList, WeightedUserTags);
                    break;
                case 8:
                    TagReco.RecommendTTagsWithSynCheckWtProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacyWeight.get(1).get(name), PrivacyWeight.get(0));                    
            }
            
            writer.write(name);
             
            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                writer.write("," + L);
            }
             
            writer.write("\n");
        }
        
        writer.close();
    }

    
    static void GenTagViewTagRec(List<String> TrainIdList, List<String> TestIdList, HashMap<String, String> PrivacySettings, String OutDir, String rand, int s, int K, int T, 
            BagOfWords TrainTags, BagOfWords TestTags, HashMap<String, HashMap<String, Double>> SimilarityMapSet, int TRC, Similarity S, int SaveRecTags) throws IOException
    {
        HashMap<String, String> TrainUserTagsVisible = TrainTags.GetUserTagsVisible();
        HashMap<String, String> TestUserTagsVisible = TestTags.GetUserTagsVisible();        
        HashMap<String, String> TestUserTagsHidden = TestTags.GetUserTagsHidden();
        
        Set<String> Vocabulary = new HashSet<>();
        Iterator<String> itr = TrainUserTagsVisible.values().iterator();
        while(itr.hasNext()){
            String Tags = itr.next();
            String SplitTags[] = Tags.split(",");
            for (int i = 0; i < SplitTags.length; i++){
                Vocabulary.add(SplitTags[i]);
            }
        }
        
        
        //Similarity S = new Similarity(); 
        
        HashMap<String, Double> WeightedUserTags = new LinkedHashMap<>();
               
        int Imcnt = 0;
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutDir + "-" + "K" + K + "-T" + T + "-r" + rand + "-s" + s + "_" + TRC + ".csv")));
        
        //Get Weighted User Tags and Calculate Performance      
        for (int i = 0; i < TestIdList.size(); i++)
        {
            WeightedUserTags.clear();

            String name = TestIdList.get(i);     
            //name = "3800193198";
            Imcnt++;
            
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }
            
            String d1 = "";
            if (TestUserTagsVisible.containsKey(name))
            {
                d1= TestUserTagsVisible.get(name);
            }

            if (d1 == null)
            {
                System.out.println(name);
                continue;
            }

            long t1 = System.currentTimeMillis();
            HashMap<String, Double> SimilarityMap;
            SimilarityMap = SimilarityMapSet.get(name);
            
            long t2 = System.currentTimeMillis();

            
            //HashMap<String, Double> SimilarityMap = S.GetTopKDocTagSimilarity(K, TrainUserTagsVisible, TrainDT, TrainDTDocLengthIDF, TrainDTDocF, d1, dt1, name, 2);
            
            //Recommend Tags based on top k doc
            List<String> TestVisibleTags;
            String vTag = "";
            if (TestUserTagsVisible.containsKey(name))
            {
                vTag = TestUserTagsVisible.get(name);
                TestVisibleTags = Arrays.asList(vTag.split(","));
            }
            else
            {
                TestVisibleTags = new ArrayList<>();
            }
            
            
            long t3 = System.currentTimeMillis();
            
            switch(TRC)
            {
                case 1:
                    TagReco.RecommendTTagsWithSynCheckWeighted(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 2:
                    TagReco.RecommendTTagsWithSynCheckRandom(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 3:
                    TagReco.RecommendTTagsWithSynCheckFrequency(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S);
                    break;
                case 4:
                    TagReco.RecommendTTagsWithSynCheckPrivacyWeighted(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings, PrivacyWeight.get(0).get(name));
                    break;
                case 5:
                    TagReco.RecommendTTagsWithSynCheckWeightedTagPrivacyProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings.get(name), PrivacyWeight.get(0));
                    break;
                case 6:
                    TagReco.RecommendTTagsWithSynCheckFrequencyTagPrivacyProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacySettings.get(name), PrivacyWeight.get(0));
                    break;
                case 7:
                    TagReco.RecommendTTagsRandom(T, Vocabulary, TestIdList, WeightedUserTags);
                    break;
                case 8:
                    TagReco.RecommendTTagsWithSynCheckWtProb(T, TrainUserTagsVisible, TestVisibleTags, SimilarityMap, WeightedUserTags, S, PrivacyWeight.get(1).get(name), PrivacyWeight.get(0));
                
            }
            
            //Save tags            
//            switch(SaveRecTags)
//            {
//                case 0:
//                    break;
//                case 1:
//                    S.WriteRecUserTags(OutDir + File.separator + Keyname + "-" + "K" + K + "-T" + T, name, WeightedUserTags);
//                    break;
//                case 2:
//                    S.WriteOrgNRecUserTags(OutDir + File.separator + Keyname + "-" + "K" + K + "-T" + T, name, WeightedUserTags, TestVisibleTags);
//                    break;
//            }
            
             writer.write(name);
             
            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                writer.write("," + L);
            }
             
            writer.write("\n"); 
        }  
        
        writer.close();
       
    }    

    
}
