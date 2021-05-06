package client.Controller;

import client.ViewModel.ChatRoomViewController;
import client.ViewModel.ErrorController;
import client.ViewModel.LoginController;
import client.ViewModel.QuestionController;
import client.ViewModel.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//test code
public class WindowLauncher {

    //creating the LogIn Window
    public void launchLogin(Client client) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Login");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/LoginWindow.fxml"));
        Parent root = loader.load();
        LoginController ctrl = loader.getController();
        ctrl.init(client);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
        stage.setOnCloseRequest((event) -> {
            Platform.exit();
        });
    }

    //Creating an window that pops up when errors appear
    public void launchError(String msg) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Error");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/ErrorWindow.fxml"));
        Parent root = loader.load();
        ErrorController ctrl = loader.getController();
        ctrl.init(msg);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }

    //creating the ChatRoom Window where the game will be played
    public void launchChat(Client client) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Chat: " + client.getName());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/ChatRoomView.fxml"));
        Parent root = loader.load();
        ChatRoomViewController ctrl = loader.getController();
        ctrl.init(client);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
        stage.setOnCloseRequest((event) -> {
            Platform.exit();
        });
    }

    // creating a window that shows up to help with questions
    public void launchQuestion(Client client, String message) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Question");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/View/QuestionWindow.fxml"));
        Parent root = loader.load();
        QuestionController ctrl = loader.getController();
        ctrl.init(client, message);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }
}

