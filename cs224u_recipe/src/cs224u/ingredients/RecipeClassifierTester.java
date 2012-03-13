package cs224u.ingredients;

import java.util.*;

public class RecipeClassifierTester {

	public static String[] categories = {
			"Bread recipes",
			"Dumpling recipes",
			"Fried recipes",
			"Beverage recipes",
			"Flatbread recipes",
			"French recipes",
			"Dessert recipes",
			"Soup recipes",
			"Pancake recipes",
			"Indian recipes",
			"Fried rice recipes",
			"Camping recipes"
			
	};
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecipeClassifier classifier = new SimpleRecipeClassifier();
		List<Recipe> allRecipes = Recipe.buildRecipes();
		classifier.train(allRecipes);
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
				double sim = classifier.assignSimilarity(recipes.get(i), recipes.get(j));
				if (!Double.isNaN(sim)) {
					count++;
					totalSimilarity += sim;
				} 
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
