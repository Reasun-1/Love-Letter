package server;

/**
 * Game class is the central component of the server side,
 * which contents the total logical functions of the Game Love Letter.
 * Moreover, Game is also applied as a "Database" for the temporary
 * storage of all the information of the cards and players during the game.
 *
 * @author can ren
 * @author yuliia shaparenko
 * @version 1.0-SNAPSHOT
 */

import java.io.IOException;
import java.util.*;

public class Game {

    private final static Game game = new Game();

    int countplayer;
    List<String> playernames; // IMPORTANT:index of the arrayList stands for the players´ index of this round
    Stack<Card> deck;
    List<Card> topcards; // the first Cards drawn at the beginning of every round
    int[] countdiscarded; // the number of discarded cards of each player
    Card[][] discardedcard; // the discarded cards of each player
    int[] status; // 0=game over, 1=in game, 2=protected by handmaid
    Card[] handcard; // the card in hand of each player
    Card[] drawncard; // the drawn card of each player
    Card[] playedcard; // temporary storage for the played card
    int[] seencard; // clientIndex for been seen, -1 for not seen
    HashMap<String, Integer> tokens; // who has how many tokens in this game already
    HashMap<String, Integer> scores; // score of each player in this round
    List<Integer> winners; // winners´ index of each round

    boolean gameover;
    boolean roundover;
    int playerinturn;
    boolean waitingforcard;
    boolean waitingforchooseplayer;
    boolean waitingforguesstyp;

    /**
     * constructor Game:
     * With singleton (DCL), the constructor will return only one instance of the class Game.
     * @return only one instance of the Game
     */
    public static Game getInstance() {
        return game;
    }

    /**
     * This method startGame() will start a new game for the players.
     * In the while-loop, the game-over condition will be checked. If not enough tokens are gained
     * by the players, the game will continue to a new round, till the game-over condition is accomplished.
     * @throws IOException
     */
    public void startGame() throws IOException {

        //initialize the game info
        countplayer = Server.playerList.size();

        playernames = new ArrayList<>(countplayer);

        gameover = false;
        discardedcard = new Card[countplayer][16];
        countdiscarded = new int[countplayer];
        handcard = new Card[countplayer];
        drawncard = new Card[countplayer];
        playedcard = new Card[countplayer];
        seencard = new int[countplayer];
        status = new int[countplayer];
        scores = new HashMap<>(countplayer);
        tokens = new HashMap<>(countplayer);
        topcards = new ArrayList<>();
        winners = new ArrayList<>();

        // initialize the tokesn for all the players
        for (int i = 0; i < countplayer; i++) {
            String name = Server.playerList.get(i);
            tokens.put(name, 0);
        }

        // Welcome info. for all: how many players are in this game, and what their names are
        Server.getServer().startGameInfo();
        System.out.println("testFlagGame00");
        // game begins
            System.out.println("testFlagGame01");
            game.newRound();

    }

    /**
     * This method newRound() simulates the scenario during one play round.
     * In the while-loop, the round-over condition will be checked. If the condition
     * is accomplished, the current round will be over and a new round will be started.
     * Otherwise, round will continue.
     * @throws IOException
     */
    public void newRound() throws IOException {

        // reset the round info
        for (int i = 0; i < discardedcard.length; i++) {
            Arrays.fill(discardedcard[i], null);
        }
        Arrays.fill(countdiscarded, 0);
        Arrays.fill(handcard, null);
        Arrays.fill(drawncard, null);
        Arrays.fill(playedcard, null);
        Arrays.fill(seencard, -1);
        Arrays.fill(status, 1);


        topcards.clear();
        roundover = false;
        playerinturn = 0;

        // update playerIndex
        if (winners.size() == 0) {
            for (int i = 0; i < countplayer; i++) {
                playernames.add(Server.playerList.get(i));
            }
        } else { // winner in last round plays first
            int lastWinner = winners.get(winners.size() - 1);

            // the player won last round plays first in this round
            for (int i = 0; i < lastWinner; i++) {
                playernames.add(playernames.get(i));
            }
            for (int i = 0; i < lastWinner; i++) {
                playernames.remove(0);
            }
            // update the player index in this round
            Server.getServer().updatePlayerIndex(playernames);
        }

        // reset the scores
        for (String name : playernames) {
            scores.put(name, 0);
        }

        deck = Card.shuffle();

        if (countplayer == 2) { // move three cards away if 2 player play
            for (int i = 0; i < 3; i++) {
                topcards.add(deck.pop());
            }
        } else { // otherwise move only one card away
            topcards.add(deck.pop());
        }


        Server.getServer().sendMessageToAll("deck shuffled \n" + "top card(s) moved");

        // each player draws one card
        for (int i = 0; i < countplayer; i++) {
            handcard[i] = deck.pop();
            // tell each player which card he/she has drawn
            Server.getServer().drawncard(i, handcard[i]);
        }

        drawncard[playerinturn] = deck.pop();

        // inform player which card he/she has drawn
        Server.getServer().drawncard(playerinturn, drawncard[playerinturn]);
        // tell all players that the player in turn is playing
        Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " has drawn one card and is playing...");

    }


    /**
     * This method is applied for other class (server) to invoke.
     * @param card is given from client side and via server in the game transported

    public void cardPlayed(Card card) {
        System.out.println("TestFlagPlayCard2");
        playedcard[playerinturn] = card;
        System.out.println(playedcard[playerinturn].getType());
    }*/

    /**
     * This method playCard() checks first whether the played card is valid in respect of the game rules.
     * In the while-loop, the type of the played card will be selected and the according function of the
     * played card will be applied.
     * @throws IOException
     */
    public void playCard(Card card) throws IOException {

        playedcard[playerinturn] = card;
                // check whether the played card if one of the handcard and drawncard
                if (playedcard[playerinturn] != handcard[playerinturn] && playedcard[playerinturn] != drawncard[playerinturn]) {
                    System.out.println("TestWrongCard");
                    Server.getServer().exception(playernames.get(playerinturn), "You don´t have this card in your hand, choose one in hand.");
                    // if player has king or prince and also a countess in hand, countess must be played
                } else if (playedcard[playerinturn] != Card.COUNTESS
                        && (((handcard[playerinturn] == Card.PRINCE || handcard[playerinturn] == Card.KING) && drawncard[playerinturn] == Card.COUNTESS)
                        || ((drawncard[playerinturn] == Card.PRINCE || drawncard[playerinturn] == Card.KING) && handcard[playerinturn] == Card.COUNTESS))) {
                    System.out.println("TestWrongCard2");
                    Server.getServer().exception(playernames.get(playerinturn), "You have royal member in your hand, the countess must be played.");
                } else {
                    System.out.println("TestWrongCard3");
                    // update the handcard and drawncard
                    if (playedcard[playerinturn] == handcard[playerinturn]) {
                        handcard[playerinturn] = drawncard[playerinturn];
                        drawncard[playerinturn] = null;
                    } else {
                        drawncard[playerinturn] = null;
                    }
                }
        System.out.println("TestFlagPlayCard3");
        // inform all the players who has played which card
        Server.getServer().playedCard(playedcard[playerinturn]);
        // apply the card function
        switch (playedcard[playerinturn]) {

            case PRINCESS:
                Card.PRINCESS.function(playerinturn);
                break;

            case COUNTESS:
                Card.COUNTESS.function(playerinturn);
                break;

            case KING:
                waitingforchooseplayer = true;
                String targetname1;
                int targetindex1 = playerinturn; // initialize the target as self

                // if all players are protected, the function goes to self
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.KING.function(playerinturn, targetindex1); // nothing happens, just change the card with self.
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is choosing a target player.");

                    while (waitingforchooseplayer) {

                        targetname1 = Server.getServer().choosePlayer(playerinturn);

                        if (targetname1 == null) {
                            continue;
                        } else if(targetname1.equals(playernames.get(playerinturn))){
                            Server.getServer().exception(playernames.get(playerinturn), "You can´t choose yourself, choose another player.");
                        } else if (status[playernames.indexOf(targetname1)] == 2) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is protected, choose another player");
                        } else if (status[playernames.indexOf(targetname1)] == 0) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetindex1 = playernames.indexOf(targetname1);
                            Card.KING.function(playerinturn, targetindex1);
                            waitingforchooseplayer = false;
                        }
                    }
                }
                break;

            case PRINCE:
                waitingforchooseplayer = true;
                String targetname2;
                int targetindex2 = playerinturn; // initialize the target as self
                status[playerinturn] = 1; // reset the status from 2 to 1, if status was 2.

                // if all players are protected, the function goes to self
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, drop a card and draw a new one.");
                    Card.PRINCE.function(playerinturn, targetindex2);
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is choosing a target player.");

                    while (waitingforchooseplayer) {

                        targetname2 = Server.getServer().choosePlayer(playerinturn);

                        if (targetname2 == null) {
                            continue;
                        } else if (status[playernames.indexOf(targetname2)] == 2) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is protected, choose another player");
                        } else if (status[playernames.indexOf(targetname2)] == 0) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetindex2 = playernames.indexOf(targetname2);
                            Card.PRINCE.function(playerinturn, targetindex2);
                            waitingforchooseplayer = false;
                        }
                    }
                }
                break;

            case HANDMAID:
                Card.HANDMAID.function(playerinturn);
                break;

            case BARON:
                waitingforchooseplayer = true;
                String targetname3;
                int targetindex3 = playerinturn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.BARON.function(playerinturn, targetindex3); // nothing happens, just compare the card with self
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is choosing a target player.");

                    while (waitingforchooseplayer) {

                        targetname3 = Server.getServer().choosePlayer(playerinturn);

                        if (targetname3 == null) {
                            continue;
                        } else if(targetname3.equals(playernames.get(playerinturn))){
                            Server.getServer().exception(playernames.get(playerinturn), "You can´t choose yourself, choose another player.");
                        } else if (status[playernames.indexOf(targetname3)] == 2) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is protected, choose another player");
                        } else if (status[playernames.indexOf(targetname3)] == 0) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetindex3 = playernames.indexOf(targetname3);
                            Card.BARON.function(playerinturn, targetindex3);
                            waitingforchooseplayer = false;
                        }
                    }
                }
                break;

            case PRIEST:
                waitingforchooseplayer = true;
                String targetname4;
                int targetindex4 = playerinturn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Server.getServer().sendMessageToAll("All the players are protected, nothing happened, play continues.");
                    Card.PRIEST.function(playerinturn, targetindex4); // nothing happens
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is choosing a target player.");

                    while (waitingforchooseplayer) {

                        targetname4 = Server.getServer().choosePlayer(playerinturn);

                        if (targetname4 == null) {
                            continue;
                        } else if(targetname4.equals(playernames.get(playerinturn))){
                            Server.getServer().exception(playernames.get(playerinturn), "You can´t choose yourself, choose another player.");
                        } else if (status[playernames.indexOf(targetname4)] == 2) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is protected, choose another player");
                        } else if (status[playernames.indexOf(targetname4)] == 0) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetindex4 = playernames.indexOf(targetname4);
                            Card.PRIEST.function(playerinturn, targetindex4);
                            waitingforchooseplayer = false;
                        }
                    }
                }
                break;

            case GUARD:
                waitingforchooseplayer = true;
                waitingforguesstyp = true;
                String targetname5;
                String guesscardtype;
                Card guesscard = Card.GUARD; // intitialize the target as self
                int targetindex5 = playerinturn; // initialize the target as self

                // if all players are protected, the function goes to self = nothing happens
                if (allPlayersProtected()) {
                    Card.GUARD.function(playerinturn, targetindex5, guesscard); // nothing happens
                    Server.getServer().sendMessageToAll("All players are protected, nothing happens, play continues.");
                } else { // if there are unprotected active players in this round, choose one

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is choosing a target player.");

                    while (waitingforchooseplayer) {

                        targetname5 = Server.getServer().choosePlayer(playerinturn);

                        if (targetname5 == null) {
                            continue;
                        } else if(targetname5.equals(playernames.get(playerinturn))){
                            Server.getServer().exception(playernames.get(playerinturn), "You can´t choose yourself, choose another player.");
                        } else if (status[playernames.indexOf(targetname5)] == 2) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is protected, choose another player");
                        } else if (status[playernames.indexOf(targetname5)] == 0) {
                            Server.getServer().exception(playernames.get(playerinturn), "the chosen player is already out of game, choose another player");
                        } else {
                            targetindex5 = playernames.indexOf(targetname5);
                            Card.GUARD.function(playerinturn, targetindex5);
                            waitingforchooseplayer = false;
                        }
                    }

                    Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " is guessing the card type");

                    while (waitingforguesstyp) {
                        guesscardtype = Server.getServer().guessCardType(playerinturn);
                        guesscard = null;

                        for (Card c : Card.values()) {
                            if (c.getType().equals(guesscardtype)) {
                                guesscard = c;
                            }
                        }

                        if (guesscard == null) {
                            Server.getServer().exception(playernames.get(playerinturn), "This card type does not exist. Choose again.");
                            continue;
                        } else if (guesscard == Card.GUARD) {
                            Server.getServer().exception(playernames.get(playerinturn), "You can´t guess guard, guess another card type.");
                            continue;
                        } else {
                            waitingforguesstyp = false;
                        }
                    }
                    Card.GUARD.function(playerinturn, targetindex5, guesscard);
                }
                break;
            default:
                //***********brauchen wir hier Exceptions eg.? Mir ist nichts aufgefallen********
                break;
        }

        // playedcard refresh to null for next round check
        playedcard[playerinturn] = null;

        playerinturn++;
        if (playerinturn == countplayer) playerinturn = 0;
        // find the active player in next turn
        while (status[playerinturn] == 0) {
            playerinturn++;
            if (playerinturn == countplayer) playerinturn = 0;
        }

        checkRoundOver();

        if(!roundover) {
            drawncard[playerinturn] = deck.pop();
            // inform player which card he/she has drawn
            Server.getServer().drawncard(playerinturn, drawncard[playerinturn]);
            // tell all players that the player in turn is playing
            Server.getServer().sendMessageToAll(playernames.get(playerinturn) + " has drawn one card and is playing...");
        }
        else{
            String winnername = updateScoresAndTokensAndWinnersForThisRound();
            Server.getServer().roundOver(scores, winnername);
            checkGameOver();
            if (!gameover){
                newRound();
            }
            else
            {
                //check who won the game
                Integer maxTokens = Collections.max(tokens.values());
                String finalwinner = "";
                for (String player : tokens.keySet()) {
                    if (tokens.get(player) == maxTokens){
                        finalwinner = player;
                        break;
                    }
                }
                // tell everyone who won the game
                Server.getServer().gameOver(tokens, finalwinner);
                Server.getServer().sendMessageToAll("The final winner of this game is " + finalwinner);
            }
        }
    }

    public void checkRoundOver() {
        int numActivePlayer = 0;
        for (int i = 0; i < countplayer; i++) {
            if (status[i] != 0) {
                numActivePlayer++;
            }
        }
        if (numActivePlayer == 1 || deck.size() == 0) {
            roundover = true;
        }
    }


    /**
     * This method will update the scores, tokens and winner name at the end of
     * every round.
     * @return winner name of this round will be returned
     */
    public String updateScoresAndTokensAndWinnersForThisRound() {
        int numactiveplayer = 0;
        for (int i = 0; i < countplayer; i++) {
            if (status[i] != 0) {
                numactiveplayer++;
            }
        }

        String winnername = null;

        if (numactiveplayer == 1) { // there is only active player left
            for (int i = 0; i < countplayer; i++) {
                if (status[i] == 1) {
                    winners.add(i);
                    winnername = playernames.get(i);
                    scores.put(winnername, handcard[i].getValue());
                    tokens.put(winnername, tokens.get(winnername) + 1);
                    break;
                }
            }
        } else { // there are several active players left when this round is over
            int max = 0;
            List<Integer> playerindexwithmax = new ArrayList<>();
            // get the max. value of hand card
            for (int i = 0; i < countplayer; i++) {
                if(handcard[i] != null && status[i] != 0){
                    max = Math.max(max, handcard[i].getValue());
                }
            }
            // get index of the player(s) with the max value
            for (int i = 0; i < countplayer; i++) {
                if (handcard[i] != null && status[i] != 0 && handcard[i].getValue() == max) {
                    playerindexwithmax.add(i);
                }
            }

            if (playerindexwithmax.size() == 1) { // if there is only one max Value
                int winnerIndex = playerindexwithmax.get(0);
                winnername = playernames.get(winnerIndex);
                winners.add(winnerIndex);
                tokens.put(winnername, tokens.get(winnername) + 1);

                for (int i = 0; i < countplayer; i++) {
                    String clientName = playernames.get(i);
                    // the player out of game has no score
                    if(status[i] != 0){
                        int score = handcard[i].getValue();
                        scores.put(clientName, score);
                    }
                }
            } else { // there are several same max values
                for (int i = 0; i < countplayer; i++) {
                    String clientname = playernames.get(i);
                    int totalScore = 0;
                    if(status[i] != 0){
                        totalScore = handcard[i].getValue();
                        for (int j = 0; j < 16; j++) {
                            if(discardedcard[i][j] != null){
                                totalScore += discardedcard[i][j].getValue();
                            }
                        }
                    }
                    scores.put(clientname, totalScore);
                }

                int maxscore = Collections.max(scores.values());
                for (String name : playernames) {
                    if (scores.get(name) == maxscore) {
                        winnername = name;
                        int winnerIndex = playernames.indexOf(winnername);
                        winners.add(winnerIndex);
                        tokens.put(winnername, tokens.get(winnername) + 1);
                    }
                }
            }
        }
        return winnername;
    }

    /**
     * This method checkGameOver() will check whether the condition of game over accomplished in
     * respect of the game rules.
     */
    public void checkGameOver() {
        if (countplayer == 2) {
            for (String name : playernames) {
                if (tokens.get(name) == 7) {
                    gameover = true;
                }
            }
        } else if (countplayer == 3) {
            for (String name : playernames) {
                if (tokens.get(name) == 5) {
                    gameover = true;
                }
            }
        } else {
            for (String name : playernames) {
                if (tokens.get(name) == 4) {
                    gameover = true;
                }
            }
        }
    }


    /**
     * This method allPlayersProtected() checked whether all the other active players in this
     * round are protected by handmaid.
     * @return false when not all the active players are protected by handmaids, otherwise return true.
     */
    public boolean allPlayersProtected() {
        boolean flag = true;
        for (int i = 0; i < countplayer; i++) {
            if (status[i] == 1 && playerinturn != i) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
