<<<<<<< HEAD
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecommendation;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Ashwini
 */
public class Similarity {
    
    static WordNetDatabase database = WordNetDatabase.getFileInstance();
    HashMap<String, List<String>> Synonym = new HashMap<>();
    HashMap<String, List<String>> Hyponym = new HashMap<>();
    HashMap<String, List<String>> Hypernym = new HashMap<>();   
    HashMap<String, Double> WordVecMap;
            
    public HashMap<String, List<String>> GetSynonym()
    {
        return Synonym;
    }

    public  void setSynonym(HashMap<String, List<String>> S)
    {
       Synonym = S;
    }
 
    public  List<String> getSynonym(String T)
    {
       return Synonym.get(T);
    }
    
    public List<String> getSynonyms(String T)
    {
        Synset[] synsets = null;

        synsets = database.getSynsets(T);

        if (Synonym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Synonym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Synonym.get(T));
        }

        List<String> L = Synonym.get(T);

        for (int i = 0; i < synsets.length; i++)
        { 
            String[] wordForms = synsets[i].getWordForms();
            for(String synonym : wordForms)
            {
                if(synonym.split(" ").length == 1)
                {
                    if(!L.contains(synonym.toLowerCase()))
                    {
                        L.add(synonym.toLowerCase());
                    }
                }
            }
        } 
        
        return new ArrayList<>(L);
    }
    
    public List<String> getHyponym(String T)            
    {
        Synset[] synsets = null;
        
        synsets = database.getSynsets(T, SynsetType.NOUN);

        if (Hyponym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Hyponym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Hyponym.get(T));
        }

        List<String> L = Hyponym.get(T);

        for(Synset syn : synsets){
                NounSynset synn = (NounSynset) syn;
                Synset[] hypernSynsets = synn.getHyponyms();
                for(Synset hyper : hypernSynsets){
                        String[] forms = hyper.getWordForms();
                        for(int i=0;i<forms.length;i++){
                                String[] parts = forms[i].split(" ");
                                if(parts.length==1){
                                        if(!L.contains(forms[i])){
                                                L.add(forms[i]);
                                        }
                                }
                        }
                }
        }
        return new ArrayList<>(L);
    }
    

    
    public List<String> getHypernym(String T)            
    {
        Synset[] synsets = null;
        
        synsets = database.getSynsets(T, SynsetType.NOUN);

        if (Hypernym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Hypernym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Hypernym.get(T));
        }        

        List<String> L = Hypernym.get(T);

        for(Synset syn : synsets)
        {
            NounSynset synn = (NounSynset) syn;
            Synset[] hypernSynsets = synn.getHypernyms();
            for(Synset hyper : hypernSynsets)
            {
                String[] forms = hyper.getWordForms();
                for(int i=0;i<forms.length;i++)
                {
                    String[] parts = forms[i].split(" ");
                    if(parts.length==1)
                    {
                        if(!L.contains(forms[i]))
                        {
                            L.add(forms[i]);
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>(L);
    }
    
    
    
    public void LoadTopKSimilarity(HashMap<String, HashMap<String, Double>> SimilarityMapSet, String Filename)
    {
        try{
            BufferedReader r = new BufferedReader(new FileReader(new File(Filename)));
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
            
        }catch(Exception Ex){
            System.out.println(Ex);
        }
    }    
    
    
    public  void WriteRecUserTags(String OutDir, String name, HashMap<String, Double> WeightedUserTags)
    {

        try
        {
            String FileName = OutDir + File.separator + name;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileName)));
            String Labels = "";

            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                Labels += L + "\n";
            }

            writer.write(Labels);
            writer.close();
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
        
    }   
    
    public  void WriteOrgNRecUserTags(String OutDir, String name, HashMap<String, Double> WeightedUserTags, List<String> UserTags)
    {
        try
        {
            String FileName = OutDir + File.separator + name;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileName)));
            String Labels = "";

            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                Labels += L + "\n";
            }

            for (int i = 0; i < UserTags.size(); i++)
            {
                Labels += UserTags.get(i) + "\n";
            }
            
            writer.write(Labels);
            writer.close();
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
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
=======
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecommendation;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Ashwini
 */
public class Similarity {
    
    static WordNetDatabase database = WordNetDatabase.getFileInstance();
    HashMap<String, List<String>> Synonym = new HashMap<>();
    HashMap<String, List<String>> Hyponym = new HashMap<>();
    HashMap<String, List<String>> Hypernym = new HashMap<>();   
    HashMap<String, Double> WordVecMap;
            
    public HashMap<String, List<String>> GetSynonym()
    {
        return Synonym;
    }

    public  void setSynonym(HashMap<String, List<String>> S)
    {
       Synonym = S;
    }
 
    public  List<String> getSynonym(String T)
    {
       return Synonym.get(T);
    }
    
    public List<String> getSynonyms(String T)
    {
        Synset[] synsets = null;

        synsets = database.getSynsets(T);

        if (Synonym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Synonym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Synonym.get(T));
        }

        List<String> L = Synonym.get(T);

        for (int i = 0; i < synsets.length; i++)
        { 
            String[] wordForms = synsets[i].getWordForms();
            for(String synonym : wordForms)
            {
                if(synonym.split(" ").length == 1)
                {
                    if(!L.contains(synonym.toLowerCase()))
                    {
                        L.add(synonym.toLowerCase());
                    }
                }
            }
        } 
        
        return new ArrayList<>(L);
    }
    
    public List<String> getHyponym(String T)            
    {
        Synset[] synsets = null;
        
        synsets = database.getSynsets(T, SynsetType.NOUN);

        if (Hyponym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Hyponym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Hyponym.get(T));
        }

        List<String> L = Hyponym.get(T);

        for(Synset syn : synsets){
                NounSynset synn = (NounSynset) syn;
                Synset[] hypernSynsets = synn.getHyponyms();
                for(Synset hyper : hypernSynsets){
                        String[] forms = hyper.getWordForms();
                        for(int i=0;i<forms.length;i++){
                                String[] parts = forms[i].split(" ");
                                if(parts.length==1){
                                        if(!L.contains(forms[i])){
                                                L.add(forms[i]);
                                        }
                                }
                        }
                }
        }
        return new ArrayList<>(L);
    }
    

    
    public List<String> getHypernym(String T)            
    {
        Synset[] synsets = null;
        
        synsets = database.getSynsets(T, SynsetType.NOUN);

        if (Hypernym.get(T) == null)
        {
            List<String> L = new ArrayList<>();
            Hypernym.put(T, L);
        }
        else
        {
            return new ArrayList<>(Hypernym.get(T));
        }        

        List<String> L = Hypernym.get(T);

        for(Synset syn : synsets)
        {
            NounSynset synn = (NounSynset) syn;
            Synset[] hypernSynsets = synn.getHypernyms();
            for(Synset hyper : hypernSynsets)
            {
                String[] forms = hyper.getWordForms();
                for(int i=0;i<forms.length;i++)
                {
                    String[] parts = forms[i].split(" ");
                    if(parts.length==1)
                    {
                        if(!L.contains(forms[i]))
                        {
                            L.add(forms[i]);
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>(L);
    }
    
    
    
    public void LoadTopKSimilarity(HashMap<String, HashMap<String, Double>> SimilarityMapSet, String Filename)
    {
        try{
            BufferedReader r = new BufferedReader(new FileReader(new File(Filename)));
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
            
        }catch(Exception Ex){
            System.out.println(Ex);
        }
    }    
    
    
    public  void WriteRecUserTags(String OutDir, String name, HashMap<String, Double> WeightedUserTags)
    {

        try
        {
            String FileName = OutDir + File.separator + name;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileName)));
            String Labels = "";

            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                Labels += L + "\n";
            }

            writer.write(Labels);
            writer.close();
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
        
    }   
    
    public  void WriteOrgNRecUserTags(String OutDir, String name, HashMap<String, Double> WeightedUserTags, List<String> UserTags)
    {
        try
        {
            String FileName = OutDir + File.separator + name;
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FileName)));
            String Labels = "";

            for (Map.Entry E : WeightedUserTags.entrySet())
            {
                String L = (String) E.getKey();            
                Labels += L + "\n";
            }

            for (int i = 0; i < UserTags.size(); i++)
            {
                Labels += UserTags.get(i) + "\n";
            }
            
            writer.write(Labels);
            writer.close();
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
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
>>>>>>> a657059ee7fd07dc4acb1b811f6f1ab30657d0f1
