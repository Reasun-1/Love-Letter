package client.viewmodel;
/**
 * LoginController Class is responsible for the LogIn Window that appears at the beginning of the game
 * It is connected with the FXML LogInWindow File and with logInViewModel in order to complete the
 * conditions of a MVVM model
 * @author Rajna Fani
 * @version 1.0-SNAPSHOT
 */


import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private AnchorPane logo;

    @FXML
    private AnchorPane background;

    @FXML
    private TextField name;

    @FXML
    private Button startButton;

    private Client client;


    // a method to initialize the logIn View (window) based on the FXML designed file
    public void init(Client client){
        this.client = client;
        //create the viewModel
        logInViewModel loginvm = new logInViewModel();
        //connect the viewModel
        /*With bidirectional binding, the two property values are synchronized so that if either
         property changes, the other property is automatically changed as well */
        name.textProperty().bindBidirectional(logInViewModel.heroNameProperty());
        //heroNameProperty() is a method declared on the LogInViewModel that returns the username required on the TextField
        startButton.disableProperty().bind(logInViewModel.loginPossibleProperty().not());
    }

    @FXML
    // creates a new event where another scene(window) is opened after pressing the "start" Button on login window
    private void loginButton(ActionEvent event) {
        Stage stage = (Stage) startButton.getScene().getWindow();
        client.checkName(name.getText());
        stage.close();
    }
}
