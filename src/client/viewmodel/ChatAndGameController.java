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
    private TextField clientName;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private AnchorPane writePane;

    @FXML
    private TextField messageField;

    @FXML
    private ComboBox<?> sendToBox;

    @FXML
    private Button sendButton;

    @FXML
    private AnchorPane chatFieldPane;

    @FXML
    private ScrollPane scrollField;

    @FXML
    private AnchorPane gameFieldPane;

    @FXML
    private AnchorPane scorePane;

    @FXML
    private ImageView imageToken;

    @FXML
    private Text scoreText;

    @FXML
    private TextField personalScore;

    @FXML
    private TextField secPlayerScore;

    @FXML
    private TextField thirdPlayerScore;

    @FXML
    private TextField fourthPlayerScore;

    @FXML
    private Button startGameButton;

    @FXML
    private GridPane mainGridPane;

    @FXML
    private AnchorPane secPlayerPane;

    @FXML
    private Text secPlayerText;

    @FXML
    private AnchorPane secHandCardPane;

    @FXML
    private MenuButton secHandCardMenuButton;

    @FXML
    private MenuItem secHandCardItemOne;

    @FXML
    private MenuItem secHandCardItemTwo;

    @FXML
    private AnchorPane secPlayerMenuPane;

    @FXML
    private MenuButton secDrawnCardMenuButton;

    @FXML
    private MenuItem secDrawnCardItemOne;

    @FXML
    private MenuItem secDrawnCardItemTwo;

    @FXML
    private AnchorPane thirdPlayerPane;

    @FXML
    private Text thirdPlayerText;

    @FXML
    private MenuButton thirdHandCardMenuButton;

    @FXML
    private MenuItem thirdHandCardItemOne;

    @FXML
    private MenuItem thirdHandCardItemTwo;

    @FXML
    private MenuButton thirdDrawnCardMenuButton;

    @FXML
    private MenuItem thirdDrawnCardItemOne;

    @FXML
    private MenuItem thirdDrawnCardItemTwo;

    @FXML
    private AnchorPane fourthPlayerPane;

    @FXML
    private Text fourthPlayerText;

    @FXML
    private AnchorPane fourthPlayerButtonPane;

    @FXML
    private MenuButton fourthHandCardMenuButton;

    @FXML
    private MenuItem fourthHandCardItemOne;

    @FXML
    private MenuItem fourthHandCardItemTwo;

    @FXML
    private MenuButton fourthDrawnCardMenuButton;

    @FXML
    private MenuItem fourthDrawnCardItemOne;

    @FXML
    private MenuItem fourthDrawnCardItemTwo;

    @FXML
    private AnchorPane youPane;

    @FXML
    private MenuButton yourHandCardMenuButton;

    @FXML
    private MenuItem yourHandCardItemOne;

    @FXML
    private MenuItem yourHandCardItemTwo;

    @FXML
    private MenuButton yourDrawnCardMenuButton;

    @FXML
    private MenuItem yourDrawnCardItemOne;

    @FXML
    private MenuItem yourDrawnCardItemTwo;

    @FXML
    private AnchorPane yourPane;

    @FXML
    private Text youText;

    private Client client;

}
