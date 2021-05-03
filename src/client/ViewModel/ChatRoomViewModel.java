package client.ViewModel;


import client.Controller.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
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
    private TextArea handCards;
    private MenuItem handCardItem;
    private MenuItem drawnCardItem;

    public ChatRoomViewModel(Client client) {
        this.client = client;
        chatInput = new TextArea();
        messageOutput = new TextField();
        handCards = new TextArea();
        handCardItem = new MenuItem();
        drawnCardItem = new MenuItem();
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

    public void createGame() {
        client.createGame();
    }

    public void joinGame() {
        client.joinGame();
    }

    public void startGame() {
        client.startGame();
    }

    public void playHandCard(){
        String newHandCard = client.getDrawnCard().getType();
        handCards.setText(newHandCard);
        handCardItem.setText(newHandCard);
        drawnCardItem.setText("");
        client.playHandCard();
    }

    public void playDrawnCard(){
        handCards.setText(client.getHandCard().getType());
        drawnCardItem.setText("");
        client.playDrawnCard();
    }

    public void updateCards(String cardName){
        handCards.appendText(cardName + "\n");

    }
}
