package Util.RecipeUtils;

import java.util.ArrayList;

public class Recipe {

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
