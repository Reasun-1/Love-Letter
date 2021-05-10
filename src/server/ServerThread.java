package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;

/**
 * server thread for several client request
 *
 * @author Can Ren
 * @author Pascal Stucky
 */
public class ServerThread implements Runnable {

    private final Socket SOCKET;
    private String clientName;


    /**
     * Constructor combines the client socket with the ServerThread socket
     * @param socket
     */
    public ServerThread(Socket socket) {
        SOCKET = socket;
    }

    public Socket getSocket(){
        return SOCKET;
    }

    @Override
    public void run() {
        try {
            // Create reader for messages from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(SOCKET.getInputStream()));
            // Create writer for messages to the client
            PrintWriter out = new PrintWriter(SOCKET.getOutputStream(), true);
            // Inform the client about an existing/running game
            out.println(Server.getServer().gameExists() + Server.getServer().gameRunning());

            // Receive the name from the client and check it with the client list
            clientName = "";
            while (clientName.isEmpty()) {
                String temp_name = in.readLine();
                if (Server.clientList.containsKey(temp_name)) {
                    out.println("user existed!");
                } else {
                    // introduce the new client
                    sendMessage("$" + temp_name + " has joined the chat.");
                    // Store the client name and add it to the name list
                    clientName = temp_name;
                    Server.clientList.put(clientName, this);
                    // send client name as a confirmation
                    out.println(clientName);
                }
            }

            // print the list of client names to the terminal (for testing)
            System.out.println(Server.clientList);

                    // wait for messages from the client
                    boolean flag = true;
                    while (flag) {
                        //wait for the output Stream from Client
                        String line = in.readLine();
                        // if no message from the client, then wait
                        if (line != null) {
                            if (line.charAt(0) == '/') {
                                executeOrder(line.substring(1));
                            } else {
                                String msg = "$" + clientName + ": " + line.substring(1);
                                // give the message to all clients online
                                sendMessage(msg);
                            }
                        }

                    }
        } catch (IOException e) {
                    try {
                        closeConnect();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
    }

    /**
     * send a message to all clients
     * @param message
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {
        //optional, for server terminal print
        System.out.println(message);
        synchronized (Server.clientList) {
            for (Enumeration<ServerThread> e = Server.clientList.elements(); e.hasMoreElements();) {
                new PrintWriter(e.nextElement().getSocket().getOutputStream(), true).println(message);
            }
        }
    }

    /**
     * transmit an order from the server to the client
     * @param order
     * @throws IOException
     */
    public void receiveOrder(String order) throws IOException{
        new PrintWriter(SOCKET.getOutputStream(),true).println("/" + order);
    }

    /**
     * terminate the connection to the client
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        //remove the socket from the set
        synchronized (Server.clientList) {
            Server.clientList.remove(clientName);
        }
        //server.Server.clientList.remove(clientName);
        // inform the other clients
        sendMessage("$" + clientName + " has left the room.");
        SOCKET.close();
    }

    /**
     * send a message only to one client
     * @param name
     * @param message
     */
    public void sendPrivateMessage(String name, String message){
        Server.getServer().sendTo(name, message);
    }

    /**
     * transmit an order from the client to the server depending on the order code
     * @param order
     * @throws IOException
     */
    public void executeOrder(String order) throws IOException{
        switch (order.charAt(0)){
            case '0': // terminate the connection
                break;
            case '1': // send private message
                String name = order.substring(1,order.indexOf('/'));
                System.out.println(name);
                if (Server.clientList.containsKey(name)) {
                    String msg = order.substring(order.indexOf('/') + 1);
                    sendPrivateMessage(name,  "$" + clientName + "[private]: " + msg);
                    new PrintWriter(SOCKET.getOutputStream(),true).println("$" + clientName + "[private]: " + msg);
                } else {
                    receiveOrder("1There is no client with this name!");
                }
                break;
            case '2': // create a new game
                Server.getServer().createGame(clientName);
                break;
            case '3': // join an existing game
                Server.getServer().addPlayer(clientName);
                break;
            case '4': // start an existing game
                Server.getServer().startGame(clientName);
                break;
            case '5': // play a card
                Server.getServer().playCard(order.substring(1));
                break;
            case '6': // transmit an answer of a question window
                Server.getServer().receiveAnswer(order.substring(1), clientName);
                break;
        }
    }

}