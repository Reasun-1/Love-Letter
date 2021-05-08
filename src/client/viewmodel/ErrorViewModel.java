package client.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ErrorViewModel Class represents the UI state and it should provide the properties included in the FXML
 * file of the Error Window, in order to bind with the ErrorController
 * @author Rajna Fani
 * @version 1.0-SNAPSHOT
 */


public class ErrorViewModel {

    private StringProperty errorMessage;

    public ErrorViewModel(String msg){

        errorMessage = new SimpleStringProperty(msg); //
    }

    public StringProperty getErrorMessage(){

        return errorMessage;
    }
}
