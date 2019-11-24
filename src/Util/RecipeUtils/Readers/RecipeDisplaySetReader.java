package Util.RecipeUtils.Readers;

import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class RecipeDisplaySetReader {
    private JSONArray recipeJSON;
    private HashSet<String> ingredientVocab;

    public RecipeDisplaySetReader(String path, SimpleWordVectorModel vectors) throws Exception {
        BufferedReader bR = new BufferedReader(new FileReader(new File(path)));
        String line;
        LinkedList<String> text = new LinkedList<>();
        while ((line = bR.readLine()) != null) {
            text.add(line);
        }
        bR.close();
        StringBuilder sB = new StringBuilder();
        for (String s : text) {
            sB.append(s);
        }
        recipeJSON = new JSONArray(sB.toString());
        ingredientVocab = new HashSet<>();
        ingredientVocab = vectors.getWordVocab();
    }

    public List<Recipe> getRecipes() {
        return getRecipes(Integer.MAX_VALUE);
    }

    public List<Recipe> getRecipes(int num) {
        ArrayList<Recipe> toReturn = new ArrayList<>();
        for (int i = 0; i < num && i < recipeJSON.length(); i++) {
            ArrayList<String> ings = new ArrayList<>();
            JSONArray in = recipeJSON.getJSONObject(i).getJSONArray("ingredients");
            for (int j = 0; j < in.length(); j++) {
                String raw = (in.getString(j));
                StringTokenizer sT = new StringTokenizer(raw);
                String longestPossibleIng = "";
                while (sT.hasMoreTokens()) {
                    String token = sT.nextToken();
                    if (ingredientVocab.contains(longestPossibleIng + token)) {
                        longestPossibleIng += token;
                    }
                    else if(ingredientVocab.contains(token) && token.length() > longestPossibleIng.length())
                    {
                        longestPossibleIng = token;
                    }
                }
                ings.add(longestPossibleIng);
            }
            toReturn.add(new Recipe(ings, i, recipeJSON.getJSONObject(i).getString("title")));
        }
        return toReturn;
    }

}
