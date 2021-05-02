package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import javafx.application.Application;
import javafx.stage.Stage;
import server.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    // socket for the TCP connection
    private volatile Socket socket;
    // writer for outgoing messages
    private final PrintWriter out;
    // reader for incoming messages
    private final BufferedReader in;
    // reader for outgoing messages
    //private final BufferedReader reader;

    private String errorMsg;


    private String[] playerList;

    private Card handCard;

    private Card drawnCard;

    private Card playedCard;

    private Card[][] discardedCards;

    private int[] tokens;

    public Card getHandCards() {
        return handCard;
    }

    private String name;

    private String winnerOfLastRound;

    private String winner;

    //public Card[] getDiscardedCards(){
    //return discardedCards;
    //}
    public BufferedReader getIn(){ return in; }

    public PrintWriter getOut(){ return out; }

    public Socket getSocket() {
        return socket;
    }

    public String getName(){
        return name;
    }

    public String getErrorMsg(){return errorMsg;}

    public String getWinnerOfLastRound(){return winnerOfLastRound;}

    public String getWinner(){return winner;}

    public String[] getPlayerList(){return playerList;}

    public int[] getTokens() {
        return tokens;
    }

    public Client() throws IOException{

            // Always connect to localhost and fixed port (maybe ask for ip and port?)
            socket = new Socket("127.0.0.1", 5200);

            // Create writer to send messages to server via the TCP-socket
            out = new PrintWriter(socket.getOutputStream(), true);

            // Create reader to receive messages from server via the TCP-socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            name = "";

            errorMsg = "";


        // Create reader for user input (currently via terminal)
        //reader = new BufferedReader(new InputStreamReader(System.in));
        // start login process
    }

    public void checkName(String temp_name) {

        // Ask for the clients name (currently via terminal)
        //System.out.println("Please enter your name:"); // Soon: Open Login-Window
        try {
            if (temp_name.contains("/")) {
                errorMsg = "The name must not contain the symbol '/'!";//errorMessage
            } else {
                out.println(temp_name);
                // Check whether the name is still available (if not, ask again)
                String answer = in.readLine();
                if (answer.equals("user existed!")) {
                    // Ask for a different name (currently via terminal)
                    errorMsg = "The chosen name already exists. Please choose another name:";// errorMessage
                } else {
                    // Print welcome-message (currently via terminal)
                    name = answer;
                    errorMsg = "done";
                    // Soon: Open Chat-Window and print welcome-message
                    //launcher.launchChat();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendPersonalMessage(String name, String msg) throws IOException {
        out.println("/1" + name + "/" + msg);
    }

    public void createGame() {
        out.println("/2");
    }

    public void joinGame() {
        out.println("/3");
    }

    public void startGame() {
        out.println("/4");
    }

    public void playCard(Card card) {
        if (card.equals(handCard)) {
            handCard = drawnCard;
            drawnCard = null;
        } else {
            drawnCard = null;
        }
        out.println("/5" + card.getType());
    }

    public void startGameInfo(String info){
        int numberOfPlayers = info.charAt(0);
        playerList = new String[numberOfPlayers];
        String rest = info.substring(1);
        for (int i = 0; i < numberOfPlayers; i++) {
            playerList[i] = info.substring(1, info.indexOf('/'));
            rest = rest.substring(info.indexOf('/'));
        }
        discardedCards = new Card[numberOfPlayers][5];
        tokens = new int[numberOfPlayers];
        Arrays.fill(discardedCards, null);
        handCard = null;
        out.println("done");
    }

    public void setDrawnCard(String cardName){
        for (Card card : Card.values()) {
            if (card.getType().equals(cardName)) {
                if (handCard == null) {
                    handCard = card;
                } else {
                    drawnCard = card;
                }
            }
        }
        out.println("done");
    }

    public void setPlayedCard(String playerCard){
        String player = playerCard.substring(0, playerCard.indexOf('/'));
        for (Card card : Card.values()) {
            if (card.getType().equals(playerCard.substring(playerCard.indexOf('/')))) {
                playedCard = card;
            }
        }
        int index = 0;
        while (discardedCards[find(playerList, player)][index] == null) {
            index++;
        }
        discardedCards[find(playerList, player)][index] = playedCard;
        String nextPlayer = playerList[(find(playerList, player) + 1) % playerList.length];
        out.println("done");
    }

    public void endOfRound(String info){
        Arrays.fill(discardedCards, null);
        handCard = null;
        for (int i = 0; i < playerList.length; i++) {
            tokens[i] = info.charAt(i);
        }
        winnerOfLastRound = info.substring(playerList.length + 1);
        out.println("done");
    }

    public void endOfGame(String info){
        for (int i = 0; i < playerList.length; i++) {
            tokens[i] = info.charAt(i);
        }

        out.println("done");
    }

    public int find(String[] array, String element) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                index = i;
            }
        }
        return index;
    }


    public void sendMessage(String msg){
            // check logout condition
            if (msg.equals("bye")) {
                out.println("/0quit");
                // stop the connection
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Confirm logout (currently via terminal)
                System.out.println("You left the room.");
                // Soon: Logout-Window
            } else {
                // send message to server
                out.println("$" + msg);
            }
    }
}
