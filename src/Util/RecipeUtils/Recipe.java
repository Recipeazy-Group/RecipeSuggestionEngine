package Util.RecipeUtils;

import Util.Math.Vector;
import Util.WordVectorization.SimpleWordVectorModel;

import java.util.ArrayList;

public class Recipe {
    public int ID;
    public CuisineTool.CUISINE cuisine;
    public ArrayList<String> ingredients;
    public String title;

    public Recipe() {
        ingredients = new ArrayList<>();

    }

    public Recipe(ArrayList<String> ingredients) {
        this();
        for (String ing : ingredients) {
            this.ingredients.add(ing);
        }
    }

    public Recipe(ArrayList<String> ingredients, int ID) {
        this(ingredients);
        this.ID = ID;
    }

    public Recipe(ArrayList<String> ingredients, int ID, String title) {
        this(ingredients, ID);
        this.title = title;
    }

    public Vector<Double> compositeVector(SimpleWordVectorModel vectors, boolean normalize) {
        Vector<Double> build = Vector.zeros(vectors.getItemDimension());
        for (String s : ingredients) {
            build.plusEquals(vectors.getWordVector(s));
        }
        if (normalize)
            build.normalize();
        return build;
    }
}
