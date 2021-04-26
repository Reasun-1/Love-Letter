import java.util.HashSet;
import java.util.List;

public class Game {
    private final static Game game;
    private List<String> deck;
    private HashSet<String> playerList = new HashSet<>();
    private String activePlayer;
    private List<String> score;

    private Game(){

    }

    public Game getInstance(){
        if (game == null) {
            synchronized (Game.class) {
                if (game == null) {
                    game = new Server();
                }
            }
        }
        return game;
    }

    public void startGame(){
        //start the gameplay
    }

    public void newRound(){
        //begin with a new round
    }

    public void nextPlayer(){
        //start the turn of the next player
    }

    public void playCard(String cardName){
        //handle the effect of the chosen card
    }
}
