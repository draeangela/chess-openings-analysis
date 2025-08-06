import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A utility class for analyzing chess openings data.
 * Provides methods for statistical analysis of opening performance,
 * player preferences, and correlations between various metrics.
 */
public class ChessOpeningsAnalysis {
    /**
     * Reads chess openings data from a CSV file and creates ChessOpening objects.
     * Filters out invalid openings (those with dashes in ECO codes).
     * 
     * @return ArrayList of valid ChessOpening objects
     * @throws FileNotFoundException if the openings.csv file is not found
     */
    public static ArrayList<ChessOpening> getOpeningsArray() throws FileNotFoundException {
        ArrayList<ChessOpening> openingsArray = new ArrayList<>();
        
        try (Scanner fileScanner = new Scanner(new File("/Users/dvizcarra/Documents/GitHub/Chess Openings/openings.csv"))) {
            // Skip the header line
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }

            // Read each openings line and create ChessOpening objects
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                try (Scanner lineScanner = new Scanner(line)) {
                    ChessOpening opening = new ChessOpening(lineScanner);
                    
                    // Filter out openings with invalid ECO codes (containing dashes)
                    if (opening.getEco() != null && opening.getEco().contains("-")) {
                        continue;
                    }
                    
                    openingsArray.add(opening);
                }
            }
        }
        return openingsArray;
    }

    /**
     * Identifies openings with statistically significant win rates.
     * For white: win rate must be >= 52%
     * For black: win rate must be >= 45%
     * Source: https://en.wikipedia.org/wiki/First-move_advantage_in_chess
     * 
     * @return ArrayList containing two lists: successful white openings and successful black openings
     * @throws FileNotFoundException if the openings file cannot be read
     */
    public static ArrayList<ArrayList<ChessOpening>> findSufficientWinRates() throws FileNotFoundException {
        ArrayList<ChessOpening> openingsArray = getOpeningsArray();
        ArrayList<ArrayList<ChessOpening>> sufficientWinRates = new ArrayList<>();
        ArrayList<ChessOpening> sufficientWinRatesWhite = new ArrayList<>();
        ArrayList<ChessOpening> sufficientWinRatesBlack = new ArrayList<>();
    
        // Categorize openings based on color and win rate thresholds
        for (int i = 0; i < openingsArray.size(); i++) {
            ChessOpening opening = openingsArray.get(i);
            
            if (opening.getColor().equals("white")) {
                if (opening.getPlayerWinPercent() >= 52.0) {
                    sufficientWinRatesWhite.add(opening);
                }
            } else if (opening.getColor().equals("black")) {
                if (opening.getPlayerWinPercent() >= 45.0) {
                    sufficientWinRatesBlack.add(opening);
                }
            }
        }
        
        sufficientWinRates.add(sufficientWinRatesWhite);
        sufficientWinRates.add(sufficientWinRatesBlack);
    
        return sufficientWinRates;
    }

    /**
     * Splits the openings into two lists based on color (white/black)
     * 
     * @return ArrayList containing two lists: white openings and black openings
     * @throws FileNotFoundException if the openings file cannot be read
     */
    public static ArrayList<ArrayList<ChessOpening>> splitByColor() throws FileNotFoundException {
        ArrayList<ChessOpening> openingsArray = getOpeningsArray();
        ArrayList<ArrayList<ChessOpening>> splitColorArr = new ArrayList<>();
    
        splitColorArr.add(new ArrayList<>());  // White openings
        splitColorArr.add(new ArrayList<>());  // Black openings
    
        for (int i = 0; i < openingsArray.size(); i++) {
            if (openingsArray.get(i).getColor().equals("white")) {
                splitColorArr.get(0).add(openingsArray.get(i));
            } else if (openingsArray.get(i).getColor().equals("black")) {
                splitColorArr.get(1).add(openingsArray.get(i));
            }
        }
    
        return splitColorArr;
    }

    /**
     * Calculates the Pearson correlation coefficient between two sets of data
     * Uses the formula: r = Σ((x - x̄)(y - ȳ)) / √(Σ(x - x̄)² * Σ(y - ȳ)²)
     * Source: https://en.wikipedia.org/wiki/Pearson_correlation_coefficient
     * 
     * @param set1 First dataset
     * @param set2 Second dataset
     * @return correlation coefficient between -1 and 1
     */
    public static double findCorrelationCoeff(ArrayList<Double> set1, ArrayList<Double> set2) throws FileNotFoundException{
        // Calculate means
        double set1Sum = 0;
        double set2Sum = 0;
 
        for (int i = 0; i < set1.size(); i++){
             set1Sum += set1.get(i);
             set2Sum += set2.get(i);
        }
 
        double set1Avg = set1Sum/set1.size();
        double set2Avg = set2Sum/set2.size();
 
        // Calculate correlation numerator: Σ((x - x̄)(y - ȳ))
        double numeratorSum = 0;
        for (int i = 0; i < set1.size(); i++){
             numeratorSum += (set1.get(i) - set1Avg)*(set2.get(i) - set2Avg);
        }
 
        // Calculate correlation denominator: √(Σ(x - x̄)² * Σ(y - ȳ)²)
        double denominatorXSum = 0;
        double denominatorYSum = 0;
        for (int i = 0; i < set1.size(); i++){
             denominatorXSum += Math.pow((set1.get(i) - set1Avg), 2);
             denominatorYSum += Math.pow((set2.get(i) - set2Avg), 2);
        }
 
        double denominator = Math.sqrt(denominatorXSum*denominatorYSum);
 
        return numeratorSum/denominator; 
     }

    /**
     * Creates lists of development levels (number of moves) for white and black openings
     * 
     * @param chessOpenings List of openings split by color
     * @return ArrayList containing two lists of development levels
     */
     public static ArrayList<ArrayList<Double>> getDevelopmentLevel(ArrayList<ArrayList<ChessOpening>> chessOpenings){
        ArrayList<ArrayList<Double>> developmentLvlArr = new ArrayList<>();

        developmentLvlArr.add(new ArrayList<>());  // White development levels
        developmentLvlArr.add(new ArrayList<>());  // Black development levels

        for (int i = 0; i < chessOpenings.size(); i++){
            for (int j = 0; j < chessOpenings.get(i).size(); j++){
                developmentLvlArr.get(i).add((double)(chessOpenings.get(i).get(j).getMovesList().length));
            }
        }

        return developmentLvlArr;
    }

    /**
     * Performs Fisher transformation to test if two correlation coefficients
     * are significantly different at α = .05 level
     * Source: https://en.wikipedia.org/wiki/Fisher_transformation
     * 
     * @param coeff1 First correlation coefficient
     * @param coeff2 Second correlation coefficient
     * @return String interpretation of the statistical test
     */
    public static String fisherTransformation(double coeff1, double coeff2) throws FileNotFoundException{
        ArrayList<ArrayList<ChessOpening>> sample = findSufficientWinRates();
        String interpretation = "";

        // Calculate total sample size
        double size = 0.0;
        for (int i = 0; i < sample.size(); i++){
            for (int j = 0; j < sample.get(i).size(); j++){
                size += 1.0;
            }
        }

        // Convert correlation coefficients to z-scores
        double zWhite = Math.atan(coeff1);
        double zBlack = Math.atan(coeff1);

        // Calculate standard error: 1/√(N-3)
        double standardErr = 1/(Math.sqrt(size-3));
        
        // Calculate test statistic
        double p = Math.abs(zWhite - zBlack)/standardErr;

        // Interpret result using critical value of 1.96 (α = .05)
        if (p > 1.96) {
            interpretation += "Correlations are significantly different";
        } else if (p <= 1.96) {
            interpretation += "Correlations are not significantly different";
        }

        return interpretation;
    }

    /**
     * Converts ECO codes to array indices and counts their frequency
     * 
     * @param openingArray List of chess openings
     * @return Array of counts for each ECO code
     */
    public static int[] countEco (ArrayList<ChessOpening> openingArray){
        int[] ecoCounter = new int[500];
        for (int i = 0; i < openingArray.size(); i++){
            String eco = openingArray.get(i).getEco();  
            char letter = eco.charAt(0);               // Get the letter part (A-E)
            int number = Integer.parseInt(eco.substring(1));  // Get the number part
            int index = ((letter - 'A') * 100) + number;  // Convert to array index
            ecoCounter[index]++;            
        }
        
        return ecoCounter;
    }

    /**
     * Converts an array index back to ECO code format (e.g., "A00")
     * 
     * @param index Array index to convert
     * @return ECO code string
     */
    public static String getEcoCode(int index) {
        int letterPos = index / 100;
        char letter = (char)('A' + letterPos);
        int number = index % 100;
        
        String numberString;
        if (number < 10) {
            numberString = "0" + number; // Add leading zero if the number is less than 10
        } else {
            numberString = String.valueOf(number); // Convert number to string
        }
    
        return "" + letter + numberString;
    }

    /**
     * Calculates the frequency of each opening in the dataset
     * 
     * @param openingArray List of chess openings
     * @return Array of frequencies for each ECO code
     */
    public static double[] findOpeningFreq (ArrayList<ChessOpening> openingArray) throws FileNotFoundException{
        int[] ecoCounter = countEco(openingArray);
        double[] freqArray = new double[500];
        
        // Convert counts to frequencies
        for (int i = 0; i < ecoCounter.length; i++){
            freqArray[i] = ((double)ecoCounter[i])/500.0;
        }

        return freqArray;
    }   

    /**
     * Calculates the mean of non-zero values in the dataset
     * 
     * @param data Array of values
     * @return Mean of non-zero values (Because ECO codes of frequency 0 means that code is not in the dataset)
     */
    public static double findMean (double[] data){
        double sum = 0.0;
        
        // Count non-zero values
        double n = 0.0;
        for (int i = 0; i < data.length; i++){
            if (data[i]!= 0){
                n += 1.0;
            }
        }

        // Calculate sum
        for (int i = 0; i < data.length; i++){
            sum += (double)(data[i]);
        }

        return sum/n;
    }

    /**
     * Calculates the standard deviation of non-zero values in the dataset
     * 
     * @param data Array of values
     * @return Standard deviation of non-zero values
     */
    public static double findSD (double[] data){
        double sum = 0.0;
        double mean = findMean(data);

        // Count non-zero values
        double n = 0.0;
        for (int i = 0; i < data.length; i++){
            if (data[i]!= 0){
                n += 1.0;
            }
        }

        // Calculate sum of squared differences from mean
        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i]-mean,2);
        }

        return Math.sqrt(sum/n);
    }

    /**
     * Converts a value to its z-score using  mean and standard deviation
     */
    public static double convertToZ (double data, double mean, double SD){
        return (data-mean)/SD;
    }

    /**
     * Identifies the openings used by the top 25% of players by rating
     * 
     * @param openingsArray List of chess openings
     * @return List of openings used by top-rated players
     */
    public static ArrayList<ChessOpening> findTop25Ratings (ArrayList<ChessOpening> openingsArray){
        ArrayList<ChessOpening> openingsList = new ArrayList<>();
        ArrayList<Integer> ratingsList = new ArrayList<>();

        // Create parallel lists of openings and their ratings
        for (ChessOpening opening : openingsArray) {
            openingsList.add(opening);
            ratingsList.add(opening.getAvgRating());
        }

        // Create list of indices for sorting
        ArrayList<Integer> sortedIndexes = new ArrayList<>();
        for (int i = 0; i < ratingsList.size(); i++) {
            sortedIndexes.add(i);
        }

        // Sort indices based on ratings (descending order)
        sortedIndexes.sort((i1, i2) -> Integer.compare(ratingsList.get(i2), ratingsList.get(i1))); // This line is not entirely coded by me-I found it online but cannot find where, sorry

        // Calculate cutoff for top 25%
        int cutoffIndex = (int) Math.floor(0.25 * sortedIndexes.size());

        ArrayList<ChessOpening> top25 = new ArrayList<>();

        // Select top 25% of openings by rating
        for (int i = 0; i < cutoffIndex; i++) {
            int index = sortedIndexes.get(i);
            top25.add(openingsList.get(index));
        }

        return top25;
    }

    /**
     * Implements the error function (erf) using Abramowitz and Stegun approximation
     * Used for calculating normal distribution probabilities
     * Source: https://stackoverflow.com/questions/9242907/how-do-i-generate-normal-cumulative-distribution-in-java-its-inverse-cdf-how
     */
    private static double erf(double x) {
        // A&S formula 7.1.26 (Abramowitz and Stegun)
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;
        x = Math.abs(x);
        double t = 1 / (1 + p * x);
        return 1 - ((((((a5 * t + a4) * t) + a3) * t + a2) * t) + a1) * t * Math.exp(-1 * x * x);
    }
    /**
     * Calculates the cumulative distribution function (CDF) of the standard normal distribution.
     * 
     * @param z Z-score to evaluate
     * @return Probability of a value less than z in a standard normal distribution
     */
    public static double normalcdf(double z) {
        double sign = 1;
        if (z < 0) sign = -1;
        return 0.5 * (1.0 + sign * erf(Math.abs(z) / Math.sqrt(2)));
    }

}
