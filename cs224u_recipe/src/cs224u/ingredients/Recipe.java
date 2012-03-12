package cs224u.ingredients;

import java.io.FileReader;
import java.util.*;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The Recipe class is just meant to be a container for a single recipe.
 * @author benjaminholtz
 *
 */
public class Recipe {
	
	private String title;
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
	
	/**
	 * Returns a list of recipes that are mostly parsed
	 */
	public static ArrayList<Recipe> buildRecipes() {
		ArrayList<Recipe> recipes;
		try {

			// Create SAX 2 parser...
			XMLReader xr = XMLReaderFactory.createXMLReader();

			RecipeHandler handler = new RecipeHandler();

			// Set the ContentHandler...
			xr.setContentHandler(handler);

			// Parse the file...
			xr.parse(new InputSource(new FileReader("WikibooksCookbookComplete-20120207011907.xml")));
			recipes = handler.getRecipes();

		}catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
		return recipes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
		return title + "\n================\n" + plaintext;
	}

	/**
	 * This method should do any of the work involved in converting the unstructured
	 * <code>plaintext</code> data into structured fields that we can work with more 
	 * easily.
	 */
	public void structure() {
		structureIngredients();
		structureDirections();
	}
	
	public void structureIngredients() {
		int ingredientsIndex = plaintext.indexOf("== Ingredients");
		ArrayList<String> ingredients = new ArrayList<String>();
		int starIndex = plaintext.indexOf('*', ingredientsIndex + 1);
		int newlineIndex = plaintext.indexOf('\n', starIndex + 1);
		while (starIndex < newlineIndex && starIndex != -1) {
			ingredients.add(plaintext.substring(starIndex + 1, newlineIndex));
			starIndex = plaintext.indexOf('*', starIndex + 1);
			newlineIndex = plaintext.indexOf('\n', newlineIndex + 1);
		}
		this.ingredients = ingredients;
	}
	
	public void structureDirections() {
		
	}

	/**
	 * 
	 * @return <code>True</code> if the <code>plaintext</code> is a recipe (as opposed
	 * to an ingredient page or category)
	 */
	public boolean isRecipe() {
		return plaintext.contains("{{recipe");
	}
	
	
	
}
