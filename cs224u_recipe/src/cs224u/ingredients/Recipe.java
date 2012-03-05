package cs224u.ingredients;

import java.util.*;

/**
 * The Recipe class is just meant to be a container for a single recipe.
 * @author benjaminholtz
 *
 */
public class Recipe {
	
	private ArrayList<String> ingredients;
	private ArrayList<String> directions;
	private String plaintext;

	public Recipe() {
	}

	public Recipe(ArrayList<String> ingredients, ArrayList<String> directions,
			String plaintext) {
		this.ingredients = ingredients;
		this.directions = directions;
		this.plaintext = plaintext;
	}

	public ArrayList<String> getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}
	
	public ArrayList<String> getDirections() {
		return directions;
	}
	
	public void setDirections(ArrayList<String> directions) {
		this.directions = directions;
	}
	
	public String getPlaintext() {
		return plaintext;
	}
	
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	@Override
	public String toString() {
		return plaintext;
	}
	
	
	
}
