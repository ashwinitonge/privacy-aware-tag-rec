<<<<<<< HEAD
% Generate Similarity using Tag or Image features
java -jar FeatureSimilarity.jar config_ut_s0.txt

% Identify top K similar images
java -jar TopKSimDocFinder.jar config_ut_s0.txt Target\Experiments\FeatureSim\ut_TagSimilarity_4_50per_0.csv

% Recommend tags
java -jar TagRecommendation.jar config_ut_s0.txt

% Generate performance of recommended tags
=======
% Generate Similarity using Tag or Image features
java -jar FeatureSimilarity.jar config_ut_s0.txt

% Identify top K similar images
java -jar TopKSimDocFinder.jar config_ut_s0.txt Target\Experiments\FeatureSim\ut_TagSimilarity_4_50per_0.csv

% Recommend tags
java -jar TagRecommendation.jar config_ut_s0.txt

% Generate performance of recommended tags
>>>>>>> a657059ee7fd07dc4acb1b811f6f1ab30657d0f1
java -jar TagRecPerf.jar config_ut_s0.txt