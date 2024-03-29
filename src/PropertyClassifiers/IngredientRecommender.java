package PropertyClassifiers;

import ServerDriver.SuggestionServer;
import Util.WordVectorization.WordVectorModel;

import java.util.*;

public abstract class IngredientRecommender {
    public static Collection<String> getRecommendedIngredients(String ingredient, int numSuggestions) {
        WordVectorModel vectors = SuggestionServer.vectors;
        if (!vectors.contains(ingredient)) {
            return new LinkedList<>();
        }
        // We will get extra ingredients so we can filter identical ones / unrelated items out
        Collection<String> r = vectors.getClosestMatches(ingredient, numSuggestions * 5);
        LinkedList<String> toReturn = new LinkedList<>();
        for (String s : r) {
            if (s.contains(ingredient) || ingredient.contains(s)) {
                // if this executes, means that the recommended ingredient is the same.. something like "apples" and "granny smith apples"
                continue; // so we will continue and avoid sending it to user
            }
            if (CuisineTool.NAME_CUISINES.containsKey(s)) {
                // this means that the recommended item is a cuisine name, which happens in some edge cases
                // we don't want to recommend a cuisine, so avoid sending this via continue
                continue;
            }
            toReturn.add(s);
            if (toReturn.size() == numSuggestions) {
                break;
            }
        }
        return toReturn;
    }

    public static String parseIngredientText(String ingName, HashSet<String> ingredientVocab) {
        StringTokenizer sT = new StringTokenizer(ingName);
        String longestPossibleIng = "";
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
        return longestPossibleIng;
    }
}
