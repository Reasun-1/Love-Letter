package client.controller;

/**
 * class for creating the stages and opening the windows
 *
 * @author Pascal Stucky
 * @version 1.0-SNAPSHOT
 */

import client.viewmodel.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WindowLauncher {

    /**
     * Create a Login-Window and init the controller with a handle on the client
     * @param client
     * @throws IOException
     */
    public void launchLogin(Client client) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Login");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/LoginWindow.fxml"));
        Parent root = loader.load();
        LoginController ctrl = loader.getController();
        ctrl.init(client);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
        stage.setOnCloseRequest((event) -> Platform.exit());
    }

    /**
     * Create an Error-Window and init the controller with the message to display
     * @param message
     * @throws IOException
     */
    public void launchError(String message) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Error");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/ErrorWindow.fxml"));
        Parent root = loader.load();
        ErrorController ctrl = loader.getController();
        ctrl.init(message);
        stage.setScene(new Scene(root, 388, 186));
        stage.showAndWait();
        stage.setOnCloseRequest((event) -> Platform.exit());
    }

    /**
     * Create a Chat&Game-Window and init the controller with a handle on the client
     * @param client
     * @throws IOException
     */
    public void launchChatAndGame(Client client) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Chat: " + client.getName());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/ChatAndGameWindow.fxml"));
        Parent root = loader.load();
        ChatAndGameController ctrl = loader.getController();
        ctrl.init(client);
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
        stage.setOnCloseRequest((event) -> Platform.exit());
    }

    /**
     * Create a Question-Window and init the controller with a handle on the client
     * and the message to display
     * @param client
     * @param message
     * @throws IOException
     */
    public void launchQuestion(Client client, String message) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("Question");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/QuestionWindow.fxml"));
        Parent root = loader.load();
        QuestionController ctrl = loader.getController();
        ctrl.init(client, message);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }

    /**
     * Create a Window displaying the results of a finished round
     * @param info
     * @throws IOException
     */
    public void launchEndOfRound(Client client, String winner) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("End of Round");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/EndOfRoundWindow.fxml"));
        Parent root = loader.load();
        EndOfRoundController ctrl = loader.getController();
        ctrl.init(client, winner);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }

    /**
     * Create a Window displaying the results of a finished game
     * @param client
     * @param winner
     * @throws IOException
     */
    public void launchEndOfGame(Client client, String winner) throws IOException{
        Stage stage = new Stage();
        stage.setTitle("End of Game");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/view/EndOfGameWindow.fxml"));
        Parent root = loader.load();
        EndOfGameController ctrl = loader.getController();
        ctrl.init(client, winner);
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
    }
}

