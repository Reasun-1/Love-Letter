package client.View;

import client.ViewModel.ChatRoomViewModel;
import client.ViewModel.ErrorViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController implements Initializable {
    @FXML
    private AnchorPane errorRootPane;

    @FXML
    private Button OKButton;

    @FXML
    private Label errorField;

    private ErrorViewModel errorVM;

    public ErrorController(ErrorViewModel error){
        errorVM = error;
    }

    public void initialize(URL url, ResourceBundle rb) {
        errorField.textProperty().bindBidirectional(errorVM.getErrorMessage());
    }

    @FXML
    private void OKButton(ActionEvent event) {
        Platform.exit();
    }
}
