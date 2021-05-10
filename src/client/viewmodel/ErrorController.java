package client.viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * ErrorController Class is responsible for the Error Window that pops up when an error occurs.
 * It is connected with the FXML ErrorWindow File and with ErrorViewModel in order to complete the
 * conditions of a MVVM model
 * @author Rajna Fani
 * @author Pascal Stucky
 * @version 1.0-SNAPSHOT
 */


public class ErrorController {

    @FXML
    private Button okButton;

    @FXML
    private Label errorField; // label provides the change of the error message based on the type of error classified on the Client class

    /**
     * Method to be called from WindowLauncher to set the error that happens.
     * @param msg
     */
    public void init(String msg) {
        errorField.setText(msg);
    }

    @FXML
    /**
     * Method that closes the stage after pressing the "ok" button and the other window decided
     * by WindowLauncher Class will get opened
     * @param event
     */
    private void okButtonClicked(ActionEvent event) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

}
