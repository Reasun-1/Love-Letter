package server;

import java.util.Stack;

public class Game {

    private volatile static Game game;

     int countPlayer;
     String[] playerList; // ClienstName := clientIndex für alle Arrays
     Stack<Card> deck;
     int[] countDiscarded;
     Card[][] discardedCard;
     int[] status; // 0=game over, 1=in game, 2=protected
     Card[] handCard;
     int[] scores;
     int[] seenCard;


    private Game(){

    }

    public static Game getInstance(){
        if (game == null) {
            synchronized (Game.class) {
                if (game == null) {
                    game = new Game();
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
