package Util.WordVectorization;

import java.util.Collection;

public interface WordVector {
    Collection<String> getClosestMatches(String word, int numReturn);
    double[] getWordVector(String word);
    double calcWordSimilarity(String word1, String word2);
}
