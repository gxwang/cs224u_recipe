package cs224u.ingredients;

import java.util.List;

public class SimpleRecipeClassifier extends RecipeClassifier {

	@Override
	public void train(List<Recipe> recipes) {
	}

	@Override
	public double assignSimilarity(Recipe r1, Recipe r2) {
		return 0;
	}

}
