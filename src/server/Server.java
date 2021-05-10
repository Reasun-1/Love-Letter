package server;

/**
 * server class listens for incoming clients and manages the communication with the clients
 *
 * @author: Can Ren
 * @author: Pascal Stucky
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class Server {

    private final static Server server = new Server();
    // a set (here is a vector type) for the accepted ServerThreads
    protected static Hashtable<Integer, String> playerList = new Hashtable<>();
    // a set for checking clients´ names
    protected static final Hashtable<String, ServerThread> clientList = new Hashtable<>();

    private boolean gamerunning = false;

    private boolean gameexists = false;

    /**
     * private constructor for singleton implementation
     */
    private Server() {
    }

    /**
     * Singleton access on server
     *
     * @return server instance
     */
    public static Server getServer() {
        return server;
    }

    /**
     * tells the client whether there is an existing game
     *
     * @return String to be transmitted to client
     */
    public String gameExists() {
        if (gameexists) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * tells the client whether there is a running game
     *
     * @return String to be transmitted to client
     */
    public String gameRunning() {
        if (gamerunning) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * Start the Server socket and listen for incoming connections
     *
     * @throws IOException
     */
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
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a new game
     *
     * @param clientName
     * @throws IOException
     */
    public void createGame(String clientName) throws IOException {
        if (gameexists) {
            exception(clientName, "There exists already a Game!");
        } else {
            gameexists = true;
            sendMessageToAll(clientName + " created a new Game");
            for (Enumeration<ServerThread> e = clientList.elements(); e.hasMoreElements(); ) {
                new PrintWriter(e.nextElement().getSocket().getOutputStream(), true).println("/a");
            }
        }
    }

    /**
     * send a message to a particular client
     *
     * @param clientName
     * @param message
     */
    public void sendTo(String clientName, String message) {
        //send message to Client clientName
        try {
            new PrintWriter(clientList.get(clientName).getSocket().getOutputStream(), true).println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a player to the current game
     *
     * @param clientName
     * @throws IOException
     */
    public void addPlayer(String clientName) throws IOException {
        //add Player to PlayerList
        if (gamerunning) {
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

    /**
     * Start the current game with the players who already joined
     *
     * @param clientName
     * @throws IOException
     */
    public void startGame(String clientName) throws IOException {
        if (gamerunning) {
            exception(clientName, "There is already a game running!");
        } else {
            if (playerList.size() < 2) {
                exception(clientName, "Not enough players!");
            } else {
                Game.getInstance().startGame();
            }
        }
    }

    /**
     * play a card in the game class
     *
     * @param cardName
     */
    public void playCard(String cardName) {
        for (Card card : Card.values()) {
            if (card.getType().equals(cardName)) {
                try {
                    Game.getInstance().playCard(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * transmit an error to the client
     *
     * @param name    Name of the client
     * @param message Error message
     * @throws IOException
     */
    public void exception(String name, String message) throws IOException {
        clientList.get(name).receiveOrder("1" + message);
    }

    /**
     * transmit a question to the client
     *
     * @param name    Name of the client
     * @param message Question message
     * @throws IOException
     */
    public void question(String name, String message) throws IOException {
        clientList.get(name).receiveOrder("2" + message);
    }

    /**
     * update the player list due to the updated sorting from the client
     *
     * @param updatedList
     */
    public void updatePlayerIndex(List<String> updatedList) {

        for (int i = 0; i < updatedList.size(); i++) {
            playerList.put(i, updatedList.get(i));
        }
    }


    /**
     * Tell the players how many players will participate and what are the names
     *
     * @throws IOException
     */
    public void startGameInfo() throws IOException {
        for (int i = 0; i < playerList.size(); i++) {
            String startInfo = "4" + playerList.size() + i;

            for (int j = 0; j < playerList.size(); j++) {
                startInfo = startInfo + playerList.get((i + j) % playerList.size()) + "/";
            }
            clientList.get(playerList.get(i)).receiveOrder(startInfo);
        }
    }

    /**
     * transmit an answer to a sent question from the client to the corresponding game method
     *
     * @param answer
     * @param name
     * @throws IOException
     */
    public void receiveAnswer(String answer, String name) throws IOException {
        boolean flag = true;
        if (playerList.contains(answer)) {
            flag = false;
            Game.getInstance().choosePlayer(answer);
        } else {
            for (Card card : Card.values()) {
                if (card.getType().equals(answer)) {
                    flag = false;
                    Game.getInstance().guessType(answer);
                }
            }
        }
        if (flag) {
            question(name, "The chosen player/card doesn't exist. Please choose another one:");
        }
    }


    /**
     * tells the active player which card was drawn
     *
     * @param playerID
     * @param card
     */
    public void drawncard(int playerID, Card card) {
        try {
            clientList.get(playerList.get(playerID)).receiveOrder("5" + card.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * inform the players about the card played by playerName
     *
     * @param card
     */
    public void playedCard(Card card) {

        try {
            for (int i = 0; i < playerList.size(); i++) {
                clientList.get(playerList.get(i)).receiveOrder("6" + card.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * inform the players about a new round and transmit the score and winner of the last round
     *
     * @param score
     * @param winner
     * @throws IOException
     */
    public void roundOver(HashMap<String, Integer> score, String winner) throws IOException {
        for (int i = 0; i < playerList.size(); i++) {
            String scoreString = "7";
            for (int j = 0; j < playerList.size(); j++) {
                String playerscore;
                if (score.get(playerList.get((i + j) % playerList.size())) < 10) {
                    playerscore = "0" + score.get(playerList.get((i + j) % playerList.size())).toString();
                } else {
                    playerscore = score.get(playerList.get((i + j) % playerList.size())).toString();
                }
                scoreString = scoreString + playerscore;
            }
            clientList.get(playerList.get(i)).receiveOrder(scoreString + winner);
        }
    }

    /**
     * inform the players about the end of the game and transmit the final tokens
     *
     * @param tokens
     * @param winner
     * @throws IOException
     */
    public void gameOver(HashMap<String, Integer> tokens, String winner) throws IOException {
        for (int i = 0; i < playerList.size(); i++) {
            String scoreString = "8";
            for (int j = 0; j < playerList.size(); j++) {
                scoreString = scoreString + tokens.get(playerList.get((i + j) % playerList.size())).toString();
            }
            clientList.get(playerList.get(i)).receiveOrder(scoreString + winner);
        }
        gameexists = false;
        gamerunning = false;
        playerList.clear();
    }

    /**
     * inform the players about a player kicked out ouf the current round
     *
     * @param name
     * @throws IOException
     */
    public void outOfRound(String name) throws IOException {
        for (int i = 0; i < playerList.size(); i++) {
            clientList.get(playerList.get(i)).receiveOrder("9" + name);
        }
    }

    /**
     * server sends message to all clients
     *
     * @param message
     */
    public void sendMessageToAll(String message) {
        try {
            synchronized (clientList) {
                for (Enumeration<ServerThread> e = clientList.elements(); e.hasMoreElements(); ) {
                    new PrintWriter(e.nextElement().getSocket().getOutputStream(), true).println("$Server: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * inform the player about dropping its current handcard
     *
     * @param playername
     * @throws IOException
     */
    public void dropCard(String playername) throws IOException {
        clientList.get(playername).receiveOrder("3");
    }

    /**
     * inform the player about its new handcard due to changing
     *
     * @param player
     * @param card
     */
    public void cardsChanged(String player, String card) {
        try {
            clientList.get(player).receiveOrder("b" + card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * inform the player about the handcard of the target
     *
     * @param player
     * @param target
     * @param card
     */
    public void seeCard(String player, String target, String card) {
        try {
            clientList.get(player).receiveOrder("c" + target + "/" + card);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}