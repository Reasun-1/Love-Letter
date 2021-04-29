package client.View;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import client.Client;
import ViewModel.ChatRoomViewModel;

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
    private ListView<String> chatView;

    private final ChatRoomViewModel ViewModel = new ChatRoomViewModel();


    //  Method for the initialization of the Button and the Text Field via bidirectional binding.
    @FXML
    void initialize() {

        typeField.textProperty().bindBidirectional(ViewModel.messageProperty());
        sendButton.defaultButtonProperty().bindBidirectional(ViewModel.sendButtonProperty());
    }

    // Method for sending messages to the server.
    @FXML
    public void sendMessage() {

        ViewModel.sendMessage();
    }

    // Method for setting Client messages in the List View.

    public void setClient(Client client) {

        ViewModel.setClient(client);
        chatView.setItems(ViewModel.getClient().chatMessages);
    }

}
