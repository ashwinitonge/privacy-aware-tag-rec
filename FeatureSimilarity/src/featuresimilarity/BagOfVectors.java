/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package featuresimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ashwini
 */
public class BagOfVectors {

    public static void LoadVectors(String FileName, HashMap<String, List<Double>> WordVectors)
    {
        try
        {
            BufferedReader Reader = new BufferedReader(new FileReader(new File(FileName)));
            String Line = "";
            
            while ((Line = Reader.readLine()) != null)
            {
                String LineSplit[] = Line.split(",");
                
                String Word = LineSplit[0];
                
                if (!WordVectors.containsKey(Word))
                {
                    List<Double> Vec = new ArrayList<>();
                    for (int i = 1; i < LineSplit.length; i++)
                    {
                        Vec.add(Double.parseDouble(LineSplit[i]));
                    }
                    
                    WordVectors.put(Word, Vec);
                }                
            }           
                    
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }
}
