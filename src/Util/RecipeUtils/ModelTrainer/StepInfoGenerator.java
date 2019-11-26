package Util.RecipeUtils.ModelTrainer;

import Util.RecipeUtils.Readers.RecipeDisplaySetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import org.nd4j.shade.guava.base.CharMatcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class StepInfoGenerator {
    public static void main(String[] args) throws Exception {
        SimpleWordVectorModel vectors = new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH"));
        List<Recipe> recipeList = new RecipeDisplaySetReader(ResourceRepo.props.get("RECIPE_DATA_PATH"), vectors).getRecipes(false);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("StepInfo.csv")));
        for (Recipe r : recipeList) {
            int stepCounter = 1;
            for (String s : r.steps) {
                s = s.replaceAll("\"", " inch");
                s = s.replaceAll("\'", " foot");
                boolean asc = CharMatcher.ascii().matchesAllOf(s);
                if (!asc) {
                    for (int i = 0; i < s.length(); i++) {
                        if ((int) s.charAt(i) > 127)
                            s = s.replaceAll("" + s.charAt(i), "");
                    }
                }
                if (s.trim().length() > 0)
                    writer.write("" + r.ID + "," + ("" + stepCounter) + ",\"" + s + "\"\n");
                stepCounter++;
            }
        }
        writer.close();
    }

}

