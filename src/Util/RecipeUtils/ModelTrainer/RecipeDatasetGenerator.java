package Util.RecipeUtils.ModelTrainer;

import ServerDriver.SuggestionServer;
import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class RecipeDatasetGenerator {
    public static void main(String[] args) throws Exception {

        SimpleWordVectorModel vectors = new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH"));
        List<Recipe> recipeList = new RecipeDisplaySetReader(ResourceRepo.props.get("RECIPE_DATA_PATH"), vectors).getRecipes(true);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("recipeDataset.csv")));
        for(Recipe r : recipeList){
            for(String s : r.ingredients){
                if(s.trim().length()>0)
                writer.write(""+r.ID + "," + s + "\n");
            }
        }
        writer.close();
    }
}
