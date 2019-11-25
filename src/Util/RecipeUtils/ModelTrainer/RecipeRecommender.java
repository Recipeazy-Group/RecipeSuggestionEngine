package Util.RecipeUtils.ModelTrainer;

import ServerDriver.SuggestionServer;
import Util.Math.Vector;
import Util.RecipeUtils.CuisineTool;
import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public abstract class RecipeRecommender {
    public static INDArray recipeVectors;
    public static SimpleWordVectorModel recipeVecs;
    public static WordVectorModel foodVec;

    private static HashMap<Integer, Recipe> recipeDB;

    private transient static HashMap<String, Integer> ingredientFrequency;
    private transient static HashMap<CuisineTool.CUISINE, Integer> cuisineFrequency;

    public static void init() {
        System.out.println("Initializing recipe database.");
        recipeDB = new HashMap<>();
        try {
            List<Recipe> recipeList = new RecipeDisplaySetReader(ResourceRepo.props.get("RECIPE_DATA_PATH"), (SimpleWordVectorModel) SuggestionServer.vectors).getRecipes();
            for (Recipe r : recipeList) {
                recipeDB.put(r.ID, r);
            }
            System.out.println("Recipe database initialized.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initRecipeVecs(MODE);
        foodVec = SuggestionServer.vectors;
    }

    static {

    }

    private enum DEV_STATE {
        TRAIN, DEPLOY
    }

    final static DEV_STATE MODE = DEV_STATE.DEPLOY;

    public static List<Integer> getRecipeRecommendationsFromID(List<Integer> userRecipeIDs, int num) {
//
//        System.out.println("REMOVE THIS CODE LATER");
//        System.out.println("Input 1: " + recipeDB.get(1).title);
//        System.out.println("Suggestion 1: " +recipeDB.get(12227).title);
//
//        System.out.println("Suggestion 2: " +recipeDB.get(4957).title);
//
//        System.out.println("Suggestion 3: " +recipeDB.get(16199).title);
//
//        System.out.println("END REMOVE");

        List<Recipe> recipes = new LinkedList<>();
        for (int i : userRecipeIDs) {
            recipes.add(recipeDB.get(i));
        }
        return getRecipeRecommendations(recipes, num);
    }

    public static List<Integer> getRecipeRecommendations(List<Recipe> userRecipes, int num) {
        loadPreferenceMaps(userRecipes);
        // Now construct the user preference vector
        // via the use of a fake, placeholder recipe
        Vector<Double> sum = Vector.zeros(recipeVecs.getItemDimension());
        for (Map.Entry<String, Integer> entry : ingredientFrequency.entrySet()) {
            sum.plusEquals(foodVec.getWordVector(entry.getKey()).scale(entry.getValue()));
        }
        sum.normalize();
        // Now perform the cosine comparison between user vector and all recipes
        Collection<String> a = recipeVecs.getClosestMatches(sum, num);
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (String s : a) {
            toReturn.add(Integer.parseInt(s));
        }
        return toReturn;
    }


    private static void initRecipeVecs(DEV_STATE d) {
        HashMap<String, Vector<Double>> recipeVecs = new HashMap<>();
        switch (d) {
            case TRAIN:
                for (int i = 0; i < recipeVectors.columns(); i++) {
                    INDArray a = recipeVectors.getColumn(i);
                    Double[] vals = new Double[(int) a.length()];
                    for (int j = 0; j < a.length(); j++) {
                        vals[j] = a.getDouble(j);
                    }
                    recipeVecs.put("" + i, new Vector(vals));
                }
                RecipeRecommender.recipeVecs = new SimpleWordVectorModel(recipeVecs);
                break;
            case DEPLOY:
                RecipeRecommender.recipeVecs = RecipeVectorBuilder.readVectors();
                break;
        }
    }

    private static void loadPreferenceMaps(List<Recipe> favoritedRecipes) {
        if (recipeVectors == null && MODE == DEV_STATE.TRAIN) {
            System.out.println("Initializing recipe vectors for RecipeRecommender...");
            recipeVectors = RecipeVectorizer.getVectors();
        }

        ingredientFrequency = new HashMap<>();
        cuisineFrequency = new HashMap<>();
        for (Recipe r : favoritedRecipes) {
            for (String i : r.ingredients) {
                ingredientFrequency.put(i,
                        ingredientFrequency.containsKey(i) ? ingredientFrequency.get(i) + 1 : 1);
            }
            cuisineFrequency.put(r.cuisine, cuisineFrequency.containsKey(r.cuisine) ? cuisineFrequency.get(r.cuisine) + 1 : 1);
        }
    }

}
