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
	
	public static String[] cuisines = {
		"Indian recipes",
		"Chinese recipes",
		"Mexican recipes",
		"Japanese recipes",
		"English recipes"
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecipeClassifier classifier = new CleverRecipeClassifier();
		List<Recipe> allRecipes = Recipe.buildRecipes();
		classifier.train(allRecipes);
		similarityTest(allRecipes, classifier);
		separationTest(allRecipes, classifier);	
	}

	private static void separationTest(List<Recipe> allRecipes, RecipeClassifier classifier) {
		for (int i = 0; i < cuisines.length; i++ ) {
			String cuisine1 = cuisines[i];
			for (int j = i + 1; j < cuisines.length; j++) {
				String cuisine2 = cuisines[j];
				List<Recipe> recipes1 = recipesFromCategory(cuisine1, allRecipes);
				List<Recipe> recipes2 = recipesFromCategory(cuisine2, allRecipes);
				double similarity = calculateSeparation(recipes1, recipes2, classifier);
				System.out.println("Simple classifier score on categories " + cuisine1 + " and " + cuisine2 + " : " + similarity);
			}
		}
	}

	private static double calculateSeparation(List<Recipe> recipes1, List<Recipe> recipes2, RecipeClassifier classifier) {
		int count = 0;
		double totalSimilarity = 0.0;
		for (int i = 0; i < recipes1.size(); i++) {
			for (int j = 0; j < recipes2.size(); j++) {
				double sim = classifier.assignSimilarity(recipes1.get(i), recipes2.get(j));
				if (!Double.isNaN(sim)) {
					count++;
					totalSimilarity += sim;
				} 
			}
		}
		return totalSimilarity / count;
	}

	private static void similarityTest(List<Recipe> allRecipes, RecipeClassifier classifier) {
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
