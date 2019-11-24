package Util.RecipeUtils.ModelTrainer;

import Util.RecipeUtils.CuisineTool;
import Util.RecipeUtils.Recipe;

import java.util.HashMap;
import java.util.List;

public class UserAnalyzer {
    public HashMap<String, Integer> ingredientFrequency;
    public HashMap<CuisineTool.CUISINE, Integer> cuisineFrequency;

    public UserAnalyzer(List<Recipe> favoritedRecipes) {
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
