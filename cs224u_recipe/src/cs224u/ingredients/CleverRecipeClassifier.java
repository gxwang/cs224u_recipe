package cs224u.ingredients;

import java.util.List;

import cs224n.util.Counter;

public class CleverRecipeClassifier extends RecipeClassifier {

	Counter<String> unitWeight;

	public CleverRecipeClassifier() {
	}

	@Override
	public void train(List<Recipe> recipes) {
		unitWeight = new Counter<String>();
		for (Recipe recipe : recipes) {
			for (Ingredient ingredient : recipe.getStructuredIngredients()) {
				String unit = ingredient.getQuant().getUnit();
				if (!unitWeight.containsKey(unit)) {
					unitWeight.setCount(unit, 1.0);
				}
			}
		}

	}

	@Override
	public double assignSimilarity(Recipe r1, Recipe r2) {
		List<Ingredient> is1 = r1.getStructuredIngredients();
		List<Ingredient> is2 = r2.getStructuredIngredients();
		Counter<String> c1 = new Counter<String>();
		Counter<String> c2 = new Counter<String>();
		for (Ingredient ingredient : is1) {
			String ingred = ingredient.getBaseIngredient();
			double quantity = ingredient.getQuant().getQuantity();
			double weight = unitWeight.getCount(ingredient.getQuant().getUnit());
			if (weight == 0) weight++;
			c1.incrementCount(ingred, weight * quantity);   
		}
		for (Ingredient ingredient : is2) {
			String ingred = ingredient.getBaseIngredient();
			double quantity = ingredient.getQuant().getQuantity();
			double weight = unitWeight.getCount(ingredient.getQuant().getUnit());
			if (weight == 0) weight++;
			c2.incrementCount(ingred, 1.0);//weight * quantity);  
		}
		return c1.cosineSimilarity(c2);
	}

}
