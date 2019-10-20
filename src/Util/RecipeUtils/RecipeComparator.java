package Util.RecipeUtils;

import Driver.SuggestionEngine;
import Util.Math.Vector;

public class RecipeComparator {

    public double inverseCosine(Recipe a, Recipe b){
        return 1-cosineSimilarity(a, b);
    }

    public double cosineSimilarity(Recipe a, Recipe b){
        Vector<Double> comA = getCompositeRecipeVector(a);
        Vector<Double> comB = getCompositeRecipeVector(b);
        double cosDistance = Vector.dotProduct(comA, comB) / (comA.getNorm() * comB.getNorm());
        return cosDistance;
    }

    public Vector<Double> getCompositeRecipeVector(Recipe a){
        Vector<Double> compositeVector = new Vector<>(SuggestionEngine.vectors.getDimension());
        for(String s :  a.ingredients){
            Vector<Double> ingredientVector = SuggestionEngine.vectors.getWordVector(s);
            ingredientVector.normalize();
            compositeVector.plusEquals(ingredientVector);
        }
        compositeVector.normalize();
        return compositeVector;
    }
}
