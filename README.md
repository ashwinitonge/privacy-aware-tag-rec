===============================================================================

# Privacy-Aware Tag Recommendation for Image Sharing

This repository contains the code, and data for our following paper:

    Ashwini Tonge, Cornelia Caragea and Anna Squicciarini. “Privacy-Aware Tag Recommendation for Image Sharing.” 
    In: Proceedings of the 29th ACM conference on Hypertext and Social Media (HT 2018), Baltimore, USA, 2018.
    
    
If you find the repository useful for your research, please cite our paper:

    @inproceedings{Tonge:2018:PTR:3209542.3209574,
       author = {Tonge, Ashwini and Caragea, Cornelia and Squicciarini, Anna},
       title = {Privacy-Aware Tag Recommendation for Image Sharing},
       booktitle = {Proceedings of the 29th on Hypertext and Social Media},
       series = {HT '18},
       year = {2018},
       isbn = {978-1-4503-5427-1},
       location = {Baltimore, MD, USA},
       pages = {52--56},
       numpages = {5},
       url = {http://doi.acm.org/10.1145/3209542.3209574},
       doi = {10.1145/3209542.3209574},
       acmid = {3209574},
       publisher = {ACM},
       address = {New York, NY, USA},
       keywords = {image tagging, image's privacy, privacy-aware tag recommendation},
    } 
    
    
# Code

    1. Generate Similarity using Tag or Image features
        java -jar FeatureSimilarity.jar config_ut_s0.txt
   
    2. Identify top K similar images
        java -jar TopKSimDocFinder.jar config_ut_s0.txt Target\Experiments\FeatureSim\ut_TagSimilarity_4_50per_0.csv
    
    3. Recommend tags
        java -jar TagRecommendation.jar config_ut_s0.txt
    
    4. Generate performance of recommended tags
        java -jar TagRecPerf.jar config_ut_s0.txt
