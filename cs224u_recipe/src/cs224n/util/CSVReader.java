/**
 * 
 */
package cs224n.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author constance
 *
 */
public class CSVReader {
	
	public static int getNumColumns(String filename) {
		BufferedReader rd;
	    try {
	        rd = new BufferedReader(new FileReader(filename));

	        String line = rd.readLine();
	        if (line == null) {
	           	rd.close();
	           	return 0; // Empty file
	        }
	        String[] cells = line.split(",");
	        return cells.length;
	    } catch(Exception e) {
	        System.out.println("Problem reading sentiment list.");
	    }		
	    return -1;
	}
	
	/*
	 * I know it's bad to have the params in different orders but Java complains otherwise
	 */
	public static void readFileAsRows(String filename, List<String[]> db, int cell_limit, boolean includeHeader) {
	    BufferedReader rd;
	    try {
	        rd = new BufferedReader(new FileReader(filename));
	        while (true) {
	            String line = rd.readLine();
	            if (line == null) {
	            	rd.close();
	            	break;
	            }
	            String[] cells = line.split(",", cell_limit);
	            db.add(cells);
	        }
	        if (!includeHeader) {
	        	db.remove(0);
	        }
	    } catch(Exception e) {
	        System.out.println("Problem reading sentiment list.");
	    }		
	}
	
	public static void readFileAsCols(String filename, List<List<String>> db, boolean includeHeader) {
	    BufferedReader rd;
	    int cell_limit = db.size();
	    try {
	        rd = new BufferedReader(new FileReader(filename));
	        while (true) {
	            String line = rd.readLine();
	            if (line == null) {
	            	rd.close();
	            	break;
	            }
	            String[] cells = line.split(",", cell_limit);
	            for (int cell = 0; cell < cell_limit; cell++) {
	            	db.get(cell).add(cells[cell]);
	            }
	        }
	        if (!includeHeader) {
	        	for (List<String> list : db) {
	        		list.remove(0);
	        	}
	        }
	    } catch(Exception e) {
	        System.out.println("Problem reading sentiment list.");
	    }
	    
	}
	
	public static void main(String[] args) {
		System.out.println("George is a genius.");
		List<String> adjectives = new ArrayList<String>();
		List<String> happyLogProbs = new ArrayList<String>();
		List<String> sadLogProbs = new ArrayList<String>();
		
		List<List<String>> data = new ArrayList<List<String>>();
		data.add(adjectives);
		data.add(happyLogProbs);
		data.add(sadLogProbs);
		
		CSVReader.readFileAsCols("/Users/constance/Downloads/Twitter-sentiment-analysis/twitter_sentiment_list.csv", data, false);
		int numLines = data.size();
		System.out.println("Read" + numLines);
		for (int i = 0; i < 10; i++) {
			System.out.println(data.get(0).get(i) 
					+ ":" + data.get(1).get(i)
					+ ":" + data.get(2).get(i));
		}
	}

}
