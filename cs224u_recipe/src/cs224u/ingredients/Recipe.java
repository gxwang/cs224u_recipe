package cs224u.ingredients;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
/**
 * The Recipe class is just meant to be a container for a single recipe.
 * @author benjaminholtz
 *
 */
public class Recipe implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4045383009678614631L;
	private String title;
	private ArrayList<String> ingredients;
	private ArrayList<String> directions;
	private ArrayList<String> categories;
	private String plaintext;
	private ArrayList<Ingredient>  structuredIngredients;

	public Recipe() {
		structuredIngredients = new ArrayList<Ingredient>();
	}

	public Recipe(ArrayList<String> ingredients, ArrayList<String> directions,
			String plaintext, ArrayList<String> categories) {
		this.ingredients = ingredients;
		this.directions = directions;
		this.plaintext = plaintext;
		this.categories = categories;
		structuredIngredients = new ArrayList<Ingredient>();
		
	}
	
	public ArrayList<Ingredient> getStructuredIngredients() {
		return structuredIngredients;
	}

	public void setStructuredIngredients(ArrayList<Ingredient> structuredIngredients) {
		this.structuredIngredients = structuredIngredients;
	}

	/**
	 * Returns a list of recipes that are mostly parsed
	 * @throws IOException 
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

	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
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
	 * @param ilp 
	 */
	public void structure(IngredientLineParser ilp) {
		structureIngredients(ilp);
		structureDirections();
		structureCategories();
	}
	
	/**
	 * Every recipe belongs to a category. We want to grab as many of those categories as possible.
	 */
	private void structureCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		int endIndex = 0;
		while (true) {
			int curIndex = plaintext.indexOf("[[Category:", endIndex);
			if (curIndex == -1) break;
			int colonIndex = plaintext.indexOf(':', curIndex);
			int pipeIndex = plaintext.indexOf('|', colonIndex);
			int braceIndex = plaintext.indexOf("]]", colonIndex);
			endIndex = (pipeIndex == -1 || braceIndex < pipeIndex) ? braceIndex : pipeIndex;
			String category = plaintext.substring(colonIndex + 1, endIndex);
			categories.add(category);
		}
		this.categories = categories;
	}

	/**
	 * Find all ingredients! Current uses the following approach: find the line that is
	 * just "== Ingredients ==" and then pull every subsequent line that starts with an asterisk
	 * We need to also grab lines that start with a colon, and maybe check for other indicators
	 * @param ilp 
	 */
	private void structureIngredients(IngredientLineParser ilp) {
		
		int ingredientsIndex = Math.max(plaintext.indexOf("==Ingredients"), plaintext.indexOf("' Ingredients"));
		ArrayList<String> ingredients = new ArrayList<String>();
		char ch = '*';
		int starIndex = plaintext.indexOf(ch, ingredientsIndex + 1);
		if (starIndex == -1) {
			ch = ':';
			starIndex = plaintext.indexOf(ch, ingredientsIndex + 1);
		}
		int newlineIndex = plaintext.indexOf('\n', starIndex + 1);
		while (starIndex < newlineIndex && starIndex != -1) {
			String ingredientLine = plaintext.substring(starIndex + 1, newlineIndex);
			
			starIndex = plaintext.indexOf(ch, starIndex + 1);
			ingredientLine = ingredientLine.trim();
			if (ingredientLine.length() > 3 && ingredientLine.length() < 160 && !ingredientLine.contains("[[Category:")) {
				if (ingredientLine.charAt(0) != '(' || ingredientLine.charAt(ingredientLine.length() -1 ) != ')') { //don't bother with lines all in parens
					ingredients.add(ingredientLine);
					structuredIngredients.add(processLine(ingredientLine, ilp));
					//System.out.println(ingredientLine);
				}
			}
			//starIndex = plaintext.indexOf(ch, starIndex + 1);
			newlineIndex = plaintext.indexOf('\n', newlineIndex + 1);
		}
		this.ingredients = ingredients;
	}
	
	public void structureDirections() {
		
	}
	
	/**
	 * Processes a line of recipe text from a dirty to clean form
	 * @param ilp 
	 */
	private Ingredient processLine(String ingredLine, IngredientLineParser ilp){
		Ingredient ingred = ilp.parseLine(ingredLine);
		return ingred;
	}
	
	/**
	 * 
	 * @return <code>True</code> if the <code>plaintext</code> is a recipe (as opposed
	 * to an ingredient page or category)
	 */
	public boolean isRecipe() {
		return plaintext.contains("{{recipe") && plaintext.contains("ngredients");
	}
	
	/**
	 * @deprecated We probably are not going to use this
	 * and use instead the in category average similarity measure
	 * 
	 * @param r1 - A recipe we want to compare 
	 * @param r2 - The recipe we want to compare to
	 * @return A value in [0,1] that represents how similar the recipes <strong>r1</strong> and 
	 *  <strong>r2</strong> are. A value of 1 should be given for the same recipe and a value of 0 
	 *  should be assigned for recipes with no relation.
	 */
	public static double similarity(Recipe r1, Recipe r2) {
		return 0;
	}
	
	public static void main(String[] args) {
		List<Recipe> recipes = Recipe.buildRecipes();
		try {
			String filename = "recipes2.txt";
			FileOutputStream out = new FileOutputStream(filename);
			ObjectOutputStream outStream = new ObjectOutputStream(out);
			outStream.writeObject(recipes);
			outStream.close();
			out.close();
			System.out.println("Fin.");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
