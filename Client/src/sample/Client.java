package sample;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{

    public static void main(String[] args) throws IOException {
        //create the client socket with localhost and port nr.
        Socket socket = new Socket("127.0.0.1",5200);
        // create a reader to get input from Terminal
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // create out and in for the client sockets
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //create a client thread to read and write from the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //client socket waits for the input from the server
                        //if there is input, print the info
                        System.out.println(in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //write message to server
        String line = reader.readLine();
        while (!"bye".equalsIgnoreCase(line)){
            // if the message is not "end", then write it to the server
            out.println(line);
            out.flush();
            //get the content of the terminal-input
            line = reader.readLine();
        }
        out.close();
        in.close();
        socket.close();

    }
}