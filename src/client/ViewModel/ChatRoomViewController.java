package client.ViewModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import client.Controller.Client;
import client.ViewModel.*;

import java.net.URL;
import java.util.ResourceBundle;

// Controller Class for the Chat Window connected with the fxml file via data binding

public class ChatRoomViewController {

    @FXML
    private MenuItem drawnCardItem;

    @FXML
    private MenuItem handCardItem;

    @FXML
    private MenuButton playCardButton;

    @FXML
    private AnchorPane chatRootPane;

    @FXML
    private AnchorPane writePane;

    @FXML
    private Button sendButton;

    @FXML
    private TextField typeField;

    @FXML
    private AnchorPane chatFieldPane;

    @FXML
    private TextArea chatView;

    @FXML
    private AnchorPane gameControlPane;

    @FXML
    private AnchorPane playCardPane;

    @FXML
    private TextArea handCards;

    @FXML
    private Button playButton;

    @FXML
    private Button createGame;

    @FXML
    private Button joinGame;

    @FXML
    private Button startGame;

    private Client client;

    //  Method for the initialization of the Button and the Text Field via bidirectional binding.
    public void init(Client client) {
        this.client = client;
        /*typeField.textProperty().bindBidirectional(chatVM.getMessageOutput());
        chatView.textProperty().bindBidirectional(chatVM.getChatInput());
        handCards.textProperty().bindBidirectional(chatVM.getHandCards());
        handCardItem.textProperty().bindBidirectional(chatVM.getHandCardItem());
        drawnCardItem.textProperty().bindBidirectional(chatVM.getDrawnCardItem());*/
        //sendButton.defaultButtonProperty().bindBidirectional(ViewModel.sendButtonProperty());
    }

    // Method for sending messages to the server.
    @FXML
    private void sendButton(ActionEvent event) {
        client.sendMessage(typeField.getText());
    }

    @FXML
    public void createGame(ActionEvent actionEvent) {
        client.createGame();
    }

    @FXML
    public void joinGame(ActionEvent actionEvent) {
        client.joinGame();
    }

    @FXML
    public void startGame(ActionEvent actionEvent) {
        client.startGame();
    }

    @FXML
    public void playHandCard(ActionEvent actionEvent) {
        client.playHandCard();
    }

    @FXML
    public void playDrawnCard(ActionEvent actionEvent){
        client.playDrawnCard();
    }


    // Method for setting Client messages in the List View.

    /*public <Client> void setClient(Client client) {

        //ViewModel.setClient(client);
        //chatView.setItems(ViewModel.getClient().chatMessages);
    }*/

}
