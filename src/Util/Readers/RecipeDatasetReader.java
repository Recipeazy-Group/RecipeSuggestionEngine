package Util.Readers;

import Util.RecipeUtils.Recipe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecipeDatasetReader {
    JSONArray recipeJSON;

    public RecipeDatasetReader(String path) throws Exception {
        BufferedReader bR = new BufferedReader(new FileReader(new File(path)));
        String line;
        LinkedList<String> text = new LinkedList<>();
        while ((line = bR.readLine()) != null) {
            text.add(line);
        }
        bR.close();
        StringBuilder sB = new StringBuilder();
        for(String s : text) {
            sB.append(s);
        }
        recipeJSON = new JSONArray(sB.toString());

    }

    public List<Recipe> getRecipes() {
        return getRecipes(Integer.MAX_VALUE);
    }

    public List<Recipe> getRecipes(int num) {
        List<Recipe> toReturn = new ArrayList<>();
        for (int i = 0; i < recipeJSON.length() && i < num; i++) {
            JSONObject obj = recipeJSON.getJSONObject(i);
            ArrayList<String> ings = new ArrayList<>();
            JSONArray ingArr = obj.getJSONArray("ingredients");
            for (int j = 0; j < ingArr.length(); j++)
                ings.add(ingArr.getString(j));
            Recipe construct = new Recipe(ings);
            toReturn.add(construct);
        }
        return toReturn;
    }
}
