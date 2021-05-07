package client.viewmodel;

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

    public void init(String info) {
        infoField.setText(info);
    }

    @FXML
    private void confirm(ActionEvent event) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

}
