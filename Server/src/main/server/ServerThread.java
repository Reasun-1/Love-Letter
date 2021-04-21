package src.main.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import static src.main.server.Server.clientList;

/**
 * server thread for several client request
 */
public class ServerThread implements Runnable {

    Socket socket;
    String clientName;

    HashSet<String> clientList = Server.getClientList();
    List<Socket> sockets = Server.getSockets();


    // combine the client socket to the serverThread Constructor
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            // check the name with client list
            clientName = "";

            while (clientName.equals("")) {
                String temp_name = in.readLine();
                if (Server.clientList.contains(temp_name)) {
                    out.println("user existed!");
                } else {
                    clientName = temp_name;
                    Server.clientList.add(clientName);
                    out.println(clientName);
                }
            }

            welcomeMessage(clientName);
            System.out.println(Server.clientList);

            boolean flag = true;
            while (flag) {
                //wait for the output Stream from Client
                String line = in.readLine();
                // if no message from the client, then wait
                if (line == null) {
                    flag = false;
                    continue;
                }

                String msg = clientName + ":" + line;
                //optional, for server terminal print
                System.out.println(msg);
                // give the message to all clients online
                sendMessage(msg);
            }

            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * create the method print: send message to all the clients online
     */
    private void sendMessage(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets) {
            for (Socket sc : sockets) {
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }

    private void welcomeMessage(String clientName) throws IOException {
        //optional, server terminal print
        System.out.println(clientName + " has joined the chat.");

        PrintWriter out = null;
        //synchronized (sockets) {
            for (Socket sc : sockets) {
                if(!sc.equals(socket)) {
                    out = new PrintWriter(sc.getOutputStream());
                    out.println(clientName + " has joined the chat.");
                    out.flush();
                }
            }
        //}
    }

    /**
     * close socket connection
     */
    public void closeConnect() throws IOException {
        //remove the socket from the set
        synchronized (sockets) {
            sockets.remove(socket);
        }
        clientList.remove(clientName);
        System.out.println(clientName + " has left the room.");
        sendMessage(clientName + " has left the room.");
        socket.close();
    }
}