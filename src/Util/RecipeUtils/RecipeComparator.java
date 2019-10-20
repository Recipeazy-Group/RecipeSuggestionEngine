package Util.RecipeUtils;

import Driver.SuggestionEngine;
import Util.Math.Vector;

public class RecipeComparator {

    public int compare(Recipe a, Recipe b){

    }

    public double cosineSimilarity(Recipe a, Recipe b){

    }

    public Vector<Double> getCompositeRecipeVector(Recipe a){
        Vector<Double> compositeVector = new Vector<>(SuggestionEngine.vectors.getDimension());
        for(String s :  a.ingredients){
            Vector<Double> ingredientVector = SuggestionEngine.vectors.getWordVector(s);
            compositeVector.plusEquals(ingredientVector.normalize());
        }
    }
}
