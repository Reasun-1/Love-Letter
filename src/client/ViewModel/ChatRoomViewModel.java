package client.ViewModel;


import client.Controller.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

public class ChatRoomViewModel {
    private Client client;
    private TextArea chatInput;
    private TextField messageOutput;

    public ChatRoomViewModel(Client client) throws IOException {
        this.client = client;
        chatInput = new TextArea();
        messageOutput = new TextField();
    }

    public StringProperty getChatInput(){
        return chatInput.textProperty();
    }

    public StringProperty getMessageOutput(){
        return messageOutput.textProperty();
    }

    public void updateChat(String msg){
        chatInput.appendText(msg + "\n");
    }

    public void sendMessage(){
        client.sendMessage(messageOutput.textProperty().get());
        messageOutput.clear();
        messageOutput.setPromptText("Type your message");
    }

}
