<<<<<<< HEAD
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecommendation;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Ashwini
 */
public class TagReco {

    HashMap<String, Double> PrivacyWeight;
    static Random rand = new Random(1234);
    
    public static void RecommendTTags(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags)
    {
        Porter stemmer = new Porter();
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    if (WeightedUserTags.containsKey(UserTagsStrSplit[i]))
                    {
                        double weight = (double) WeightedUserTags.get(UserTagsStrSplit[i]);
                        weight += similarity;
                        WeightedUserTags.put(UserTagsStrSplit[i], weight);
                    }
                    else
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    public static void RecommendTTagsWithSynCheckWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);
                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }

    public static void RecommendTTagsWithSynCheckWeightedTagImp(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, HashMap<String, Double> TagImportance)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for (String Str : WeightedUserTags.keySet())
        {
            Double weight = WeightedUserTags.get(Str);
            Double TagImpWt = TagImportance.get(Str);
            WeightedUserTags.put(Str, weight * TagImpWt);
        }
        
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }

    //public static void RecommendTTagsWithSynCheckWeightedTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    public static void RecommendTTagsWithSynCheckWeightedTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
                               
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            if (Privacy.compareTo("private") == 0){
                tagprob = TagPrivcayProb.get(Tag);
            }        
            else{
                tagprob = 1 - TagPrivcayProb.get(Tag);
            } 
            
            double weight = WeightedUserTags.get(Tag);
            WeightedUserTags.put(Tag, weight * tagprob);
        }
                    
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    
    public static void RecommendTTagsWithSynCheckWtProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, Double PrivacyProb, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
                               
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            tagprob = TagPrivcayProb.get(Tag);
              
            double weight = WeightedUserTags.get(Tag);
            weight = weight * (tagprob * PrivacyProb + (1 - tagprob) * ( 1 - PrivacyProb));
            WeightedUserTags.put(Tag, weight);
        }
                    
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    public static void RecommendTTagsWithSynCheckFrequencyTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = 1.0;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
//                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
//                    {
//                        continue;
//                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            if (Privacy.compareTo("private") == 0){
                tagprob = TagPrivcayProb.get(Tag);
            }        
            else{
                tagprob = 1 - TagPrivcayProb.get(Tag);
            } 
            
            double weight = WeightedUserTags.get(Tag);
            WeightedUserTags.put(Tag, weight * tagprob);
        }        
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    public static void RecommendTTagsRandom(int T, Set<String>Vocabulary, List<String> TestTags, HashMap<String, Double> WeightedUserTags)
    {
        Porter stemmer = new Porter();

        
        int VocabSize = Vocabulary.size();
        String[] vocabarr = new String[VocabSize];
        Vocabulary.toArray(vocabarr);
        
        for (int i = 0; i < T; i++){
            int index = rand.nextInt(VocabSize);            
            String Tag = vocabarr[index];
            WeightedUserTags.put(Tag, 1.0);
        }        
    }    
    
    
    public static void RecommendTTagsWithSynCheckPrivacyWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, HashMap<String, String> PrivacyS, double privateProb)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            String pSetting = PrivacyS.get(name);
            double PrivacyWeight = 1.0;
            if (pSetting.compareToIgnoreCase("private") == 0)
            {
                PrivacyWeight = privateProb;
            }
            else
            {
                PrivacyWeight = 1.0 - privateProb;
            }
            
            double similarity = (double)E.getValue() * PrivacyWeight;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    
    public static void RecommendTTagsWithSynCheckFrequency(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = 1.0;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    public static void RecommendTTagsWithSynCheckRandom(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        List<String> AllRecTags = new ArrayList<>(WeightedUserTags.keySet());
        WeightedUserTags.clear();
        
        int i = 0;
        Random rand = new Random();
        while(i < T)
        {
            if (AllRecTags.size() == 0)
            {
                break;
            }
            
            int index = rand.nextInt(AllRecTags.size());
            String Tag = AllRecTags.get(index);
            WeightedUserTags.put(Tag, 0.0);
            AllRecTags.remove(index);
            i++;
        }
    }    
    
    public static void RecommendTTagsWithSynCheckAddSynWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        HashMap<String, List<String>> PassedSynCheck = new HashMap<>();
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                            
                            if (PassedSynCheck.containsKey(Str))
                            {
                                List<String> Syns = PassedSynCheck.get(Str);
                                Syns.add(UserTagsStrSplit[i]);
                                PassedSynCheck.put(Str, Syns);
                            }
                            else
                            {
                                List<String> Syns = new ArrayList<>();
                                Syns.add(UserTagsStrSplit[i]);
                                PassedSynCheck.put(Str, Syns);                                
                            }
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
            List<String> Syns = PassedSynCheck.get(Tag);
            
            for (int j = 0; j < Syns.size(); j++)
            {
                WeightedUserTags.put(Syns.get(i), weight);
            }
            
        }        
        
    }    

    public static int check2wordsForSynonymty(String word1, String word2, Similarity S){
            int check = 0;
            Porter stemmer = new Porter();
            String stemmedWord1 = stemmer.stripAffixes(word1);
            String stemmedWord2 = stemmer.stripAffixes(word2);

            if(word1.equals(word2) || stemmedWord1.equals(stemmedWord2) || word1.equals(stemmedWord2) || word2.equals(stemmedWord1)){
                    return 1;
            } 
            
            return check;
    }
    
    
    public static int check2wordsForSynonymtyOld(String word1, String word2, Similarity S){
            int check = 0;
            Porter stemmer = new Porter();
            String stemmedWord1 = stemmer.stripAffixes(word1);
            String stemmedWord2 = stemmer.stripAffixes(word2);

            if(word1.equals(word2) || stemmedWord1.equals(stemmedWord2) || word1.equals(stemmedWord2) || word2.equals(stemmedWord1)){
                    return 1;
            } else{
                    List<String> synonymsWord1 = S.getSynonyms(word1);
                    if(synonymsWord1.contains(word2) || synonymsWord1.contains(stemmer.stripAffixes(word2))){
                            return 1;
                    }else{
                            for(String synonym : synonymsWord1){
                                    if(stemmer.stripAffixes(word2).equals(stemmer.stripAffixes(synonym))){
                                            return 1;
                                    }
                            }
                    }

                    List<String> synonymsWord2 = S.getSynonyms(word2);
                    if(synonymsWord2.contains(word1) || synonymsWord2.contains(stemmer.stripAffixes(word1))){
                            return 1;
                    }else{
                            for(String synonym : synonymsWord2){
                                    if(stemmer.stripAffixes(word1).equals(stemmer.stripAffixes(synonym))){
                                            return 1;
                                    }
                            }
                    }
            }

            return check;
    }
    
}


=======
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecommendation;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Ashwini
 */
public class TagReco {

    HashMap<String, Double> PrivacyWeight;
    static Random rand = new Random(1234);
    
    public static void RecommendTTags(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags)
    {
        Porter stemmer = new Porter();
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    if (WeightedUserTags.containsKey(UserTagsStrSplit[i]))
                    {
                        double weight = (double) WeightedUserTags.get(UserTagsStrSplit[i]);
                        weight += similarity;
                        WeightedUserTags.put(UserTagsStrSplit[i], weight);
                    }
                    else
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    public static void RecommendTTagsWithSynCheckWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);
                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }

    public static void RecommendTTagsWithSynCheckWeightedTagImp(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, HashMap<String, Double> TagImportance)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for (String Str : WeightedUserTags.keySet())
        {
            Double weight = WeightedUserTags.get(Str);
            Double TagImpWt = TagImportance.get(Str);
            WeightedUserTags.put(Str, weight * TagImpWt);
        }
        
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }

    //public static void RecommendTTagsWithSynCheckWeightedTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    public static void RecommendTTagsWithSynCheckWeightedTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
                               
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            if (Privacy.compareTo("private") == 0){
                tagprob = TagPrivcayProb.get(Tag);
            }        
            else{
                tagprob = 1 - TagPrivcayProb.get(Tag);
            } 
            
            double weight = WeightedUserTags.get(Tag);
            WeightedUserTags.put(Tag, weight * tagprob);
        }
                    
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    
    public static void RecommendTTagsWithSynCheckWtProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, Double PrivacyProb, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
                               
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            tagprob = TagPrivcayProb.get(Tag);
              
            double weight = WeightedUserTags.get(Tag);
            weight = weight * (tagprob * PrivacyProb + (1 - tagprob) * ( 1 - PrivacyProb));
            WeightedUserTags.put(Tag, weight);
        }
                    
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    public static void RecommendTTagsWithSynCheckFrequencyTagPrivacyProb(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, String Privacy, HashMap<String, Double> TagPrivcayProb)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = 1.0;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
//                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
//                    {
//                        continue;
//                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        for(String Tag : WeightedUserTags.keySet())
        {
            double tagprob = 0.0;
            
            if (Privacy.compareTo("private") == 0){
                tagprob = TagPrivcayProb.get(Tag);
            }        
            else{
                tagprob = 1 - TagPrivcayProb.get(Tag);
            } 
            
            double weight = WeightedUserTags.get(Tag);
            WeightedUserTags.put(Tag, weight * tagprob);
        }        
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }    
    
    public static void RecommendTTagsRandom(int T, Set<String>Vocabulary, List<String> TestTags, HashMap<String, Double> WeightedUserTags)
    {
        Porter stemmer = new Porter();

        
        int VocabSize = Vocabulary.size();
        String[] vocabarr = new String[VocabSize];
        Vocabulary.toArray(vocabarr);
        
        for (int i = 0; i < T; i++){
            int index = rand.nextInt(VocabSize);            
            String Tag = vocabarr[index];
            WeightedUserTags.put(Tag, 1.0);
        }        
    }    
    
    
    public static void RecommendTTagsWithSynCheckPrivacyWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S, HashMap<String, String> PrivacyS, double privateProb)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            String pSetting = PrivacyS.get(name);
            double PrivacyWeight = 1.0;
            if (pSetting.compareToIgnoreCase("private") == 0)
            {
                PrivacyWeight = privateProb;
            }
            else
            {
                PrivacyWeight = 1.0 - privateProb;
            }
            
            double similarity = (double)E.getValue() * PrivacyWeight;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    
    public static void RecommendTTagsWithSynCheckFrequency(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();

        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = 1.0;
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) || TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
        }        
        
    }
    
    public static void RecommendTTagsWithSynCheckRandom(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        if (Similarity == null)
        {
            return;
        }        
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    {
                        continue;
                    }
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        List<String> AllRecTags = new ArrayList<>(WeightedUserTags.keySet());
        WeightedUserTags.clear();
        
        int i = 0;
        Random rand = new Random();
        while(i < T)
        {
            if (AllRecTags.size() == 0)
            {
                break;
            }
            
            int index = rand.nextInt(AllRecTags.size());
            String Tag = AllRecTags.get(index);
            WeightedUserTags.put(Tag, 0.0);
            AllRecTags.remove(index);
            i++;
        }
    }    
    
    public static void RecommendTTagsWithSynCheckAddSynWeighted(int T, HashMap<String, String> UserTags, List<String> TestTags, HashMap<String, Double> Similarity, HashMap<String, Double> WeightedUserTags, Similarity S)
    {
        Porter stemmer = new Porter();
        
        HashMap<String, List<String>> PassedSynCheck = new HashMap<>();
        
        for (Map.Entry E : Similarity.entrySet())
        {
            String name = (String)E.getKey();
            double similarity = (double)E.getValue();
            
            if (UserTags.containsKey(name))
            {
                String UserTagsStr = UserTags.get(name);

                String UserTagsStrSplit[] = UserTagsStr.split(",");                

                for (int i = 0; i < UserTagsStrSplit.length; i++)
                {
                    //if((TestTags.contains(UserTagsStrSplit[i]) && TestTags.contains(stemmer.stripAffixes(UserTagsStrSplit[i]))))
                    //{
                    //    continue;
                    //}
                    
                    boolean IsMatched = false;
                    
                    for (String Str : WeightedUserTags.keySet())
                    {
                        if(check2wordsForSynonymty(Str,UserTagsStrSplit[i], S)==1)
                        {
                            double weight = (double) WeightedUserTags.get(Str);
                            weight += similarity;
                            WeightedUserTags.put(Str, weight);
                            IsMatched = true;
                            
                            if (PassedSynCheck.containsKey(Str))
                            {
                                List<String> Syns = PassedSynCheck.get(Str);
                                Syns.add(UserTagsStrSplit[i]);
                                PassedSynCheck.put(Str, Syns);
                            }
                            else
                            {
                                List<String> Syns = new ArrayList<>();
                                Syns.add(UserTagsStrSplit[i]);
                                PassedSynCheck.put(Str, Syns);                                
                            }
                        }
                    }
                    
                    if (IsMatched == false)
                    {
                        WeightedUserTags.put(UserTagsStrSplit[i], similarity);
                    }
                    
                    
                }
            }
            else
            {
                //System.out.println(name);
            }
        }
        
        ValueComparator vc = new ValueComparator(WeightedUserTags);
        TreeMap<String, Double> WeightedTreeMap = new TreeMap<>(vc);   
        
        WeightedTreeMap.putAll(WeightedUserTags);     
        WeightedUserTags.clear();
        int i = 0;
        for(Map.Entry E : WeightedTreeMap.entrySet())
        {
            String Tag  = (String) E.getKey();
            double weight = (double) E.getValue();
            
            i++;
            
            if (i > T)
                break;
            
            WeightedUserTags.put(Tag, weight);
            List<String> Syns = PassedSynCheck.get(Tag);
            
            for (int j = 0; j < Syns.size(); j++)
            {
                WeightedUserTags.put(Syns.get(i), weight);
            }
            
        }        
        
    }    

    public static int check2wordsForSynonymty(String word1, String word2, Similarity S){
            int check = 0;
            Porter stemmer = new Porter();
            String stemmedWord1 = stemmer.stripAffixes(word1);
            String stemmedWord2 = stemmer.stripAffixes(word2);

            if(word1.equals(word2) || stemmedWord1.equals(stemmedWord2) || word1.equals(stemmedWord2) || word2.equals(stemmedWord1)){
                    return 1;
            } 
            
            return check;
    }
    
    
    public static int check2wordsForSynonymtyOld(String word1, String word2, Similarity S){
            int check = 0;
            Porter stemmer = new Porter();
            String stemmedWord1 = stemmer.stripAffixes(word1);
            String stemmedWord2 = stemmer.stripAffixes(word2);

            if(word1.equals(word2) || stemmedWord1.equals(stemmedWord2) || word1.equals(stemmedWord2) || word2.equals(stemmedWord1)){
                    return 1;
            } else{
                    List<String> synonymsWord1 = S.getSynonyms(word1);
                    if(synonymsWord1.contains(word2) || synonymsWord1.contains(stemmer.stripAffixes(word2))){
                            return 1;
                    }else{
                            for(String synonym : synonymsWord1){
                                    if(stemmer.stripAffixes(word2).equals(stemmer.stripAffixes(synonym))){
                                            return 1;
                                    }
                            }
                    }

                    List<String> synonymsWord2 = S.getSynonyms(word2);
                    if(synonymsWord2.contains(word1) || synonymsWord2.contains(stemmer.stripAffixes(word1))){
                            return 1;
                    }else{
                            for(String synonym : synonymsWord2){
                                    if(stemmer.stripAffixes(word1).equals(stemmer.stripAffixes(synonym))){
                                            return 1;
                                    }
                            }
                    }
            }

            return check;
    }
    
}


>>>>>>> a657059ee7fd07dc4acb1b811f6f1ab30657d0f1
