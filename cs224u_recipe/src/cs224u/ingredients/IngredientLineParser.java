package cs224u.ingredients;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
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

	public String parseLine(String line){

		String qty = "";
		String units = "";
		String ingredient = "";


		Tree parseTree = lexParser.apply(line);
		//parseTree.pennPrint();
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		TreePrint tp = new TreePrint("typedDependenciesCollapsed");
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parseTree);
		System.out.println(gs.typedDependenciesCollapsed());
		ArrayList<TypedDependency> deps = (ArrayList<TypedDependency>) gs.typedDependenciesCollapsed();
		HashSet<Integer> indicesToSkip = new HashSet<Integer>();
		
		
		for (TypedDependency dep : deps) {
			if (dep.reln().toString().equals("num")) {
				indicesToSkip.add(dep.dep().index());
				indicesToSkip.add(dep.gov().index());
				System.out.println(dep.dep().label().word());
				System.out.println(dep.gov().label().word());
			}
			
			if (dep.reln().toString().equals("number")) {
				System.out.println(dep.dep().label().word());
				System.out.println(dep.gov().label().word());
			}
				
		}
		System.out.println();
		tp.printTree(parseTree);
		System.out.println("----------");
		//System.out.println(parseTree.toString());

		StringTokenizer tokenizer = new StringTokenizer(line, " *()[]");
		while (tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if (token.matches(QUANTITY_REGEX)) qty += token+"_";
			else if (token.matches(measurementRegex)) units += token +"_";
			else ingredient += " " + token;
		}
		String joined = qty+units;
		if (!joined.isEmpty()) joined = joined.substring(0, joined.length()-1);

		return joined+ingredient;
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
