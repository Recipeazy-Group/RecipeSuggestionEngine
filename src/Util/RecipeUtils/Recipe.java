package Util.RecipeUtils;

import java.util.ArrayList;

public class Recipe {
    public int ID;
    public CuisineTool.CUISINE cuisine;
    public ArrayList<String> ingredients;

    public Recipe(){
        ingredients = new ArrayList<>();

    }

    public Recipe(ArrayList<String> ingredients){
        this();
        for(String ing : ingredients){
            this.ingredients.add(ing);
        }
    }
}
