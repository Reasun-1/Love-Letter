package client.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Client class is responsible for the connection to the server and for storing the properties connected with the GUI.
 * It also holds the main method which starts the Application.
 *
 * @author Pascal Stucky
 * @author Can Ren
 * @version 1.0-SNAPSHOT
 */
public class Client extends Application {
    // Socket for the TCP connection
    private volatile Socket socket;
    // Writer for outgoing messages
    private final PrintWriter OUT;
    // Reader for incoming messages
    private final BufferedReader IN;
    // Launcher for new Windows
    private final WindowLauncher LAUNCHER = new WindowLauncher();
    // Name can be chosen in the login process
    private String name;

    // Binding to Chat Window for displaying incoming messages
    private final StringProperty CHATHISTORY = new SimpleStringProperty();

    // List-of-Players-binding to Chat Window
    private final StringProperty[] PLAYERS = new StringProperty[4];

    // Binding to Chat Window
    private final StringProperty PLAYERINTURN = new SimpleStringProperty();

    // Position of the player in turn (relatively to this client)
    private int playerinturnid;

    // Number of players in the current game
    private int numberofplayers;

    // List of players out of round
    private final StringProperty OUTOFROUND = new SimpleStringProperty();

    // Bindings to display the hand card of each player
    private final IntegerProperty[] HANDCARD = new IntegerProperty[4];

    // Bindings to display the drawn card of each player
    private final IntegerProperty[] DRAWNCARD = new IntegerProperty[4];

    // Bindings to list the discarded cards of each player
    private final StringProperty DISCARDEDCARDS = new SimpleStringProperty("");

    // Bindings to list the current tokens of each player
    private final IntegerProperty[] TOKENS = new IntegerProperty[4];

    // Bindings to list the current tokens of each player
    private final StringProperty[] SCORE = new StringProperty[4];

    // Bindings enables/disables certain buttons (see ChatWindowController)
    private final BooleanProperty GAMEEXISTS = new SimpleBooleanProperty();
    private final BooleanProperty GAMERUNNING = new SimpleBooleanProperty();
    private final BooleanProperty INTURN = new SimpleBooleanProperty(false);

    private final List<String> CARDS = new ArrayList<>(9);

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return IN;
    }

    public PrintWriter getOut() {
        return OUT;
    }

    public String getName() {
        return name;
    }

    public StringProperty getChatHistory() {
        return CHATHISTORY;
    }

    public StringProperty getPlayers(int playerindex) {
        return PLAYERS[playerindex];
    }

    public StringProperty getPlayerInTurn() {
        return PLAYERINTURN;
    }

    public StringProperty getOutOfRound() {
        return OUTOFROUND;
    }

    public IntegerProperty getHandCard(int playerindex) {
        return HANDCARD[playerindex];
    }

    public IntegerProperty getTOKENS(int playerindex) {
        return TOKENS[playerindex];
    }

    public StringProperty getScore(int playerindex) {
        return SCORE[playerindex];
    }

    public IntegerProperty getDrawnCard(int playerindex) {
        return DRAWNCARD[playerindex];
    }

    public StringProperty getDiscardedCards() {
        return DISCARDEDCARDS;
    }

    public BooleanProperty getGameExists() {
        return GAMEEXISTS;
    }

    public BooleanProperty getGameRunning() {
        return GAMERUNNING;
    }

    public BooleanProperty getInTurn() {
        return INTURN;
    }

    /**
     * Constructor establishes the TCP-Connection and initializes the remaining variables
     *
     * @throws IOException
     */
    public Client() throws IOException {

        // Always connect to localhost and fixed port (maybe ask for ip and port?)
        socket = new Socket("127.0.0.1", 5200);
        //socket = new Socket("sep21.dbs.ifi.lmu.de", 52018);

        // Create writer to send messages to server via the TCP-socket
        OUT = new PrintWriter(socket.getOutputStream(), true);

        // Create reader to receive messages from server via the TCP-socket
        IN = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Start with an empty name, will be set during Login
        name = "";

        CHATHISTORY.set("");

        for (int i = 0; i < 4; i++) {
            PLAYERS[i] = new SimpleStringProperty();
            HANDCARD[i] = new SimpleIntegerProperty(9);
            DRAWNCARD[i] = new SimpleIntegerProperty(9);
            TOKENS[i] = new SimpleIntegerProperty();
            SCORE[i] = new SimpleStringProperty("");
        }
        // The first input from the Server will be info about a Game existing/running on this Server
        String gameinfo = IN.readLine();
        GAMEEXISTS.set(gameinfo.charAt(0) != '0');
        GAMERUNNING.set(gameinfo.charAt(1) != '0');

        // Initialize the list of cards
        CARDS.add("back");
        CARDS.add("guard");
        CARDS.add("priest");
        CARDS.add("baron");
        CARDS.add("handmaid");
        CARDS.add("prince");
        CARDS.add("king");
        CARDS.add("countess");
        CARDS.add("princess");
    }

    /**
     * Method to be called from LoginWindowController to check the entered name.
     *
     * @param temp_name
     */
    public void checkName(String temp_name) {
        try {
            // The name must not contain a '/', since we use this character as a separator for game info
            if (temp_name.contains("/")) {
                // Open error Window, then restart the Login
                LAUNCHER.launchError("The name must not contain the symbol '/'!");
                LAUNCHER.launchLogin(this);
            } else if (CARDS.contains(temp_name)) {
                LAUNCHER.launchError("That's a card, not a name!");
                LAUNCHER.launchLogin(this);
            } else {
                // Send name to Server to check whether it is available
                OUT.println(temp_name);
                String answer = IN.readLine();
                if (answer.equals("user existed!")) {
                    // Open error Window, then restart the Login
                    LAUNCHER.launchError("The chosen name already exists. Please choose another name:");
                    LAUNCHER.launchLogin(this);
                } else {
                    // Store the chosen name
                    name = answer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to only one client. The name and the message are separated by '/'. Order Code: 1
     *
     * @param name
     * @param message
     */
    public void sendPersonalMessage(String name, String message) {
        OUT.println("/1" + name + "/" + message);
    }

    /**
     * Create a new Game (only possible if no Game exists on the Server). Order Code: 2
     */
    public void createGame() {
        GAMEEXISTS.set(true);
        OUT.println("/2");
    }

    /**
     * Join an existing Game (only possible if a Game exists and there are less than 4 players registered). Order Code: 3
     */
    public void joinGame() {
        //handcard[0].set(0);
        OUT.println("/3");
    }

    /**
     * Start an existing Game (only possible if this client has joined the Game). Order Code: 4
     */
    public void startGame() {
        OUT.println("/4");
    }

    /**
     * Play your hand card (only possible if this client is player in turn in the current game). Order Code: 5
     */
    public void playHandCard() {

        OUT.println("/5" + CARDS.get(HANDCARD[0].get()));
        int playedcard = HANDCARD[0].get();
        HANDCARD[0].set(DRAWNCARD[0].get());
        DRAWNCARD[0].set(playedcard);
    }

    /**
     * Play your hand card (only possible if this client is player in turn in the current game). Order Code: 5
     */
    public void playDrawnCard() {
        OUT.println("/5" + CARDS.get(DRAWNCARD[0].get()));
    }

    /**
     * Receive the Game info from the Server
     *
     * @param info
     */
    public void startGameInfo(String info) {
        // The first character is the number of players
        //numberofplayers = info.charAt(0); // war ASCII 2 => hei??t war 50

        numberofplayers = Integer.valueOf(Character.toString((char) info.charAt(0)));

        // The second character encodes the start player
        //playerinturnid = (- (int) info.charAt(1)) % numberofplayers;
        playerinturnid = (numberofplayers - Integer.valueOf(Character.toString((char) info.charAt(1)))) % numberofplayers;
        // GUI cannot be directly updated from a non-application thread, here a Runnable object is needed.
        //Platform.runLater(new Runnable() {
        //  @Override
        //public void run() {
        // The rest of the String contains the names of the other players separated by a '/'
        //String rest = info.substring(1);
        String rest = info.substring(2) + '0';

        for (int i = 0; i < numberofplayers; i++) {
            PLAYERS[i].set(rest.substring(0, rest.indexOf('/')));
            TOKENS[i].set(0);
            rest = rest.substring(rest.indexOf('/') + 1);
            HANDCARD[i].set(0);
        }
        // Set the name of the player in turn
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        if (playerinturnid == 0) {
            INTURN.set(true);
        }
        GAMERUNNING.set(true);
        // }
        // });
        OUTOFROUND.set("");

    }

    /**
     * Assign the drawn card to the correct slot
     *
     * @param cardname
     */
    public void setDrawnCard(String cardname) {
        if (HANDCARD[0].get() == 0 || HANDCARD[0].get() == 9) {
            HANDCARD[0].set(CARDS.indexOf(cardname));
            DRAWNCARD[0].set(9);
        } else {
            DRAWNCARD[0].set(CARDS.indexOf(cardname));
        }
    }

    /**
     * Show the card played by the player in turn
     *
     * @param cardname
     */
    public void setPlayedCard(String cardname) {
        // reset inturn-property
        INTURN.set(false);
        // If it is someone else's turn, show the played card in the drawn card slot
        if (playerinturnid != 0) {
            DRAWNCARD[playerinturnid].set(CARDS.indexOf(cardname));
            HANDCARD[playerinturnid].set(0);
        }
        // Reset the drawn card's slot of the last player
        int previousplayer = (playerinturnid - 1 + numberofplayers) % numberofplayers;
        while (OUTOFROUND.get().contains(PLAYERS[previousplayer].get())) {
            previousplayer = (previousplayer - 1 + numberofplayers) % numberofplayers;
        }
        DRAWNCARD[previousplayer].set(9);
        // Add the played card to the discarded card pile
        DISCARDEDCARDS.set(DISCARDEDCARDS.get() + cardname + " - " + PLAYERINTURN.get() + "\n");
        // Change the player in turn
        playerinturnid = (playerinturnid + 1) % numberofplayers;
        while (OUTOFROUND.get().contains(PLAYERS[playerinturnid].get())) {
            playerinturnid = (playerinturnid + 1) % numberofplayers;
        }
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        if (playerinturnid == 0) {
            INTURN.set(true);
        }
        // Show a hidden card in the active player's drawn card slot
        DRAWNCARD[playerinturnid].set(0);
    }

    /**
     * Add the given Player to the Out-Of-Round-List and clear the corresponding player panel
     *
     * @param name
     */
    public void outOfRound(String name) {
        OUTOFROUND.set(OUTOFROUND.get() + name + "\n");
        for (int i = 0; i < numberofplayers; i++) {
            if (PLAYERS[i].get().equals(name)) {
                HANDCARD[i].set(9);
                DRAWNCARD[i].set(9);
            }
        }
        while (OUTOFROUND.get().contains(PLAYERS[playerinturnid].get())) {
            playerinturnid = (playerinturnid + 1) % numberofplayers;
        }
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        INTURN.set(playerinturnid == 0);
    }

    /**
     * Display the results of this round in a Window and reset the player panels for the next round
     *
     * @param info
     * @throws IOException
     */
    public void endOfRound(String info) throws IOException {
        String winneroflastround = info.substring(2 * numberofplayers);
            for (int i = 0; i < numberofplayers; i++) {
                HANDCARD[i].set(0);
                SCORE[i].set(info.substring(2 * i, 2 * i + 2));
                if (winneroflastround.contains(PLAYERS[i].get())) {
                    TOKENS[i].set(TOKENS[i].get() + 1);
                    if (winneroflastround.substring(winneroflastround.lastIndexOf('/') + 1).equals(PLAYERS[i].get())){
                        playerinturnid = i;
                    }
                }
            }
        DISCARDEDCARDS.set("");
        HANDCARD[0].set(9);
        OUTOFROUND.set("");
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        LAUNCHER.launchEndOfRound(this, winneroflastround);
    }

    /**
     * Display the game results in a Window and reset the game panels
     *
     * @param info
     * @throws IOException
     */
    public void endOfGame(String info) throws IOException {
        String winner = info.substring(numberofplayers + 1);
        for (int i = 0; i < numberofplayers; i++) {
            TOKENS[i].set(info.charAt(i) - '0');
        }
        LAUNCHER.launchEndOfGame(this, winner);
        // Reset the game panels
        playerinturnid = 0;
        for (int i = 0; i < numberofplayers; i++) {
            PLAYERS[i].set("");
            TOKENS[i].set(0);
            HANDCARD[i].set(9);
            DRAWNCARD[i].set(9);

        }
        DISCARDEDCARDS.set("");
        numberofplayers = 0;
        PLAYERINTURN.set("");
        INTURN.set(false);
        GAMERUNNING.set(false);
        GAMEEXISTS.set(false);
    }

    /**
     * drop the current handcard without playing its effect
     */
    public void dropCard() {
        HANDCARD[0].set(9);
    }

    /**
     * Send message to the server, quit if logout order is given
     *
     * @param message
     */
    public void sendMessage(String message) {
        // Check logout condition
        if (message.equals("bye")) {
            OUT.println("/0quit");
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
            // Send message to server
            OUT.println("$" + message);
        }
    }

    public void sendAnswer(String answer) {
        OUT.println("/6" + answer);
    }

    public void seeCard(String info) {
        String playername = info.substring(0, info.indexOf('/'));
        String cardname = info.substring(info.indexOf('/') + 1);
        for (int i = 0; i < numberofplayers; i++) {
            if (PLAYERS[i].get().equals(playername)) {
                HANDCARD[i].set(CARDS.indexOf(cardname));
            }
        }
    }

    /**
     * Execute an order from the server by checking the order code and calling the correct method
     *
     * @param order
     * @throws IOException
     */
    public void executeOrder(String order) throws IOException {
        Client client = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (order.charAt(0)) {
                        case '0' -> // terminate the connection
                                socket.close();
                        case '1' -> // open Error Window
                                LAUNCHER.launchError(order.substring(1));
                        case '2' -> // open Question Window to ask for a player
                                LAUNCHER.launchQuestion(client, order.substring(1));
                        case '3' -> // drop handcard
                                dropCard();
                        case '4' -> // start Game
                                startGameInfo(order.substring(1));
                        case '5' -> // show the drawn card
                                setDrawnCard(order.substring(1));
                        case '6' -> // show the played card
                                setPlayedCard(order.substring(1));
                        case '7' -> // end round
                                endOfRound(order.substring(1));
                        case '8' -> // end game
                                endOfGame(order.substring(1));
                        case '9' -> // out of round
                                outOfRound(order.substring(1));
                        case 'a' -> GAMEEXISTS.set(true);
                        case 'b' -> HANDCARD[0].set(CARDS.indexOf(order.substring(1)));
                        case 'c' -> seeCard(order.substring(1));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Starts the application on the client side
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // start the login process
        LAUNCHER.launchLogin(this);

        // Open chat and game window after logging in successfully
        CHATHISTORY.set("Welcome " + name + "\n");
        LAUNCHER.launchChatAndGame(this);
        new Thread(() -> {
            try {
                String line;
                while (!socket.isClosed()) {
                    // Client socket waits for the input from the server
                    line = IN.readLine();
                    //if(!line.isEmpty()) { // NullPointerException
                    if (line != null) {
                        // Pass an order to the corresponding method or a message to the ChatWindow
                        if (line.charAt(0) == '/') {
                            executeOrder(line.substring(1));
                        } else {


                            CHATHISTORY.set(CHATHISTORY.get() + line.substring(1) + "\n");

                        }
                    }
                }
                Platform.exit();
            } catch (IOException e) {
                try {
                    socket.close();
                    Platform.exit();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
