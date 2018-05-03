/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package featuresimilarity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 *
 * @author Ashwini
 */
public class Pool5Features {
    HashMap<String, String> Deep_Pool5_Features = new HashMap<>(); //Pool5    
        
    public void LoadDeepFeatures(String CsvFilename)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(CsvFilename)));
            String Line = "";
            
            reader.readLine();
            while ((Line = reader.readLine()) != null )
            {
                String Filename = Line.substring(0, Line.indexOf(","));
                Deep_Pool5_Features.put(Filename, Line);
            }
            
            reader.close();
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }
    
    public HashMap<String, String> GetImFeatures()
    {
        return Deep_Pool5_Features;
    }    
}
