package Day26;
public class ChessGameMain {
    public static void main(String[] args) {
        System.out.println("Strategy Design Pattern = Behavioral Design Pattern");

        ChessGame game = new ChessGame();

        game.setDifficultyLevel(new EasyLevel());
        game.startChessGame();

        game.setDifficultyLevel(new MediumLevel());
        game.startChessGame();

        game.setDifficultyLevel(new DifficultLevel());
        game.startChessGame();
    }
}

