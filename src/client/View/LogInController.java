package client.View;

import client.ViewModel.logInViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class LogInController {

    @FXML
    private AnchorPane logo;

    @FXML
    private AnchorPane background;

    @FXML
    private TextField name;

    @FXML
    private Button startButton;

    private logInViewModel logInVM;

    // a method to initialize the logIn View (window) based on the FXML designed file
    public void initialize(logInViewModel logInVM){
        this.logInVM = logInVM;
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
