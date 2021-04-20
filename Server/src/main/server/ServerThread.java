package src.main.server;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * server thread for several client request
 */
public class ServerThread extends Server implements Runnable{

    Socket socket;
    String socketName;
    // combine the client socket to the serverThread Constructor
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // set the socket name of the client
            // ****the socket name must be adjusted ****
            socketName = socket.getRemoteSocketAddress().toString();
            System.out.println("Client "+socketName+" has joined.");
            print("Client "+socketName+" has joined");
            boolean flag = true;
            while (flag)
            {
                //wait for the output Stream from Client
                String line = reader.readLine();
                // if no message from the client, then wait
                if (line == null){
                    flag = false;
                    continue;
                }
                String msg = "Client "+socketName+":"+line;
                System.out.println(msg);
                // give the message to all clients online
                print(msg);
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
     create the method print: send message to all the clients online
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }
    /**
    close socket connection
     */
    public void closeConnect() throws IOException {
        System.out.println("Client "+socketName+" has left the room.");
        print("Client "+socketName+" has left the room.");
        //remove the socket from the set
        synchronized (sockets){
            sockets.remove(socket);
        }
        socket.close();
    }
}