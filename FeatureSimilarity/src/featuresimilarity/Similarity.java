/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package featuresimilarity;

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
    
    public void SetWordVecMap(HashMap<String, Double> WVecMap)
    {
        WordVecMap = WVecMap;
    }

    public  double ComputeImageSimilarity(String Im1, String Im2)
    {
        double similarity = 0.0;
        
        String Im1Split[] = Im1.split(",");
        String Im2Split[] = Im2.split(",");
       
        double ProductSum = 0.0;
        double SquareSum1 = 0.0;
        double SquareSum2 = 0.0;
        
        for (int i = 1; i < Im1Split.length - 1; i++)
        {
            double d1 = Double.parseDouble(Im1Split[i]);
            double d2 = Double.parseDouble(Im2Split[i]);
            
            ProductSum += d1 * d2;
            SquareSum1 += d1 * d1;
            SquareSum2 += d2 * d2;            
        }
        
        similarity = ProductSum / (Math.sqrt(SquareSum1) * Math.sqrt(SquareSum2));       
        
        return similarity;
    }  

//    public  double ComputeWordVecSimilarity(String Tag1, String Tag2)
//    {
//        double similarity = 0.0;
//        
//        String Tag1Split[] = Tag1.split(",");
//        String Tag2Split[] = Tag2.split(",");
//       
//        double ProductSum = 0.0;
//        double SquareSum1 = 0.0;
//        double SquareSum2 = 0.0;
//        
//        for (int i = 1; i < Tag1Split.length - 1; i++)
//        {
//            String w1 = Tag1Split[]
//            
//            double d1 = Double.parseDouble(Im1Split[i]);
//            double d2 = Double.parseDouble(Im2Split[i]);
//            
//            ProductSum += d1 * d2;
//            SquareSum1 += d1 * d1;
//            SquareSum2 += d2 * d2;            
//        }
//        
//        similarity = ProductSum / (Math.sqrt(SquareSum1) * Math.sqrt(SquareSum2));       
//        
//        return similarity;
//    }
    
    public  double ComputeTagTfExactMatchSimilarity(String Tag1, String Tag2)
    {
        double similarity = 0.0;
        
        String Tag1Split[] = Tag1.split(",");
        String Tag2Split[] = Tag2.split(",");
       
        List<String> CommonTags = new ArrayList<String>();
        List<String> Tag2List = Arrays.asList(Tag2Split);
        List<String> Tag1List = Arrays.asList(Tag1Split);
        
        for (int i = 0; i < Tag1Split.length; i++)
        {
            if (Tag2List.contains(Tag1Split[i]))
            {
                CommonTags.add(Tag1Split[i]);
            }
        }
        
        double ProductSum = 0.0;
        
        for (int i = 0; i < CommonTags.size(); i++)
        {
            ProductSum += 1;           
        }
        
        //similarity = ProductSum / (Math.sqrt(DocLengthTf.get(FN1)) * Math.sqrt(DocLengthTf.get(FN2)));       
        similarity = ProductSum / (Math.sqrt(Tag1List.size()) * Math.sqrt(Tag2List.size()));       
        
        return similarity;
    }    
    
    public double ComputeTagTfVocabMatchSimilarity(String Tag1, String Tag2, int Syn)
    {
        double similarity = 0.0;
        
        String Tag1Split[] = Tag1.split(",");
        String Tag2Split[] = Tag2.split(",");
       
        List<String> Tag2List = Arrays.asList(Tag2Split);
        List<String> Tag1List = Arrays.asList(Tag1Split);
        
        long t2 = System.currentTimeMillis();
        //List<String> vocabulary = GetTagListVocab(Tag1List, Tag2List);
        long t3 = System.currentTimeMillis();
        
        
        List<String> vocabulary = null;
        
        switch(Syn)
        {
            case 1: 
                vocabulary = GetTagListVocabSyn2(Tag1List, Tag2List);
                break;
            case 2:
                vocabulary = GetTagListVocabWNet(Tag1List, Tag2List);
                break;
            case 3:
                vocabulary = GetTagListVocab(Tag1List, Tag2List);                
            default:
                break;
        }

        long t4 = System.currentTimeMillis();
        
        List<Integer> targetEntries = computeEntriesInVocab(vocabulary, Tag2List);
        List<Integer> otherEntries = computeEntriesInVocab(vocabulary, Tag1List);
        
        long t5 = System.currentTimeMillis();
        
        double lengthTarget = 0;
        double lengthOther = 0;
        double dotProduct = 0;
//        for(int value : targetEntries){
//                lengthTarget+=value*value;
//        }
        lengthTarget = Collections.frequency(targetEntries, 1);
        lengthTarget = Math.sqrt(lengthTarget);

//        for(int value : otherEntries){
//                lengthOther+=value*value;
//        }
        lengthOther = Collections.frequency(otherEntries, 1);
        lengthOther = Math.sqrt(lengthOther);
        for(int i=0;i<targetEntries.size();i++){
                dotProduct += targetEntries.get(i) * otherEntries.get(i);
        }
        similarity = (double)dotProduct/(lengthOther*lengthTarget);	
        
        long t6 = System.currentTimeMillis();
        
        long t2diff = t3 - t2;
        long t3diff = t4 - t3;
        long t4diff = t5 - t4;
        long t5diff = t6 - t5;
        
        return similarity;
    }

    public double ComputeTagTfExactNVocabMatchSimilarity(String Tag1, String Tag2, String dt1, String dt2, int Syn)
    {
        double similarity = 0.0;

        long t1 = System.currentTimeMillis();
        
        String Tag1Split[] = Tag1.split(",");
        String Tag2Split[] = Tag2.split(",");
       
        //List<String> CommonTags = new ArrayList<String>();
        List<String> Tag2List = Arrays.asList(Tag2Split);//new ArrayList<>();//Arrays.asList(Tag2Split);
        List<String> Tag1List = Arrays.asList(Tag1Split);//new ArrayList<>();//Arrays.asList(Tag1Split);
        
//        for (int i = 0; i < Tag2Split.length; i++)
//        {
//            if (Tag2Split[i].length() > 0)
//            {
//                Tag2List.add(Tag2Split[i]);
//            }
//            else
//            {
//                int test = 0;
//            }
//        }
        
//        for (int i = 0; i < Tag1Split.length; i++)
//        {
//            if (Tag1Split[i].length() > 0)
//            {
//                Tag1List.add(Tag2Split[i]);
//            }
//            else
//            {
//                int test = 0;
//            }            
//        }        
        
        int test = 0;
        long t2 = System.currentTimeMillis();
                
        List<String> vocabulary = null;
        
        switch(Syn)
        {
            case 1: 
                vocabulary = GetTagListVocabSyn2(Tag1List, Tag2List);
                break;
            case 2:
                vocabulary = GetTagListVocabWNet(Tag1List, Tag2List);
                break;
            default:
                break;
        }
        
        
        List<Integer> targetEntries = computeEntriesInVocab(vocabulary, Tag2List);
        List<Integer> otherEntries = computeEntriesInVocab(vocabulary, Tag1List);
        
        long t3 = System.currentTimeMillis();
        
        double lengthTarget = 0;
        double lengthOther = 0;
        double dotProduct = 0;
//        for(int value : targetEntries){
//                lengthTarget+=value*value;
//        }
        //lengthTarget = Math.sqrt(lengthTarget);
        lengthTarget = Collections.frequency(targetEntries, 1);

//        for(int value : otherEntries){
//                lengthOther+=value*value;
//        }
        
        lengthOther = Collections.frequency(otherEntries, 1);
        
        //lengthOther = Math.sqrt(lengthOther);
        for(int i=0;i<targetEntries.size();i++){
                dotProduct += targetEntries.get(i) * otherEntries.get(i);
        }
        

        
        String dt1Split[] = dt1.split(",");
        String dt2Split[] = dt2.split(",");       
        
        List<String> dt2List = Arrays.asList(dt2Split);
        List<String> dt1List = Arrays.asList(dt1Split);
        List<String> CommonTags = new ArrayList<String>(dt1List);
        
//        for (int i = 0; i < dt1Split.length; i++)
//        {
//            if (dt2List.contains(dt1Split[i]))
//            {
//                CommonTags.add(dt1Split[i]);
//            }
//        }
        
        CommonTags.retainAll(dt2List);
        
        double ProductSum = 0.0;
        
//        for (int i = 0; i < CommonTags.size(); i++)
//        {
//            ProductSum += 1;           
//        }
        
        ProductSum = CommonTags.size();
        
        //similarity = ProductSum / (Math.sqrt(DocLengthTf.get(FN1)) * Math.sqrt(DocLengthTf.get(FN2)));       
        
        
        //similarity = (double)(dotProduct + ProductSum) /((Math.sqrt(lengthOther + DocLengthTf.get(FN1))) * Math.sqrt(lengthTarget + DocLengthTf.get(FN2)));	
        similarity = (double)(dotProduct + ProductSum) /((Math.sqrt(lengthOther + dt1List.size())) * Math.sqrt(lengthTarget + dt2List.size()));	
        
        long t4 = System.currentTimeMillis();;
        
        long t1diff = t2 - t1;
        long t2diff = t3 - t2;
        long t3diff = t4 - t3;
        
        return similarity;
    }

    public double ComputeTagTfVocabVocabMatchSimilarity(String Tag1, String Tag2, String dt1, String dt2, int Syn)
    {
        double similarity = 0.0;

        long t1 = System.currentTimeMillis();
        
        String CTag1 = Tag1 + "," + dt1;
        String CTag2 = Tag2 + "," + dt2;
        
        String Tag1Split[] = CTag1.split(",");
        String Tag2Split[] = CTag2.split(",");
       
        //List<String> CommonTags = new ArrayList<String>();
        List<String> Tag2List = Arrays.asList(Tag2Split);//new ArrayList<>();//Arrays.asList(Tag2Split);
        List<String> Tag1List = Arrays.asList(Tag1Split);//new ArrayList<>();//Arrays.asList(Tag1Split);
        
//        String dt1Split[] = Tag1.split(",");
//        String dt2Split[] = Tag2.split(",");       
//        
//        for (int i = 0; i < dt2Split.length; i++)
//        {
//            Tag2List.add(dt2Split[i]);
//        }
//
//        for (int i = 0; i < dt1Split.length; i++)
//        {
//            Tag1List.add(dt1Split[i]);
//        }        
        

        int test = 0;
        long t2 = System.currentTimeMillis();
                
        List<String> vocabulary = null;
        
        switch(Syn)
        {
            case 1: 
                vocabulary = GetTagListVocabSyn2(Tag1List, Tag2List);
                break;
            case 2:
                vocabulary = GetTagListVocabWNet(Tag1List, Tag2List);
                break;
            case 3:
                vocabulary = GetTagListVocab(Tag1List, Tag2List);
            default:
                break;
        }
        
        
        List<Integer> targetEntries = computeEntriesInVocab(vocabulary, Tag2List);
        List<Integer> otherEntries = computeEntriesInVocab(vocabulary, Tag1List);
        
        long t3 = System.currentTimeMillis();
        
        double lengthTarget = 0;
        double lengthOther = 0;
        double dotProduct = 0;
//        for(int value : targetEntries){
//                lengthTarget+=value*value;
//        }
        //lengthTarget = Math.sqrt(lengthTarget);
        lengthTarget = Collections.frequency(targetEntries, 1);

//        for(int value : otherEntries){
//                lengthOther+=value*value;
//        }
        
        lengthOther = Collections.frequency(otherEntries, 1);
        
        //lengthOther = Math.sqrt(lengthOther);
        for(int i=0;i<targetEntries.size();i++){
                dotProduct += targetEntries.get(i) * otherEntries.get(i);
        }
        
        similarity = (double)dotProduct/(lengthOther*lengthTarget);
        
        long t4 = System.currentTimeMillis();;
        
        long t1diff = t2 - t1;
        long t2diff = t3 - t2;
        long t3diff = t4 - t3;
        
        return similarity;
    }
    
    
    public List<Integer> computeEntriesInVocab(List<String> vocabulary, List<String> TagList) 
    {
            List<Integer> vocabEntriesForId = new ArrayList<Integer>();
            Porter stemmer = new Porter();
            
            for(int i = 0; i<vocabulary.size();i++){
                    vocabEntriesForId.add(0);
            }
            
            List<String> stemmedVocab = new ArrayList<String>();
           
            long t2 = System.currentTimeMillis();
            for(String word : vocabulary){
                stemmedVocab.add(stemmer.stripAffixes(word));
            }            
            long t3 = System.currentTimeMillis();
            long diff = t3-t2;
            for(String tag : TagList){
                    if(vocabulary.contains(tag)){
                            int index = vocabulary.indexOf(tag);
                            vocabEntriesForId.set(index, 1);
                    } else if(vocabulary.contains(stemmer.stripAffixes(tag))){
                            int index = vocabulary.indexOf(stemmer.stripAffixes(tag));
                            //System.out.println(index);
                            vocabEntriesForId.set(index, 1);
                    }else{
                            List<String> synonyms = getSynonyms(tag);
                            int ok=0;

                            for(String synonym : synonyms){
                                    if(vocabulary.contains(synonym)){
                                            int index = vocabulary.indexOf(synonym);
                                            vocabEntriesForId.set(index, 1);
                                            ok=1;
                                            break;
                                    } else if(vocabulary.contains(stemmer.stripAffixes(synonym))){
                                            int index = vocabulary.indexOf(stemmer.stripAffixes(synonym));
                                            vocabEntriesForId.set(index, 1);
                                            ok=1;
                                            break;
                                    } else {

                                            if(stemmedVocab.contains(stemmer.stripAffixes(synonym))){
                                                    int index = stemmedVocab.indexOf(stemmer.stripAffixes(synonym));
                                                    vocabEntriesForId.set(index, 1);
                                                    ok=1;
                                                    break;
                                            }
                                    }
                            }
                            if(ok==0){
                                    for(String tagVoc : vocabulary){
                                            synonyms = getSynonyms(tagVoc);
                                            if(synonyms.contains(tag)){
                                                    int index = vocabulary.indexOf(tagVoc);
                                                    vocabEntriesForId.set(index, 1);
                                                    ok=1;
                                                    break;
                                            } else{
                                                    for(String synonym : synonyms){
                                                            if(stemmer.stripAffixes(synonym).equals(tag)||stemmer.stripAffixes(synonym).equals(stemmer.stripAffixes(tag))){
                                                                    int index = vocabulary.indexOf(tagVoc);
                                                                    vocabEntriesForId.set(index, 1);
                                                                    ok=1;
                                                                    break;
                                                            } 
                                                    }
                                            }
                                    }
                            }
                    }
            }

            return vocabEntriesForId;
    }
    
    public  List<String> GetTagListVocabSyn2(List<String> Tag1List, List<String> Tag2List)
    {
	String tagExp = "";
        Set<String> vocabulary = new HashSet<String>();
        Set<String> TagList = new HashSet<>(Tag1List);
        TagList.addAll(Tag2List);   
        
        Set<String> stemmedVocabulary = new HashSet<String>();
        
        try
        {
        //List<String> vocabulary = new ArrayList<String>();
        Porter stemmer = new Porter();
        
        for(String tag : TagList){
            tagExp = tag;
            String tagStem = stemmer.stripAffixes(tag); 
            if(vocabulary.size() != 0){
                    if(!vocabulary.contains(tag)){                             

//                            for(String updatedTag : vocabulary){
//                                    stemmedVocabulary.add(stemmer.stripAffixes(updatedTag));
//                            }
                            if(!stemmedVocabulary.contains(stemmer.stripAffixes(tag))){
                                    Set<String> synonyms = new HashSet<>(getSynonyms(tag));
                                    int ok=1;
                                    for(String synonym : synonyms){
                                            if(vocabulary.contains(synonym)){
                                                    ok=0;
                                                    break;
                                            } else if(stemmedVocabulary.contains(stemmer.stripAffixes(synonym))){
                                                    ok=0;
                                                    break;
                                            }
                                    }
                                    if(ok==1){
                                            for(String updateTag : vocabulary){
                                                    synonyms.clear();
                                                    synonyms.addAll(getSynonyms(updateTag));
                                                    if(synonyms.contains(tag)){
                                                            ok=0;
                                                            break;
                                                    } else{
                                                            for(String synonym : synonyms){
                                                                   String synonymStem = stemmer.stripAffixes(synonym);
                                                                   
                                                                    if(synonymStem.equals(tag)||synonymStem.equals(tagStem)){
                                                                            ok=0;
                                                                            break;
                                                                    } 
                                                            }
                                                    }
                                            }
                                    }

                                    if(ok==1){
                                            vocabulary.add(tag);
                                            stemmedVocabulary.add(tagStem);
                                    }
                            }
                    }
            } else{
                    vocabulary.add(tag);
                    stemmedVocabulary.add(tagStem);
            }
        }
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
            System.out.println(tagExp);
        }

        return new ArrayList<>(vocabulary);
	        
    }    
    
    public  List<String> GetTagListVocab(List<String> Tag1List, List<String> Tag2List)
    {
	String tagExp = "";
        Set<String> vocabulary = new HashSet<String>();
        Set<String> TagList = new HashSet<>(Tag1List);
        TagList.addAll(Tag2List);   
        
        Set<String> stemmedVocabulary = new HashSet<String>();
        
        try
        {
        //List<String> vocabulary = new ArrayList<String>();
        Porter stemmer = new Porter();
        
        for(String tag : TagList){
            tagExp = tag;
            String tagStem = stemmer.stripAffixes(tag); 
            if(vocabulary.size() != 0){
                    if(!vocabulary.contains(tag)){                             

//                            for(String updatedTag : vocabulary){
//                                    stemmedVocabulary.add(stemmer.stripAffixes(updatedTag));
//                            }
                            if(!stemmedVocabulary.contains(stemmer.stripAffixes(tag))){
                                        vocabulary.add(tag);
                                        stemmedVocabulary.add(tagStem);
                            }
                    }
            } else{
                    vocabulary.add(tag);
                    stemmedVocabulary.add(tagStem);
            }
        }
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
            System.out.println(tagExp);
        }

        return new ArrayList<>(vocabulary);
	        
    }    

    public  List<String> GetTagListVocabWNet(List<String> Tag1List, List<String> Tag2List)
    {
	String tagExp = "";
        Set<String> vocabulary = new HashSet<String>();
        Set<String> TagList = new HashSet<>(Tag1List);
        TagList.addAll(Tag2List);   
        
        Set<String> stemmedVocabulary = new HashSet<String>();
        
        try
        {
        //List<String> vocabulary = new ArrayList<String>();
        Porter stemmer = new Porter();
        
        for(String tag : TagList){
            tagExp = tag;
            String tagStem = stemmer.stripAffixes(tag); 
            if(vocabulary.size() != 0){
                    if(!vocabulary.contains(tag)){                             

//                            for(String updatedTag : vocabulary){
//                                    stemmedVocabulary.add(stemmer.stripAffixes(updatedTag));
//                            }
                            if(!stemmedVocabulary.contains(stemmer.stripAffixes(tag))){
                                    Set<String> synonyms = new HashSet<>(getWordNet(tag));
                                    int ok=1;
                                    for(String synonym : synonyms){
                                            if(vocabulary.contains(synonym)){
                                                    ok=0;
                                                    break;
                                            } else if(stemmedVocabulary.contains(stemmer.stripAffixes(synonym))){
                                                    ok=0;
                                                    break;
                                            }
                                    }
                                    if(ok==1){
                                            for(String updateTag : vocabulary){
                                                    synonyms.clear();
                                                    synonyms.addAll(getWordNet(updateTag));
                                                    if(synonyms.contains(tag)){
                                                            ok=0;
                                                            break;
                                                    } else{
                                                            for(String synonym : synonyms){
                                                                   String synonymStem = stemmer.stripAffixes(synonym);
                                                                   
                                                                    if(synonymStem.equals(tag)||synonymStem.equals(tagStem)){
                                                                            ok=0;
                                                                            break;
                                                                    } 
                                                            }
                                                    }
                                            }
                                    }

                                    if(ok==1){
                                            vocabulary.add(tag);
                                            stemmedVocabulary.add(tagStem);
                                    }
                            }
                    }
            } else{
                    vocabulary.add(tag);
                    stemmedVocabulary.add(tagStem);
            }
        }
        }
        catch(Exception Ex)
        {
            Ex.printStackTrace();
            System.out.println(tagExp);
        }

        return new ArrayList<>(vocabulary);
	        
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
    
    public void SaveDocImSimilarity(BufferedWriter writer, HashMap<String, String> TrainImFeatures, String d1, String FN1, int choiceSim) throws IOException
    {
        HashMap<String, Double> DocSimilarity = new HashMap<>();
        
        for(Map.Entry E : TrainImFeatures.entrySet())
        {
            String Name  = (String) E.getKey();
            String d2 = (String) E.getValue();
            double Similarity = ComputeImSimilarity(choiceSim, d1, d2);
            DocSimilarity.put(Name, Similarity);
            writer.write(FN1 + "," + Name + "," + Similarity + "\n");
        }
    }    
    
    public void SaveDocTagSimilarity(BufferedWriter writer, HashMap<String, String> Tags, HashMap<String, String> DeepTags, String d1, String dt1, String FN1, int choiceSim, int Syn) throws IOException
    {
        if (Tags == null || DeepTags == null)
        {
            return;
        }
        
        for(Map.Entry E : Tags.entrySet())
        {
            String Name  = (String) E.getKey();
            String d2 = (String) E.getValue();
            String dt2 = DeepTags.get(Name);
            //dt2 = DeepTags.get("4371587027");
            //d2 = Tags.get("4371587027");
            double Similarity = ComputeTagSimilarity(choiceSim, d1, d2, dt1, dt2, Syn);
            writer.write(FN1 + "," + Name + "," + Similarity + "\n");
        }
    }     
    
    public  double ComputeTagSimilarity(int choice, String d1, String d2, String dt1, String dt2, int Syn)
    {
        switch(choice)
        {
            case 1:
                return ComputeTagTfExactMatchSimilarity(dt1, dt2);
            case 2:
                return ComputeTagTfVocabMatchSimilarity(dt1, dt2, Syn);
            case 3:
                return ComputeTagTfExactMatchSimilarity(d1, d2);
            case 4:
                if (d1.length() == 0 || d2.length() == 0)                
                {
                    return 0.0;
                }
                return ComputeTagTfVocabMatchSimilarity(d1, d2, Syn);
            case 5:
                if (d1.length() == 0 || d2.length() == 0)
                {
                    return ComputeTagTfVocabMatchSimilarity(dt1, dt2, Syn);
                }
                return ComputeTagTfExactNVocabMatchSimilarity(d1, d2, dt1, dt2, Syn);                
            case 6:
                if (d1.length() == 0 || d2.length() == 0)
                {
                    return ComputeTagTfExactMatchSimilarity(dt1, dt2);
                }
                return ComputeTagTfExactNVocabMatchSimilarity(d1, d2, dt1, dt2,Syn);
            case 7:
                if (d1.length() == 0 || d2.length() == 0)
                {
                    return ComputeTagTfVocabMatchSimilarity(dt1, dt2, Syn);
                }
                return ComputeTagTfVocabVocabMatchSimilarity(d1, d2, dt1, dt2, Syn);
            default:
                System.out.println("Invalid choice");
        }
        
        return 0.0;
    }
    
    public  double ComputeImSimilarity(int choice, String d1, String d2)
    {
        switch(choice)
        {
            case 1:
                return ComputeImageSimilarity(d1, d2);
            default:
                System.out.println("Invalid choice");
        }
        
        return 0.0;
    }    
}
