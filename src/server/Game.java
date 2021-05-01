package server;

import javafx.beans.property.IntegerProperty;

import java.io.IOException;
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
    Card[] playedCard; // ********************notwendig?************************************
    int[] seenCard; // clientIndex for been seen, -1 for not seen
    // **********tokens für ganzes Spiel ergänzt*******************
    HashMap<String, Integer> tokens; // who has how many tokens in this game already
    // **********scores mit playerIndex für jede Runde**************
    HashMap<String, Integer> scores; // score of each player in this round
    List<Integer> winners; // winners´ name of each round
    int roundNr;

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
        scores = new HashMap<>(countPlayer);
        tokens = new HashMap<>(countPlayer);
        topCards = new ArrayList<>();

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
            if (tokens.get(player) == maxTokens)
                finalWinner = player;
            break;
        }
        // tell everyone who won the game
        //****************haben wir die Funktion hier doppelt geschrieben?********************
        Server.getServer().gameOver(tokens);
        Server.getServer().sendMessageToAll("The final winner of this game is " + finalWinner);
    }

    public void newRound() throws IOException {
        // inform all players about the new round
        //Server.getServer().newRound(tokens); // ***********hier eg über tokens?********** ++++ ja ++++
        // +++++++++++++ geändert zu roundOver am Ende (eins reicht) ++++++++++++++++++++++++++++

        // reset the round info
        Arrays.fill(discardedCard, null);
        Arrays.fill(countDiscarded, 0);
        Arrays.fill(handCard, null);
        Arrays.fill(drawnCard, null);
        Arrays.fill(playedCard, null);
        Arrays.fill(seenCard, -1);

        // reset the scores
        for (String name : playerNames) {
            scores.put(name, 0);
        }

        topCards.clear();
        roundOver = false;
        playerInTurn = 0;

        // update playNames for the first round
        if (winners.size() == 0) {
            for (int i = 0; i < countPlayer; i++) {
                playerNames.add(Server.playerList.get(i));
            }
        } else { // winner in last round plays first
            int lastWinner = winners.get(winners.size() - 1);
            String nameLastWinner = Server.playerList.get(lastWinner);

            for (int i = 0; i < lastWinner; i++) {
                playerNames.remove(i);
                playerNames.add(Server.playerList.get(i));
            }
            // update the player index in this round
            Server.getServer().updatePlayerIndex(playerNames);
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
            checkRoundOver();
        }
        updateScoresAndTokensAndWinnersForThisRound();
        //**********Server function optional: roundOver?*************
        //++++++++++++ enter winner name (how do i get it from the list winners?? ++++++++++
        //Server.getServer().roundOver(tokens, winner);
    }


    //*******************diese Funktion notwendig?*************************
    //+++++++++++++ in dieser Version geht die action vom client aus, dann notwendig ++++
    //+++++++++++++ falls action von server ausgehen soll, funktion nicht nötig (dann braucht man ein chooseCard)++++++
    public void cardPlayed(Card card) {
        playedCard[playerInTurn] = card;
        waitingForCard = false;
    }

    public void playCard() throws IOException {

        drawnCard[playerInTurn] = deck.pop();
        // inform player which card he/she has drawn
        Server.getServer().drawnCard(playerInTurn, drawnCard[playerInTurn]);
        // tell all players that the player in turn is playing
        // ************generelle Frage: brauchen wir diese Funktion sendMessageToAll? Sonst noch eine Funktion in Server zu ergänzen?*********************
        Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + "has drawn one card and is playing...");

        waitingForCard = true;
        Card cardInTurn = null;

        while (waitingForCard) {
            // **********need a chooseCard functon from Server**********
            // ++++++++++++++action von server oder client aus?? ++++++++++
            //cardInTurn = Server.getServer().chooseCardToPlay(playerInTurn);
            if (cardInTurn == null) {
                continue;
            } else {
                //check the chosen card and update the handCard and drawnCard
                if (cardInTurn.getType() == handCard[playerInTurn].getType()) {
                    handCard[playerInTurn] = drawnCard[playerInTurn];
                    drawnCard[playerInTurn] = null;
                    waitingForCard = false;
                } else if (cardInTurn.getType() == drawnCard[playerInTurn].getType()) {
                    drawnCard[playerInTurn] = null;
                    waitingForCard = false;
                } else {
                    Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen card is not available, choose a card in hand");
                }
            }
        }

        // inform all the players who has played which card
        Server.getServer().playedCard(playerNames.get(playerInTurn), cardInTurn);
        // apply the card function
        switch (cardInTurn) {

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
                    Card.KING.function(playerInTurn, targetIndex1); // nothing happens, just change the card with self.
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName1 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName1 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName1)] == 2) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName1)] == 0) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
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

                // if all players are protected, the function goes to self
                if (allPlayersProtected()) {
                    Card.PRINCE.function(playerInTurn, targetIndex2);
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName2 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName2 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName2)] == 2) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName2)] == 0) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
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
                    Card.BARON.function(playerInTurn, targetIndex3); // nothing happens, just compare the card with self
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName3 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName3 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName3)] == 2) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName3)] == 0) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
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
                    Card.BARON.function(playerInTurn, targetIndex4); // nothing happens
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName4 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName4 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName4)] == 2) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName4)] == 0) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
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
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is choosing a target player.");

                    while (waitingForChoosePlayer) {

                        targetName5 = Server.getServer().choosePlayer(playerInTurn);

                        if (targetName5 == null) {
                            continue;
                        } else if (status[playerNames.indexOf(targetName5)] == 2) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is protected, choose another player");
                        } else if (status[playerNames.indexOf(targetName5)] == 0) {
                            Server.getServer().sendTo(playerNames.get(playerInTurn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetIndex5 = playerNames.indexOf(targetName5);
                            Card.PRIEST.function(playerInTurn, targetIndex5);
                            waitingForChoosePlayer = false;
                        }
                    }

                    Server.getServer().sendMessageToAll(playerNames.get(playerInTurn) + " is guessing the card type");

                    while (waitingForGuessTyp){
                        guessCardType = Server.getServer().guessCardType(playerInTurn);
                        guessCard = null;

                        for(Card c : Card.values()){
                            if(c.getType().equals(guessCardType)){
                                guessCard = c;
                            }else{
                                //*************exception function in Server needed**********
                            }
                        }

                        if(guessCard == null){
                            continue;
                        }else{
                            waitingForGuessTyp = false;
                        }
                    }
                    Card.GUARD.function(playerInTurn, targetIndex5, guessCard);
                }
                break;
            default:
                // ********************brauch wir in Server Class ein exception Funktion?**********
                // **********z.B. Server.getServer().exception(msg)*****************
                break;
        }
    }

    public void checkRoundOver() {
        int numActivePlayer = 0;
        for (int i = 0; i < countPlayer; i++) {
            if(status[i] != 0){
                numActivePlayer++;
            }
        }
        if(numActivePlayer == 1 || deck.size() == 0){
            roundOver = true;
        }
    }


    public void updateScoresAndTokensAndWinnersForThisRound() {
        int numActivePlayer = 0;
        for (int i = 0; i < countPlayer; i++) {
            if(status[i] != 0){
                numActivePlayer++;
            }
        }

        String winnerName;

        if(numActivePlayer == 1){ // there is only active player left
            for (int i = 0; i < countPlayer; i++) {
                if(status[i] == 1){
                    winners.add(i);
                    winnerName = playerNames.get(i);
                    scores.put(winnerName, handCard[i].getValue());
                    tokens.put(winnerName, tokens.get(winnerName)+1);
                    break;
                }
            }
        }else{ // there are several active players left when this round is over
            int max = 0;
            List<Integer> playerIndexWithMax = new ArrayList<>();
            // get the max. value of hand card
            for (int i = 0; i < countPlayer; i++) {
                max = Math.max(max, handCard[i].getValue());
            }
            for (int i = 0; i < countPlayer; i++) {
                if(handCard[i].getValue() == max){
                    playerIndexWithMax.add(i);
                }
            }

            if(playerIndexWithMax.size() == 1){ // if there is only one max Value
                int winnerIndex = playerIndexWithMax.indexOf(0);
                winnerName = playerNames.get(winnerIndex);
                winners.add(winnerIndex);
                tokens.put(winnerName, tokens.get(winnerName)+1);

                for (int i = 0; i < countPlayer; i++) {
                    String clientName = playerNames.get(i);
                    int score = handCard[i].getValue();
                    scores.put(clientName, score);
                }
            }else{ // there are several same max values
                for (int i = 0; i < countPlayer; i++) {
                    String clientName = playerNames.get(i);
                    int totalScore = handCard[i].getValue();
                    for (int j = 0; j < 16; j++) {
                        totalScore += discardedCard[i][j].getValue();
                    }
                    scores.put(clientName, totalScore);
                }

                int maxScore = Collections.max(scores.values());
                for (String name : playerNames) {
                    if(scores.get(name) == maxScore){
                        winnerName = name;
                        int winnerIndex = playerNames.indexOf(winnerName);
                        winners.add(winnerIndex);
                        tokens.put(winnerName, tokens.get(winnerName)+1);
                    }
                }
            }
        }
    }

    public void checkGameOver() {
        if (countPlayer == 2) {
            for (String name : playerNames) {
                if(tokens.get(name) == 7){
                    gameOver = true;
                }
            }
        }else if(countPlayer == 3){
            for (String name : playerNames) {
                if(tokens.get(name) == 5){
                    gameOver = true;
                }
            }
        }else {
            for (String name : playerNames) {
                if(tokens.get(name) == 4){
                    gameOver = true;
                }
            }
        }
    }

    // check if all the active players are under protect
    public boolean allPlayersProtected() {
        boolean flag = true;
        for (int i = 0; i < countPlayer; i++) {
            if (status[i] == 1) {
                flag = false;
                break;
            }
        }
        return flag;
    }




}
