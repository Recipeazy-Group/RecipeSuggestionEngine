package ServerDriver;

import Util.Network.NetServer;
import Util.RecipeUtils.ModelTrainer.RecipeRecommender;
import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SuggestionServer {

    public static WordVectorModel vectors;

    public static void main(String[] args) {
        vectors = new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json");

        NetServer server = new NetServer(5050);


        server.addRoute("/suggestionAPI/", new NetServer.NetAction() {
            @Override
            public void actionGET(Map<String, String> params) {
                String subroute = getSubroute();
                if (subroute.equals("ingredients")) {
                    long recipeID = Long.parseLong(params.get("recipeID"));
                    int numSuggestions = Integer.parseInt(params.get("numSuggestions"));
                    String ingName = params.get("ingredientName");
                    JSONArray response = new JSONArray();
                    response.put(vectors.getClosestMatches(ingName, numSuggestions));
                    JSONObject toWrite = new JSONObject();
                    toWrite.put("substitutes", response);
                    write(toWrite.toString());
                }
                else if(subroute.equals("recipes")){
                    long userID = Long.parseLong(params.get("userID"));
                    int numSuggestions = Integer.parseInt(params.get("numSuggestions"));

                    // TODO: figure out how to get user favorites and store them here
                    List<Recipe> userFavorites = null;


                    JSONArray recommendations = new JSONArray();
                    JSONObject toWrite = new JSONObject();
                    List<Integer> recs = RecipeRecommender.getRecipeRecommendations(userFavorites, numSuggestions);
                    recommendations.put(recs);


                    toWrite.put("recommendations", recommendations);
                    write(toWrite.toString());

                    // Recommendation strategy: get composite vector of recent user recipes
                    // then do simple vec comparison again
                }
            }
        });
        server.start();
        System.out.println("Suggestion server started.");

    }

}
