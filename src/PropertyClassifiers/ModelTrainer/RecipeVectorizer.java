package PropertyClassifiers.ModelTrainer;

import Util.Math.Vector;
import PropertyClassifiers.CuisineTool;
import Util.Readers.RecipeDatasetReader;
import Util.RecipeUtils.Recipe;
import Util.ResourceRepo;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeVectorizer {
    public WordVectorModel vectorModel;

    public static INDArray features;
    public static final String RECIPE_VECTORS_PATH = ResourceRepo.props.get("RECIPE_VECTORS_PATH");

    public static INDArray getVectors(){
        try (DataInputStream sRead = new DataInputStream(new FileInputStream(new File(RECIPE_VECTORS_PATH)))) {
            return Nd4j.read(sRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RecipeVectorizer() {
    }

    public void setVectors(WordVectorModel wM) {
        vectorModel = wM;
    }

    public static void main(String[] args) throws Exception {
        int num_recipes = 15;
        RecipeVectorizer recipeVectorizer = new RecipeVectorizer();
        recipeVectorizer.setVectors(new SimpleWordVectorModel(ResourceRepo.props.get("FOOD_VECS_PATH")));
        features = Nd4j.zeros(recipeVectorizer.vectorModel.getItemDimension(), num_recipes);
        List<Recipe> recipes = new RecipeDatasetReader(ResourceRepo.props.get("RECIPE_TRAIN_JSON")).getRecipes();
        System.out.println("Got recipes.");
        int count = 0;
        for (Recipe r : recipes) {
            Vector<Double> a = recipeVectorizer.getRecipeVector(r);
            INDArray b = recipeVectorizer.recipeToColVec(a);
            recipeVectorizer.putRecipe(count++, b);
        }
        System.out.println("Writing results...");
        try (DataOutputStream sWrite = new DataOutputStream(new FileOutputStream(new File(RECIPE_VECTORS_PATH)))) {
            Nd4j.write(recipeVectorizer.features, sWrite);
        }
        System.out.println("Writing complete.");
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
                .build();

        //STEP 4: establish the tsne values and save them to a file
        //log.info("Store TSNE Coordinates for Plotting....");
        String outputFile = ResourceRepo.props.get("TSNE_OUTPUT");
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