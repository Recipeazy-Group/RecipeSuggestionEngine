package ServerDriver;

import Util.Network.NetServer;
import Util.Network.RequestSender;
import Util.RecipeUtils.IngredientRecommender;
import PropertyClassifiers.ModelTrainer.RecipeRecommender;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SuggestionServer {

    public static WordVectorModel vectors;

    public static void main(String[] args) throws IOException {

        vectors = new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH"));
        RecipeRecommender.init();

        System.out.println("INITIALIZATIONS COMPLETE!\n\n");

        NetServer server = new NetServer(5050);


        server.addRoute("/suggestionAPI/", new NetServer.NetAction() {
            @Override
            public void actionGET(Map<String, String> params) {
                try {
                    String subroute = getSubroute();
                    if (subroute.equals("ingredients")) {
                        long recipeID = -1;
                        int numSuggestions = 5;
                        try {
                            recipeID = Long.parseLong(params.get("recipeID"));
                            numSuggestions = Integer.parseInt(params.get("numSuggestions"));

                        } catch (Exception e) {
                            System.out.println("\tWarning: either recipeID or numSuggestions could not be parsed.");
                        }

                        boolean parse = false;
                        String ingName = params.get("ingredientName");
                        if (params.containsKey("parse")) {
                            if (params.get("parse").equals("1")) {
                                System.out.println("\tParsing ingredient name for:" + ingName);
                                parse = true;
                            }
                        }

                        System.out.println("\tReceived ingredient substitute request, item: " + ingName + "\tNumber suggestions: " + numSuggestions);
                        JSONArray response = new JSONArray();
                        if (parse) {
                            ingName = IngredientRecommender.parseIngredientText(ingName, ((SimpleWordVectorModel) vectors).getWordVocab());
                        }
                        Collection<String> recIngs = IngredientRecommender.getRecommendedIngredients(ingName, numSuggestions);
                        for (String s : recIngs)
                            response.put(s);
                        write(response.toString());

                    } else if (subroute.equals("recipes")) {
                        String userID = params.get("userID"); // Represents URI encoded email address
                        int numSuggestions = Integer.parseInt(params.get("numSuggestions"));
                        System.out.println("Received recipe recommendation request for user " + userID);

                        JSONArray recommendations = new JSONArray();

                        String userFavs = null;
                        try {
                            userFavs = RequestSender.sendRequest(ResourceRepo.props.get("DB_API_BASEURL"), "UserFavorites/Email?Email=" + userID, "", "GET");
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
                            List<Recipe> recs = RecipeRecommender.getRecipeRecommendationsFromID(favIDs, numSuggestions);
                            for (Recipe rec : recs) {
                                JSONObject item = new JSONObject();
                                item.put("ID", rec.ID);
                                item.put("title", rec.title);
                                recommendations.put(item);
                            }
                        }
                        System.out.println("Replying with recipe recommendations...");
                        //Id and title
                        write(recommendations.toString());

                        // Recommendation strategy: get composite vector of recent user recipes
                        // then do simple vec comparison again
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        server.start();
        System.out.println("Suggestion server started.");

    }

}
