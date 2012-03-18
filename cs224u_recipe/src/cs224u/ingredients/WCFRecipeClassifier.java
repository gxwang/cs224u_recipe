package cs224u.ingredients;

import java.util.*;

import Jama.*;

public class WCFRecipeClassifier extends RecipeClassifier {
	SingularValueDecomposition svd;
	int dimensionsToKeep;

	@Override
	public void train(List<Recipe> recipes) {
		HashMap<String, Integer> ingredients = buildIngredientSet(recipes);
		Matrix incidenceMatrix = buildIncidenceMatrix(recipes, ingredients);
		svd = new SingularValueDecomposition(incidenceMatrix);
		Matrix s = svd.getS();
		double totalPower = 0.0;
		for(int i = 0; i < s.getColumnDimension(); i++) {
			totalPower += s.get(i, i);
			if (i == s.getColumnDimension() / 100) {
				System.out.println("Total power after " + i + " singular values: "+ totalPower );
			}
		}

	}

	private Matrix buildIncidenceMatrix(List<Recipe> recipes, HashMap<String, Integer> ingredients) {
		Matrix incidenceMatrix = new Matrix(ingredients.size(), recipes.size());
		for (int j = 0; j < recipes.size(); j++) {
			Recipe recipe = recipes.get(j);
			for (Ingredient ingred : recipe.getStructuredIngredients()) {
				String bI = ingred.getBaseIngredient();
				int i = ingredients.get(bI);
				incidenceMatrix.set(i, j, 1.0);
			}
		}
		System.out.println("Recipe - Ingredient incidence matrix made");
		return incidenceMatrix;
	}

	private HashMap<String, Integer> buildIngredientSet(List<Recipe> recipes) {
		HashMap<String, Integer> ingredients = new HashMap<String, Integer>();
		int index = 0;
		for (Recipe recipe : recipes) {
			for (Ingredient ingredient : recipe.getStructuredIngredients()) {
				String bI = ingredient.getBaseIngredient();
				if (ingredients.containsKey(bI)) {
					ingredients.put(bI, index);
					index++;
				}
			}
		}
		System.out.println("Indexed " + index + " unique ingredients");
		return ingredients;
	}

	@Override
	public double assignSimilarity(Recipe r1, Recipe r2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
