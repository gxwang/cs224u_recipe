package cs224u.ingredients;

import java.util.*;

public abstract class RecipeClassifier {
	
	public RecipeClassifier(){}

	public abstract void train(List<Recipe> recipes);

	public abstract double assignSimilarity(Recipe r1, Recipe r2);
}
