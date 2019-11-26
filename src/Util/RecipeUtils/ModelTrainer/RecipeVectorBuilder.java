package Util.RecipeUtils.ModelTrainer;

import Util.Math.Vector;
import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class RecipeVectorBuilder {

    public static final String SAVE_LOCATION = ResourceRepo.props.get("RECIPE_VECTOR_SAVE_PATH");

    public static SimpleWordVectorModel readVectors() {
        try {
            ObjectInputStream oI = new ObjectInputStream(new FileInputStream(new File(SAVE_LOCATION)));
            SimpleWordVectorModel toReturn =  (SimpleWordVectorModel) oI.readObject();
            oI.close();
            System.out.println("Read vectors successfully.");
            return toReturn;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Corrupted vectors file. Deleting...");
        }
        System.out.println("Vectors could not be read from " + SAVE_LOCATION);
        return null;
    }

    public static void main(String[] args) throws Exception {
        // This will build a SimpleWordVectorModel for Recipes a.k.a. a SimpleRecipeVectorModel
        System.out.println("Generating recipe vectors.");
        SimpleWordVectorModel vectors = new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH"));
        List<Recipe> dataset = loadDataset(vectors);
        System.out.println("\tDataset loaded");
        HashMap<String, Vector<Double>> toBuild = new HashMap<>();
        for (Recipe r : dataset) {
            toBuild.put("" + r.ID, r.compositeVector(vectors, true));
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
        RecipeDisplaySetReader r = new RecipeDisplaySetReader(ResourceRepo.props.get("RECIPE_DATA_PATH"), vectors);
        return r.getRecipes(true);
    }

}
