package client.viewmodel;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import client.controller.Client;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;

/**
 * This class is the controller class for the Chat and Game Window connected with the fxml file via data binding.
 *
 * @author Rajna Fani
 * @author Xheneta Krasniqi
 * @author Pascal Stucky
 * @version 1.0-SNAPSHOT
 */
public class ChatAndGameController {

    @FXML
    private TextArea outOfRoundCards1; //registers the written messages on TextField

    @FXML
    private TextField messageField; //bind the typed message with message history scroll pane

    @FXML
    private TextField sendTo; //send Message to a specific player on private

    @FXML
    private Button sendButton; //send from messageField a typed message to message history

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

    private final List<Image> IMAGES = Arrays.asList(new Image(getClass().getResource("/client/resources/Card_Back_Red.jpg").toString()),
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
    public void init(Client client) {
        this.client = client;

        //connects the send button and the message field together (if message field is empty then u can't press the send button)
        sendButton.disableProperty().bind(messageField.textProperty().isEmpty());

        //binds the button of sending a message with the chat TextArea that saves all the messages(chat history)
        outOfRoundCards1.textProperty().bindBidirectional(client.getChatHistory());

        //adapting the hand and drawn cards and their methods from Client class with the card images
        yourHandCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getHandCard(0).getValue()),
                client.getHandCard(0)));
        yourDrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getDrawnCard(0).getValue()),
                client.getDrawnCard(0)));
        player2HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getHandCard(1).getValue()),
                client.getHandCard(1)));
        player2DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getDrawnCard(1).getValue()),
                client.getDrawnCard(1)));
        player3HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getHandCard(2).getValue()),
                client.getHandCard(2)));
        player3DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getDrawnCard(2).getValue()),
                client.getDrawnCard(2)));
        player4HandCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getHandCard(3).getValue()),
                client.getHandCard(3)));
        player4DrawnCard.imageProperty().bind(Bindings.createObjectBinding(() -> IMAGES.get(client.getDrawnCard(3).getValue()),
                client.getDrawnCard(3)));

        // Text area will be bonded with getDiscardedCards() method from Client Class and will list
        //the cards that are played and aren't available  anymore to play
        outOfRoundCards.textProperty().bindBidirectional(client.getDiscardedCards());

        //Methods from client class that give the options of playing the cards that are drawn or that are on the hand
        playHandCardYou.disableProperty().bind(client.getInTurn().not());
        playDrawnCardYou.disableProperty().bind(client.getInTurn().not());

        //the name of the clients(players) will be written on these text areas
        secPlayerText.textProperty().bind(client.getPlayers(1));
        thirdPlayerText.textProperty().bind(client.getPlayers(2));
        fourthPlayerText.textProperty().bind(client.getPlayers(3));
        player2.textProperty().bind(client.getPlayers(1));
        player3.textProperty().bind(client.getPlayers(2));
        player4.textProperty().bind(client.getPlayers(3));

        //binding the createGame button if there is no other game created by the server
        createGame.disableProperty().bind(client.getGameExists());

        //binding the joinGame button with the getGameRunning() method on Client class to join the game if it has already been started
        joinGame.disableProperty().bind(client.getGameExists().not().or(client.getGameRunning()));

        //binding the startGame button with the getGameRunning() method on Client class to start the game if it has not been started
        startGame.disableProperty().bind(client.getGameExists().not().or(client.getGameRunning()));

        // binds the score textarea with the actual score during the game
        score1.textProperty().bindBidirectional(client.getTOKENS(0), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        score2.textProperty().bindBidirectional(client.getTOKENS(1), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        score3.textProperty().bindBidirectional(client.getTOKENS(2), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        score4.textProperty().bindBidirectional(client.getTOKENS(3), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
    }

    @FXML
    //send method makes the message get sent from message field to messages History(ScrollPane)
    private void send() {
        if (sendTo.getText().isEmpty()) {
            client.sendMessage(messageField.getText());
        } else {
            client.sendPersonalMessage(sendTo.getText(), messageField.getText());
        }
        messageField.clear();
        sendTo.clear();
    }

    @FXML
    private void playHandCard() {
        client.playHandCard();
    } // method that provides playing the Cards on the hand

    @FXML
    private void playDrawnCard() {
        client.playDrawnCard();
    } // method for playing drawn cards

    @FXML
    private void joinGame() {
        client.joinGame();
    } //method for joining the game

    @FXML
    private void createGame() {
        client.createGame();
    } //method for creating a game

    @FXML
    private void startGame(ActionEvent event) {
        client.startGame();
    } //method for starting a game

}
