package cs224u.ingredients;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class IngredientLineParser {

	private static String MEASUREMENTS_FILE = "measurements";
	private static String TEST_FILE = "ingred_test";
	private static String QUANTITY_REGEX = "(\\d++(?! */))? *-? *(?:(\\d+) */ *(\\d+))?";
	private static String[] test = {"2 pints milk","4 garlic cloves"};
	private String measurementRegex;
	private static ArrayList<String> testLines = new ArrayList<String>();
	private LexicalizedParser lexParser;

	public IngredientLineParser(){
		measurementRegex = "";
		loadMeasurements();
		lexParser = new LexicalizedParser("englishPCFG.ser.gz");
		lexParser.getOp().setOptions(new String[]{"-outputFormat", "typedDependenciesCollapsed", "-retainTmpSubcategories"});
		//		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	}

	/*
	 * Parses an ingredient line by first running it through the Stanford dependency parser to determine
	 * which quantities fit with which units. Then it joins the quantity with the associated units and
	 * re-parses the ingredient line.
	 */
	public String parseLine(String line){

		String qty = "";
		String units = "";
		String ingredient = "";

		Tree parseTree = lexParser.apply(line);
		
		/* Boilerplate for getting typed dependencies */
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		TreePrint tp = new TreePrint("typedDependenciesCollapsed");
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		//System.out.println(gs.typedDependenciesCollapsed());
		
		ArrayList<TypedDependency> deps = (ArrayList<TypedDependency>) gs.typedDependenciesCollapsed();
		
		HashSet<Integer> indicesToSkip = new HashSet<Integer>(); // Skip these indicies when recreating sentence
		
		String qtyAndUnits = "";
		
		for (TypedDependency dep : deps) {
			String relation = dep.reln().toString();
			TreeGraphNode dependent = dep.dep();
			TreeGraphNode governor = dep.gov();
			String depStr = dependent.label().word();
			String govStr = governor.label().word();
			
			if (govStr.matches(measurementRegex) && (relation.equals("num") || relation.equals("number"))) {
				indicesToSkip.add(dependent.index());
				indicesToSkip.add(governor.index());
				qtyAndUnits += depStr + "_" + govStr + " ";
			}		
		}
		
		//System.out.println(indicesToSkip.toString());
		
		System.out.println();
		//tp.printTree(parseTree); // prints dependencies
		
		String parsed = qtyAndUnits;
		
		PennTreebankTokenizer tokenizer = new PennTreebankTokenizer(new StringReader(line));
		//StringTokenizer tokenizer = new StringTokenizer(line, " *()[]");
		int index = 1;
		while (tokenizer.hasNext()){
			String token = tokenizer.next();
			if (!indicesToSkip.contains(index) && !token.matches("[()\\[\\]]"))
				parsed += token + " ";
			index++;
		}
		
		System.out.println("----------");
		//System.out.println(parseTree.toString());

		return parsed;
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


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IngredientLineParser lnParse= new IngredientLineParser();

		//		for (int i = 0; i < test.length; i++){
		//			System.out.println(test[i] + " -> " + lnParse.parseLine(test[i]));
		//		}
		loadTest();
		for (String ln : testLines) {
			System.out.println(ln + " -> " + lnParse.parseLine(ln));
		}


	}

}
