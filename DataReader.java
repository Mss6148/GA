
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader; 
import java.io.IOException;
import java.util.*;

//CLASS DataReader: Reads in a two-dimensional, tab-delimited data file (.txt) and returns an 
// ArrayList<ArrayList<Double>> with the parsed data file.
public class DataReader {
	
	//Create a new scanner object to collect user-input
	Scanner scanner = new Scanner(System.in);
	
	//database: stores the parsed data-file
	ArrayList<ArrayList<Double>> database = new ArrayList<ArrayList<Double>>();
	
	String fileName;		//stores the user-input file name
	String titles;			//stores variable titles/names
	String line;			//temporary line storage
	String tempLine;		//temporary line storage
	double tempValue;		//temporary storage for variable scores

	//CONSTRUCTOR:  Creates a new DataReader object
	public DataReader(){
		
	}	//End constructor
	
	//METHOD: ReadFile
	//Returns: ArrayList<ArrayList<Double>>
	//Throws: FIleNotFoundException
	//		  
	public ArrayList<ArrayList<Double>> ReadFile() throws FileNotFoundException {
		
		// Prompts the user for the name of a data file
		System.out.println("Enter the name of your data file: ");
		fileName = scanner.next();
		
		// Create a new scanner object to read in the data file
		Scanner scanner = new Scanner(new File(fileName));
		
		// Stores the variable titles
		titles = scanner.nextLine();
		
		// Parse and store the data file line by line.
		// Each patient's information is stored in an arrayList<Double> then added to "database"
		while(scanner.hasNextLine()){
			// Each line is stored as a string then parsed for doubles
			line = scanner.nextLine();
			
			// Create a new ArrayList<Double> for each line (patient) in the data file.
			ArrayList<Double> patient = new ArrayList<Double>();
			
			//split the patient line into an array of values, but skip the first column (patient ID)
			String[] split = line.split("\t");
			for(int i = 1; i < split.length; i++){
				tempLine = split[i];
				patient.add(Double.parseDouble(tempLine));
			}	//End for-loop
			database.add(patient);
		}	//End while
		
		
		return database;
		
	}	//End ReadFile method
	
	public String getTitles(){
		return titles;
	}

}	//End class DataReader
