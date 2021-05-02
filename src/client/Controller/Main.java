package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Client client = new Client();
        WindowLauncher launcher = new WindowLauncher();

        launcher.launchLogin(client);

        boolean flag1 = true;
        while(flag1) {
            boolean flag2 = true;
            while(flag2) {
                String errorMsg = client.getErrorMsg();
                if (errorMsg.isEmpty()) {
                    continue;
                } else if (!errorMsg.equals("done")) {
                    launcher.launchError(errorMsg);
                }
                flag2 = false;
            }
            if(client.getName().isEmpty()){
                launcher.launchLogin(client);
            } else {
                flag1 = false;
            }
        }
        ChatRoomViewModel chatVM = new ChatRoomViewModel(client);
        launcher.launchChat(client.getName(), chatVM);
            new Thread(() -> {
                try {
                    while (!client.getSocket().isClosed()){
                        // Client socket waits for the input from the server
                        // If there is input, display the message (currently via terminal)
                        String line = client.getIn().readLine();
                        if (line.charAt(0) == '/'){
                            client.executeOrder(line.substring(1));
                        } else {
                            chatVM.updateChat(line.substring(1));
                        }
                        // Soon: Pass the message to the chat window
                    }
                    Platform.exit();
                } catch (IOException e) {
                    try{
                        client.getSocket().close();
                        Platform.exit();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            // Wait for messages from the user passed on by the InputStream, stop if the connection is terminated
           /* while (!client.getSocket().isClosed()) {
                String msg = "";
                if (!msg.isEmpty()) {
                    client.sendMessage(msg);
                }
            }*/
    }
}
