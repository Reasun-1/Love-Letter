package client.View;

import client.ViewModel.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {

    @FXML
    private AnchorPane logo;

    @FXML
    private AnchorPane background;

    @FXML
    private TextField name;

    @FXML
    private Button startButton;

    private logInViewModel logInVM;

    public LogInController(logInViewModel logIn){
        logInVM = logIn;
    }


    // a method to initialize the logIn View (window) based on the FXML designed file
    @Override
    public void initialize(URL url, ResourceBundle rb){
        /*With bidirectional binding, the two property values are synchronized so that if either
         property changes, the other property is automatically changed as well */
        name.textProperty().bindBidirectional(logInVM.heroNameProperty());
        //heroNameProperty() is a method declared on the LogInViewModel that returns the username required on the TextField
    }

    @FXML
    private void loginButton(ActionEvent event) {
        logInVM.nameEntered();
        Platform.exit();
    }
}
