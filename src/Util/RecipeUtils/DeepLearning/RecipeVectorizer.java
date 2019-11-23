package Util.RecipeUtils.DeepLearning;

import Util.Math.Vector;
import Util.RecipeUtils.CuisineTool;
import Util.RecipeUtils.Recipe;
import Util.RecipeUtils.RecipeReader;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeVectorizer {
    public WordVectorModel vectorModel;

    public static INDArray features;

    public RecipeVectorizer() {
    }

    public void setVectors(WordVectorModel wM) {
        vectorModel = wM;
    }

    public static void main(String[] args) throws Exception {
        int num_recipes = 15;
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
        System.out.println(recipeVectorizer.reduce(90).toString());
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
        return PCA.pca(features.transpose(), numParams, true).transpose();
    }

    public void tSNE(){
        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
                .setMaxIter(5).theta(0.5)
                .normalize(false)
                .learningRate(500)
                .useAdaGrad(false)
//                .usePca(false)
                .build();

        //STEP 4: establish the tsne values and save them to a file
        //log.info("Store TSNE Coordinates for Plotting....");
        String outputFile = "target/archive-tmp/tsne-standard-coords.csv";
        (new File(outputFile)).getParentFile().mkdirs();
        List<String> c = new ArrayList<>();
        for(Map.Entry<CuisineTool.CUISINE, String> entry: CuisineTool.CUISINE_NAMES.entrySet())
            c.add(entry.getValue());
        try {
            tsne.plot(features,2, c, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //This tsne will use the weights of the vectors as its matrix, have two dimensions, use the words strings as
        //labels, and be written to the outputFile created on the previous line

    }

}
