package client.viewmodel;

/**
 * Controller for question window
 *
 * @author Pascal Stucky
 */

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class QuestionController {

    @FXML
    private Button sendButton;

    @FXML
    private Label questionField; //label provides the change of the text based on the context

    @FXML
    private TextField answer; // textfield provides input of a message

    private Client client;

    /**
     * store the client to transmit the answer and display the question
     *
     * @param client
     * @param message
     */
    public void init(Client client, String message) {
        this.client = client;
        questionField.setText(message);
        sendButton.disableProperty().bind(answer.textProperty().isEmpty());
    }

    @FXML
    public void send(ActionEvent actionEvent) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        client.sendAnswer(answer.getText());
        stage.close();
    }
}
