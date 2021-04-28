package server;

import java.util.*;

public class Game {

    private volatile static Game game;

     int countPlayer;
     List<String> playerNames; // ClienstName := clientIndex für alle Arrays
     Stack<Card> deck;
     Card topCard;
     int[] countDiscarded;
     Card[][] discardedCard;
     int[] status; // 0=game over, 1=in game, 2=protected
     Card[] handCard;
     int[] scores;
     int[] seenCard; // clientIndex for been seen, -1 for not seen
     List<Integer> winners; // index of winner for each round

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

    public void newRound(){

        //reset the round info
        Arrays.fill(discardedCard,null);
        Arrays.fill(countDiscarded,0);
        Arrays.fill(handCard,null);
        Arrays.fill(seenCard, -1);
        Arrays.fill(scores, 0);

        //update playNames for this round
        if(winners.size() == 0){
            for (int i = 0; i < countPlayer; i++) {
                playerNames.add(Server.playerList.get(i));
            }
        }else{ //winner in last round plays first
            int lastWinner = winners.get(winners.size()-1);
            String nameLastWinner = Server.playerList.get(lastWinner);

            for (int i = 0; i < lastWinner; i++) {
                playerNames.remove(i);
                playerNames.add(Server.playerList.get(i));
            }

            tellNewPlayerIndexToServer();
        }

        deck = Card.shuffle();
        topCard = deck.pop();

        //each player draws one card
        for (int i = 0; i < countPlayer; i++) {
            handCard[i] = deck.pop();
        }
    }

    public void startGame(){
        //initialize the game info
        countPlayer = Server.playerList.size();

        playerNames = new ArrayList<>(countPlayer);

        discardedCard = new Card[countPlayer][16];
        countDiscarded = new int[countPlayer];
        handCard = new Card[countPlayer];
        seenCard = new int[countPlayer];
        scores = new int[countPlayer];
        winners = new ArrayList<Integer>();

        game.newRound();
    }

    public Hashtable<Integer, String> tellNewPlayerIndexToServer(){
        Hashtable<Integer, String> newPlayerList = new Hashtable<>();
        for (int i = 0; i < countPlayer; i++) {
            newPlayerList.put(i, playerNames.get(i));
        }
        return newPlayerList;
    }



    public void nextPlayer(){
        //start the turn of the next player
    }

    public void playCard(String cardName){
        //handle the effect of the chosen card
    }
}
