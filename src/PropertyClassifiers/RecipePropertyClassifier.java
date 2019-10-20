package PropertyClassifiers;

import Driver.SuggestionEngine;
import Util.Math.Vector;
import Util.RecipeUtils.CuisineTool;

import static Util.RecipeUtils.CuisineTool.CUISINE;

import Util.RecipeUtils.Recipe;

import java.util.HashMap;
import java.util.Map;


public class RecipePropertyClassifier {

    public static CUISINE getDominantCuisine(Recipe evaluate) {
        Map<CUISINE, Double> correlations = getCuisineCorrelations(evaluate);
        CUISINE best = CUISINE.NORTH_AMERICAN;
        for (CUISINE c : CUISINE.values()) {
            best = correlations.get(c) > correlations.get(best) ? c : best;
        }
        return best;
    }

    public static Map<CUISINE, Double> getCuisineCorrelations(Recipe evaluate) {
        Map<CUISINE, Double> toReturn = new HashMap<>();
        for (CUISINE c : CUISINE.values()) {
            if (!toReturn.containsKey(c))
                toReturn.put(c, 0d);
            for (String ing : evaluate.ingredients) {
                toReturn.put(c,
                        toReturn.get(c) + SuggestionEngine.vectors.calcWordSimilarity(ing, CuisineTool.CUISINE_NAMES.get(c)));
            }
        }
        Vector<Double> sums = new Vector<>(toReturn.values());
        sums.normalize();
        int i = 0;
        for (CUISINE c : CUISINE.values()) {
            toReturn.put(c, sums.at(i));
            i++;
        }
        return toReturn;
    }


}
