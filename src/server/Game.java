package server;

import java.io.IOException;
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
    Card[] playedCard;
    int[] seenCard; // clientIndex for been seen, -1 for not seen
    List<Integer> winners; // index of winner for each round
    HashMap<String, Integer> scores; // name<=>score
    boolean gameOver;
    boolean roundOver;
    int playerInTurn;
    boolean waitingForCard;

    public static Game getInstance() {
        if (game == null) {
            synchronized (Game.class) {
                if (game == null) {
                    game = new Game();
                }
            }
        }
        return game;
    }

    public void startGame() throws IOException {
        //initialize the game info
        countPlayer = Server.playerList.size();

        playerNames = new ArrayList<>(countPlayer);

        gameOver = false;
        discardedCard = new Card[countPlayer][16];
        countDiscarded = new int[countPlayer];
        handCard = new Card[countPlayer];
        playedCard = new Card[countPlayer];
        seenCard = new int[countPlayer];
        winners = new ArrayList<Integer>();
        scores = new HashMap<>(countPlayer);
        // initialize the scores for all the players
        for (int i = 0; i < countPlayer; i++) {
            String name = Server.playerList.get(i);
            scores.put(name, 0);
        }

        while (!gameOver) {
            game.newRound();
            checkGameOver();
        }

        Integer maxScore = Collections.max(scores.values());
        String finalWinner = "";
        for (String player : scores.keySet()) {
            if (scores.get(player) == maxScore)
                finalWinner = player;
            break;
        }
        System.out.println("The final winner of this game is " + finalWinner);
    }

    public void newRound() throws IOException {

        //reset the round info
        Arrays.fill(discardedCard, null);
        Arrays.fill(countDiscarded, 0);
        Arrays.fill(handCard, null);
        Arrays.fill(playedCard, null);
        Arrays.fill(seenCard, -1);
        roundOver = false;
        playerInTurn = 0;

        //update playNames for the first round
        if (winners.size() == 0) {
            for (int i = 0; i < countPlayer; i++) {
                playerNames.add(Server.playerList.get(i));
            }
        } else { //winner in last round plays first
            int lastWinner = winners.get(winners.size() - 1);
            String nameLastWinner = Server.playerList.get(lastWinner);

            for (int i = 0; i < lastWinner; i++) {
                playerNames.remove(i);
                playerNames.add(Server.playerList.get(i));
            }

            Server.getServer().updatePlayerIndex(playerNames);

        }

        deck = Card.shuffle();
        topCard = deck.pop();

        //*********UPDATE THE INFO TO SERVER AND CLIENT!!*****************
        //option: new attributes in server class:
        //Card[] handCard; Card[] drawnCard; Card[][] discardedCards; Card[] playedCard;


        //each player draws one card
        for (int i = 0; i < countPlayer; i++) {
            handCard[i] = deck.pop();
            //*********UPDATE THE INFO TO SERVER AND CLIENT!!*****************
        }

        while (!roundOver) {

            while (true) {
                playCard();
                playerInTurn++;
                if (playerInTurn == 4) playerInTurn = 0;
                checkRoundOver();
            }
        }
    }


    public void cardPlayed(Card card){
        playedCard[playerInTurn] = card;
        waitingForCard = false;
    }

    public void playCard() throws IOException {

        handCard[playerInTurn] = deck.pop();//evtl drawnCard statt handCard?
        // **********tell server and client(in turn)**************
        Server.getServer().drawnCard(playerInTurn, drawnCard);
        waitingForCard = true;
        // **********server waits for the chosen played card from client**********
        while (waitingForCard){
            continue;
        }

        Card cardInTurn = playedCard[playerInTurn];

        switch (cardInTurn) {
            case PRINCESS:
                Card.PRINCESS.function(playerInTurn);
                break;
            case COUNTESS:
                Card.COUNTESS.function(playerInTurn);
                break;
            case KING:
                String targetName1 = Server.getServer().choosePlayer(playerInTurn);
                int targetIndex1 = playerNames.indexOf(targetName1);
                Card.KING.function(playerInTurn, targetIndex1);
                break;
            case PRINCE:
                String targetName2 = Server.getServer().choosePlayer(playerInTurn);
                int targetIndex2 = playerNames.indexOf(targetName2);
                Card.PRINCE.function(playerInTurn, targetIndex2);
                break;
            case HANDMAID:
                Card.HANDMAID.function(playerInTurn);
                break;
            case BARON:
                String targetName3 = Server.getServer().choosePlayer(playerInTurn);
                int targetIndex3 = playerNames.indexOf(targetName3);
                Card.BARON.function(playerInTurn, targetIndex3);
                break;
            case PRIEST:
                String targetName4 = Server.getServer().choosePlayer(playerInTurn);
                int targetIndex4 = playerNames.indexOf(targetName4);
                Card.PRIEST.function(playerInTurn, targetIndex4);
                break;
            case GUARD:
                String targetName5 = Server.getServer().choosePlayer(playerInTurn);
                String guessName = Server.getServer().guessCardType(playerInTurn);
                int targetIndex5 = playerNames.indexOf(targetName5);
                Card.GUARD.function(playerInTurn, targetIndex5, guessName);
                break;
            default:
                //Exception...
        }
    }

    public void checkRoundOver() {
        if(1==1 ){// platzhalter
            roundOver = true;
        }else{
            roundOver = false;
        }
    }

    public void checkGameOver(){
        if(1==1 ){// platzhalter
            gameOver = true;
        }else{
            gameOver = false;
        }
    }
}
