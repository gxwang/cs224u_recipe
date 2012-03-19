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
//		similarityTest(trainingSet, classifier);
//		separationTest(trainingSet, classifier);
//		classificationTest(testSet, trainingSet, classifier);
		strictClassificationTest(testSet, trainingSet, classifier);
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
	

	private static void strictClassificationTest(List<Recipe> testSet, List<Recipe> trainingSet, RecipeClassifier classifier) {
		int[] correct = new int[classes.length];
		int[] wrong = new int[classes.length];
		ArrayList<List<Recipe>> classLists = new ArrayList<List<Recipe>>();
		for (int i = 0; i < classes.length; i++) {
			classLists.add(recipesFromCategory(classes[i], trainingSet));
		}
		List<Recipe> prunedTestSet = new ArrayList<Recipe>();
		for (Recipe recipe : testSet) {
			List<String> categories = recipe.getCategories();
			boolean belongsInSet = false;
			for (int i = 0; i < classes.length; i++ ) {
				if (categories.contains(classes[i])) belongsInSet = true;
			}
			if (belongsInSet) prunedTestSet.add(recipe);
		}
		for (Recipe recipe : prunedTestSet) {
			List<Recipe> testRecipe = new ArrayList<Recipe>();
			testRecipe.add(recipe);
			List<Double> seps = new ArrayList<Double>(classes.length);
			for (int i = 0; i < classes.length; i++) {
				List<Recipe> aClass = classLists.get(i);
				seps.add(calculateSeparation(testRecipe, aClass, classifier));
			}
			int maxIndex = findMax(seps);
			if (recipe.getCategories().contains(classes[maxIndex])) correct[maxIndex]++;
			else wrong[maxIndex]++;
		}
		for (int i = 0; i < classes.length; i++) {
			System.out.println(classes[i] + " : " + correct[i] + " / " + (correct[i] + wrong[i]) );
		}
	}

	private static void classificationTest(List<Recipe> testSet, List<Recipe> trainingSet, RecipeClassifier classifier) {
		int fp = 0;
		int tp = 0;
		int fn = 0;
		int tn = 0;
		ArrayList<List<Recipe>> classLists = new ArrayList<List<Recipe>>();
		for (int i = 0; i < classes.length; i++) {
			classLists.add(recipesFromCategory(classes[i], trainingSet));
		}
		for (Recipe recipe : testSet) {
			List<Recipe> testRecipe = new ArrayList<Recipe>();
			testRecipe.add(recipe);
			List<Double> seps = new ArrayList<Double>(classes.length);
			for (int i = 0; i < classes.length; i++) {
				List<Recipe> aClass = classLists.get(i);
				seps.add(calculateSeparation(testRecipe, aClass, classifier));
			}
			int conf = confidence(seps);
			if (conf > 101 && conf != 900) { // confident enough to try to classify
				int maxIndex = findMax(seps);
				if (recipe.getCategories().contains(classes[maxIndex])) tp++;
				else fp ++;
			}
			else {
				boolean isCorrect = true;
				for (int j = 0; j < classes.length; j++) {
					if (recipe.getCategories().contains(classes[j])) {
						isCorrect = false;
					}
				}
				if (isCorrect) tn++;
				else fn++;
			}
		}
		System.out.println("fp :" + fp);
		System.out.println("tp :" + tp);
		System.out.println("fn :" + fn);
		System.out.println("tn :" + tn);
	}
	
	private static int findMax(List<Double> seps) {
		double max = -1;
		int maxIndex = -1;
		for (int i = 0; i < seps.size(); i++) {
			if (seps.get(i) > max || Double.isNaN(seps.get(i))) {
				max = seps.get(i);
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	private static int confidence(List<Double> values) {
		double sum = 0;
		for (double val : values) {
			sum += val;
		}
		for (int i = 0; i < values.size(); i++) {
			values.set(i, values.get(i)/sum);
		}
		double confidence = 0;
		for (double val : values) {
			if (val > 0)
				confidence += - val * Math.log(val) / Math.log(9);
		}
		return (int)(100*Math.pow(9,1 - confidence));
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
