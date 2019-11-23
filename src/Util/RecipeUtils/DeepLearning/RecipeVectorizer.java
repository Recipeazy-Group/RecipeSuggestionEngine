package Util.RecipeUtils.DeepLearning;

import Util.Math.Vector;
import Util.RecipeUtils.Recipe;
import Util.WordVectorization.SimpleWordVectorModel;
import Util.WordVectorization.WordVectorModel;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class RecipeVectorizer {
    public WordVectorModel vectorModel;

    public void setVectors(WordVectorModel wM){
        vectorModel = wM;
    }

    public static void main(String[] args) {
    RecipeVectorizer r = new RecipeVectorizer();
    r.setVectors(new SimpleWordVectorModel("RecipeSuggestionEngine/lib/models/foodVecs.json"));

    }

    public Vector<Double> getRecipeVector(Recipe r){
        Vector<Double> toReturn = new Vector<>(vectorModel.getDimension());
        for(String s : r.ingredients){
            Vector<Double> a = vectorModel.getWordVector(s);
            toReturn.add(a);
        }
        return toReturn.getNormalized();
    }

    public INDArray recipeToColVec(Vector<Double> vec){
        double[] d = new double[vec.getSize()];
        for(int i=0; i<vec.getSize(); i++){
            d[i] = vec.at(i);
        }
        INDArray col = Nd4j.create(d, new int[]{1});
        return col;
    }



}
