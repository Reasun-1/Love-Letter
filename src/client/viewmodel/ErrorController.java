package client.viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ErrorController {
    @FXML
    private Pane errorRootPane;

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
