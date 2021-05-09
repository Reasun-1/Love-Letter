package client.viewmodel;

/**
 * Controller for the end-of-round window
 * @author Pascal Stucky
 */

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class EndOfRoundController {

    @FXML
    private Button okButton;

    @FXML
    private Label infoField;

    /**
     * Method to be called from WindowLauncher to write the needed info based on the context.
     * @param info
     */
    public void init(String info) {
        infoField.setText(info);
    }

    @FXML
    /**
     * Method that closes the stage after pressing the "ok" button and the other window decided
     * by WindowLauncher Class will get opened
     * @param event
     */
    private void confirm(ActionEvent event) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

}
