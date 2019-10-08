package Driver;

import Util.WordVectorization.WordVector;

public class SuggestionEngine {


    public static void main(String[] args) {
        WordVector vectors = new WordVector("RecipeSuggestionEngine/lib/models/GoogleNews-vectors-negative300.bin.gz");
        System.out.println(vectors.getClosestMatches("bourbon", 10));
        System.out.println();
        System.out.println(vectors.getClosestMatches("apple", 10));
    }

}
