package client.viewmodel;


import client.controller.Client;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

/**
 * This class is the ViewModel class for the Chat and Game Window.
 * @author Xheneta, Rajna
 */

public class ChatAndGameViewModel {/*
    private Client client;
    private TextArea chatInput;
    private TextField messageOutput;
    private Image handCards;
    private Image handCardItem;
    private Image drawnCardItem;

    public ChatAndGameViewModel(Client client) {
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

    public StringProperty getHandCards() {return handCards.textProperty();}

    public StringProperty getHandCardItem() {return handCardItem.textProperty();}

    public StringProperty getDrawnCardItem() {return drawnCardItem.textProperty();}

    public void updateChat(String msg){
        chatInput.appendText(msg + "\n");
    }

    public void sendMessage(){
        client.sendMessage(messageOutput.textProperty().get());
        messageOutput.clear();
        messageOutput.setPromptText("Type your message");
    }


    public void playHandCard(){
        client.playHandCard();
    }

    public void playDrawnCard(){
        client.playDrawnCard();
    }

    public void updateCards(String cardName){
        handCards.appendText(cardName + "\n");

    }*/
}
