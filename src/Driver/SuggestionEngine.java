package Driver;

import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;

public class SuggestionEngine {

    public static WordVectorModel vectors;

    public static void main(String[] args) {
        vectors = new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json");
       // System.out.println(vectors.getClosestMatches("pear", 10));
        System.out.println();
        System.out.println(vectors.getClosestMatches("curry", 25));
    }

}
