package client.viewmodel;

import client.controller.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
// import javafx.util.StringConverter;
// import client.controller.Client;

/**
 * This class is the controller class for the End of Game fxml file and it represents the end of game pop-up window.
 * @author Xheneta, Pascal
 */

public class EndOfGameController {

    @FXML
    private AnchorPane endRootPane;

    @FXML
    private AnchorPane backgroundPane;

    @FXML
    private AnchorPane displayPane;

    @FXML
    private Label infoField; //label provides the change of the text based on the context

    @FXML
    private Button doneButton;

    @FXML
    private Label winnerName;

    @FXML
    private Label secondPlayer;

    @FXML
    private Label thirdPlayer;

    @FXML
    private Label fourthPlayer;

    @FXML
    private Label yourScore;

    @FXML
    private Label secondScore;

    @FXML
    private Label thirdScore;

    @FXML
    private Label fourthScore;


    /**
     * Method to be called from WindowLauncher to write the needed info based on the context.
     *
     * @param client
     */
    public void init(Client client, String winner) {
        secondPlayer.textProperty().bindBidirectional(client.getPlayers(1));
        thirdPlayer.textProperty().bindBidirectional(client.getPlayers(2));
        fourthPlayer.textProperty().bindBidirectional(client.getPlayers(3));
        yourScore.textProperty().bindBidirectional(client.getTOKENS(0), new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        secondScore.textProperty().bindBidirectional(client.getTOKENS(1), new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        thirdScore.textProperty().bindBidirectional(client.getTOKENS(2), new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Integer fromString(String s) {
                return null;
            }
        });
        fourthScore.textProperty().bindBidirectional(client.getTOKENS(3), new StringConverter<Number>() {
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

//        yourScore.textProperty().bindBidirectional(client.getTOKENS(0), new StringConverter<Number>() {
//            @Override
//            public String toString(Number number) {
//                return number.toString();
//            }
//
//            @Override
//            public Integer fromString(String s) {
//                return null;
//            }
//        });
//        secondScore.textProperty().bindBidirectional(client.getTOKENS(1), new StringConverter<Number>() {
//            @Override
//            public String toString(Number number) {
//                return number.toString();
//            }
//
//            @Override
//            public Integer fromString(String s) {
//                return null;
//            }
//        });
//        thirdScore.textProperty().bindBidirectional(client.getTOKENS(2), new StringConverter<Number>() {
//            @Override
//            public String toString(Number number) {
//                return number.toString();
//            }
//
//            @Override
//            public Integer fromString(String s) {
//                return null;
//            }
//        });
//        fourthScore.textProperty().bindBidirectional(client.getTOKENS(3), new StringConverter<Number>() {
//            @Override
//            public String toString(Number number) {
//                return number.toString();
//            }
//
//            @Override
//            public Integer fromString(String s) {
//                return null;
//            }
//        });
    }
}


