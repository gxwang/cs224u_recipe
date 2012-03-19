package cs224u.ingredients;

import java.io.*;
import java.util.*;

import cs224n.util.Counter;

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
	
	public static String[] classes = {
		"Dessert recipes",
		"American recipes",
		"Inexpensive recipes",
		"Indian recipes",
		"Bread recipes",
		"Soup recipes",
		"Sauce recipes",
		"Italian recipes",
		"Camping recipes"
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecipeClassifier classifier = new CleverRecipeClassifier();
		List<Recipe> allRecipes = getRecipes();
		List<Recipe> trainingSet = getTrainingSet(allRecipes);
		List<Recipe> testSet = getTestSet(allRecipes);
		classifier.train(trainingSet);
		similarityTest(trainingSet, classifier);
		separationTest(trainingSet, classifier);
		classificationTest(testSet, trainingSet, classifier);
	}

	private static List<Recipe> getTestSet(List<Recipe> allRecipes) {
		List<Recipe> testSet = new ArrayList<Recipe>();
		for (int i = 0; i < allRecipes.size(); i++) {
			if (!trainingFilter(i)) {
				testSet.add(allRecipes.get(i));
			}
		}
		return testSet;
	}

	private static void countCategories(List<Recipe> allRecipes) {
		Counter<String> categories = new Counter<String>();
		for (Recipe recipe : allRecipes) {
			for (String category : recipe.getCategories()) {
				categories.incrementCount(category, 1.0);
			}
		}
		System.out.println(categories);
	}

	private static void classificationTest(List<Recipe> testSet, List<Recipe> trainingSet, RecipeClassifier classifier) {
		ArrayList<List<Recipe>> classLists = new ArrayList<List<Recipe>>();
		for (int i = 0; i < classes.length; i++) {
			classLists.add(recipesFromCategory(classes[i], trainingSet));
		}
		for (Recipe recipe : testSet) {
			List<Recipe> testRecipe = new ArrayList<Recipe>();
			testRecipe.add(recipe);
			for (List<Recipe> aClass : classLists) {
				calculateSeparation(testRecipe, aClass, classifier);
			}
		}
	}

	private static List<Recipe> getTrainingSet(List<Recipe> allRecipes) {
		List<Recipe> trainingSet = new ArrayList<Recipe>();
		for (int i = 0; i < allRecipes.size(); i++) {
			if (trainingFilter(i)) {
				trainingSet.add(allRecipes.get(i));
			}
		}
		return trainingSet;
	}

	private static boolean trainingFilter(int i) {
		int modI = i % 10;
		return modI == 1 || modI == 4 || modI == 7;
	}

	@SuppressWarnings("unchecked")
	private static List<Recipe> getRecipes() {
		List<Recipe> recipes = null;
		try {
			String filename = "recipes.txt";
			FileInputStream in = new FileInputStream(filename);
			ObjectInputStream inStream = new ObjectInputStream(in);
			recipes = (List<Recipe>) inStream.readObject();
			inStream.close();
			in.close();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return recipes;
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
					if (sim > 1.0) System.err.println("Similiarity too high!" );
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
