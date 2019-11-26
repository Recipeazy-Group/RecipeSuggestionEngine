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

    public List<Recipe> getRecipes(boolean dictionaryLookup) {
        return getRecipes(Integer.MAX_VALUE, dictionaryLookup);
    }

    public List<Recipe> getRecipes(int num, boolean dictionaryLookup) {
        LinkedList<Recipe> toReturn = new LinkedList<>();
        for (int i = 0; i < num && i < recipeJSON.length(); i++) {
            ArrayList<String> ings = new ArrayList<>();
            if (!recipeJSON.getJSONObject(i).has("ingredients"))
                continue;
            JSONArray in = recipeJSON.getJSONObject(i).getJSONArray("ingredients");
            for (int j = 0; j < in.length(); j++) {
                String raw = (in.getString(j));
                StringTokenizer sT = new StringTokenizer(raw);
                String longestPossibleIng = "";
                if (dictionaryLookup) {
                    while (sT.hasMoreTokens()) {
                        String tok = sT.nextToken();

                        ArrayList<String> processedToks = new ArrayList<>();
                        processedToks.add(tok);
                        if (tok.endsWith("s"))
                            processedToks.add(tok.substring(0, tok.length() - 1));
                        for (String token : processedToks) {
                            if (ingredientVocab.contains(longestPossibleIng + token)) {
                                longestPossibleIng += token;
                            } else if (ingredientVocab.contains(token) && token.length() > longestPossibleIng.length()) {
                                longestPossibleIng = token;
                            }
                        }
                    }
                }
                else longestPossibleIng = raw;
                ings.add(longestPossibleIng);
            }
            Recipe toAdd = new Recipe(ings, i+1, recipeJSON.getJSONObject(i).getString("title"));
            if(!dictionaryLookup){
                JSONArray arr = recipeJSON.getJSONObject(i).getJSONArray("directions");
                for(int j = 0; j<arr.length(); j++){
                    toAdd.steps.add(arr.getString(j));
                }
            }
            toReturn.add(toAdd);
        }
        return toReturn;
    }

}
