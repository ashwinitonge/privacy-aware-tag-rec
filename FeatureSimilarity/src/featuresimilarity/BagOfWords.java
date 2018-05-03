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
public class BagOfWords {
    HashMap<String, String> UserTagsHidden = new HashMap<>();
    HashMap<String, String> UserTagsVisible = new HashMap<>();
    
    HashMap<String, String> DeepTags = new HashMap<>();
    
    public void LoadTags(String TagFolder, HashMap<String, String> Tags)
    {
        try
        {
            if (new File(TagFolder).isDirectory())
            {
                File TagFiles[] = new File(TagFolder).listFiles();

                for (int i = 0; i < TagFiles.length; i++)
                {
                    BufferedReader reader = new BufferedReader(new FileReader(new File(TagFiles[i].getAbsolutePath())));

                    String Line = "";
                    String UserTagsStr = "";
                    while ((Line = reader.readLine()) != null)
                    {
                        if (Line.compareTo("") == 0 || Line.length() == 0)
                        {
                            continue;
                        }

                        UserTagsStr += Line + ",";
                    }

                    if (UserTagsStr.compareTo("") != 0)
                    {
                        UserTagsStr = UserTagsStr.substring(0, UserTagsStr.length() - 1);                    
                        Tags.put(TagFiles[i].getName(), UserTagsStr.toLowerCase());
                    }

                    reader.close();
                }
            }
            else
            {
                BufferedReader reader = new BufferedReader(new FileReader(new File(TagFolder)));
                String Line;
                while ((Line = reader.readLine()) != null)
                {
                    try
                    {
                        String UserTagsStr = Line.substring(Line.indexOf(",") + 1, Line.length());
                        if (UserTagsStr.compareTo("") != 0)
                        {
                            String TagFile = Line.substring(0, Line.indexOf(","));
                            Tags.put(TagFile, UserTagsStr.toLowerCase());
                        }
                    }
                    catch(Exception Ex)
                    {
                        continue;
                    }
                }    
                
                reader.close();
            }
            
        }
        catch(Exception Ex)
        {
            System.out.println(Ex);
        }
    }
    
    public HashMap<String, String> GetUserTagsVisible()
    {
        return UserTagsVisible;
    }
    
    public HashMap<String, String> GetUserTagsHidden()
    {
        return UserTagsHidden;
    }    
    
    public HashMap<String, String> GetDeepTags()
    {
        return DeepTags;
    }    
}
