/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package topksimdocfinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 *
 * @author Ashwini
 */
public class TopKSimDocFinder {

    /**
     * @param args the command line arguments
     */

    	static class Config {
            public static String WordNetDir;
            public static double wt1;
            public static double wt2;
            public static double th;
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
                                else if(parts[0].equalsIgnoreCase("wt_sameclass"))
                                {
                                        wt1 = Double.parseDouble(values[0]);
                                } 
                                else if(parts[0].equalsIgnoreCase("wt_otherclass"))
                                {
                                        wt2 = Double.parseDouble(values[0]);
                                }    
                                else if(parts[0].equalsIgnoreCase("SimTH"))
                                {
                                        th = Double.parseDouble(values[0]);
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
            String FilePath = args[1];
            Double weight1 = Config.wt1;
            Double weight2 = Config.wt2;
            Double threshold = Config.th;
            HashMap<String, Double> SimilarityMap = new HashMap<>();
            HashMap<String, Double> SimilarityMapLoad = new HashMap<>();
            String CurrentLine = new String();
            String CurrentFName = new String();
            
            HashMap<String, String> Privacy8k = new HashMap<>();
            HashMap<String, String> Privacy16k = new HashMap<>();
            
            LoadPrivacySetting(Config.DBFolder + File.separator + "privacysetting.csv", Privacy8k);
            LoadPrivacySetting(Config.TargetFolder + File.separator + "privacysetting.csv", Privacy16k);
            
            for (int i = 0; i < Config.kValues.length; i++)
            {
                int k = Config.kValues[i];
                System.out.println("Processing for " + k + " images");
                String OutFile = FilePath.replace(".csv", "_" + k + ".csv");

                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OutFile)));
                BufferedReader reader = new BufferedReader(new FileReader(new File(FilePath)));
                
                int Counter = 0;
                while (true)
                {
                    SimilarityMap.clear();

                    Counter++;

                    if (Counter % 50 == 0)
                    {
                        System.out.println("Processed " + Counter + " images...");
                    }
                    
                    SimilarityMapLoad.clear();
                    
                    String Res = LoadSimFilePart(reader, SimilarityMapLoad);
                    
                    CurrentLine = Res.substring(0, Res.lastIndexOf(","));
                    CurrentFName = Res.substring(Res.lastIndexOf(",") + 1);
                    
                    if (SimilarityMapLoad.size() == 0){
                        break;
                    }
                    
                    MultiplyWeight(SimilarityMapLoad, weight1, weight2, Privacy8k, Privacy16k.get(CurrentFName));
                    
                    GetTopKDocImSimilarity(k, SimilarityMapLoad, SimilarityMap, threshold);
                    SaveTopKSimilarity(SimilarityMap, CurrentFName, writer);
                }

                //System.out.println(Counter);
                writer.close();
                reader.close();
            }
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
        }
        
        
    }    
    
    static void LoadSimFile(String File, HashMap<String, HashMap<String, Double>> SimilarityMapSet)
    {
        try{
            BufferedReader r = new BufferedReader(new FileReader(new File(File)));
            String Line = "";
            while((Line = r.readLine()) != null)
            {
                String Split[] = Line.split(",");
                if (SimilarityMapSet.containsKey(Split[0]))
                {
                    HashMap<String, Double> Sim = SimilarityMapSet.get(Split[0]);
                    Sim.put(Split[1], Double.parseDouble(Split[2]));
                    SimilarityMapSet.put(Split[0], Sim);
                }
                else
                {
                    HashMap<String, Double> Sim = new HashMap<>();
                    Sim.put(Split[1], Double.parseDouble(Split[2]));
                    SimilarityMapSet.put(Split[0], Sim);                    
                }
            }               
            
            r.close();
        }catch(Exception Ex){
            System.out.println(Ex);
        }        
    }

    static String LoadSimFilePart(BufferedReader r, HashMap<String, Double> SimilarityMapSet)
    {

        String CurrentLine = "";
        String CurrentFName = "";        
        try{
            String Line = "";
            String Prev = "";
            int i = 0;
            
            while((Line = r.readLine()) != null)
            {
                String Split[] = Line.split(",");
                
                if (i == 0){
                    Prev = Split[0];
                    String CurrentSplit[] = CurrentLine.split(",");
                    if (CurrentSplit[0].compareTo(Split[0]) == 0){
                        SimilarityMapSet.put(CurrentSplit[1], Double.parseDouble(CurrentSplit[2]));
                    }
                }
                
                if (Split[0].compareTo(Prev) != 0){
                    break;
                }
                
                SimilarityMapSet.put(Split[1], Double.parseDouble(Split[2]));
                i++;
                Prev = Split[0];
            }               
            
            CurrentLine = Line;
            CurrentFName = Prev;            
            
        }catch(Exception Ex){
            //System.out.println(Ex);\
            Ex.printStackTrace();
        }      
        
        return CurrentLine + "," + CurrentFName;
    }    
    
    static void SaveTopKSimilarity(HashMap<String, Double> SimilarityMap, String name, BufferedWriter w)
    {
        try{
            for (String S : SimilarityMap.keySet())
            {
                w.write(name + "," + S + "," + SimilarityMap.get(S) + "\n");
            }
            
            
        }catch(Exception Ex){
            System.out.println(Ex);
        }
    }
    
    static  void GetTopKDocImSimilarity(int k, HashMap<String, Double> SimilarityMap, HashMap<String, Double> DocSimilarity, Double Threshold)
    {

        ValueComparator vc = new ValueComparator(SimilarityMap);
        TreeMap<String, Double> SimilarityTreeMap = new TreeMap<>(vc);   
        
        SimilarityTreeMap.putAll(SimilarityMap);
        DocSimilarity.clear();
        int i = 0;
        for(Map.Entry E : SimilarityTreeMap.entrySet())
        {
            String Name  = (String) E.getKey();
            double Similarity = (double) E.getValue();
            i++;
            
            if (i > k)
                break;
            
            if (Similarity < Threshold)
                break;
            
            DocSimilarity.put(Name, Similarity);
        } 
        
    }
    
    static void LoadPrivacySetting(String Filename, HashMap<String, String> P)
    {
        try
        {
            BufferedReader Reader = new BufferedReader(new FileReader(new File(Filename)));
            String Line = "";

            while ((Line = Reader.readLine()) != null)
            {
                String Split[] = Line.split(",");

                P.put(Split[0], Split[1]);
            }

            Reader.close();        
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }    
    
    static void MultiplyWeight(HashMap<String, Double> SimilarityMap, double weight1, double weight2, HashMap<String, String> Privacy8k, String TestPrivacy){
        
        for (String S : SimilarityMap.keySet()){
            String TrainPrivacy = Privacy8k.get(S);
            Double sim = SimilarityMap.get(S);
            
            if (TrainPrivacy.compareTo(TestPrivacy) == 0){
                sim = sim * weight1;
            }
            else{
                sim = sim * weight2;
            }
            
            SimilarityMap.put(S, sim);            
        }
        
    }
    
    
    
}

class ValueComparator implements Comparator<String>
{
    Map<String,Double> map;
    
    public ValueComparator(Map<String,Double> map)
    {
        this.map = map;
    }
    
    @Override
    public int compare(String key1, String key2) {
        if(map.get(key1) >= map.get(key2))
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
    
}
