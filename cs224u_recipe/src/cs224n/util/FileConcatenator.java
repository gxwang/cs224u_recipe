package cs224n.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileConcatenator {
	private String[] filenames = {
			"tweet1.txt",
			"tweet2.txt",
			"tweet3.text"
	};
	
	private String newFilename = "";
	
	public static void concatenate(String[] filenames, String newFilename) {
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new FileWriter(newFilename));
			for (int fileIndex = 0; fileIndex < filenames.length; fileIndex++) {
				BufferedReader rd = new BufferedReader(new FileReader(filenames[fileIndex]));
				String line;
				while ((line = rd.readLine()) != null) {
					fw.write(line);
				}
				rd.close();
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// concatenate(filenames, newFilename);
	}

}
