package src.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader reader;
    private ViewModel viewmodel;

    public Client() throws IOException {
        this.startConnection();
    }

    public void startConnection() throws IOException {
        // Always connect to localhost and fixed port (evtl. noch ändern)
        socket = new Socket("127.0.0.1", 5200);
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

        String temp_name = viewmodel.getName();
        out.println(temp_name);

        boolean flag = true;

        while (flag) {
            String answer = in.readLine();
            if (answer.equals("user existed!")) {
                viewmodel.errorMessage("The chosen name already exists. Please choose another name.");
                String nameTry = viewmodel.getName();
                out.println(nameTry);

            } else {
                flag = false;
                viewmodel.welcomeMessage(answer);
            }
        }

    }

    public void sendMessage(String msg) throws IOException {
        // check logout condition
        if (!msg.equals("bye")) {
            out.println(msg);
        } else {
            // stop the connection
            if(out != null){
                try {
                    out.close();
                    System.out.println("You left the room.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        Client client = new Client();

        //create a client thread to read and write from the server
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //client socket waits for the input from the server
                        //if there is input,
                        //client.viewmodel.receiveMessage(client.in.readLine());
                        System.out.println(client.in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //wait for messages from ViewModel passed on by the inputstream, stop if the connection is terminated
        while (!client.socket.isClosed()) {
            String msg = client.reader.readLine();
            if (!msg.equals("")) {
                client.sendMessage(msg);
            }
        }
    }
}