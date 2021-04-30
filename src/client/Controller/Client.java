package client.Controller;

import server.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    // socket for the TCP connection
    private volatile Socket socket;
    // writer for outgoing messages
    private final PrintWriter out;
    // reader for incoming messages
    private final BufferedReader in;
    // reader for outgoing messages
    private final BufferedReader reader;

    private String[] playerList;

    private Card handCard; // **************Card handCard => there is only at one time*********

    private Card drawnCard;

    private Card playedCard;

    private Card[][] discardedCards;

    private int[] score;

    public Card getHandCards(){
        return handCard;
    }

    //public Card[] getDiscardedCards(){
        //return discardedCards;
    //}

    public Client() throws IOException {
        // Always connect to localhost and fixed port (maybe ask for ip and port?)
        socket = new Socket("127.0.0.1", 5200);

        // Create writer to send messages to server via the TCP-socket
        out = new PrintWriter(socket.getOutputStream(), true);

        // Create reader to receive messages from server via the TCP-socket
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Create reader for user input (currently via terminal)
        reader = new BufferedReader(new InputStreamReader(System.in));
        // start login process
        login();
    }

    public void login() throws IOException {

        // Ask for the clients name (currently via terminal)
        System.out.println("Please enter your name:"); // Soon: Open Login-Window
        String temp_name = reader.readLine();
        while(temp_name.contains("/")){
            System.out.println("The name must not contain the symbol '/'!");
            temp_name = reader.readLine();
        }

        // Send the name to the server
        out.println(temp_name);

        // Check whether the name is still available (if not, ask again)
        boolean flag = true;
        while (flag) {
            String answer = in.readLine();
            if (answer.equals("user existed!")) {
                // Ask for a different name (currently via terminal)
                System.out.println("The chosen name already exists. Please choose another name:");
                // Soon: errorMessage("The chosen name already exists. Please choose another name:");
                temp_name = reader.readLine();
                // Soon: Open Login-Window
                out.println(temp_name);
            } else {
                // If the name is available, break the loop
                flag = false;
                // Print welcome-message (currently via terminal)
                System.out.println("Welcome " + answer);
                // Soon: Open Chat-Window and print welcome-message
            }
        }
    }

    public void sendPersonalMessage(String name, String msg) throws IOException {
        out.println("/1" + name + "/" + msg);
    }

    public void joinGame() throws IOException{
        out.println("/2");
    }

    public void startGame() throws IOException{
        out.println("/3");
    }

    public void playCard(Card card) throws IOException {
        if (card.equals(handCard)){
            handCard = drawnCard;
            drawnCard = null;
        } else{
            drawnCard = null;
        }
        out.println("/4" + card.getType());
    }

    public void executeOrder(String order) throws IOException{
        switch (order.charAt(0)){
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
                playerList = new String[numberOfPlayers]
                String rest = order.substring(1);
                for(int i=0;i<numberOfPlayers;i++){
                    playerList[i] = order.substring(1,indexOf('/'));
                    rest = rest.substring(indexOf('/'));
                }
                discardedCards = new Card[numberOfPlayers][5];
                score = new int[numberOfPlayers];
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
                String player = order.substring(1,order.indexOf('/'));
                for (Card card : Card.values()) {
                    if (card.getType().equals(order.substring(order.indexOf('/')))) {
                        playedCard = card;
                    }
                }
                for(int i=0;i<5;i++) {
                    if(discardedCards[find(playerList, player)][i] == null){
                        discardedCards[find(playerList, player)][i] = playedCard;
                        break;
                    }
                }
                String nextPlayer = playerList[(find(playerList, player) + 1) % playerList.size()];
                out.println("done");
                break;
            case '7':
                Arrays.fill(discardedCard, null);
                Arrays.fill(handCard, null);
                for(int i=0;i<playerList.length();i++){
                    score[i] = order.charAt(i+1);
                }
                out.println("done");
                break;
            case '8':
                for(int i=0;i<playerList.length();i++){
                    score[i] = order.charAt(i+1);
                }
                // End-Of-Game-Window
                out.println("done");
                break;
        }
    }

    public int find(Array<T> array, T element){
        for(int i=0;i<array.length();i++){
            if(array[i] == element){
                return i;
            }
        }
    }


    public void sendMessage(String msg) throws IOException {
        // check logout condition
        if (msg.equals("bye")) {
            out.println("/0quit");
            // stop the connection
            try {
                if(socket != null){
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

    public static void main(String[] args) {
        try {
            Client client = new Client();
            // Create a client thread to read messages from the server
            new Thread(() -> {
                try {
                    while (!client.socket.isClosed()){
                    // Client socket waits for the input from the server
                    // If there is input, display the message (currently via terminal)
                        String line = client.in.readLine();
                        if (line.charAt(0) == '/'){
                         //   client.executeOrder(line.substring(1));
                        } else {
                            System.out.println(line.substring(1));
                        }
                    // Soon: Pass the message to the chat window
                    }
                } catch (IOException e) {
                    try{
                        client.socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            // Wait for messages from the user passed on by the InputStream, stop if the connection is terminated
            while (!client.socket.isClosed()) {
                String msg = client.reader.readLine();
                if (!msg.isEmpty()) {
                    client.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
