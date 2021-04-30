package client.Controller;

import client.ViewModel.logInViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//test code
public class MainLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("logInViewModel.fxml"));
        Parent root = loader.load();

        LogInController ctrl = loader.getController();
        ctrl.initialize(new logInViewModel((new Client())));

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }
}

