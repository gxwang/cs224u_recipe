package cs224u.ingredients;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;
import java.util.*;

public class RecipeHandler extends DefaultHandler{
	
	// Arraylist of recipes we want to store
	private ArrayList<Recipe> recipes = new ArrayList<Recipe>();
	
	// Local object for collecting data as we read through data
	private Recipe curRecipe = new Recipe();
	
	private CharArrayWriter contents = new CharArrayWriter();
	
	public void startDocument( ) throws SAXException {
		System.out.println( "SAX Event: START DOCUMENT" );
	}

	public void endDocument( ) throws SAXException {
		System.out.println( "SAX Event: END DOCUMENT" );
	}

	public void startElement( String namespaceURI, String localName, String qName, Attributes attr ) throws SAXException {
		contents.reset();
		// If the element is a new page, then we want to make a new recipe object and toss it in the arraylist
		if (localName.equals("page")) {
			curRecipe = new Recipe();
		}
	}

	public void endElement( String namespaceURI, String localName, String qName ) throws SAXException {
		if (localName.equals("page")) {
			curRecipe.setPlaintext(contents.toString());
			if (curRecipe.isIngredient()) {
				curRecipe.process();
				recipes.add(curRecipe);
			}
		}
		else if (localName.equals("title")) {
			curRecipe.setTitle(contents.toString());
		}
			
	}

	public void characters( char[] ch, int start, int length ) throws SAXException {
		contents.write(ch, start, length);
	}
	
	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}

	public static void main( String[] argv ){

		System.out.println( "RecipeHandler SAX Events:" );
		try {

			// Create SAX 2 parser...
			XMLReader xr = XMLReaderFactory.createXMLReader();
			
			RecipeHandler handler = new RecipeHandler();
			
			// Set the ContentHandler...
			xr.setContentHandler(handler);

			// Parse the file...
			xr.parse(new InputSource(new FileReader("WikibooksCookbookComplete-20120207011907.xml")));
			ArrayList<Recipe> recipes = handler.getRecipes();
			System.out.println("" + recipes.size() + " total recipes");
			System.out.println(recipes.get(0).getIngredients());
			ArrayList<String> ingreds;
			String ingred;
			Random rand = new Random();
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter("sampleIngreds.txt"));
				int count = 0;
				int i = 0;
				while (count < 100 && i < recipes.size()) {
					ingreds = recipes.get(i).getIngredients();
					if (ingreds.size() != 0) {
						ingred = ingreds.get(rand.nextInt(ingreds.size()));
						bw.write(ingred); bw.newLine();
						System.out.println(ingred);
						count++;
					}
					i++;
				}	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				bw.flush();
				bw.close();
			}
		}catch ( Exception e ) {
			e.printStackTrace();
		}

	}

}
