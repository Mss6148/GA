import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//CLASS GA (main method): The GA class calls the DataReader class to read in a tab-delimited 
//data file.  A chromosome is called by sending the database file and stored as a static object.
//An initial population of 1000 chromosomes with random weights is generated as a starting point.
//The simulation runs for a total of 1000 generations and each generation follows the same pattern:
//1. Determine the highest scoring chromosome and store the score and weights.
//2. For the top 15 scoring chromosomes: add 1 exact copy, and 4 crossover + mutated copies to the new generation
//3. For 910 chromosomes: select 2 chromosomes randomly, mutate both, crossover, and add the resulting chromosome to the new generation
//4. Add 5 completely random new chromosomes to the new generation (gene flow)
//5. Add 10 chromosomes which have been crossed over from two random chromosomes(one randomly from the top 100, one completely randomly)
//6. The new generation (of 1000 chromosomes) then has the Cohens D scores calculated, and the next generation begins.
//
// Finally, each generation's best scoring chromosome and it's weight are output to a specified text file.
public class GA {
	
	static String tempLine = new String();
	static String tempOutput = new String();
	static String outfile = new String();		//User-input outfile name
	
	//MAIN METHOD:  The main method calls the DataReader and Chromosome classes.
	public static void main(String[] args) {
		
		int bestGen = 0;					//Stores the generation of the highest scoring chromosome
		final int POPULATION_SIZE = 1000;	//Constant for the size of the population
		int generation = 0;					//Generation counter
		double bestD = 0;					//Stores the best Cohen's D score
		boolean reachedMax = false;
		
		String titles = new String();
		StringBuilder output = new StringBuilder();		//String of information to be printed in the output file
	
		Random r = new Random();			//Random object used throughout the program
		
		//Scanner to read in user-input out-file
		Scanner scanner = new Scanner(System.in);
		
		//Temporary weights
		ArrayList<Short> tempWeights = new ArrayList<Short>();
		
		//populationBestD: Stores the population's best Cohen's D score
		ArrayList<Double> populationBestD = new ArrayList<Double>();
		
		//generationBestWeights: Stores the best weight for each generation
		ArrayList<ArrayList<Short>> generationBestWeights = new ArrayList<ArrayList<Short>>();
		
		//generationBestD: Stores the best Cohen's D score for each generation
		ArrayList<Double> generationBestD = new ArrayList<Double>();
		
		//generationAvgD: Stores the average Cohen's D score for each generation
		ArrayList<Double> generationAvgD = new ArrayList<Double>();
		
		//database: Stores the user-input database file.
		ArrayList<ArrayList<Double>> database = new ArrayList<ArrayList<Double>>();
		
		//population: Stores the current population of chromosomes
		Chromosome[] population = new Chromosome[POPULATION_SIZE];
		
		//populationBuild: A temporary population used to build each next-generation of chromosomes
		ArrayList<Chromosome> populationBuild = new ArrayList<Chromosome>();
		
		//------------------------------------------------------------------------------------------------
		
		System.out.println("Enter desired output filename: ");
		outfile = scanner.next();
		
		//Call the DataReader object which prompts the user for a data (text) file and returns the 
		//database as an ArrayList<ArrayList<Double>>
		//Throws file not found exception.
		
		DataReader dr = new DataReader();
		try {
			database = dr.ReadFile();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}	//End catch-statement
		titles = dr.getTitles();
		
		output.append(System.getProperty("line.separator"));
		output.append("Generation");
		output.append("\t");
		output.append("Maximum");
		output.append("\t");
		output.append("Average");
		output.append(titles);
		output.append(System.getProperty("line.separator"));
		
		tempOutput = output.toString();
		
		//------------------------------------------------------------------------------------------------
		
		long startTime = System.nanoTime();
		
		//Create a non-population chromosome object to store the static database file
		Chromosome cr = new Chromosome(database);
		
		//------------------------------------------------------------------------------
		//Shutdown Loop - prints the current written file into the user defined output file.
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				
				try {
					
					File file = new File(outfile);
					
					if (!file.exists()){
						file.createNewFile();
					}	//End if
					
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(tempOutput);
					bw.close();
					
					System.out.println();
					System.out.println("Data successfully printed to file: " + outfile);
				} catch (IOException e) {
					e.printStackTrace();
				}	//End catch
			}
		});
		
		//------------------------------------------------------------------------------------------------
		
		//POPULATE INITIAL POPULATION
		
		//Initialize population (1000) with chromosomes of random weights:
		//Calls chromosome.CalculateD
		for(int i = 0; i < POPULATION_SIZE; i++){
			Chromosome chromosome = new Chromosome();
			chromosome.CalculateD();
			population[i] = chromosome;
			
		}	//End for-loop
		
		//------------------------------------------------------------------------------------------------
		
		//PRIMARY LOOP
		
		//For 1000 generations:
		//1. Sort the population based on Cohen's D scores
		//2. Determine if any chromosomes are higher than the previous best
		//3. Build a new population of 1000 chromosomes
		//4. End while-loop
		while(!reachedMax){
			populationBuild.clear();
			tempLine = "";
			
			//CALCULATE NEW COHENS D
			
			for(int i = 0; i < population.length; i++){
				population[i].CalculateD();
			}
			
			//Sort population based on Cohen's D scores (ascending)
			Arrays.sort(population);
			
			//FIND BEST SCORE
			
			//Add the weights of the best scoring chromosome (in this generation) to an ArrayList
			//of top weights per generation
			generationBestWeights.add(population[999].getWeights());
			tempWeights = (ArrayList<Short>) population[999].getWeights().clone();
			
			//Add the Cohen's D score of the highest scoring chromosome to an ArrayList of the top
			//Cohen's D scores per generation
			generationBestD.add(population[999].getD());
			double tempD = population[999].getD();
			
			//Add the average Cohen's D score per generation to an ArrayList storing median values
			generationAvgD.add( (population[0].getD() + population[999].getD()) / 2 );
			double tempAvg = ( (population[0].getD() + population[999].getD()) / 2 );
			
			//Update output string with generation results
			
			output.append(System.getProperty("line.separator"));
			tempLine += (System.getProperty("line.separator"));
			output.append(generation);
			tempLine += (generation);
			output.append("\t");
			tempLine += ("\t");
			output.append(tempD);
			tempLine += (tempD);
			output.append("\t");
			tempLine += "\t";
			output.append(tempAvg);
			tempLine += (tempAvg);
			output.append("\t");
			tempLine += "\t";
			
			for(int i = 0; i < tempWeights.size(); i++){
				short weight =  tempWeights.get(i);
				int intWeight = weight + 32768;
				output.append(intWeight);
				tempLine += (intWeight);
				output.append("\t");
				tempLine += "\t";
			}
			
			tempOutput += tempLine;
			
			
			//Determine if any chromosome has a Cohen's D score higher than the current best (across all generations)
				if(population[999].getD() > bestD){
					bestGen = generation;
					bestD = population[999].getD();
					populationBestD.add(bestD);
					
				}	//End if-statement
				
				if(generation > bestGen + 98){
					reachedMax = true;
				}
			
			//Prints generation numbers in increments of 50 - to reduce printing while giving users regular updates
			if(generation % 50 == 0){
				System.out.println("Processing generation: " + generation);
			}	//End if-statement

			
			
			//BEGIN ADDING TO NEW POPULATION
					
			// Add copies of the top 15 high scoring chromosomes = 75 total
			// 1 exact replica of each of the top 15 scoring chromosomes
			// 4 chromosomes of each of the top 15 scoring chromosomes have been crossed over
			// with another chromosome in the top 100 scoring chromosomes and then mutated (Chromosome.Mutation())
			// Totaling: 75/1000
			for(int i = population.length - 1 ; i > population.length-16; i--){
				
				//Add an exact replica of this chromosome to the next population
				Chromosome cr1 = new Chromosome(population[i]);
				
				//Generate a random number, 899 through 999 (top 100) for crossing over events
				//a new chromosome is created by crossing over the randomly selected chromosome
				//with the high scoring chromosome ( 4 times )
				int randomChromosome = r.nextInt(100) + 900;
				Chromosome cr2 = new Chromosome(population[i],population[randomChromosome]);
				
				randomChromosome = r.nextInt(100) + 900;
				Chromosome cr3 = new Chromosome(population[i],population[randomChromosome]);
				
				randomChromosome = r.nextInt(100) + 900;
				Chromosome cr4 = new Chromosome(population[i],population[randomChromosome]);
				
				randomChromosome = r.nextInt(100) + 900;
				Chromosome cr5 = new Chromosome(population[i],population[randomChromosome]);
				
				//Mutate the 4 crossed over chromosomes
				cr2.Mutation();
				cr3.Mutation();
				cr4.Mutation();
				cr5.Mutation();

				
				//Add the 5 chromosomes to the new population build
				populationBuild.add(cr1);
				populationBuild.add(cr2);
				populationBuild.add(cr3);
				populationBuild.add(cr4);
				populationBuild.add(cr5);
				
			}	//End for-loop
			
			
			//Select 2 random chromosomes, crossover their weights and add them to the populationBuild = 910 (985 total)
			//Simulates random mating between individuals
			for(int i = 0; i < 910 ; i++){
				
				// Select two random chromosomes for crossing over.
				int crossover1 = r.nextInt(1000);
				int crossover2 = r.nextInt(1000);

				// Mutate the randomly selected chromosomes
				population[crossover1].Mutation();
				population[crossover2].Mutation();
				
				//Create a new chromosome (offspring) as a result of crossing over the two randomly selected
				//parent chromosomes
				Chromosome cr6 = new Chromosome(population[crossover1],population[crossover2]);

				//Mutate the offspring chromosome
				cr6.Mutation();
				
				//Add the offspring chromosome to the population
				populationBuild.add(cr6);
			}	//End for-loop
			
			// Add 5 completely new chromosomes with randomly selected weights to the population
			// to simulate gene flow (migration) = 5 (990 total)
			for(int i = 0; i < 5 ; i++){
				Chromosome cr7 = new Chromosome();
				populationBuild.add(cr7);
				
			}	//End for loop
			
			//Add ten randomly selected and mutated chromosomes to the population = 10 (1000 total)
			for(int i = 0; i < 10 ; i++){
				
				// Select two random chromosomes: 1 in range 899 - 999 ( top 100 )
				// and the 2nd in range 0-1000 (any).
				int crossover1 = r.nextInt(100) + 900;
				int crossover2 = r.nextInt(1000);
				
				//Create a new offspring chromosome by crossing over the two parents.  
				Chromosome cr8 = new Chromosome(population[crossover1],population[crossover2]);
				
				//Add the offspring chromosome to the population.
				populationBuild.add(cr8);
			}	//End for-loop
			
			// END POPULATING NEW POPULATION
	
			
			//Overwrite the next generation (populationBuild) to the original population (population)
			for(int i = 0; i < POPULATION_SIZE; i++){
				
				//Copy each element from populationBuild to population.
				population[i] = populationBuild.get(i);
				
			}	//End for-loop
			
			//Increment generation counter
			generation += 1;
			
		}	//End while-loop
		
		System.out.println();
		int weightSize = generationBestWeights.size() - 1;
		
		//Print the highest weights 
		System.out.println("===================================================================");
		System.out.println("Best Weights: ");
		for(int i = 0; i < generationBestWeights.get(weightSize).size(); i++){
			int tweights = (generationBestWeights.get(weightSize).get(i)) + 32768;
			System.out.print(tweights + " ");
		}	//End for-loop
		
		System.out.println();
		//PRINT
		
		long printTime = System.nanoTime();
		
		try {
			
			File file = new File(outfile);
			
			if (!file.exists()){
				file.createNewFile();
			}	//End if
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(output.toString());
			bw.close();
			
			System.out.println();
			System.out.println("Data successfully printed to file: " + outfile);
		} catch (IOException e) {
			e.printStackTrace();
		}	//End catch
		
		System.out.println();
		System.out.println("Cohen's D: " + bestD);
		System.out.println("Best Generation: " + bestGen);
		System.out.println("Final Generation: " + generation);
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		long elapsedTime = (duration/1000000);
		
		System.out.println("Total time: " + elapsedTime);
		
	}	//End main method
}	//End class GA
