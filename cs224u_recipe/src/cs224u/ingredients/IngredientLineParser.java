package cs224u.ingredients;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class IngredientLineParser {

	private static String MEASUREMENTS_FILE = "measurements";
	private static String QUANTITY_REGEX = "(\\d++(?! */))? *-? *(?:(\\d+) */ *(\\d+))?";
	private static String[] test = {"2 pints milk","4 garlic cloves"};
	private String measurementRegex;
	
	public IngredientLineParser(){
		measurementRegex = "";
		loadMeasurements();
	}
	
	public String parseLine(String line){
		
		String qty = "";
		String units = "";
		String ingredient = "";
		
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if (token.matches(QUANTITY_REGEX)) qty += token+"_";
			else if (token.matches(measurementRegex)) units += token +"_";
			else ingredient += " " + token;
		}
		if (!units.isEmpty()) units = units.substring(0, units.length()-1);
		
		return qty+units+ingredient;
	}
	
	private void loadMeasurements(){
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(MEASUREMENTS_FILE));
			while ((line = in.readLine()) != null) {
				measurementRegex += line;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IngredientLineParser lnParse= new IngredientLineParser();
		for (int i = 0; i < test.length; i++){
			System.out.println(test[i] + " -> " + lnParse.parseLine(test[i]));
		}

	}

}
