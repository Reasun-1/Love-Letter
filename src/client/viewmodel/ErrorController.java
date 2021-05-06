package client.viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ErrorController {
    @FXML
    private Pane errorRootPane;

    @FXML
    private Button okButton;

    @FXML
    private Label errorField;

    public void init(String msg) {
        errorField.setText(msg);
    }

    @FXML
    private void okButtonClicked(ActionEvent event) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
