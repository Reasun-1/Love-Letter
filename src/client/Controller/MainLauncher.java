package client.Controller;

import client.View.ChatRoomViewController;
import client.View.ErrorController;
import client.View.LogInController;
import client.ViewModel.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;

//test code
public class MainLauncher{

    public Scene launchLogin(PipedInputStream instream) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("logInWindow.fxml"));
        Parent root = loader.load();
        LogInController ctrl = loader.getController();
        ctrl.init(new logInViewModel(instream));
        return new Scene(root, 600, 400);
    }

    public Scene launchError(String msg) throws IOException{
        // errorMessage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ErrorWindow.fxml"));
        Parent root = loader.load();
        ErrorController ctrl = loader.getController();
        ctrl.init(new ErrorViewModel(msg));
        return new Scene(root, 600, 400);
    }

    public Scene launchChat(ChatRoomViewModel chatVM) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ChatRoomView.fxml"));
        Parent root = loader.load();
        ChatRoomViewController ctrl = loader.getController();
        ctrl.init(chatVM);
        return new Scene(root, 600, 400);
    }
}

