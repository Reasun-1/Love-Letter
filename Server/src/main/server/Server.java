package src.main.server;

import src.sample.Client;

import java.io.IOException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    // a set (here is a vector type) for the accepted sockets
    protected static List<Socket> sockets = new Vector<>();

    public static void main(String[] args) throws IOException {
        // create the server and define the port nr.
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        // accept the client request
        while (flag){
            try {
                // when new client comes, will be put into the sockets-set
                // with synchronized, there is only one thread at one time
                Socket accept = server.accept();
                synchronized (sockets){
                    sockets.add(accept);
                }
                // several server threads will respond to the client requests
                Thread thread = new Thread(new ServerThread(accept));
                thread.start();
                //catch the exception
            }catch (Exception e){
                flag = false;
                e.printStackTrace();
            }
        }
        //close the server
        server.close();
    }

}