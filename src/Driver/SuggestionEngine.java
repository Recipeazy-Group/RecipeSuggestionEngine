package Driver;

import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;

public class SuggestionEngine {


    public static void main(String[] args) {
        WordVectorModel vectors = new SimpleWordVectorModel("lib/models/foodVecs.json");
        System.out.println(vectors.getClosestMatches("bourbon", 10));
        System.out.println();
        System.out.println(vectors.getClosestMatches("apple", 10));
    }

}
