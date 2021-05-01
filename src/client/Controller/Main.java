package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client client = new Client();
        PipedInputStream instream = new PipedInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        WindowLauncher launcher = new WindowLauncher();

        while(client.getName().isEmpty()) {
            primaryStage.setTitle("Login");
            primaryStage.setScene(launcher.launchLogin(instream));
            primaryStage.show();

            String result = client.checkName(reader.readLine());
            if(!result.isEmpty()) {
                primaryStage.setTitle("Error");
                primaryStage.setScene(launcher.launchError(result));
                primaryStage.show();
            }
        }
        primaryStage.setTitle("Chat: " + client.getName());
        ChatRoomViewModel chatVM = new ChatRoomViewModel(instream);
        primaryStage.setScene(launcher.launchChat(chatVM));
        primaryStage.show();

        try {
            new Thread(() -> {
                try {
                    while (!client.getSocket().isClosed()){
                        // Client socket waits for the input from the server
                        // If there is input, display the message (currently via terminal)
                        String line = client.getIn().readLine();
                        if (line.charAt(0) == '/'){
                            //   client.executeOrder(line.substring(1));
                        } else {
                            chatVM.updateChat(line.substring(1));
                        }
                        // Soon: Pass the message to the chat window
                    }
                } catch (IOException e) {
                    try{
                        client.getSocket().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            // Wait for messages from the user passed on by the InputStream, stop if the connection is terminated
            while (!client.getSocket().isClosed()) {
                String msg = reader.readLine();
                if (!msg.isEmpty()) {
                    client.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
