package Day26;
public class ChessGame {
    private DifficultyLevel difficultyLevel;

    // Setter for strategy
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    // Executes the chosen strategy
    public void startChessGame() {
        if (difficultyLevel != null) {
            difficultyLevel.play();   // Delegates to current strategy
        } else {
            System.out.println("Please set a difficulty level first.");
        }
    }
}

