package client.View;

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

    private ChatRoomViewModel chatVM;

    //  Method for the initialization of the Button and the Text Field via bidirectional binding.
    public void init(ChatRoomViewModel chat) {
        chatVM = chat;
        typeField.textProperty().bindBidirectional(chatVM.getMessageOutput());
        chatView.textProperty().bindBidirectional(chatVM.getChatInput());
        //sendButton.defaultButtonProperty().bindBidirectional(ViewModel.sendButtonProperty());
    }

    // Method for sending messages to the server.
    @FXML
    private void sendButton(ActionEvent event) {
        chatVM.sendMessage();
    }


    // Method for setting Client messages in the List View.

    /*public <Client> void setClient(Client client) {

        //ViewModel.setClient(client);
        //chatView.setItems(ViewModel.getClient().chatMessages);
    }*/

}
