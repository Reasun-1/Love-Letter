package client.View;

import client.ViewModel.ChatRoomViewModel;
import client.ViewModel.ErrorViewModel;
import client.ViewModel.QuestionViewModel;
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

public class QuestionController {
    @FXML
    private Pane questionRootPane;

    @FXML
    private Button sendButton;

    @FXML
    private Label questionField;

    @FXML
    private TextField answer;

    private QuestionViewModel questionVM;

    public void init(QuestionViewModel question) {
        questionVM = question;
        questionField.textProperty().bindBidirectional(questionVM.getQuestionMessage());
        answer.textProperty().bindBidirectional(questionVM.getAnswer());
    }

    @FXML
    public void send(ActionEvent actionEvent) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        questionVM.answerEntered();
        stage.close();
    }
}
