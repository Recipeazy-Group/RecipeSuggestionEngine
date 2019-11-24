package ServerDriver;

import Util.Network.NetServer;
import Util.Network.RequestSender;
import Util.RecipeUtils.IngredientRecommender;
import Util.RecipeUtils.ModelTrainer.RecipeRecommender;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SuggestionServer {

    public static WordVectorModel vectors;

    public static void main(String[] args) throws IOException {

        vectors = new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH"));

        NetServer server = new NetServer(5050);


        server.addRoute("/suggestionAPI/", new NetServer.NetAction() {
            @Override
            public void actionGET(Map<String, String> params) {
                String subroute = getSubroute();
                if (subroute.equals("ingredients")) {
                    long recipeID = Long.parseLong(params.get("recipeID"));
                    int numSuggestions = Integer.parseInt(params.get("numSuggestions"));
                    String ingName = params.get("ingredientName");
                    System.out.println("\tReceived ingredient substitute request, item: " + ingName + "\tNumber suggestions: " + numSuggestions);
                    JSONArray response = new JSONArray();
                    response.put(IngredientRecommender.getRecommendedIngredients(ingName, numSuggestions));
                    JSONObject toWrite = new JSONObject();
                    toWrite.put("substitutes", response);
                    write(toWrite.toString());

                } else if (subroute.equals("recipes")) {
                    String userID = params.get("userID"); // Represents URI encoded email address
                    int numSuggestions = Integer.parseInt(params.get("numSuggestions"));
                    System.out.println("Received recipe recommendation request for user " + userID);
                    // TODO: figure out how to get user favorites and store them here

                    JSONArray recommendations = new JSONArray();
                    JSONObject toWrite = new JSONObject();

                    String userFavs = null;
                    try {
                        userFavs = RequestSender.sendRequest("https://0e40647c-d0c2-4462-b1ac-fd44d5b88d1e.mock.pstmn.io", "UserFavorites", "", "GET");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (userFavs == null) {
                        System.out.println("Error: could not get a response from the DB API for user favorite recipes.");
                    } else {
                        JSONArray fav = new JSONArray(userFavs);
                        List<Integer> favIDs = new LinkedList<>();
                        for (int i = 0; i < fav.length(); i++) {
                            favIDs.add(fav.getJSONObject(i).getInt("RecipeId"));
                        }
                        List<Integer> recs = RecipeRecommender.getRecipeRecommendationsFromID(favIDs, numSuggestions);
                        recommendations.put(recs);
                    }


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
