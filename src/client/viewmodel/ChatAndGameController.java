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
    private AnchorPane chatRootPane;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private AnchorPane clientPane;

    @FXML
    private TextField clientName;

    @FXML
    private ScrollPane chat;

    @FXML
    private TextField messageField;

    @FXML
    private ComboBox<?> sendToBox;

    @FXML
    private Button sendButton;

    @FXML
    private GridPane playersField;

    @FXML
    private AnchorPane chatFieldPane;

    @FXML
    private TextField yourscore;

    @FXML
    private TextField player2score;

    @FXML
    private TextField player3score;

    @FXML
    private TextField player4score;

    @FXML
    private Button getStartGameButton;

    @FXML
    private AnchorPane yourPane;

    @FXML
    private Text youText;

    @FXML
    private Button getPlayHandCard1;

    @FXML
    private Button getPlayDrawnCard1;

    @FXML
    private AnchorPane player2;

    @FXML
    private Text player2text;

    @FXML
    private Button getPlayHandCard2;

    @FXML
    private Button getPlayDrawnCard2;

    @FXML
    private AnchorPane player3;

    @FXML
    private Text player3text;

    @FXML
    private Button getPlayHandCard3;

    @FXML
    private Button getPlayDrawnCard3;

    @FXML
    private AnchorPane player4;

    @FXML
    private Text player4text;

    @FXML
    private Button getPlayHandCard4;

    @FXML
    private Button getPlayDrawnCard4;

    private Client client;



}
