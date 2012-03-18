package cs224u.ingredients;

import java.util.*;

public class WCFRecipeClassifier extends RecipeClassifier {

	@Override
	public void train(List<Recipe> recipes) {
		Set<String> ingredients = new HashSet<String>();
		for (Recipe recipe : recipes) {
			for (Ingredient ingredient : recipe.getStructuredIngredients()) {
				ingredients.add(ingredient.getBaseIngredient());
			}
		}

	}

	@Override
	public double assignSimilarity(Recipe r1, Recipe r2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
