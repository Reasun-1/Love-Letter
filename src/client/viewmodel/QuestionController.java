package client.viewmodel;

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class QuestionController {
    @FXML
    private Pane questionRootPane;

    @FXML
    private Button sendButton;

    @FXML
    private Label questionField;

    @FXML
    private TextField answer;

    private Client client;


    public void init(Client client, String message) {
        this.client = client;
        questionField.setText(message);
    }

    @FXML
    public void send(ActionEvent actionEvent) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        client.sendMessage(answer.getText());
        stage.close();
    }
}