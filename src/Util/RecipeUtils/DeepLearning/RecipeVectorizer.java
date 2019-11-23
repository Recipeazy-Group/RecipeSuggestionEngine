package Util.RecipeUtils.DeepLearning;

import Util.Math.Vector;
import Util.RecipeUtils.Recipe;
import Util.RecipeUtils.RecipeReader;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

public class RecipeVectorizer {
    public WordVectorModel vectorModel;

    public static INDArray features;

    public RecipeVectorizer() {
    }

    public void setVectors(WordVectorModel wM) {
        vectorModel = wM;
    }

    public static void main(String[] args) throws Exception {
        int num_recipes = 10;
        RecipeVectorizer recipeVectorizer = new RecipeVectorizer();
        recipeVectorizer.setVectors(new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json"));
        features = Nd4j.zeros(recipeVectorizer.vectorModel.getItemDimension(), num_recipes);
        List<Recipe> recipes = new RecipeReader("RecipeSuggestionEngine/lib/models/train.json").getRecipes(num_recipes);
        int count = 0;
        for (Recipe r : recipes) {
            var a = recipeVectorizer.getRecipeVector(r);
            var b = recipeVectorizer.recipeToColVec(a);
            recipeVectorizer.putRecipe(count++, b);
        }
        System.out.println(recipeVectorizer.reduce(5).toString());

    }

    public Vector<Double> getRecipeVector(Recipe r) {

        Vector<Double> toReturn = Vector.zeros(vectorModel.getItemDimension());
        for (String s : r.ingredients) {
            Vector<Double> a = vectorModel.getWordVector(s);
            toReturn.plusEquals(a);
        }
        return toReturn.getNormalized();
    }

    public INDArray recipeToColVec(Vector<Double> vec) {
        double[] d = new double[vec.getSize()];
        for (int i = 0; i < vec.getSize(); i++) {
            d[i] = vec.at(i);
        }
        INDArray col = Nd4j.create(d);
        return col;
    }

    public void putRecipe(int index, INDArray recipeCol) {
        features.putColumn(index, recipeCol);
    }

    public INDArray reduce(int numParams) {
        return PCA.pca(features, numParams, true);
    }
}
