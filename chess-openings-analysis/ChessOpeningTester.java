import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * A program that analyzes chess openings data to answer various statistical questions
 * about opening effectiveness, popularity, and usage patterns across different player levels.
 */
public class ChessOpeningTester {
 
    /**
     * Question 1: Analyzes which chess openings are considered "good" based on win rates
     * - For white: win rate > 50% is considered good (due to first-move advantage)
     * - For black: win rate > 45% is considered good
     * Also analyzes the distribution of good openings across ECO codes (A-E)
     */
    public static void questionOne() throws FileNotFoundException {
        // Get lists of openings with sufficient win rates for both white and black
        ArrayList<ArrayList<ChessOpening>> sufficientWinRates = ChessOpeningsAnalysis.findSufficientWinRates();
    
        // Create lists to store just the opening names for easier output
        ArrayList<ArrayList<String>> sufficientWinRatesNames = new ArrayList<>();
        sufficientWinRatesNames.add(new ArrayList<>());  // White openings
        sufficientWinRatesNames.add(new ArrayList<>());  // Black openings
    
        // Extract opening names from the ChessOpening objects
        for (int i = 0; i < sufficientWinRates.size(); i++) { 
            for (int j = 0; j < sufficientWinRates.get(i).size(); j++) {  
                sufficientWinRatesNames.get(i).add(sufficientWinRates.get(i).get(j).getOpening());
            }
        }
    
        // Output the results
        System.out.println("For white, the openings with sufficient win rates are: " + sufficientWinRatesNames.get(0));
        System.out.println("Number of 'good' white openings: " + sufficientWinRatesNames.get(0).size());
        System.out.println("For black, the openings with sufficient win rates are: " + sufficientWinRatesNames.get(1));
        System.out.println("Number of 'good' black openings: " + sufficientWinRatesNames.get(1).size());

        // Find majority across ECO codes
        int[] wEcoCounter = new int[5];  
        int[] bEcoCounter = new int[5]; 
        for (int i = 0; i < sufficientWinRates.size(); i++){
            for (int j = 0; j < sufficientWinRates.get(i).size(); j++) { 
                String eco = sufficientWinRates.get(i).get(j).getEco();
                char letter = eco.charAt(0);               
                int index = (letter - 'A');  
                if (i == 0){
                    wEcoCounter[index]++;  
                } else if (i == 1){
                    bEcoCounter[index]++;  
                }
            }
        }

        //System.out.println ("White ECO counter: " + Arrays.toString(wEcoCounter));
        //System.out.println ("Black ECO counter: " + Arrays.toString(bEcoCounter));
    }

    /**
     * Question 2: Analyzes if more developed openings (more number of moves) correlate with higher win percentages
     * Uses correlation coefficient analysis and Fisher transformation to compare white vs black
     */
    public static void questionTwo() throws FileNotFoundException{
        ArrayList<ArrayList<Double>> winRates = new ArrayList<>();
        ArrayList<ArrayList<ChessOpening>> splitByColor = ChessOpeningsAnalysis.splitByColor();

        // Initialize lists for storing win rates
        winRates.add(new ArrayList<>());  // White win rates
        winRates.add(new ArrayList<>());  // Black win rates

        // Extract win percentages for both colors
        for (int i = 0; i < splitByColor.size(); i++){
            for (int j = 0; j < splitByColor.get(i).size(); j++){
                winRates.get(i).add(splitByColor.get(i).get(j).getPlayerWinPercent());
            }
        }

        // Calculate correlation coefficients between development level and win rates
        double rWhite = ChessOpeningsAnalysis.findCorrelationCoeff(ChessOpeningsAnalysis.getDevelopmentLevel(splitByColor).get(0), winRates.get(0));
        double rBlack = ChessOpeningsAnalysis.findCorrelationCoeff(ChessOpeningsAnalysis.getDevelopmentLevel(splitByColor).get(1), winRates.get(1));

        System.out.println ("White correlation: " + rWhite + "\nBlack Correlation: " + rBlack);
        System.out.println(ChessOpeningsAnalysis.fisherTransformation(rWhite, rBlack));
    }

    /**
     * Question 3: Analyzes if there's a correlation between an opening's popularity (games played)
     * and its success rate. Uses correlation analysis and Fisher transformation to compare colors
     */
    public static void questionThree() throws FileNotFoundException{
        ArrayList<ArrayList<Double>> numGamesArr = new ArrayList<>();
        ArrayList<ArrayList<ChessOpening>> splitByColor = ChessOpeningsAnalysis.splitByColor();
        ArrayList<ArrayList<Double>> winRates = new ArrayList<>();

        // Initialize arrays for storing data
        numGamesArr.add(new ArrayList<>());  // Number of games for white
        numGamesArr.add(new ArrayList<>());  // Number of games for black

        winRates.add(new ArrayList<>());  // Win rates for white
        winRates.add(new ArrayList<>());  // Win rates for black

        // Extract win rates for both colors
        for (int i = 0; i < splitByColor.size(); i++){
            for (int j = 0; j < splitByColor.get(i).size(); j++){
                winRates.get(i).add(splitByColor.get(i).get(j).getPlayerWinPercent());
            }
        }

        // Extract number of games played for both colors
        for (int i = 0; i < splitByColor.size(); i++){
            for (int j = 0; j < splitByColor.get(i).size(); j++){
                numGamesArr.get(i).add((double)splitByColor.get(i).get(j).getNumGames());
            }
        }

        // Calculate correlation between popularity and win rates
        double wPopulationWinCorrelation = ChessOpeningsAnalysis.findCorrelationCoeff(numGamesArr.get(0), winRates.get(0));
        double bPopulationWinCorrelation = ChessOpeningsAnalysis.findCorrelationCoeff(numGamesArr.get(1), winRates.get(1));

        System.out.println ("White correlation: " + wPopulationWinCorrelation + "\nBlack Correlation: " + bPopulationWinCorrelation);        
        System.out.println(ChessOpeningsAnalysis.fisherTransformation(wPopulationWinCorrelation, bPopulationWinCorrelation));
    }

    /**
     * Question 4: Identifies which openings are more commonly used by higher-rated players (top 25%)
     * Uses statistical analysis to compare opening frequencies between top players and overall population
     */
    public static void questionFour() throws FileNotFoundException{
        ArrayList<ArrayList<ChessOpening>> splitByColor = ChessOpeningsAnalysis.splitByColor();

        // Calculate frequency distributions for all players
        double[] wOverallFreq = ChessOpeningsAnalysis.findOpeningFreq(splitByColor.get(0));
        double[] bOverallFreq = ChessOpeningsAnalysis.findOpeningFreq(splitByColor.get(1));

        // Calculate frequency distributions for top 25% rated players
        double[] wTopFreq = ChessOpeningsAnalysis.findOpeningFreq(ChessOpeningsAnalysis.findTop25Ratings(splitByColor.get(0)));
        double[] bTopFreq = ChessOpeningsAnalysis.findOpeningFreq(ChessOpeningsAnalysis.findTop25Ratings(splitByColor.get(1)));

        // Convert frequencies to z-scores for comparison
        // White overall population z-scores
        double[] wOverallZ = new double[500];
        for (int i = 0; i < wOverallFreq.length; i++){
            if (wOverallFreq[i] != 0) {
                wOverallZ[i] = (ChessOpeningsAnalysis.convertToZ(wOverallFreq[i], ChessOpeningsAnalysis.findMean(wOverallFreq), ChessOpeningsAnalysis.findSD(wOverallFreq)));
            } else {
                wOverallZ[i] = 0;
            }
        }
        
        // Black overall population z-scores
        double[] bOverallZ = new double[500];
        for (int i = 0; i < bOverallFreq.length; i++){
            if (bOverallFreq[i] != 0) {          
                bOverallZ[i] = (ChessOpeningsAnalysis.convertToZ(bOverallFreq[i], ChessOpeningsAnalysis.findMean(bOverallFreq), ChessOpeningsAnalysis.findSD(bOverallFreq)));
            } else {
                bOverallZ[i] = 0;
            }
        }

        // White top players z-scores
        double[] wTopZ = new double[500];
        for (int i = 0; i < wTopFreq.length; i++){
            if (wTopFreq[i] != 0){
                wTopZ[i] = (ChessOpeningsAnalysis.convertToZ(wTopFreq[i], ChessOpeningsAnalysis.findMean(wTopFreq), ChessOpeningsAnalysis.findSD(wTopFreq)));
            } else {
                wTopZ[i] = 0;
            }
        }

        // Black top players z-scores
        double[] bTopZ = new double[500];
        for (int i = 0; i < bTopFreq.length; i++){
            if (bTopFreq[i] != 0){
                bTopZ[i] = (ChessOpeningsAnalysis.convertToZ((double)bTopFreq[i], ChessOpeningsAnalysis.findMean(wTopFreq), ChessOpeningsAnalysis.findSD(bTopFreq)));
            } else {
                bTopZ[i] = 0;
            }
        }

        // Identify openings where top players' usage is higher than overall population
        ArrayList<Integer> wToTest = new ArrayList<>();
        for (int i = 0; i < 500; i++){
            if (wOverallZ[i] < wTopZ[i]) {
                wToTest.add(i);
            }
        }

        ArrayList<Integer> bToTest = new ArrayList<>();
        for (int i = 0; i < 500; i++){
            if (bOverallZ[i] < bTopZ[i]) {
                bToTest.add(i);
            }
        }

        // Perform hypothesis testing (α = .05) to identify statistically significant differences
        ArrayList<Integer> wEcoIndices = new ArrayList<>();
        ArrayList<Integer> bEcoIndices = new ArrayList<>();

        // Test white openings
        for (int i = 0; i < wToTest.size(); i++){
            int index = wToTest.get(i);
            double wZDiff = wTopZ[index] - wOverallZ[index];
            double p = 1 - ChessOpeningsAnalysis.normalcdf(wZDiff);
            if (p < .05) {
                wEcoIndices.add(index);
            }
        }

        // Test black openings
        for (int i = 0; i < bToTest.size(); i++){
            int index = bToTest.get(i);
            double bZDiff = bTopZ[index] - bOverallZ[index];
            double p = 1 - ChessOpeningsAnalysis.normalcdf(bZDiff);
            if (p < .05) {
                bEcoIndices.add(index);
            }
        }
        
        //System.out.println ("Number of white values to test:" + wToTest.size());
        //System.out.println ("Number of black values to test:" + bToTest.size());

        // Convert significant indices to ECO codes
        ArrayList<String> wHigherEcos = new ArrayList<>();
        ArrayList<String> bHigherEcos = new ArrayList<>();

        for (int i = 0; i < wEcoIndices.size(); i++){
            wHigherEcos.add(ChessOpeningsAnalysis.getEcoCode(wEcoIndices.get(i)));
        }

        for (int i = 0; i < bEcoIndices.size(); i++){
            bHigherEcos.add(ChessOpeningsAnalysis.getEcoCode(bEcoIndices.get(i)));
        }

        System.out.println("For white, the opening variations that higher players tend to use more often are " + wHigherEcos);
        System.out.println("For black, the opening variations that higher players tend to use more often are " + bHigherEcos);
    }   

    /**
     * Main method that runs all four analysis questions
     */
    public static void main(String[] args) throws FileNotFoundException{
        System.out.println ("QUESTION ONE: Which openings by their ECO codes are considered \"good\"? For white (who has first-move advantage), a win rate above 50% is considered\n" + //
                "    high, and 45% for black. ");
        questionOne();

        System.out.println (" ");
        System.out.println ("QUESTION TWO: Do more developed openings correlate to higher win percentages? Does this differ significantly between white and black? Whether or not \n" + //
                "    an opening is \"developed\" is dependent on its number of moves. ");
        questionTwo();

        System.out.println (" ");
        System.out.println ("QUESTION THREE: Is there a correlation between an opening's popularity (number of games played) and its success rate?");
        questionThree();

        System.out.println (" ");
        System.out.println ("QUESTION FOUR: Are there specific groups of openings (same ECO code) most commonly used by higher-rated players (top 25%)? If so, what are they?");
        questionFour();
    }
}