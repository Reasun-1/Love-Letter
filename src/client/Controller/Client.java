package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.stage.Stage;
import server.*;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client extends Application{
    // socket for the TCP connection
    private volatile Socket socket;
    // writer for outgoing messages
    private final PrintWriter out;
    // reader for incoming messages
    private final BufferedReader in;
    // reader for outgoing messages
    //private final BufferedReader reader;
    private WindowLauncher launcher;

    private String name;

    private StringProperty errormessage;

    private StringProperty questionmessage;

    private StringProperty chathistory;

    private StringProperty[] players;

    private StringProperty playerinturn;

    private int playerinturnid;

    private int numberofplayers;

    private ObjectProperty[] handcard;

    private ObjectProperty[] drawncard;

    private StringProperty[] discardedcards;

    private IntegerProperty[] tokens;

    private BooleanProperty gameexists;

    private BooleanProperty gamerunning;

    private BooleanProperty inturn;

    public Socket getSocket() { return socket; }

    public BufferedReader getIn(){ return in; }

    public PrintWriter getOut(){ return out; }

    public String getName(){
        return name;
    }

    public StringProperty getErrorMessage(){return errormessage;}

    public StringProperty getQuestionMessage() {
        return questionmessage;
    }

    public StringProperty getChatHistory() {return chathistory;}

    public StringProperty getPlayers(int playerindex) {
        return players[playerindex];
    }

    public StringProperty getPlayerinTurn() {return playerinturn;}

    public ObjectProperty getHandCard(int playerindex) {
        return handcard[playerindex];
    }

    public ObjectProperty getDrawnCard(int playerindex) {
        return handcard[playerindex];
    }

    public StringProperty getDiscardedCards(int playerindex) {return discardedcards[playerindex];}

    public BooleanProperty getGameExists(){ return gameexists;}

    public BooleanProperty getGameRunning() {return gamerunning;}

    public BooleanProperty getinTurn() {return inturn;}

    //public Card[] getDiscardedCards(){
    //return discardedCards;
    //}


    public Client() throws IOException{

            // Always connect to localhost and fixed port (maybe ask for ip and port?)
            socket = new Socket("127.0.0.1", 5200);

            // Create writer to send messages to server via the TCP-socket
            out = new PrintWriter(socket.getOutputStream(), true);

            // Create reader to receive messages from server via the TCP-socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            name = "";

            errormessage = new SimpleStringProperty();

            questionmessage = new SimpleStringProperty();

            chathistory = new SimpleStringProperty();

            players = new StringProperty[4];

            playerinturn = new SimpleStringProperty();

            handcard = new ObjectProperty[4];

            drawncard = new ObjectProperty[4];

            discardedcards = new StringProperty[4];

            tokens = new IntegerProperty[4];

            for (int i=0;i<4;i++){
                players[i] = new SimpleStringProperty();
                handcard[i] = new ObjectProperty();
                drawncard[i] = new ObjectProperty();
                discardedcards[i] = new SimpleStringProperty();
                tokens[i] = new SimpleIntegerProperty();
            }

            gameexists = new BooleanProperty();
            gamerunning = new BooleanProperty();
            inturn = new BooleanProperty(false);

            String gameinfo = in.readLine();
            if(gameinfo.charAt(0) == '0') {
                gameexists.set(false);
            } else {
                gameexists.set(true);
            }
        if(gameinfo.charAt(1) == '0') {
            gamerunning.set(false);
        } else {
            gamerunning.set(true);
        }
        launcher = new WindowLauncher();
    }

    public void checkName(String temp_name) {

        // Ask for the clients name (currently via terminal)
        //System.out.println("Please enter your name:"); // Soon: Open Login-Window
        try {
            if (temp_name.contains("/")) {
                launcher.launchError("The name must not contain the symbol '/'!");
                launcher.launchLogin(this);
            } else {
                out.println(temp_name);
                // Check whether the name is still available (if not, ask again)
                String answer = in.readLine();
                if (answer.equals("user existed!")) {
                    // Ask for a different name (currently via terminal)
                    launcher.launchError("The chosen name already exists. Please choose another name:");
                    launcher.launchLogin(this);
                } else {
                    // Print welcome-message (currently via terminal)
                    name = answer;
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

    public void playHandCard() {
            out.println("/5" + handcard[0].get());
            Object playedcard = handcard[0].get();
            handcard[0].set(drawncard[0].get());
            drawncard[0].set(playedcard);
    }

    public void playDrawnCard() {
        out.println("/5" + drawncard[0].get());
    }

    public void startGameInfo(String info){
        numberofplayers = info.charAt(0);
        playerinturnid = (- (int) info.charAt(1)) % numberofplayers;
        String rest = info.substring(1);
        for (int i = 0; i < numberofplayers; i++) {
            players[i].set(rest.substring(1, info.indexOf('/')));
            tokens[i].set(0);
            rest = rest.substring(info.indexOf('/'));
        }
        playerinturn.set(players[playerinturnid].get());
    }

    public void setDrawnCard(String cardname){
            if (handcard[0].get() == null) {
                    //handcard[0].set(cardname); TODO
                } else {
                    //drawncard[0].set(cardname); TODO
                }
        out.println("done");
    }

    public void setPlayedCard(String cardname){
        if (playerinturnid != 0){
            //drawncard[playerinturnid].set(cardname); TODO
        }
        drawncard[(playerinturnid - 1) % numberofplayers].set(null);
        discardedcards[playerinturnid].concat(cardname + "\n");
        playerinturnid = (playerinturnid + 1) % numberofplayers;
        playerinturn.set(players[playerinturnid].get());
        //drawncard[playerinturnid].set(dummycard); TODO
        out.println("done");
    }

    public void endOfRound(String info){
        String winneroflastround = info.substring(numberofplayers + 1);
        for (int i = 0; i < numberofplayers; i++) {
            discardedcards[i].set("");
            //handcard[i].set(dummycard);
            tokens[i].set(info.charAt(i));
            if(players[i].get().equals(winneroflastround)){
                playerinturnid = i;
            }
        }
        handcard[0].set(null);
        playerinturn.set(players[playerinturnid].get());
        //end-of-round window?
    }

    public void endOfGame(String info){
        for (int i = 0; i < numberofplayers; i++) {
            tokens[i].set(info.charAt(i));
        }
        // end-of-game Window?
    }

    public void sendMessage(String message){
            // check logout condition
            if (message.equals("bye")) {
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
                out.println("$" + message);
            }
    }

    public void executeOrder(String order) throws IOException {
        Client client = this;
        switch (order.charAt(0)) {
            case '0':
                out.println("done");
                socket.close();
                break;
            case '1':
                out.println("done");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            launcher.launchError(order.substring(1));
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '2':
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            launcher.launchQuestion(client, "Please choose a Player:");
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '3':
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            launcher.launchQuestion(client, "Please enter your card guess:");
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case '4':
                startGameInfo(order.substring(1));
                out.println("done");
                break;
            case '5':
                setDrawnCard(order.substring(1));
                out.println("done");
                break;
            case '6':
                setPlayedCard(order.substring(1));
                out.println("done");
                break;
            case '7':
                endOfRound(order.substring(1));
                out.println("done");
                break;
            case '8':
                endOfGame(order.substring(1));
                out.println("done");
                break;
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        launcher.launchLogin(this);

        launcher.launchChat(this);
        new Thread(() -> {
            try {
                String line;
                while (!socket.isClosed()){
                    // Client socket waits for the input from the server
                    // If there is input, display the message (currently via terminal)
                    line = in.readLine();
                    if(!line.isEmpty()) {
                        if (line.charAt(0) == '/') {
                            executeOrder(line.substring(1));
                        } else {
                            chathistory.concat(line.substring(1) + "\n");
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
        try {
            Client client = new Client();
            client.launch(args);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
