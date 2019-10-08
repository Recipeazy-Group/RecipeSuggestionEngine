package Util;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.util.Collection;

public class TarGZWordVector {
    private static Word2Vec vector;

    public WordVector(String path) {
        File model = new File(path);
        System.out.println("WordVectorLoader: loading word vector at path " + path);
        vector = WordVectorSerializer.readWord2VecModel(model);
        System.out.println("WordVectorLoader: vector loaded.");
    }

    /**
     * @param word      the term to match closest words to
     * @param numReturn number of words to match
     * @return a collection of closest n words
     */
    public Collection<String> getClosestMatches(String word, int numReturn) {
        if(vector.hasWord(word))
        return vector.wordsNearestSum(word, numReturn);
        return null;
    }

    /**
     * @param word the term which will be converted to a vector via the imported model
     * @return the vector representing the word
     */
    public double[] vectorizeWord(String word) {
        if(vector.hasWord(word))
        return vector.getWordVector(word);
        return null;
    }

    public double calcWordSimilarity(String word1, String word2) {
        if(vector.hasWord(word1) && vector.hasWord(word2))
        return vector.similarity(word1, word2);
        return Double.MIN_VALUE;
    }
}
