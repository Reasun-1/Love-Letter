package client.viewmodel;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import client.controller.Client;

// Controller Class for the Chat and Game Window connected with the fxml file via data binding

public class ChatAndGameController {


    @FXML
    private BorderPane chatSplitting;

    @FXML
    private TextField clientName; //must be bonded with the logInWindow (clientName)

    @FXML
    private ScrollPane messagesHistory; //registers the written messages on TextField

    @FXML
    private TextField messageField; //bind the typed message with message history scroll pane

    @FXML
    private ComboBox sendTo; //send Message to a specific player on private

    @FXML
    private Button send; //send from messageField a typed message to message history

    @FXML
    private GridPane playBoard;

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
    private ScrollPane outOfRoundCards;

    private Client client;



}
