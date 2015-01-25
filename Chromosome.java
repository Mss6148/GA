import java.util.ArrayList;
import java.util.Random;

//CLASS Chromosome:
//Implements: Comparable
// The chromosome object stores a 2-dimensional arraylist containing
// a user input data-file (.txt) as a static object and references it often.
// This has cut down on runtimes in comparison with sending the data to the
// chromosome every instance a Cohen's D score needs to be calculated.
// Chromosome objects have all of their own methods required to run the 
// Genetic Algorithm (GA) - thus reducing the need to pass objects around.

public class Chromosome implements Comparable<Chromosome> {
	
	
	double sumSubjectScores;	//Sum of subject scores
	double sumControlScores;	//Sum of control scores
	int bit;					//Stores a randomly selected bit (for Mutation)
	double controlAverageScore;	//Used in calculating Cohen's D
	double subjectAverageScore;	//Used in calculating Cohen's D
	double controlStdD;			//Used in calculating Cohen's D
	double subjectStdD;			//Used in calculating Cohen's D
	static double mutationTop = 0.8;	//Used in the Mutation method
	static double mutationRate = 0.3;	//Used in the Mutation method
	double Cohens;						//Stores the chromosomes Cohen's D score
	static int numVariables;			//Number of variables (from Data file)
	int randomInt;						//Used in Mutation method
	double rand;						//Used in Mutation method
	short random;						//Used in Mutation method
	short tempWeight;					//Used in Mutation method
	
	//Stores chromosomes weights 
	public ArrayList<Short> weights = new ArrayList<Short>();
	
	//Database file (stores text file)
	private static ArrayList<ArrayList<Double>> database = new ArrayList<ArrayList<Double>>();
	//Random object used in Mutation Method
	Random r = new Random();
	
	//subjectScores and controlScores are used in calculating Cohen's D
	ArrayList<Double> subjectScores = new ArrayList<Double>();
	ArrayList<Double> controlScores = new ArrayList<Double>();
	
	//--------------------------------------------------------------------------------
	//Constructor: Creates a chromosome with random weights
	public Chromosome() {
		setRandomWeights();
	}	//End Constructor
	
	
	//--------------------------------------------------------------------------------
	//Constructor: Accepts an arrayList<ArrayList<Double>> database file
	//NOTE: This chromosome should NOT be added to the population of chromosomes
	public Chromosome( ArrayList<ArrayList<Double>> database) {	
		this.database = database;
		numVariables = database.get(0).size() - 1;
		
	}	//End Constructor
	
	//--------------------------------------------------------------------------------
	//Constructor: Accepts a chromosome object and copies it's weights creating a duplicate
	//			   chromosome
	public Chromosome( Chromosome cr ){
		this.weights = (ArrayList<Short>) cr.weights.clone();
	}
	
	//--------------------------------------------------------------------------------
	//Constructor: Accepts two chromosomes, crosses over their weights at random locations,
	// and creates a new chromosome from the resulting weights.
	@SuppressWarnings("unchecked")
	public Chromosome( Chromosome cr1, Chromosome cr2 ) {
		
		randomInt = r.nextInt(numVariables + 1);
	    double weightSelector = Math.random();
	    
		ArrayList<Short> tempWeights1 = new ArrayList<Short>();
		ArrayList<Short> tempWeights2 = new ArrayList<Short>();
		ArrayList<Short> reCombinedWeights = new ArrayList<Short>();
		
		tempWeights1 = (ArrayList<Short>) cr1.weights.clone();
		tempWeights2 = (ArrayList<Short>) cr2.weights.clone();
		
		if(weightSelector <= 0.5){
			for(int i = 0; i < cr1.weights.size(); i++){
				if(i <= randomInt){
					reCombinedWeights.add(tempWeights1.get(i));
				}
				else{
					reCombinedWeights.add(tempWeights2.get(i));
				}
			}
			
		}	//End if
		else if(weightSelector > 0.5){
			for(int i = 0; i < cr2.weights.size(); i++){
				if(i <= randomInt){
					reCombinedWeights.add(tempWeights2.get(i));
				}
				else{
					reCombinedWeights.add(tempWeights1.get(i));
				}
			}
		}
		
		weights.clear();
		
		for(int i = 0; i < reCombinedWeights.size(); i++){
			weights.add(reCombinedWeights.get(i));
		}
		//weights = reCombinedWeights;
		
		reCombinedWeights.clear();
	}
	
	//--------------------------------------------------------------------------------	
	//Method getWeights: Returns an ArrayList<Short> containing the chromosomes weights
	public ArrayList<Short> getWeights(){
		
		return weights;
	}
	
	//--------------------------------------------------------------------------------
	//Method setRandomWeights: Creates a set of completely random weights
	public void setRandomWeights() {
		
		for(int i = 0; i < numVariables; i++){
			random = (short) (r.nextDouble() * (65534 - 32768));
			//System.out.println(random);
			weights.add(random);
		}	//End for-loop
	}	//End setRandomWeights method
	
	//--------------------------------------------------------------------------------
	//Method Mutation: Mutates random weights
	// A maximum number of possible mutations is determined by (numVariables * mutationTop)
	// For each possible mutation, a random number is generated - if it is below the mutation
	// rate, a random weight is selected and randomly mutated via bitflicking
	public void Mutation() {
		
		for(int i = 0; i < numVariables * mutationTop; i++){
			rand = Math.random();
			if(rand < mutationRate){
				randomInt = r.nextInt(numVariables);
				bit = r.nextInt(16);
				
				tempWeight = (short) (weights.get(randomInt) ^ (1 << bit));
				weights.set(randomInt, tempWeight);
			}	//End if statement
		}	//End for loop
		

	}	//End Mutation Method
	
	
	
	//--------------------------------------------------------------------------------
	//Method CalculateD: Calculates the Cohen's D from a chromosome's weights 
	public void CalculateD() {
		
		double CohensTop = 0;
		double CohensBottom = 0;
		controlAverageScore = 0;
		subjectAverageScore = 0;
		controlStdD = 0;
		subjectStdD = 0;
		double patientScore = 0;
		sumSubjectScores = 0;
		sumControlScores = 0;
		int numSubjects = 0;
		int numControls = 0;
		
		controlScores.clear();
		subjectScores.clear();
		
		for( int i = 0; i < database.size(); i++){
			patientScore = 0;
				if(database.get(i).get(0) == 0){
					numControls += 1;
					for( int j = 0; j < database.get(i).size() - 1; j++){
						sumControlScores += ((weights.get(j) + 32768) * database.get(i).get(j+1));
						
						//System.out.println("Weight  : " + weights.get(j));
						//System.out.println("Variable: " + database.get(i).get(j+1));
						
						patientScore += ((weights.get(j) + 32768) * database.get(i).get(j+1));
					}	//End for-loop
					controlScores.add(patientScore);
				}	//End if-statement
				else if(database.get(i).get(0) == 1){
					numSubjects += 1;
					for (int k = 0; k < database.get(i).size() - 1; k++){
						sumSubjectScores += ((weights.get(k) + 32768) * database.get(i).get(k+1));
						patientScore += ((weights.get(k) + 32768) * database.get(i).get(k+1));
					}	//End for-loop
					subjectScores.add(patientScore);
				}	//End else-if statement
			
			
		}	//End outer for loop
		
		//Calculate average score for control and subjects
		controlAverageScore = (sumControlScores/numControls);

		subjectAverageScore = (sumSubjectScores/numSubjects);
		
		//Calculate variance of subject scores
		for(int i = 0; i < subjectScores.size(); i++){
			double tempVariance = (subjectScores.get(i) - subjectAverageScore);
			tempVariance = tempVariance * tempVariance;
			subjectStdD += tempVariance;
		}	//End for-loop
		
		
		subjectStdD = subjectStdD/(numSubjects - 1);
		subjectStdD = Math.sqrt(subjectStdD);
		
		for(int i = 0; i < controlScores.size(); i++){
			controlStdD += Math.pow((controlScores.get(i) - controlAverageScore), 2);
		}
		
		controlStdD = controlStdD/(numControls - 1);
		controlStdD = Math.sqrt(controlStdD);
		
		CohensTop = Math.abs(controlAverageScore - subjectAverageScore);
		CohensBottom = ((controlStdD + subjectStdD) / 2);
		
		Cohens = CohensTop/CohensBottom;
		
	}	//End CalculateD Method
	
	//DEBUGGING METHODS-----------------------------------------------------------------------------
	
	public double getSumControl(){
		return sumControlScores;
	}
	
	public double getSumSubject(){
		return sumSubjectScores;
	}
	
	public double getControlStd(){
		return controlStdD;
	}
	
	public double getSubjectStd(){
		return subjectStdD;
	}
	
	public double getControlAvg(){
		return controlAverageScore;
	}
	
	public double getSubjectAvg(){
		return subjectAverageScore;
	}
	
	public ArrayList<Double> getControlScores(){
		return controlScores;
	}
	
	public ArrayList<Double> getSubjectScores(){
		return subjectScores;
	}
	
	//END OF DEBUGGING METHODS--------------------------------------------------------------------
	
	//--------------------------------------------------------------------------------
	//Method GetD: Returns Cohen's D (double)
	public double getD() {
		
		return Cohens;
	}	//End getD Method

	
	//--------------------------------------------------------------------------------
	//Method compareTo: Implemented from the Comparable Class, allows sorting of chromosomes
	// based on the Cohen's D value.  
	@Override
	public int compareTo(Chromosome cr) {
		if(cr.getD() > this.Cohens){
			return -1;
		}
		else if(cr.getD() < this.Cohens){
			return 1;
		}
		else {
			return 0;
		}
		
	}	//End method compareTo
	
	
	
	
}	//End class Chromosome