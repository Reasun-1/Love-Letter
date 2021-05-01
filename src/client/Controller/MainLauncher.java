package client.Controller;

import client.ViewModel.logInViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//test code
public class MainLauncher extends Application {

    private Stage stage;

    public Stage getStage(){
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        try {
            Client client = new Client(this);
            // Create a client thread to read messages from the server
            new Thread(() -> {
                try {
                    while (!client.socket.isClosed()){
                        // Client socket waits for the input from the server
                        // If there is input, display the message (currently via terminal)
                        String line = client.in.readLine();
                        if (line.charAt(0) == '/'){
                            //   client.executeOrder(line.substring(1));
                        } else {
                            System.out.println(line.substring(1));
                        }
                        // Soon: Pass the message to the chat window
                    }
                } catch (IOException e) {
                    try{
                        client.socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }).start();
            // Wait for messages from the user passed on by the InputStream, stop if the connection is terminated
            while (!client.socket.isClosed()) {
                String msg = client.reader.readLine();
                if (!msg.isEmpty()) {
                    client.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String launchLogin(Client client){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("logInViewModel.fxml"));
        Parent root = loader.load();

        PipedInputStream instream = new PipedInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        LogInController ctrl = loader.getController();
        ctrl.initialize(new logInViewModel(instream));

        stage.setTitle("Login");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();

        return reader.readLine();

    }
}

