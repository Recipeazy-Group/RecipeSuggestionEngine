package Util.RecipeUtils.ModelTrainer;

import Executables.RecipeVectorizer;
import Util.Math.Vector;
import Util.RecipeUtils.CuisineTool;
import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAnalyzer {
    public static INDArray recipeVectors;
    public static SimpleWordVectorModel recipeVecs;
    public static WordVectorModel foodVec;

    public HashMap<String, Integer> ingredientFrequency;
    public HashMap<CuisineTool.CUISINE, Integer> cuisineFrequency;

    public UserAnalyzer(List<Recipe> favoritedRecipes) {
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

    private void initRecipeVecs() {
        HashMap<String, Vector<Double>> recipeVecs = new HashMap<>();
        for (int i = 0; i < recipeVectors.columns(); i++) {
            INDArray a = recipeVectors.getColumn(i);
            Double[] vals = new Double[a.length()];
            for (int j = 0; j < a.length(); j++) {
                vals[j] = a.getDouble(j);
            }
            recipeVecs.put("" + i, new Vector(vals));
        }
        this.recipeVecs = new SimpleWordVectorModel(recipeVecs);
    }

    public List<Integer> getRecipeRecommendations(int num) {
        if (recipeVecs == null)
            initRecipeVecs();
        // Now construct the user preference vector
        // via the use of a fake, placeholder recipe
        Vector<Double> sum = Vector.zeros(recipeVecs.getItemDimension());
        for (Map.Entry<String, Integer> entry : ingredientFrequency.entrySet()) {
            sum.plusEquals(foodVec.getWordVector(entry.getKey()).scale(entry.getValue()));
        }
        sum.normalize();
        // Now perform the cosine comparison between user vector and all recipes
        var a = recipeVecs.getClosestMatches(sum, num);
        ArrayList<Integer> toReturn = new ArrayList<>();
        for (String s : a) {
            toReturn.add(Integer.parseInt(s));
        }
        return toReturn;
    }


}
