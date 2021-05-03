package client.ViewModel;

import client.ViewModel.ChatRoomViewModel;
import client.ViewModel.ErrorViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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
