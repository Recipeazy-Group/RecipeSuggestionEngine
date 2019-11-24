package Util.RecipeUtils.ModelTrainer;

import ServerDriver.SuggestionServer;
import Util.Math.Vector;
import Util.RecipeUtils.CuisineTool;
import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import jdk.jshell.SourceCodeAnalysis;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

public abstract class RecipeRecommender {
    public static INDArray recipeVectors;
    public static SimpleWordVectorModel recipeVecs;
    public static WordVectorModel foodVec;

    private static HashMap<Integer, Recipe> recipeDB;

    private transient static HashMap<String, Integer> ingredientFrequency;
    private transient static HashMap<CuisineTool.CUISINE, Integer> cuisineFrequency;

    static {
        System.out.println("Initializing recipe database.");
        recipeDB = new HashMap<>();
        try {
            List<Recipe> recipeList = new RecipeDisplaySetReader(ResourceRepo.props.get("RECIPE_DATA_PATH"), (SimpleWordVectorModel) SuggestionServer.vectors).getRecipes();
            for (Recipe r : recipeList) {
                recipeDB.put(r.ID, r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private enum DEV_STATE {
        TRAIN, DEPLOY
    }

    public static List<Integer> getRecipeRecommendationsFromID(List<Integer> userRecipeIDs, int num) {
        List<Recipe> recipes = new LinkedList<>();
        for (int i : userRecipeIDs) {
            recipes.add(recipeDB.get(i));
        }
        return getRecipeRecommendations(recipes, num);
    }

    public static List<Integer> getRecipeRecommendations(List<Recipe> userRecipes, int num) {
        loadPreferenceMaps(userRecipes);
        if (recipeVecs == null)
            initRecipeVecs(DEV_STATE.DEPLOY);
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
        if (recipeVectors == null) {
            System.out.println("Initializing recipe vectors for UserAnalyzer...");
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
