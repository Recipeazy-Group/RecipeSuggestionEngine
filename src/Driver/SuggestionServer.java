package Driver;

import PropertyClassifiers.RecipePropertyClassifier;
import Util.Network.NetServer;
import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;

import java.util.ArrayList;

public class SuggestionServer {

    public static WordVectorModel vectors;

    public static void main(String[] args) {
//        vectors = new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json");
//       // System.out.println(vectors.getClosestMatches("pear", 10));
//        System.out.println(vectors.getClosestMatches("pear", 25));
//        Recipe r = new Recipe(new ArrayList<>());
//        r.ingredients.add("jalapeno chilies");
//        System.out.println(RecipePropertyClassifier.getCuisineCorrelations(r));

        NetServer server;


        server.addRoute("/recommend/", new NetServer.NetAction(){
            @Override
            public void actionPOST(String bodyPOST) {

            }
        });


    }

}
