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
public class WindowLauncher {

    public Scene launchLogin(PipedInputStream instream) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/logInWindow.fxml"));
        LogInController ctrl = new LogInController(new logInViewModel(instream));
        loader.setController(ctrl);
        Parent root = loader.load();
        return new Scene(root, 600, 400);
    }

    public Scene launchError(String msg) throws IOException{
        // errorMessage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ErrorWindow.fxml"));
        ErrorController ctrl = new ErrorController(new ErrorViewModel(msg));
        loader.setController(ctrl);
        Parent root = loader.load();
        return new Scene(root, 600, 400);
    }

    public Scene launchChat(ChatRoomViewModel chatVM) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ChatRoomView.fxml"));
        ChatRoomViewController ctrl = new ChatRoomViewController(chatVM);
        loader.setController(ctrl);
        Parent root = loader.load();
        return new Scene(root, 600, 400);
    }
}

