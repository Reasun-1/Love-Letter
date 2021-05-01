package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import client.ViewModel.logInViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    private final MainLauncher launcher;

    private String[] playerList;

    private Card handCard; // **************Card handCard => there is only at one time*********

    private Card drawnCard;

    private Card playedCard;

    private Card[][] discardedCards;

    private int[] tokens;

    public Card getHandCards() {
        return handCard;
    }

    private String name;

    //public Card[] getDiscardedCards(){
    //return discardedCards;
    //}

    public Socket getSocket() {
        return socket;
    }

    public String getName(){
        return name;
    }


    public Client() throws IOException{

            // Always connect to localhost and fixed port (maybe ask for ip and port?)
            socket = new Socket("127.0.0.1", 5200);

            // Create writer to send messages to server via the TCP-socket
            out = new PrintWriter(socket.getOutputStream(), true);

            // Create reader to receive messages from server via the TCP-socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            launcher = new MainLauncher();

        // Create reader for user input (currently via terminal)
        //reader = new BufferedReader(new InputStreamReader(System.in));
        // start login process
    }

    public String checkName(String temp_name) throws IOException {

        // Ask for the clients name (currently via terminal)
        //System.out.println("Please enter your name:"); // Soon: Open Login-Window
        if (temp_name.contains("/")) {
            return "The name must not contain the symbol '/'!";//errorMessage
        } else {
            out.println(temp_name);

        // Check whether the name is still available (if not, ask again)
            String answer = in.readLine();
            if (answer.equals("user existed!")) {
                // Ask for a different name (currently via terminal)
                return "The chosen name already exists. Please choose another name:";// errorMessage
            } else {
                // Print welcome-message (currently via terminal)
                name = answer;
                return "";
                // Soon: Open Chat-Window and print welcome-message
                //launcher.launchChat();
            }
        }
    }

    public void sendPersonalMessage(String name, String msg) throws IOException {
        out.println("/1" + name + "/" + msg);
    }

    public void joinGame() throws IOException {
        out.println("/2");
    }

    public void startGame() throws IOException {
        out.println("/3");
    }

    public void playCard(Card card) throws IOException {
        if (card.equals(handCard)) {
            handCard = drawnCard;
            drawnCard = null;
        } else {
            drawnCard = null;
        }
        out.println("/4" + card.getType());
    }

    public void executeOrder(String order) throws IOException {
        switch (order.charAt(0)) {
            case '0':
                out.println("done");
                socket.close();
                break;
            case '1':
                // open errorWindow with errorMessage = order.substring(1)
                out.println("done");
                break;
            case '2':
                String name = "";
                // String name = ask the user to choose another player
                out.println(name);
                break;
            case '3':
                String cardname = "";
                // String cardname = ask the user to guess a card
                out.println(cardname);
                break;
            case '4':
                int numberOfPlayers = order.charAt(1);
                playerList = new String[numberOfPlayers];
                String rest = order.substring(1);
                for (int i = 0; i < numberOfPlayers; i++) {
                    playerList[i] = order.substring(1, order.indexOf('/'));
                    rest = rest.substring(order.indexOf('/'));
                }
                discardedCards = new Card[numberOfPlayers][5];
                tokens = new int[numberOfPlayers];
                Arrays.fill(discardedCards, null);
                handCard = null;
                out.println("done");
                break;
            case '5':
                for (Card card : Card.values()) {
                    if (card.getType().equals(order.substring(1))) {
                        if (handCard == null) {
                            handCard = card;
                        } else {
                            drawnCard = card;
                        }
                    }
                }
                out.println("done");
                break;
            case '6':
                String player = order.substring(1, order.indexOf('/'));
                for (Card card : Card.values()) {
                    if (card.getType().equals(order.substring(order.indexOf('/')))) {
                        playedCard = card;
                    }
                }
                int index=0;
                while (discardedCards[find(playerList, player)][index] == null){
                    index++;
                }
                discardedCards[find(playerList, player)][index] = playedCard;
                String nextPlayer = playerList[(find(playerList, player) + 1) % playerList.length];
                out.println("done");
                break;
            case '7':
                Arrays.fill(discardedCards, null);
                handCard = null;
                for (int i = 0; i < playerList.length; i++) {
                    tokens[i] = order.charAt(i + 1);
                }
                String winner = order.substring(playerList.length + 1);
                // print out the winner
                out.println("done");
                break;
            case '8':
                for (int i = 0; i < playerList.length; i++) {
                    tokens[i] = order.charAt(i + 1);
                }
                // End-Of-Game-Window
                out.println("done");
                break;
        }
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


    public void sendMessage(String msg) throws IOException {
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
    @Override
    public void start(Stage primaryStage) throws Exception {
        PipedInputStream instream = new PipedInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

        while(name.isEmpty()) {
            primaryStage.setTitle("Login");
            primaryStage.setScene(launcher.launchLogin(instream));
            primaryStage.show();

            String result = checkName(reader.readLine());
            if(!result.isEmpty()) {
                primaryStage.setTitle("Error");
                primaryStage.setScene(launcher.launchError(result));
                primaryStage.show();
            }
        }
        primaryStage.setTitle("Chat: " + name);
        ChatRoomViewModel chatVM = new ChatRoomViewModel(instream);
        primaryStage.setScene(launcher.launchChat(chatVM));
        primaryStage.show();

        try {
            new Thread(() -> {
            try {
                while (!socket.isClosed()){
                    // Client socket waits for the input from the server
                    // If there is input, display the message (currently via terminal)
                    String line = in.readLine();
                    if (line.charAt(0) == '/'){
                        //   client.executeOrder(line.substring(1));
                    } else {
                        chatVM.updateChat(line.substring(1));
                    }
                    // Soon: Pass the message to the chat window
                }
            } catch (IOException e) {
                try{
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
        // Wait for messages from the user passed on by the InputStream, stop if the connection is terminated
        while (!socket.isClosed()) {
            String msg = reader.readLine();
            if (!msg.isEmpty()) {
                sendMessage(msg);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.launch(args);
            // Create a client thread to read messages from the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
