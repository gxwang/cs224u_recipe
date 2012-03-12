package cs224u.ingredients;

import java.util.*;

public class RecipeClassifierTester {

	public static String[] categories = {
			"Bread recipes"
	};
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecipeClassifier classifier = new SimpleRecipeClassifier();
		List<Recipe> allRecipes = Recipe.buildRecipes();
		for (int i = 0; i < categories.length; i++ ) {
			String category = categories[i];
			List<Recipe> recipes = recipesFromCategory(category, allRecipes);
			double similarity = calculateSimilarity(recipes, classifier);
			System.out.println("Simple classifier score on " + category + " : " + similarity);
		}
	}

	private static double calculateSimilarity(List<Recipe> recipes, RecipeClassifier classifier) {
		int count = 0;
		double totalSimilarity = 0.0;
		for (int i = 0; i < recipes.size(); i++) {
			for (int j = i + 1; j < recipes.size(); j++) {
				count++;
				totalSimilarity += classifier.assignSimilarity(recipes.get(i), recipes.get(j));
			}
		}
		return totalSimilarity / count;
	}
	
	private static List<Recipe> recipesFromCategory(String category, List<Recipe> allRecipes) {
		List<Recipe> recipes = new ArrayList<Recipe>();
		for (Recipe recipe : allRecipes) {
			if (recipe.getCategories().contains(category)) {
				recipes.add(recipe);
			}
		}
		return recipes;
	}
}
