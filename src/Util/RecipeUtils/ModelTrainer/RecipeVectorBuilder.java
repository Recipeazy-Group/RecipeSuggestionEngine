package Util.RecipeUtils.ModelTrainer;

import Util.Math.Vector;
import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

public class RecipeVectorBuilder {

    public static final String SAVE_LOCATION = "RecipeSuggestionEngine/lib/models/recipeVectors.bin";

    public static void main(String[] args) throws Exception {
        // This will build a SimpleWordVectorModel for Recipes a.k.a. a SimpleRecipeVectorModel
        System.out.println("Generating recipe vectors.");
        SimpleWordVectorModel vectors = new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json");
        List<Recipe> dataset = loadDataset(vectors);
        System.out.println("\tDataset loaded");
        HashMap<String, Vector<Double>> toBuild = new HashMap<>();
        for(Recipe r : dataset){
            toBuild.put(""+r.ID, r.compositeVector(vectors, true));
        }
        System.out.println("\tBuilt composite vectors.");
        SimpleWordVectorModel build = new SimpleWordVectorModel(toBuild);
        System.out.println("\tBuild vector model.");
        ObjectOutputStream oO = new ObjectOutputStream(new FileOutputStream(new File(SAVE_LOCATION)));
        oO.writeObject(build);
        oO.close();
        System.out.println("\tWrote recipe vector object.");
        System.out.println("RecipeVectorBuilder complete. You can now use the model saved at:\n" + SAVE_LOCATION);
    }

    public static List<Recipe> loadDataset(SimpleWordVectorModel vectors) throws Exception {
        RecipeDisplaySetReader r = new RecipeDisplaySetReader("RecipeSuggestionEngine/lib/models/recipes.json", vectors);
        return r.getRecipes(15);
    }

}
