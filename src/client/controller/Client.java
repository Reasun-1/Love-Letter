package client.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client extends Application{
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

    //private StringProperty errormessage;

    //private StringProperty questionmessage;

    //errormessage = new SimpleStringProperty();

    //questionmessage = new SimpleStringProperty();

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

    // Current score of the players
    private int[] score = new int[4];

    // List of players out of round
    private final StringProperty OUTOFROUND = new SimpleStringProperty();

    // Bindings to display the hand card of each player
    //private ObjectProperty[] handcard = new ObjectProperty[4];

    // Bindings to display the drawn card of each player
    //private ObjectProperty[] drawncard = new ObjectProperty[4];

    // Bindings to list the discarded cards of each player
    private final StringProperty[] DISCARDEDCARDS = new StringProperty[4];

    // Bindings to list the current tokens of each player
    private final IntegerProperty[] TOKENS = new IntegerProperty[4];

    // Bindings enables/disables certain buttons (see ChatWindowController)
    private final BooleanProperty GAMEEXISTS = new SimpleBooleanProperty();
    private final BooleanProperty GAMERUNNING = new SimpleBooleanProperty();
    private final BooleanProperty INTURN = new SimpleBooleanProperty(false);

    public Socket getSocket() { return socket; }

    public BufferedReader getIn(){ return IN; }

    public PrintWriter getOut(){ return OUT; }

    public String getName(){
        return name;
    }

    //public StringProperty getErrorMessage(){return errormessage;}

    //public StringProperty getQuestionMessage() {return questionmessage;}

    public StringProperty getChatHistory() {return CHATHISTORY;}

    public StringProperty getPlayers(int playerindex) {
        return PLAYERS[playerindex];
    }

    public StringProperty getPlayerInTurn() {return PLAYERINTURN;}

    public StringProperty getOutOfRound() {return OUTOFROUND;}

   /* public ObjectProperty getHandCard(int playerindex) {
        return handcard[playerindex];
    }

    public ObjectProperty getDrawnCard(int playerindex) {
        return handcard[playerindex];
    }*/

    public StringProperty getDiscardedCards(int playerindex) {return DISCARDEDCARDS[playerindex];}

    public BooleanProperty getGameExists(){ return GAMEEXISTS;}

    public BooleanProperty getGameRunning() {return GAMERUNNING;}

    public BooleanProperty getInTurn() {return INTURN;}

    // Constructor establishes the TCP-Connection
    public Client() throws IOException{

            // Always connect to localhost and fixed port (maybe ask for ip and port?)
            socket = new Socket("127.0.0.1", 5200);

            // Create writer to send messages to server via the TCP-socket
            OUT = new PrintWriter(socket.getOutputStream(), true);

            // Create reader to receive messages from server via the TCP-socket
            IN = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start with an empty name, will be set during Login
            name = "";

        for (int i=0;i<4;i++){
                PLAYERS[i] = new SimpleStringProperty();
                //handcard[i] = new ObjectProperty();
                //drawncard[i] = new ObjectProperty();
                DISCARDEDCARDS[i] = new SimpleStringProperty();
                TOKENS[i] = new SimpleIntegerProperty();
            }
        // The first input from the Server will be info about a Game existing/running on this Server
        String gameinfo = IN.readLine();
        GAMEEXISTS.set(gameinfo.charAt(0) != '0');
        GAMERUNNING.set(gameinfo.charAt(1) != '0');
    }

    // Method call from LoginWindowController to check the entered name
    public void checkName(String temp_name) {
        try {
            // The name must not contain a '/', since we use this character as a separator for game info
            if (temp_name.contains("/")) {
                // Open error Window, then restart the Login
                LAUNCHER.launchError("The name must not contain the symbol '/'!");
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
                    // Soon: Open Chat-Window and print welcome-message
                    //LAUNCHER.launchChat();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Send a message to only one client. The name and the message are separated by '/'. Order Code: 1
    public void sendPersonalMessage(String name, String message) throws IOException {
        OUT.println("/1" + name + "/" + message);
    }

    // Create a new Game (only possible if no Game exists on the Server). Order Code: 2
    public void createGame() {
        OUT.println("/2");
    }

    // Join an existing Game (only possible if a Game exists and there are less than 4 players registered). Order Code: 3
    public void joinGame() {
        OUT.println("/3");
    }

    // Start an existing Game (only possible if this client has joined the Game). Order Code: 4
    public void startGame() {
        OUT.println("/4");
    }

    // Play your hand card (only possible if this client is player in turn in the current game). Order Code: 5
    public void playHandCard() {
            /*OUT.println("/5" + handcard[0].get());
            Object playedcard = handcard[0].get();
            handcard[0].set(drawncard[0].get());
            drawncard[0].set(playedcard);*/
    }

    // Play your hand card (only possible if this client is player in turn in the current game). Order Code: 5
    public void playDrawnCard() {
        //OUT.println("/5" + drawncard[0].get());
    }

    // Receive the Game info from the Server
    public void startGameInfo(String info){
        // The first character is the number of players
        numberofplayers = info.charAt(0);
        // The second character encodes the start player
        playerinturnid = (- (int) info.charAt(1)) % numberofplayers;
        // The rest of the String contains the names of the other players separated by a '/'
        String rest = info.substring(1);
        for (int i = 0; i < numberofplayers; i++) {
            PLAYERS[i].set(rest.substring(1, info.indexOf('/')));
            TOKENS[i].set(0);
            score[i] = 0;
            rest = rest.substring(info.indexOf('/'));
        }
        // Set the name of the player in turn
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
    }

    // Assign the drawn card to the correct slot
    public void setDrawnCard(String cardname){
            /*if (handcard[0].get() == null) {
                    //handcard[0].set(cardname); TODO
                } else {
                    //drawncard[0].set(cardname); TODO
                }*/
        OUT.println("done");
    }

    // Show the card played by the player in turn
    public void setPlayedCard(String cardname){
        // If it is someone else's turn, show the played card in the drawn card slot
        if (playerinturnid != 0){
            //drawncard[playerinturnid].set(cardname); TODO
        }
        // Reset the drawn card's slot of the last player
        //drawncard[(playerinturnid - 1) % numberofplayers].set(null);
        // Add the played card to the discarded card pile
        DISCARDEDCARDS[playerinturnid].concat(cardname + "\n");
        // Update the score
        // score[playerinturnid] = ??
        // Change the player in turn
        playerinturnid = (playerinturnid + 1) % numberofplayers;
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        // Show a hidden card in the active player's drawn card slot
        //drawncard[playerinturnid].set(dummycard); TODO
        OUT.println("done");
    }

    public void endOfRound(String info){
        String winneroflastround = info.substring(numberofplayers + 1);
        for (int i = 0; i < numberofplayers; i++) {
            DISCARDEDCARDS[i].set("");
            //handcard[i].set(dummycard);
            TOKENS[i].set(info.charAt(i));
            if(PLAYERS[i].get().equals(winneroflastround)){
                playerinturnid = i;
            }
        }
        //handcard[0].set(null);
        OUTOFROUND.set("");
        PLAYERINTURN.set(PLAYERS[playerinturnid].get());
        //end-of-round window?
    }

    public void endOfGame(String info){
        for (int i = 0; i < numberofplayers; i++) {
            TOKENS[i].set(info.charAt(i));
        }
        // end-of-game Window?
    }

    public void sendMessage(String message){
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

    public void executeOrder(String order) throws IOException {
        Client client = this;
        switch (order.charAt(0)) {
            case '0': // terminate the connection
                OUT.println("done");
                socket.close();
                break;
            case '1': // open Error Window
                OUT.println("done");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            LAUNCHER.launchError(order.substring(1));
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '2': // open Question Window to ask for a player
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            LAUNCHER.launchQuestion(client, "Please choose a Player:");
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '3': // open Question Window to ask for a card
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            LAUNCHER.launchQuestion(client, "Please enter your card guess:");
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '4': // start Game
                startGameInfo(order.substring(1));
                OUT.println("done");
                break;
            case '5': // show the drawn card
                setDrawnCard(order.substring(1));
                OUT.println("done");
                break;
            case '6': // show the played card
                setPlayedCard(order.substring(1));
                OUT.println("done");
                break;
            case '7': // end round
                endOfRound(order.substring(1));
                OUT.println("done");
                break;
            case '8': // end game
                endOfGame(order.substring(1));
                OUT.println("done");
                break;
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        // start the login process
        LAUNCHER.launchLogin(this);

        // Only for tests
        LAUNCHER.launchError("This is a test error");

        // Only for tests
        LAUNCHER.launchQuestion(this, "This is a test Question");

        // Open chat after logging in successfully
        LAUNCHER.launchChat(this);
        new Thread(() -> {
            try {
                String line;
                while (!socket.isClosed()){
                    // Client socket waits for the input from the server
                    line = IN.readLine();
                    if(!line.isEmpty()) {
                        // Pass an order to the corresponding method or a message to the ChatWindow
                        if (line.charAt(0) == '/') {
                            executeOrder(line.substring(1));
                        } else {
                            CHATHISTORY.concat(line.substring(1) + "\n");
                        }
                    }
                }
                Platform.exit();
            } catch (IOException e) {
                try{
                    socket.close();
                    Platform.exit();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args){
        launch(args);
    }

}
