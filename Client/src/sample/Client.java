package src.sample;

import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader reader;
    private ViewModel viewmodel;

    public void startConnection() throws IOException {
        // Always connect to localhost and fixed port (evtl. noch ändern)
        socket = new Socket("127.0.0.1",5200);
        // Create Writer to send messages to server
        out = new PrintWriter(socket.getOutputStream(), true);
        // Create Reader to receive messages from server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // create ViewModel which communicates with View
        viewmodel = new ViewModel();
        // Create Reader to receive messages from ViewModel
        reader = new BufferedReader(new InputStreamReader(viewmodel.getInputStream()));
        // start login process
        login();
    }

    public void login() throws IOException {

        // we start with an empty string as name (for the while loop)
        name = "";
        while(name.equals("")){
            // Receive a name from the Viewmodel
            String temp_name = viewmodel.getName();
            // send the name to the server
            out.println(temp_name);
            // if the name is accepted, the server sends the name back
            if(in.readLine().equals(temp_name)){
                name = temp_name;
            }
            else{
                // if the name is not accepted, send an error message and return to the beginning of the loop
                viewmodel.errorMessage("The chosen name already exists. Please choose another name.");
            }
        }
        // send welcome message with the accepted name
        viewmodel.welcomeMessage(name);
    }

    public void sendMessage(String msg) throws IOException {
        // check logout condition
        if(!msg.equals("bye")){
            out.println(name + ": " + msg);
        }
        else{
            // stop the connection
            in.close();
            out.close();
            socket.close();
        }
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection();

        //create a client thread to read and write from the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //client socket waits for the input from the server
                        //if there is input,
                        client.viewmodel.receiveMessage(client.in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //wait for messages from ViewModel passed on by the inputstream, stop if the connection is terminated
        while(!client.socket.isClosed()){
            client.sendMessage(client.reader.readLine());
        }
    }
}