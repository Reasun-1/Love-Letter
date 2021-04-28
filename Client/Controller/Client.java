package Controller;

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

    private Card[] handCards;

    private Card[] discardedCards;

    private int score;

    public int getScore(){
        return score;
    }

    public Card[] getHandCards(){
        return handCards;
    }

    public Card[] getDiscardedCards(){
        return discardedCards;
    }

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

    public void sendMessage(String msg) throws IOException {
        // check logout condition
        if (msg.equals("bye")) {
            out.println("quit");
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
            out.println(msg);
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
                    System.out.println(client.in.readLine());
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
