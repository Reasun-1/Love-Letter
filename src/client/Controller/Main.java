package client.Controller;

import client.ViewModel.ChatRoomViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.util.Arrays;

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
                    String line;
                    while (!client.getSocket().isClosed()){
                        // Client socket waits for the input from the server
                        // If there is input, display the message (currently via terminal)
                        line = client.getIn().readLine();
                        if (line.charAt(0) == '/'){
                            String order = line.substring(1);
                            switch (order.charAt(0)) {
                                case '0':
                                    client.sendMessage("done");
                                    client.getSocket().close();
                                    break;
                                case '1':
                                    client.sendMessage("done");
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                launcher.launchError(order.substring(1));
                                            } catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case '2':
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                launcher.launchQuestion(client, "Please choose a Player:");
                                            } catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case '3':
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                launcher.launchQuestion(client, "Please enter your card guess:");
                                            } catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                case '4':
                                    client.startGameInfo(order.substring(1));
                                    break;
                                case '5':
                                    client.setDrawnCard(order.substring(1));
                                    chatVM.updateCards(order.substring(1));
                                    break;
                                case '6':
                                    client.setPlayedCard(order.substring(1));
                                    break;
                                case '7':
                                    client.endOfRound(order.substring(1));
                                    // End-Of-Round-Window
                                    break;
                                case '8':
                                    client.endOfGame(order.substring(1));
                                    // End-Of-Game-Window
                                    break;
                            }
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
