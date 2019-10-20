package Util.RecipeUtils;

import Driver.SuggestionEngine;
import Util.Math.Vector;

import java.util.ArrayList;
import java.util.Collection;

public abstract class IngredientAnalyzer {

    public static Collection<CuisineTool.CUISINE> getTopCuisines(String ingredient){
        return getTopCuisines(SuggestionEngine.vectors.getWordVector(ingredient));
    }

    public static Collection<CuisineTool.CUISINE> getTopCuisines(Vector<Double> ingredientVector){
        Collection<String> cuisines = SuggestionEngine.vectors.getClosestMatches(ingredientVector, 50);
        ArrayList<CuisineTool.CUISINE> toReturn = new ArrayList<>();
        for(String cuisine : cuisines){
            if(CuisineTool.NAME_CUISINES.containsKey(cuisine))
                toReturn.add(CuisineTool.NAME_CUISINES.get(cuisine));
        }
        return toReturn;
    }
}
