/*
 * Bag of words
 */
package cs224u.ingredients;

import java.util.List;
import cs224n.util.Counter;

public class SimpleRecipeClassifier extends RecipeClassifier {

	@Override
	public void train(List<Recipe> recipes) {
	}

	@Override
	public double assignSimilarity(Recipe r1, Recipe r2) {
		List<String> is1 = r1.getIngredients();
		List<String> is2 = r2.getIngredients();
		Counter<String> c1 = new Counter<String>();
		Counter<String> c2 = new Counter<String>();
		for (String line : is1) {
			for (String ingred : line.split(" ")) {
				c1.incrementCount(ingred, 1.0);
			}
		}
		for (String line : is2) {
			for (String ingred : line.split(" ")) {
				c2.incrementCount(ingred, 1.0);
			}
		}
		return c1.cosineSimilarity(c2);
	}

}
