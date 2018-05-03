/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tagrecperf;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ashwini
 */
public class TagPerformance {
    
    
	public static int noOfTagsCorreclyReccomended(List<String> TestHiddenTags, List<String> RecoTags, Similarity S) 
        {
            Porter stemmer = new Porter();
	    List<String> stemmedReccomendedTags = new ArrayList<String>();
            List<Integer> reccomTagsAndFlag = new ArrayList<Integer>();
		for(String tag : RecoTags){
			stemmedReccomendedTags.add(stemmer.stripAffixes(tag));
			reccomTagsAndFlag.add(0);
		}

		int nrCorrectlyPredicted = 0;
		for(String tag : TestHiddenTags){
			//	System.out.println(tag);
                       
			int ok=0;
			if(RecoTags.contains(tag) && reccomTagsAndFlag.get(RecoTags.indexOf(tag))==0){
				nrCorrectlyPredicted++;
				reccomTagsAndFlag.set(RecoTags.indexOf(tag),1);
				//		System.out.println(reccomTagsAndFlag);
				//		System.out.println("rec: "+tag);
				ok=1;
			} else if(stemmedReccomendedTags.contains(stemmer.stripAffixes(tag)) && reccomTagsAndFlag.get(stemmedReccomendedTags.indexOf(stemmer.stripAffixes(tag)))==0){
				nrCorrectlyPredicted++;
				reccomTagsAndFlag.set(stemmedReccomendedTags.indexOf(stemmer.stripAffixes(tag)),1);
				//		System.out.println("rec: "+tag);

				ok=1;
			} else{
				List<String> synonyms = S.getSynonyms(tag);//tagsAndSynonyms.get(tag);//
                                synonyms.addAll(S.getHypernym(tag));
                                synonyms.addAll(S.getHyponym(tag));
                                
				for(String synonym : synonyms){
					if((RecoTags.contains(synonym)&& reccomTagsAndFlag.get(RecoTags.indexOf(synonym))==0)){
						nrCorrectlyPredicted++;
						//	System.out.println("rec: "+tag);
						reccomTagsAndFlag.set(RecoTags.indexOf(synonym),1);

						ok=1;
						break;
					} else if(stemmedReccomendedTags.contains(stemmer.stripAffixes(synonym))&& reccomTagsAndFlag.get(stemmedReccomendedTags.indexOf(stemmer.stripAffixes(synonym)))==0){
						nrCorrectlyPredicted++;
						//System.out.println("rec: "+tag);
						reccomTagsAndFlag.set(stemmedReccomendedTags.indexOf(stemmer.stripAffixes(synonym)),1);
						ok=1;
						break;
					}
				}
			}

			if(ok==0){
				for(String reccomTarget : RecoTags) if(reccomTagsAndFlag.get(RecoTags.indexOf(reccomTarget))==0){
					List<String> synonyms = S.getSynonyms(reccomTarget);//tagsAndSynonyms.get(tag);//
                                        synonyms.addAll(S.getHypernym(reccomTarget));
                                        synonyms.addAll(S.getHyponym(reccomTarget));
                                        
					if(synonyms.contains(tag)){
						nrCorrectlyPredicted++;
						//		System.out.println("rec: "+tag);
						reccomTagsAndFlag.set(RecoTags.indexOf(reccomTarget),1);
						ok=1;
						break;
					} else {
						for(String synonym : synonyms){
							if(stemmer.stripAffixes(synonym).equals(tag)|| stemmer.stripAffixes(synonym).equals(stemmer.stripAffixes(tag))){
								nrCorrectlyPredicted++;
								//			System.out.println("rec: "+tag);
								reccomTagsAndFlag.set(RecoTags.indexOf(reccomTarget),1);
								ok=1;
								break;
							}
						}
					}
					if(ok==1){
						break;
					}
				}
			}
		}

		return nrCorrectlyPredicted;
	}
    

	public static double computePrecision(List<String> TestHiddenTags, List<String> RecoTags, Similarity S) {
		double prec = 0;		
		int noOfCorrectlyPredictedTags = noOfTagsCorreclyReccomended(TestHiddenTags, RecoTags, S);
		if (RecoTags.size() == 0)
		{
                    return 0;
		}
		prec = (double) noOfCorrectlyPredictedTags/RecoTags.size();
		return prec;
	}

	public static double computeRecall(List<String> TestHiddenTags, List<String> RecoTags, Similarity S){
		double recall = 0;
		int noOfCorrectlyPredictedTags = noOfTagsCorreclyReccomended(TestHiddenTags, RecoTags, S);
		
		if (TestHiddenTags.size() == 0)
		{
			return 0;
		}
		
		recall = (double) noOfCorrectlyPredictedTags/TestHiddenTags.size();
		return recall;
	}

	public static int noOfTagsCorreclyReccomendedExact(List<String> TestHiddenTags, List<String> RecoTags, Similarity S) 
        {
            int nrCorrectlyPredicted = 0;
            List<String> HiddenTagsLwr = new ArrayList<String>();
            List<String> RecoTagsLwr = new ArrayList<String>();
            
            for (int i = 0; i < TestHiddenTags.size(); i++)
            {
                HiddenTagsLwr.add(TestHiddenTags.get(i).toLowerCase());
            }
            
            for (int i = 0; i < RecoTags.size(); i++)
            {
                RecoTagsLwr.add(RecoTags.get(i).toLowerCase());
            }
            
            for (int i = 0; i < RecoTagsLwr.size(); i++)
            {
                if (HiddenTagsLwr.contains(RecoTagsLwr.get(i)))
                {
                    nrCorrectlyPredicted++;
                }
            }
            
            return nrCorrectlyPredicted;
	}
    

	public static double computePrecisionExact(List<String> TestHiddenTags, List<String> RecoTags, Similarity S) {
		double prec = 0;		
		int noOfCorrectlyPredictedTags = noOfTagsCorreclyReccomendedExact(TestHiddenTags, RecoTags, S);
		if (RecoTags.size() == 0)
		{
                    return 0;
		}
		prec = (double) noOfCorrectlyPredictedTags/RecoTags.size();
		return prec;
	}

	public static double computeRecallExact(List<String> TestHiddenTags, List<String> RecoTags, Similarity S){
		double recall = 0;
		int noOfCorrectlyPredictedTags = noOfTagsCorreclyReccomendedExact(TestHiddenTags, RecoTags, S);
		
		if (TestHiddenTags.size() == 0)
		{
			return 0;
		}
		
		recall = (double) noOfCorrectlyPredictedTags/TestHiddenTags.size();
		return recall;
	}

//	public static void getAveragedResults(){
//		double avgPrecision = 0;
//		double avgRecall = 0;
//		double avgFMeasure = 0;
//		int nr=0;
//		double maxPrecision = 0;
//		double maxRecall = 0;
//		double maxFmeasure = 0;
//
//		int nr1=0;
//		int nrf=0;
//		for(String id : testIds){
//			int noOfCorrectlyPredictedTags = noOfTagsCorreclyReccomended(id);
//			double precision;
//			if(idAndReccomendedTags.get(id).size() == 0){
//				precision =0;
//			} else{
//				
//				precision = 0;
//				if (idAndReccomendedTags.get(id).size() != 0)
//				{
//					precision = (double) noOfCorrectlyPredictedTags/idAndReccomendedTags.get(id).size();
//				}
//			}
//			double recall;
//			
//			recall = 0.0;
//			
//			if (idAndHiddenTags.get(id).size() != 0)
//			recall = (double) noOfCorrectlyPredictedTags/idAndHiddenTags.get(id).size();
//
//			double fMeasure = 0;
//			if(precision + recall !=0){
//				fMeasure = (double)(2*precision*recall)/(precision+recall);
//			}
//			avgPrecision += precision;
//			avgRecall += recall;
//			avgFMeasure += fMeasure;
//
//			if(precision > maxPrecision){
//				maxPrecision = precision;
//			}
//			if(recall > maxRecall){
//				maxRecall = recall;
//			}
//			if(fMeasure > maxFmeasure){
//				maxFmeasure = fMeasure;
//			}
//			nr++;
//			if(nr%50==0){
//				System.out.println("processed "+nr+" images");
//			}
//
//			if(precision>1){
//				System.out.println(id+" precision="+precision);
//				System.out.println(noOfCorrectlyPredictedTags);
//				System.out.println(idAndReccomendedTags.get(id).size());				
//				System.out.println(idAndHiddenTags.get(id).size());
//
//			}
//			if(recall>1){
//				System.out.println(id+" recall="+recall);
//			}
//			if(fMeasure>1){
//				System.out.println(id+" fMeasure="+fMeasure);
//			}
//
//			if(precision>1 || recall>1 || fMeasure>1){
//				nr1++;
//			}
//			nrf++;
//		}
//
//		fw.write("Average precision: "+ (double)avgPrecision/testIds.size()+"\n");
//		fw.write("Average recall: "+ (double)avgRecall/testIds.size()+"\n");
//		fw.write("Average fMeasure: "+ (double)avgFMeasure/testIds.size()+"\n");
//
//
//		System.out.println("Average precision: "+ (double)avgPrecision/testIds.size());
//		System.out.println("Average recall: "+ (double)avgRecall/testIds.size());
//		System.out.println("Average fMeasure: "+ (double)avgFMeasure/testIds.size());
//
//		System.out.println("max precision: "+ maxPrecision);
//		System.out.println("max recall: "+ maxRecall);
//		System.out.println("max fMeasure: "+ maxFmeasure);
//		System.out.println(nr1+" images with values>1");
//		System.out.println(nrf);
//	}    
}
