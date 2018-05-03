/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecperf;

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
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Ashwini
 */
public class TagRecPerf {

    static List<String> TrainIdList = new ArrayList<String>();
    static List<String> TestIdList = new ArrayList<>();
    static HashMap<String, String> PrivacySettings = new HashMap<>();
    static HashMap<String, Double> PrivacyWeight;

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
            public static int   perf;
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
                                else if(parts[0].equalsIgnoreCase("PERF"))
                                {
                                        perf = Integer.parseInt(values[0]);
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
            String TagRecPerf = DB2 + File.separator + "Experiments" + File.separator + "TagRecPerf";
            
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
            
            String OutFile = "";
            switch(ViewFlag)
            {
                case 1: 
                    OutFile = TagRecPerf + File.separator + Config.name + "_ImTagRec";
                    break;
                case 2:
                    OutFile = TagRecPerf + File.separator + Config.name + "_TagTagRec";
                    break;
            }
        
        //BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutFile)));
        Similarity S = new Similarity();
        HashMap<String, List<String>> WeightedUserTags = new HashMap<>();

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
            
            String TestHiddenTag = DB2 + File.separator + "Tags" + File.separator + Config.r + "-s" + Config.s + "-HiddenUT";
            TestTags.GetUserTagsHidden().clear();
            TestTags.LoadTags(TestHiddenTag, TestTags.GetUserTagsHidden());            
        } 
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutFile + "-r" + Config.r + "-s" + Config.s + "Perf.csv")));
        
        for (int K = 0; K < Kvalues.length; K++)
        {

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
                writer.write(Kvalues[K] + "," + Tvalues[T]);
                
                WeightedUserTags.clear();
                
                LoadRecTags(OutFile + "-" + "K" + Kvalues[K] + "-T" + Tvalues[T] + "-r" + Config.r + "-s" + Config.s + "_" + Config.TRC + ".csv", WeightedUserTags);
                
                switch(Config.perf)
                {
                    case 1:
                        GenRecTagPerf(TestIdList, writer, TestTags, WeightedUserTags, S);
                        break;
                    case 2:
                        GenRecTagPerfExact(TestIdList, writer, TestTags, WeightedUserTags, S);
                        break;
                }
                writer.write("\n");
            }
            
        }
        
        writer.close();
        
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

    static void GenRecTagPerf(List<String> TestIdList, BufferedWriter writer, BagOfWords TestTags, 
            HashMap<String, List<String>> WeightedUserTags, Similarity S) throws IOException
    {        
        HashMap<String, String> TestUserTagsVisbile = TestTags.GetUserTagsVisible();
        HashMap<String, String> TestUserTagsHidden = TestTags.GetUserTagsHidden();
        
            
        HashMap<String, Double> PrecisionMap = new HashMap<>();
        HashMap<String, Double> RecallMap = new HashMap<>();
        HashMap<String, Double> FmeasureMap = new HashMap<>();  
        
        int Imcnt = 0;
        
        for (int i = 0; i < TestIdList.size(); i++)
        {
            String name = TestIdList.get(i);
            
            Imcnt++;
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }            
            
            //Calculate Performance
            List<String> TestHiddenTags = new ArrayList<>();
            String vTag = "";
            if (TestUserTagsHidden.containsKey(name))
            {
                vTag = TestUserTagsHidden.get(name);
            }

            String vTagSplit[] = vTag.split(",");
            for (int t = 0; t < vTagSplit.length; t++)
            {
                if (vTagSplit[t].length() != 0)
                {
                    TestHiddenTags.add(vTagSplit[t]);
                }
            }

            List<String> RecoTags = WeightedUserTags.get(name);
            
            if (RecoTags == null || RecoTags.size() == 0)
            {
                continue;
            }
            
            double p = TagPerformance.computePrecision(TestHiddenTags, RecoTags, S);
            double r = TagPerformance.computeRecall(TestHiddenTags, RecoTags, S);

            double f = (2 * p * r) / (p + r);

            PrecisionMap.put(name, p);
            RecallMap.put(name, r);
            FmeasureMap.put(name, f);            

        }

        double AvgPrecision = 0.0;
        double AvgRecall = 0.0;
        double AvgFmeasure = 0.0;

        for (String F : PrecisionMap.keySet())
        {
            double p = PrecisionMap.get(F);
            double r = RecallMap.get(F);
            double f = FmeasureMap.get(F);

            AvgPrecision += p;
            AvgRecall += r;
            AvgFmeasure += f;
        }

        AvgPrecision = AvgPrecision / PrecisionMap.size();
        AvgRecall = AvgRecall / RecallMap.size();
        AvgFmeasure = (2 * AvgPrecision * AvgRecall) / (AvgPrecision + AvgRecall);

        System.out.println(AvgPrecision + "," + AvgRecall + "," + AvgFmeasure);
        writer.write("," + AvgPrecision + "," + AvgRecall + "," + AvgFmeasure);
       
    }    

    static void GenRecTagPerfExact(List<String> TestIdList, BufferedWriter writer, BagOfWords TestTags, 
            HashMap<String, List<String>> WeightedUserTags, Similarity S) throws IOException
    {        
        HashMap<String, String> TestUserTagsVisbile = TestTags.GetUserTagsVisible();
        HashMap<String, String> TestUserTagsHidden = TestTags.GetUserTagsHidden();
        
            
        HashMap<String, Double> PrecisionMap = new HashMap<>();
        HashMap<String, Double> RecallMap = new HashMap<>();
        HashMap<String, Double> FmeasureMap = new HashMap<>();  
        
        int Imcnt = 0;
        
        for (int i = 0; i < TestIdList.size(); i++)
        {
            String name = TestIdList.get(i);
            
            Imcnt++;
            if (Imcnt % 100 == 0)
            {
                System.out.println("processed " + Imcnt + "images...");
            }            
            
            //Calculate Performance
            List<String> TestHiddenTags = new ArrayList<>();
            String vTag = "";
            if (TestUserTagsHidden.containsKey(name))
            {
                vTag = TestUserTagsHidden.get(name);
            }

            String vTagSplit[] = vTag.split(",");
            for (int t = 0; t < vTagSplit.length; t++)
            {
                if (vTagSplit[t].length() != 0)
                {
                    TestHiddenTags.add(vTagSplit[t]);
                }
            }

            List<String> RecoTags = WeightedUserTags.get(name);
            
            if (RecoTags == null)
            {
                continue;
            }
            
            double p = TagPerformance.computePrecisionExact(TestHiddenTags, RecoTags, S);
            double r = TagPerformance.computeRecallExact(TestHiddenTags, RecoTags, S);

            double f = (2 * p * r) / (p + r);

            PrecisionMap.put(name, p);
            RecallMap.put(name, r);
            FmeasureMap.put(name, f);            

        }

        double AvgPrecision = 0.0;
        double AvgRecall = 0.0;
        double AvgFmeasure = 0.0;

        for (String F : PrecisionMap.keySet())
        {
            double p = PrecisionMap.get(F);
            double r = RecallMap.get(F);
            double f = FmeasureMap.get(F);

            AvgPrecision += p;
            AvgRecall += r;
            AvgFmeasure += f;
        }

        AvgPrecision = AvgPrecision / PrecisionMap.size();
        AvgRecall = AvgRecall / RecallMap.size();
        AvgFmeasure = (2 * AvgPrecision * AvgRecall) / (AvgPrecision + AvgRecall);

        System.out.println(AvgPrecision + "," + AvgRecall + "," + AvgFmeasure);
        writer.write("," + AvgPrecision + "," + AvgRecall + "," + AvgFmeasure);
       
    } 
    
    static void LoadRecTags(String File, HashMap<String, List<String>> WeightedUserTags)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(File)));
            String Line = "";
            
            try
            {
                while((Line = reader.readLine()) != null)
                {
                    String Splits[] = Line.split(",");
                    
                    List<String> Tags = new ArrayList<String>();
                    for (int i = 1; i < Splits.length; i++)
                    {
                        Tags.add(Splits[i]);
                    }
                    
                    WeightedUserTags.put(Splits[0], Tags);
                }
                
                reader.close();
            }
            catch(Exception Ex)
            {
                System.out.println(Ex);
            }
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }
    
}
