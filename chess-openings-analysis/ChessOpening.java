import java.util.Scanner;

/**
 * Represents a chess opening with associated statistics and move information.
 * This class is designed to store and manage data about chess openings,
 * including win rates, popularity, and the sequence of moves.
 */
public class ChessOpening {
    private String opening;
    private String color;
    private String eco;
    private int numGames;
    private int avgRating;
    private double playerWinPercent;
    private String[] movesList;

   /**
    * Default constructor creating an empty ChessOpening object.
    */
   public ChessOpening() {}

   // Full constructor 
   public ChessOpening(String opening, String color, String eco, int numGames,int avgRating, double playerWinPercent, String[] movesList) {
        this.opening = opening;
        this.color = color;
        this.eco = eco;
        this.numGames = numGames;
        this.avgRating = avgRating;
        this.playerWinPercent = playerWinPercent;
        this.movesList = movesList;
    }

   // Scanner constructor for CSV parsing
   public ChessOpening(Scanner lineScanner) {
     String line = lineScanner.nextLine();
     // Declare values outside try-catch to ensure it's in scope
     String[] values = null;  // Initialize as null to avoid compilation error
 
      values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
 
     // Parse values according to CSV column positions
     opening = values[1].replaceAll("\"", "");  // Column 1: Opening
     color = values[2].toLowerCase();           // Column 2: Colour
     numGames = Integer.parseInt(values[3]);    // Column 3: Num Games
     eco = values[4];                          // Column 4: ECO
     avgRating = Integer.parseInt(values[7]);   // Column 7: Avg Player
     playerWinPercent = Double.parseDouble(values[8]); // Column 8: Player Win %
 
     // Parse movesList from the moves_list column (column 12)
     String movesString = values[12].replaceAll("\"", "")  // Remove quotes
                                   .replaceAll("\\[|\\]", "") // Remove square brackets
                                   .replaceAll("'", ""); // Remove single quotes
     movesList = movesString.split(", ");
 
}

   // Getters
   public String getOpening() { return opening; }
   public String getColor() { return color; }
   public String getEco() { return eco; }
   public int getNumGames() { return numGames; }
   public int getAvgRating() { return avgRating; }
   public double getPlayerWinPercent() { return playerWinPercent; }
   public String[] getMovesList() { return movesList; }

}