package server;

import javafx.beans.property.IntegerProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Game {

    private volatile static Game game;

    int countPlayer;
    List<String> playerNames; // IMPORTANT:index of the arrayList stands for the players´ index of this round
    Stack<Card> deck;
    List<Card> topCards; // the first Cards drawn at the beginning of every round
    int[] countDiscarded; // the number of discarded cards of each player
    Card[][] discardedCard; // the discarded cards of each player
    int[] status; // 0=game over, 1=in game, 2=protected by handmaid
    Card[] handCard; // the card in hand of each player
    Card[] drawnCard; // the drawn card of each player
    Card[] playedCard; // temporary storage for the played card
    int[] seenCard; // clientIndex for been seen, -1 for not seen
    HashMap<String, Integer> tokens; // who has how many tokens in this game already
    HashMap<String, Integer> scores; // score of each player in this round
    List<Integer> winners; // winners´ index of each round

    boolean gameOver;
    boolean roundOver;
    int playerInTurn;
    boolean waitingForCard;
    boolean waitingForChoosePlayer;
    boolean waitingForGuessTyp;

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
        drawnCard = new Card[countPlayer];
        playedCard = new Card[countPlayer];
        seenCard = new int[countPlayer];
        status = new int[countPlayer];
        scores = new HashMap<>(countPlayer);
        tokens = new HashMap<>(countPlayer);
        topCards = new ArrayList<>();
        winners = new ArrayList<>();

        // initialize the tokesn for all the players
        for (int i = 0; i < countPlayer; i++) {
            String name = Server.playerList.get(i);
            tokens.put(name, 0);
        }

        // Welcome info. for all: how many players are in this game, and what their names are
        Server.getServer().startGameInfo();

        // game begins
        while (!gameOver) {
            game.newRound();
            checkGameOver();
        }

        // check who won the game
        Integer maxTokens = Collections.max(tokens.values());
        String finalWinner = "";
        for (String player : tokens.keySet()) {
            if (tokens.get(player) == maxTokens){
                finalWinner = player;
                break;
            }
        }
        // tell everyone who won the game
        Server.getServer().gameOver(tokens);
        Server.getServer().sendMessageToAll("The final winner of this game is " + finalWinner);
    }

    public void newRound() throws IOException {

        // reset the round info
        for (int i = 0; i < discardedCard.length; i++) {
            Arrays.fill(discardedCard[i], null);
        }
        Arrays.fill(countDiscarded, 0);
        Arrays.fill(handCard, null);
        Arrays.fill(drawnCard, null);
        Arrays.fill(playedCard, null);
        Arrays.fill(seenCard, -1);
        Arrays.fill(status, 1);


        topCards.clear();
        roundOver = false;
        playerInTurn = 0;

        // update playerIndex
        if (winners.size() == 0) {
            for (int i = 0; i < countPlayer; i++) {
                playerNames.add(Server.playerList.get(i));
            }
        } else { // winner in last round plays first
            int lastWinner = winners.get(winners.size() - 1);

            // the player won last round plays first in this round
            for (int i = 0; i < lastWinner; i++) {
                playerNames.add(playerNames.get(i));
            }
            for (int i = 0; i < lastWinner; i++) {
                playerNames.remove(0);
            }
            // update the player index in this round
            Server.getServer().updatePlayerIndex(playerNames);
        }

        // reset the scores
        for (String name : playerNames) {
            scores.put(name, 0);
        }

        deck = Card.shuffle();

        if (countPlayer == 2) { // move three cards away if 2 player play
            for (int i = 0; i < 3; i++) {
                topCards.add(deck.pop());
            }
        } else { // otherwise move only one card away
            topCards.add(deck.pop());
        }


        Server.getServer().sendMessageToAll("deck shuffled \n" + "top card(s) moved");

        // each player draws one card
        for (int i = 0; i < countPlayer; i++) {
            handCard[i] = deck.pop();
            // tell each player which card he/she has drawn
            Server.getServer().drawnCard(i, handCard[i]);
        }

        while (!roundOver) {

            playCard();
            playerInTurn++;
            if (playerInTurn == countPlayer) playerInTurn = 0;
            // find the active player in next turn
            while (status[playerInTurn] == 0) {
                playerInTurn++;
                if (playerInTurn == countPlayer) playerInTurn = 0;
            }

            checkRoundOver();
        }
        String winnerName = updateScoresAndTokensAndWinnersForThisRound();
        Server.getServer().roundOver(tokens, winnerName);
    }

    // function for other class to invoke
    public void cardPlayed(Card card) {
        playedCard[playerInTurn] = card;
    }

    public void playCard() throws IOException {

        drawnCard[playerInTurn] = deck.pop();

        // inform player which card he/she has drawn
        Server.getServer().drawnCard(playerInTurn, drawnCard[playerInTurn]);
        // tell all players that the player in turn is playing
        Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " has drawn one card and is playing...");

        waitingForCard = true;

        while (waitingForCard) {

            if (playedCard[playerInTurn] == null) {
                continue;
                // check whether the played card if one of the handCard and drawnCard
            } else if (playedCard[playerInTurn] != handCard[playerInTurn] && playedCard[playerInTurn] != drawnCard[playerInTurn]) {
                Server.getServer().exception(playerNames.get(playerInTurn), "You don´t have this card in your hand, choose one in hand.");
                // if player has king or prince and also a countess in hand, countess must be played
            } else if (playedCard[playerInTurn] != Card.COUNTESS
                    && (((handCard[playerInTurn] == Card.PRINCE || handCard[playerInTurn] == Card.KING) && drawnCard[playerInTurn] == Card.COUNTESS)
                    || ((drawnCard[playerInTurn] == Card.PRINCE || drawnCard[playerInTurn] == Card.KING) && handCard[playerInTurn] == Card.COUNTESS))) {
                Server.getServer().exception(playerNames.get(playerInTurn), "You have royal member in your hand, the countess must be played.");
            } else {
                // update the handCard and drawnCard
                if (playedCard[playerInTurn] == handCard[playerInTurn]) {
                    handCard[playerInTurn] = drawnCard[playerInTurn];
                    drawnCard[playerInTurn] = null;
                    waitingForCard = false;
                } else {
                    drawnCard[playerInTurn] = null;
                    waitingForCard = false;
                }
            }
        }

        // inform all the players who has played which card
        Server.getServer().playedCard(playerNames.get(playerInTurn), playedCard[playerInTurn]);
        // apply the card function
        switch (playedCard[playerInTurn]) {

            case PRINCESS:
                Card.PRINCESS.function(playerInTurn);
                break;

            case COUNTESS:
                Card.COUNTESS.function(playerInTurn);
                break;

            case KING:
                waitingForChoosePlayer = true;
                String targetName1;
                int targetIndex1 = playerInTurn; // initialize the target as self

                // if all players are protected, the function goes to self
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.KING.function(playerInTurn, targetIndex1); // nothing happens, just change the card with self.
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName1 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName1 == null) {
                            continue;
                        } else if(targetName1.equals(playerNames.get(playerInTurn))){
                            Server.getServer().exception(playerNames.get(playerInTurn), "You can´t choose yourself, choose another player.");
                        } else if (status[playerNames.indexOf(targetName1)] == 2) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName1)] == 0) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex1 = playerNames.indexOf(targetName1);
                            Card.KING.function(playerInTurn, targetIndex1);
                            waitingForChoosePlayer = false;
                        }
                    }
                }
                break;

            case PRINCE:
                waitingForChoosePlayer = true;
                String targetName2;
                int targetIndex2 = playerInTurn; // initialize the target as self
                status[playerInTurn] = 1; // reset the status from 2 to 1, if status was 2.

                // if all players are protected, the function goes to self
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, drop a card and draw a new one.");
                    Card.PRINCE.function(playerInTurn, targetIndex2);
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName2 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName2 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName2)] == 2) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName2)] == 0) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex2 = playerNames.indexOf(targetName2);
                            Card.PRINCE.function(playerInTurn, targetIndex2);
                            waitingForChoosePlayer = false;
                        }
                    }
                }
                break;

            case HANDMAID:
                Card.HANDMAID.function(playerInTurn);
                break;

            case BARON:
                waitingForChoosePlayer = true;
                String targetName3;
                int targetIndex3 = playerInTurn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.BARON.function(playerInTurn, targetIndex3); // nothing happens, just compare the card with self
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName3 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName3 == null) {
                            continue;
                        } else if(targetName3.equals(playerNames.get(playerInTurn))){
                            Server.getServer().exception(playerNames.get(playerInTurn), "You can´t choose yourself, choose another player.");
                        } else if (status[playerNames.indexOf(targetName3)] == 2) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName3)] == 0) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex3 = playerNames.indexOf(targetName3);
                            Card.BARON.function(playerInTurn, targetIndex3);
                            waitingForChoosePlayer = false;
                        }
                    }
                }
                break;

            case PRIEST:
                waitingForChoosePlayer = true;
                String targetName4;
                int targetIndex4 = playerInTurn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.PRIEST.function(playerInTurn, targetIndex4); // nothing happens
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName4 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName4 == null) {
                            continue;
                        } else if(targetName4.equals(playerNames.get(playerInTurn))){
                            Server.getServer().exception(playerNames.get(playerInTurn), "You can´t choose yourself, choose another player.");
                        } else if (status[playerNames.indexOf(targetName4)] == 2) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName4)] == 0) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex4 = playerNames.indexOf(targetName4);
                            Card.PRIEST.function(playerInTurn, targetIndex4);
                            waitingForChoosePlayer = false;
                        }
                    }
                }
                break;

            case GUARD:
                waitingForChoosePlayer = true;
                waitingForGuessTyp = true;
                String targetName5;
                String guessCardType;
                Card guessCard = Card.GUARD; // intitialize the target as self
                int targetIndex5 = playerInTurn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Card.GUARD.function(playerInTurn, targetIndex5, guessCard); // nothing happens
                    Server.getServer().sendMessageToAll("All players are protected, nothing happens, play continues.");
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName5 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName5 == null) {
                            continue;
                        } else if(targetName5.equals(playerNames.get(playerInTurn))){
                            Server.getServer().exception(playerNames.get(playerInTurn), "You can´t choose yourself, choose another player.");
                        } else if (status[playerNames.indexOf(targetName5)] == 2) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName5)] == 0) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex5 = playerNames.indexOf(targetName5);
                            Card.GUARD.function(playerInTurn, targetIndex5);
                            waitingForChoosePlayer = false;
                        }
                    }

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is guessing the card type");

                    while (waitingForGuessTyp) {
                        guessCardType = Server.getServer().guessCardType(playerInTurn);
                        guessCard = null;

                        for (Card c : Card.values()) {
                            if (c.getType().equals(guessCardType)) {
                                guessCard = c;
                            }
                        }

                        if (guessCard == null) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "This card type does not exist. Choose again.");
                            continue;
                        } else if (guessCard == Card.GUARD) {
                            Server.getServer().exception(playerNames.get(playerInTurn), "You can´t guess guard, guess another card type.");
                            continue;
                        } else {
                            waitingForGuessTyp = false;
                        }
                    }
                    Card.GUARD.function(playerInTurn, targetIndex5, guessCard);
                }
                break;
            default:
                //***********brauchen wir hier Exceptions eg.? Mir ist nichts aufgefallen********
                break;
        }

        // playedCard refresh to null for next round check
        playedCard[playerInTurn] = null;
    }

    public void checkRoundOver() {
        int numActivePlayer = 0;
        for (int i = 0; i < countPlayer; i++) {
            if (status[i] != 0) {
                numActivePlayer++;
            }
        }
        if (numActivePlayer == 1 || deck.size() == 0) {
            roundOver = true;
        }
    }


    public String updateScoresAndTokensAndWinnersForThisRound() {
        int numActivePlayer = 0;
        for (int i = 0; i < countPlayer; i++) {
            if (status[i] != 0) {
                numActivePlayer++;
            }
        }

        String winnerName = null;

        if (numActivePlayer == 1) { // there is only active player left
            for (int i = 0; i < countPlayer; i++) {
                if (status[i] == 1) {
                    winners.add(i);
                    winnerName = playerNames.get(i);
                    scores.put(winnerName, handCard[i].getValue());
                    tokens.put(winnerName, tokens.get(winnerName) + 1);
                    break;
                }
            }
        } else { // there are several active players left when this round is over
            int max = 0;
            List<Integer> playerIndexWithMax = new ArrayList<>();
            // get the max. value of hand card
            for (int i = 0; i < countPlayer; i++) {
                if(handCard[i] != null && status[i] != 0){
                    max = Math.max(max, handCard[i].getValue());
                }
            }
            // get index of the player(s) with the max value
            for (int i = 0; i < countPlayer; i++) {
                if (handCard[i] != null && status[i] != 0 && handCard[i].getValue() == max) {
                    playerIndexWithMax.add(i);
                }
            }

            if (playerIndexWithMax.size() == 1) { // if there is only one max Value
                int winnerIndex = playerIndexWithMax.get(0);
                winnerName = playerNames.get(winnerIndex);
                winners.add(winnerIndex);
                tokens.put(winnerName, tokens.get(winnerName) + 1);

                for (int i = 0; i < countPlayer; i++) {
                    String clientName = playerNames.get(i);
                    // the player out of game has no score
                    if(status[i] != 0){
                        int score = handCard[i].getValue();
                        scores.put(clientName, score);
                    }
                }
            } else { // there are several same max values
                for (int i = 0; i < countPlayer; i++) {
                    String clientName = playerNames.get(i);
                    int totalScore = 0;
                    if(status[i] != 0){
                        totalScore = handCard[i].getValue();
                        for (int j = 0; j < 16; j++) {
                            if(discardedCard[i][j] != null){
                                totalScore += discardedCard[i][j].getValue();
                            }
                        }
                    }
                    scores.put(clientName, totalScore);
                }

                int maxScore = Collections.max(scores.values());
                for (String name : playerNames) {
                    if (scores.get(name) == maxScore) {
                        winnerName = name;
                        int winnerIndex = playerNames.indexOf(winnerName);
                        winners.add(winnerIndex);
                        tokens.put(winnerName, tokens.get(winnerName) + 1);
                    }
                }
            }
        }
        return winnerName;
    }

    public void checkGameOver() {
        if (countPlayer == 2) {
            for (String name : playerNames) {
                if (tokens.get(name) == 7) {
                    gameOver = true;
                }
            }
        } else if (countPlayer == 3) {
            for (String name : playerNames) {
                if (tokens.get(name) == 5) {
                    gameOver = true;
                }
            }
        } else {
            for (String name : playerNames) {
                if (tokens.get(name) == 4) {
                    gameOver = true;
                }
            }
        }
    }

    // check if all the active players are under protect
    public boolean allPlayersProtected() {
        boolean flag = true;
        for (int i = 0; i < countPlayer; i++) {
            if (status[i] == 1 && playerInTurn != i) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
