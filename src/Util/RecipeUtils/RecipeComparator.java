package Util.RecipeUtils;

import ServerDriver.SuggestionServer;
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
        Vector<Double> compositeVector = Vector.zeros(SuggestionServer.vectors.getDimension());
        for(String s :  a.ingredients){
            Vector<Double> ingredientVector = SuggestionServer.vectors.getWordVector(s);
            ingredientVector.normalize();
            compositeVector.plusEquals(ingredientVector);
        }
        compositeVector.normalize();
        return compositeVector;
    }
}
