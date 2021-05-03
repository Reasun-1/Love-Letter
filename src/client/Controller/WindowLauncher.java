package client.Controller;

import client.View.ChatRoomViewController;
import client.View.ErrorController;
import client.View.LogInController;
import client.View.QuestionController;
import client.ViewModel.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

//test code
public class WindowLauncher {

    public void launchLogin(Client client) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Login");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/logInWindow.fxml"));
        Parent root = loader.load();
        LogInController ctrl = loader.getController();
        ctrl.init(new logInViewModel(client));
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
        stage.setOnCloseRequest((event) -> {
            Platform.exit();
        });
    }

    public void launchError(String msg) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Error");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/ErrorWindow.fxml"));
        Parent root = loader.load();
        ErrorController ctrl = loader.getController();
        ctrl.init(new ErrorViewModel(msg));
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }

    public void launchChat(String name, ChatRoomViewModel chatVM) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Chat: " + name);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/ChatRoomView.fxml"));
        Parent root = loader.load();
        ChatRoomViewController ctrl = loader.getController();
        ctrl.init(chatVM);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        stage.setOnCloseRequest((event) -> {
            Platform.exit();
        });
    }

    public void launchQuestion(Client client, String msg) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Login");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/QuestionWindow.fxml"));
        Parent root = loader.load();
        QuestionController ctrl = loader.getController();
        ctrl.init(new QuestionViewModel(client, msg));
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }
}

