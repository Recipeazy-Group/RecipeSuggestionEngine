package Util.WordVectorization;

import Util.Math.Vector;

import java.util.Collection;

public interface WordVectorModel {
    Collection<String> getClosestMatches(String word, int numReturn);
    Vector<Double> getWordVector(String word);
    double calcWordSimilarity(String word1, String word2);
}