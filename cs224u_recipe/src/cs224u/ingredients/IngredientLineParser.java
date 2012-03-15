
package cs224u.ingredients;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.PennTreebankTokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Class for parsing a given line from the ingredient list into Ingredient objects.
 */

public class IngredientLineParser {

	private static String MEASUREMENTS_FILE = "measurements";
	private static String TEST_FILE = "sampleIngreds.txt";
	private String measurementRegex = "";
	private static ArrayList<String> testLines = new ArrayList<String>();
	private LexicalizedParser lexParser;
	private GrammaticalStructureFactory gsf;

	public IngredientLineParser(){
		loadMeasurements();
		lexParser = new LexicalizedParser("englishPCFG.ser.gz");
		//lexParser.getOp().setOptions(new String[]{"-outputFormat", "typedDependenciesCollapsed", "-retainTmpSubcategories"});
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
	}

	/*
	 * Takes in a line, parses it for an ingredient, then returns a String  for the base ingredient
	 */
	public String extractIngredientString(String line){
		Ingredient ingredient = parseLine(line);
		return ingredient.getBase();
	}
	
	/*
	 * Parses an ingredient line by first running it through the Stanford dependency parser to determine
	 * which quantities fit with which units. Then it joins the quantity with the associated units and
	 * re-parses the ingredient line. Returns an Ingredient object.
	 */
	public Ingredient parseLine(String line){

		line = IngredientLineParser.stripWikiLinks(line); // pull out the wikilinks
		line = line.replaceAll("\\((.*?)\\)", ""); // removes all text in parens... for now...
		
		/* Retrieves list of dependencies */
		Tree parseTree = lexParser.apply(line);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		ArrayList<TypedDependency> deps = (ArrayList<TypedDependency>) gs.typedDependenciesCollapsed();

		HashSet<Integer> indicesToSkip = new HashSet<Integer>(); // Skip these indicies when recreating sentence

		String qtyAndUnits = "";

		/* loop through the depedencies to pick out qty and units*/
		for (TypedDependency dep : deps) {
			String relation = dep.reln().toString();
			TreeGraphNode dependent = dep.dep();
			TreeGraphNode governor = dep.gov();
			String depStr = dependent.nodeString();
			String govStr = governor.nodeString();

			/* Identify the units and quantity of that unit */
			if (govStr.matches(measurementRegex) && (relation.equals("num") || relation.equals("number"))) {
				indicesToSkip.add(dependent.index());
				indicesToSkip.add(governor.index());
				qtyAndUnits += depStr + "_" + govStr + " "; // join them
			}		
		}

		//System.out.println();
		//tp.printTree(parseTree); // prints dependencies

		String processedLine = "";
		
		/* reconstitute string */
		PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(new StringReader(line));
		int index = 1;
		while (tokenizer.hasNext()){
			String token = tokenizer.next();
			if (!indicesToSkip.contains(index))
				processedLine += token + " ";
			index++;
		}

		/* print statements for debugging */
		//System.out.println("----------");
		//System.out.println(parseTree.toString());
		//System.out.println(parsed);
		processedLine = processedLine.replaceAll(" of ", " ");
		return composeIngredientObject(processedLine, qtyAndUnits);
	}

	/*
	 * Method that takes a pre-processed String and gathers information
	 * for a ingredient including the base ingredient and the related
	 * modifiers. Runs the Stanford parser to find dependencies.
	 */
	public Ingredient composeIngredientObject(String line, String qty){
		Ingredient ingred = new Ingredient();
		ingred.setQuant(qty);
		//String base = "NONE";
		
		Tree parseTree = lexParser.apply(line);
		
		/* Boilerplate for getting typed dependencies */
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		//System.out.println(gs.typedDependenciesCollapsed());
		ArrayList<TypedDependency> deps = (ArrayList<TypedDependency>) gs.typedDependenciesCollapsed();
		
		for (TypedDependency dep : deps) {
			TreeGraphNode depNode = dep.dep();
			TreeGraphNode govNode = dep.gov();
			String depPos = depNode.parent().nodeString();
			
			String relation = dep.reln().toString();
			String depStr = depNode.nodeString();
			String govStr = govNode.nodeString();
			if (relation.equals("root")){
				if (depPos.startsWith("N")) ingred.setBase(depStr);
				continue;
			} 
			String base = ingred.getBase();
			String govPos = govNode.parent().nodeString();
			if (relation.equals("nn") || relation.equals("appos") || (relation.equals("amod") && !ingred.getQuant().contains(depStr))){
				//System.out.println(relation + " dep: " + depNode.nodeString() +  depPos +" gov: " + govNode.nodeString() + govPos);
				if (base.equals(Ingredient.NULL) && govPos.startsWith("N")) ingred.setBase(govStr);
				base = ingred.getBase();
				if (govStr.equals(base)) ingred.addToProps(depStr);
			} else if (relation.equals("nsubj")) {
				ingred.addToProps(govNode.nodeString());
				if (base.equals(Ingredient.NULL) && depPos.startsWith("N")) ingred.setBase(depStr);
				base = ingred.getBase();
				if (depStr.equals(base)) ingred.addToProps(govStr);
			}
		}
		
		return ingred;
	}
	
	/*
	 * Inner class to keep track of ingredient information.
	 */
	private class Ingredient{
		private String base;
		private HashSet<String> properties;
		private String quantities;
		public static final String NULL = "<NULL>";
		
		public Ingredient(){
			base = NULL;
			properties = new HashSet<String>();	
		}
		
		public void setBase(String ingred){
			base = ingred;
		}
		
		public String getBase(){
			return base;
		}
		
		public void addToProps(String prop){
			properties.add(prop);
		}
		
		public HashSet<String> getProps(){
			return properties;
		}
		
		public void setQuant(String quant){
			quantities = quant;
		}
		
		public String getQuant(){
			return quantities;
		}
		
		@Override
		public String toString(){
			return "Ingredient: " + base + " Quant: " + quantities + " Properties: " + properties.toString();
		}
	}
	
	/*
	 * Loads measurement file of regex to identify units.
	 */
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
	 * ----------------------------------------------------------------------------------------------------------------
	 * Main method to test run code.
	 */
	public static void main(String[] args) {
		IngredientLineParser lnParse= new IngredientLineParser();

		//		for (int i = 0; i < test.length; i++){
		//			System.out.println(test[i] + " -> " + lnParse.parseLine(test[i]));
		//		}
		loadTest();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("ingredEval.txt"));
			
			for (String ln : testLines) {
				Ingredient modLine = lnParse.parseLine(ln);
				bw.write(ln + ": " + modLine.toString());
				bw.newLine();
				//System.out.println(lnParse.extractBaseIngredient(modLine).toString());
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	private static void loadTest(){
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(TEST_FILE));
			while ((line = in.readLine()) != null) {
				testLines.add(line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find file.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String stripWikiLinks(String line) {
		line = line.replaceAll("\\*", "");
		int sBracesIndex = line.indexOf("[[");
		if (sBracesIndex == -1) {
			return line;
		}
		int pipeIndex = line.indexOf('|', sBracesIndex);
		int endBracesIndex = line.indexOf("]]", pipeIndex);
		return line.substring(0, sBracesIndex) 
				+ line.substring(pipeIndex + 1, endBracesIndex) 
				+ stripWikiLinks(line.substring(endBracesIndex+2));
	}

}
