package client.viewmodel;

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * This class is the controller class for the End of Game fxml file and it represents the end of game pop-up window.
 *
 * @author Xheneta, Pascal
 * @version 1.0-SNAPSHOT
 */
public class EndOfGameController {

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
     *
     * @param client
     */
    public void init(Client client, String winner) {
        personalPlayer.textProperty().bindBidirectional(client.getPlayers(0));
        secondPlayer.textProperty().bindBidirectional(client.getPlayers(1));
        thirdPlayer.textProperty().bindBidirectional(client.getPlayers(2));
        fourthPlayer.textProperty().bindBidirectional(client.getPlayers(3));

        personalScore.textProperty().bindBidirectional(client.getTOKENS(0), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        secondScore.textProperty().bindBidirectional(client.getTOKENS(1), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        thirdScore.textProperty().bindBidirectional(client.getTOKENS(2), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        fourthScore.textProperty().bindBidirectional(client.getTOKENS(3), new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
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


