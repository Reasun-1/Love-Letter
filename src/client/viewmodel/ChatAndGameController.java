package client.viewmodel;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import client.controller.Client;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

// Controller Class for the Chat and Game Window connected with the fxml file via data binding

public class ChatAndGameController {


    @FXML
    private BorderPane chatSplitting;

    //@FXML
    //private Label clientName; //must be bonded with the logInWindow (clientName)

    @FXML
    private TextArea messagesHistory; //registers the written messages on TextField

    @FXML
    private TextField messageField; //bind the typed message with message history scroll pane

    @FXML
    private TextField sendTo; //send Message to a specific player on private

    @FXML
    private Button sendButton; //send from messageField a typed message to message history

    @FXML
    private GridPane playBoard;

    @FXML
    private Text secPlayerText;

    @FXML
    private Text thirdPlayerText;

    @FXML
    private Text fourthPlayerText;

    @FXML
    private Button playHandCardYou;

    @FXML
    private Button playDrawnCardYou;

    @FXML
    private ImageView yourHandCard; //the card u have in your hands

    @FXML
    private ImageView yourDrawnCard; //the taken card

    @FXML
    private ImageView player2DrawnCard;

    @FXML
    private ImageView player2HandCard;

    @FXML
    private ImageView player3DrawnCard;

    @FXML
    private ImageView player3HandCard;

    @FXML
    private ImageView player4DrawnCard;

    @FXML
    private ImageView player4HandCard;

    @FXML
    private TextField yourScore; //score table

    @FXML
    private TextField player2Score;

    @FXML
    private TextField player3Score;

    @FXML
    private TextField player4Score;

    @FXML
    private TextArea outOfRoundCards;

    @FXML
    private Button createGame;

    @FXML
    private Button joinGame;

    @FXML
    private Button startGame;

    @FXML
    private Label player2;

    @FXML
    private Label player3;

    @FXML
    private Label player4;

    @FXML
    private Label score1;

    @FXML
    private Label score2;

    @FXML
    private Label score3;

    @FXML
    private Label score4;

    private Client client;

    private List<Image> images = Arrays.asList(new Image(getClass().getResource("/client/resources/Card_Back_Red.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Guard_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Spy_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Baron_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Handmaid_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Prince_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/King_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Countess_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Princess_Card.jpg").toString()),
            new Image(getClass().getResource("/client/resources/Card_Back_Beige.jpg").toString()));


    @FXML
    /**
     * Method to be called from WindowLauncher to start the attributes from the FXML File of
     * ChatAndGameController.
     * @param client
     */
    public void init(Client client){
        this.client = client;
        //clientName.setText(client.getName());
        //create the view model of Chat and Game Room
        //ChatAndGameViewModel chgvm = new ChatAndGameViewModel();
        //connects the send button and the message field together (if message field is empty then u can't press the send button)
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());
        messagesHistory.textProperty().bindBidirectional(client.getChatHistory());
        yourHandCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getHandCard(0).getValue()),
                client.getHandCard(0)));
        yourDrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getDrawnCard(0).getValue()),
                client.getDrawnCard(0)));
        player2HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getHandCard(1).getValue()),
                client.getHandCard(1)));
        player2DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getDrawnCard(1).getValue()),
                client.getDrawnCard(1)));
        player3HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getHandCard(2).getValue()),
                client.getHandCard(2)));
        player3DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getDrawnCard(2).getValue()),
                client.getDrawnCard(2)));
        player4HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getHandCard(3).getValue()),
                client.getHandCard(3)));
        player4DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> images.get(client.getDrawnCard(3).getValue()),
                client.getDrawnCard(3)));
        outOfRoundCards.textProperty().bindBidirectional(client.getDiscardedCards());
        playHandCardYou.disableProperty().bind(client.getInTurn().not());
        playDrawnCardYou.disableProperty().bind(client.getInTurn().not());
        secPlayerText.textProperty().bind(client.getPlayers(1));
        thirdPlayerText.textProperty().bind(client.getPlayers(2));
        fourthPlayerText.textProperty().bind(client.getPlayers(3));
        player2.textProperty().bind(client.getPlayers(1));
        player3.textProperty().bind(client.getPlayers(2));
        player4.textProperty().bind(client.getPlayers(3));
        createGame.disableProperty().bind(client.getGameExists());
        joinGame.disableProperty().bind(client.getGameExists().not().or(client.getGameRunning()));
        startGame.disableProperty().bind(client.getGameExists().not().or(client.getGameRunning()));
    }

    @FXML
    //send method makes the message get sent from message field to messages History(ScrollPane)
    private void send(){
        if (sendTo.getText().isEmpty()) {
            client.sendMessage(messageField.getText());
        } else {
            client.sendPersonalMessage(sendTo.getText(), messageField.getText());
        }
        messageField.clear();
        sendTo.clear();
    }

    @FXML
    private void playHandCard(){
        client.playHandCard();
    }

    @FXML
    private void playDrawnCard(){
        client.playDrawnCard();
    }

    @FXML
    private void joinGame(){
        client.joinGame();
    }

    @FXML
    private void createGame(){
        client.createGame();
    }

    @FXML
    private void startGame(){
        client.startGame();
    }

}
