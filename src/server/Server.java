package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public Game createGame(){
        //create a new game
        return null;
    }

    public void sendTo(String clientName, String message){
        //send message to Client clientName
    }

    public void addPlayer(String clientName){
        //add Player to PlayerList
        playerList.put(playerList.size(), clientName);
    }

    public void startGame(){
        //initiate the Gameplay
    }

    public void playCard(String cardName){
        //play the card cardName
    }

    public String choosePlayer(int playerID) throws IOException{
        //ask the active player to choose another player
        String chosenPlayer = clientList.get(playerList.get(playerID)).receiveOrder("2");
        while (!playerList.contains(chosenPlayer)){
            clientList.get(playerList.get(playerID)).receiveOrder("1The chosen player doesn't exist!");
            chosenPlayer = clientList.get(playerList.get(playerID)).receiveOrder("2");
        }
        return chosenPlayer;
    }

    public String chooseCard(int playerID) throws IOException{
        //ask the active player to choose a card (no error handling yet)
        return clientList.get(playerList.get(playerID)).receiveOrder("3");
    }

    //***********************zu ergänzen****************

    public void updatePlayerIndex(List<String> updatedList){

        for (int i = 0; i < updatedList.size(); i++) {
            playerList.clear();
            playerList.put(i, updatedList.get(i));
        }
    }

    public String guessCardType(int playerID){
        return ""; // zu implementieren..
    }

    // update the played Cards infos and the result after each play => for the server and client
    public void updatePlay(){

    }

    //server sends message to all clients
    public void sendMessageToAll(String message){

    }
}