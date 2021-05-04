package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Server {

    //*********UPDATE THE INFO FROM GAME AND FURTHER TO CLIENTS!!*****************
    //option: new attributes in server class:
    //Card[] handCard; Card[] drawnCard; Card[][] discardedCards; Card[] playedCard;

    private volatile static Server server;
    // a set (here is a vector type) for the accepted ServerThreads
    protected static Hashtable<Integer, String> playerList = new Hashtable<Integer, String>();
    // a set for checking clients´ names
    protected static Hashtable<String, ServerThread> clientList = new Hashtable<String, ServerThread>();

    private boolean gamerunning = false;

    private boolean gameexists = false;

    private Server() {
    }

    //Singleton with DCL (double-checked locking)
    public static Server getServer() {
        if (server == null) {
            synchronized (Server.class) {
                if (server == null) {
                    server = new Server();
                }
            }
        }
        return server;
    }

    public String gameExists(){
        if(gameexists){
            return "1";
        }
        else {
            return "0";
        }
    }

    public String gameRunning(){
        if(gamerunning){
            return "1";
        }
        else {
            return "0";
        }
    }

    //public static List<server.ServerThread> getThreads() {
      //  return threads;
    //}

    public void start() throws IOException {
        // create the server and define the port nr.
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        // accept the client request
        while (flag) {
            try {
                // when new client comes, will be put into the thread-set
                // with synchronized, there is only one thread at one time
                Socket clientSocket = server.accept();
                synchronized (clientList) {
                    ServerThread thread = new ServerThread(clientSocket);
                    new Thread(thread).start();
                }
                // several server threads will respond to the client requests

                //catch the exception
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
            }
        }
        //close the server
        server.close();
    }

    public static void main(String[] args) {
        try{
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGame(String clientName) throws IOException{
        if(gameexists){
            exception(clientName, "There exists already a Game!");
        }
        else {
            Game.getInstance();
            gameexists = true;
            sendMessageToAll(clientName + "created a new Game");
        }
    }

    public void sendTo(String clientName, String message){
        //send message to Client clientName
        try{
            new PrintWriter(clientList.get(clientName).getSocket().getOutputStream(), true).println("$Server: " + message);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addPlayer(String clientName) throws IOException{
        //add Player to PlayerList
        if(gamerunning){
            exception(clientName, "There is already a game running!");
        } else if (playerList.contains(clientName)) {
            exception(clientName, "You already joined the Game!");
        } else if (playerList.size() < 4) {
                playerList.put(playerList.size(), clientName);
                sendMessageToAll(clientName + " has joined the Game");
        } else {
                exception(clientName, "Too many players!");
        }
    }

    public void startGame(String clientName) throws IOException{
        if(gamerunning){
            exception(clientName, "There is already a game running!");
        } else {
            if (playerList.size() < 2) {
                exception(clientName, "Not enough players!");
            } else {
                Game.getInstance().startGame();
            }
        }
    }

    public void playCard(String cardName){
        for (Card card : Card.values()) {
            if (card.getType().equals(cardName)) {
                Game.getInstance().cardPlayed(card);
            }
        }
    }

    public void exception(String name, String msg) throws IOException {
        clientList.get(name).receiveOrder("1" + msg);
    }

    public String choosePlayer(int playerID) throws IOException{
        //ask the active player to choose another player
        String chosenPlayer = clientList.get(playerList.get(playerID)).receiveOrder("2").substring(1);
        while (!playerList.contains(chosenPlayer)){
            exception(playerList.get(playerID), "The chosen player doesn't exist!");
            chosenPlayer = clientList.get(playerList.get(playerID)).receiveOrder("2").substring(1);
        }
        return chosenPlayer;
    }

    public String guessCardType(int playerID) throws IOException{
        //ask the active player to choose a card (no error handling yet)
        return clientList.get(playerList.get(playerID)).receiveOrder("3").substring(1);
    }


    public void updatePlayerIndex(List<String> updatedList){

        for (int i = 0; i < updatedList.size(); i++) {
            playerList.put(i, updatedList.get(i));
        }
    }

    //public String guessCardType(int playerID){
      //  return ""; // zu implementieren..
    //}

    // Tell the players how many players will participate and what are the names
    public void startGameInfo() throws IOException{
        for(int i=0;i<playerList.size();i++) {
            String startInfo = "4" + playerList.size() + i;
            for (int j = 0; j < playerList.size(); j++) {
                startInfo = startInfo + playerList.get((i + j) % playerList.size()) + "/";
            }
            clientList.get(playerList.get(i)).receiveOrder(startInfo);
        }
    }

    // tells the active player which card was drawn
    public void drawnCard(int playerID, Card card){
        try{
            clientList.get(playerList.get(playerID)).receiveOrder("5" + card.getType());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // inform the players about the card played by playerName
    public void playedCard(String playerName, Card card){

        try{
            for(int i=0;i<playerList.size();i++) {
                clientList.get(playerList.get(i)).receiveOrder("6" + playerName + "/" + card.getType());
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    // inform the players about a new round and transmit the current score
    //************über scores soll mann auch wissen, laut Aufgabestellung***********
    public void roundOver(HashMap<String, Integer> tokens, String winner) throws IOException{
        for(int i=0;i<playerList.size();i++){
            String scoreString = "7";
            for (int j = 0; j < playerList.size(); j++) {
                scoreString = scoreString + tokens.get(playerList.get((i + j) % playerList.size())).toString() + winner;
            }
            clientList.get(playerList.get(i)).receiveOrder(scoreString);
        }
    }

    // inform the players about the end of the game and transmit the final tokens
    public void gameOver(HashMap<String, Integer> tokens) throws IOException{
        for(int i=0;i<playerList.size();i++){
            String scoreString = "8";
            for (int j = 0; j < playerList.size(); j++) {
                scoreString = scoreString + tokens.get(playerList.get((i + j) % playerList.size())).toString();
            }
            clientList.get(playerList.get(i)).receiveOrder(scoreString);
        }
    }

    //server sends message to all clients
    public void sendMessageToAll(String message){
        try{
            synchronized (clientList) {
                for (Enumeration<ServerThread> e = clientList.elements(); e.hasMoreElements();) {
                    new PrintWriter(e.nextElement().getSocket().getOutputStream(), true).println("$Server: " + message);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}