package client.viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EndOfGameController {

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
