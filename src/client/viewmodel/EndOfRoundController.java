package client.viewmodel;

/**
 * Controller for the end-of-round window
 *
 * @author Pascal Stucky
 */

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class is the controller class for the End of Game fxml file and it represents the end of game pop-up window.
 * @author Xheneta, Pascal
 */

public class EndOfRoundController {

    @FXML
    private Button doneButton;

    @FXML
    private Label winnerName;

    @FXML
    private Text personalPlayer;

    @FXML
    private Text secondPlayer;

    @FXML
    private Text thirdPlayer;

    @FXML
    private Text fourthPlayer;

    @FXML
    private Label personalScore;

    @FXML
    private Label secondScore;

    @FXML
    private Label thirdScore;

    @FXML
    private Label fourthScore;


    /**
     * This method initializes the players and the winner in the end of game window using data binding.
     * @param client
     */
    public void init(Client client, String winner) {
        personalPlayer.textProperty().bindBidirectional(client.getPlayers(0));
        secondPlayer.textProperty().bindBidirectional(client.getPlayers(1));
        thirdPlayer.textProperty().bindBidirectional(client.getPlayers(2));
        fourthPlayer.textProperty().bindBidirectional(client.getPlayers(3));

        personalScore.textProperty().bindBidirectional(client.getScore(0));
        secondScore.textProperty().bindBidirectional(client.getScore(1));
        thirdScore.textProperty().bindBidirectional(client.getScore(2));
        fourthScore.textProperty().bindBidirectional(client.getScore(3));
        winnerName.setText(winner);
    }

    @FXML
    /**
     * This method closes the stage after pressing the "Done!" button
     * signalizing the end of the Game
     * @param event
     */
    private void confirm(ActionEvent event) {
        Stage stage = (Stage) doneButton.getScene().getWindow();
        stage.close();

    }
}

