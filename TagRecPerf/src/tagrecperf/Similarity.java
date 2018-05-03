/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecperf;

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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ashwini
 */

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
    
    public List<String> getWordNet(String T)
    {
        List<String> L = new ArrayList<>();
        
        List<String> W;
        
        W = getSynonym(T);
        if (W != null)
        {
            L.addAll(W);
        }

        W = getHypernym(T);
        if (W != null)
        {
            L.addAll(W);
        }        
        
        W = getHyponym(T);
        if (W != null)
        {
            L.addAll(W);
        }
        
        return L;
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
    
}
