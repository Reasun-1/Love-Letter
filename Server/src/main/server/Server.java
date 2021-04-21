package src.main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class Server {

    private volatile static Server server;
    // a set (here is a vector type) for the accepted sockets
    protected static List<ServerThread> threads = new Vector<>();
    // a set for checking clients´ name
    protected static HashSet<String> clientList = new HashSet<>();

    private Server() {
    }

    //Singleton with DCL (double-checked locking)
    public static Server getServer() {
        if (server == null) {
            synchronized (Server.class) {
                if (server == null) {
                    server = new Server();
                }
            }
        }
        return server;
    }

    public static List<ServerThread> getThreads() {
        return threads;
    }

    /*public static HashSet<String> getClientList() {
        return clientList;
    }*/

    public void start() throws IOException {
        // create the server and define the port nr.
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        // accept the client request
        while (flag) {
            try {
                // when new client comes, will be put into the sockets-set
                // with synchronized, there is only one thread at one time
                Socket clientSocket = server.accept();
                synchronized (threads) {
                    ServerThread thread = new ServerThread(clientSocket);
                    threads.add(thread);
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

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }

}