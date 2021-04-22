import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * server thread for several client request
 */
public class ServerThread implements Runnable {

    private final Socket socket;
    private String clientName;

    //HashSet<String> threadList = Server.getClientList();
    protected List<ServerThread> threads = Server.getThreads();


    // combine the client socket to the serverThread Constructor
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket(){
        return socket;
    }

    public String getClientName(){
        return clientName;
    }

    @Override
    public void run() {
        try {
            // Create reader for messages from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Create writer for messages to the client
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            // Receive the name from the client and check it with the client list
            clientName = "";
            while (clientName.isEmpty()) {
                String temp_name = in.readLine();
                if (Server.clientList.contains(temp_name)) {
                    out.println("user existed!");
                } else {
                    // introduce the new client
                    sendMessage(temp_name + " has joined the chat.");
                    // Store the client name and add it to the name list
                    clientName = temp_name;
                    Server.clientList.add(clientName);
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
                if (line == null) {
                    flag = false;
                    continue;
                }

                String msg = clientName + ": " + line;
                // give the message to all clients online
                sendMessage(msg);
            }
            // close the connection
            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //create the method to send message to all the clients online
    private void sendMessage(String msg) throws IOException {
        //optional, for server terminal print
        System.out.println(msg);
        synchronized (threads) {
            for (ServerThread st : threads) {
                if(!st.getClientName().isEmpty()){
                    new PrintWriter(st.getSocket().getOutputStream(), true).println(msg);
                }
            }
        }
    }

    //close socket connection
    public void closeConnect() throws IOException {
        //remove the socket from the set
        synchronized (threads) {
            threads.remove(this);
        }
        Server.clientList.remove(clientName);
        // inform the other clients
        sendMessage(clientName + " has left the room.");
        socket.close();
    }
}