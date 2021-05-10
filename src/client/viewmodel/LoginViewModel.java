package client.viewmodel;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * logInVieModel Class represents the UI state and it should provide the properties included in the FXML
 * file of the LogIn Window, in order to bind with the LoginController
 *
 * @author Rajna Fani
 * @version 1.0-SNAPSHOT
 */
public class LoginViewModel {

    private final static StringProperty USERNAME = new SimpleStringProperty();

    // ReadOnlyBooleanWrapper logInIndeed checks if the input on the "username" textField is a word or empty
    private final static ReadOnlyBooleanWrapper LOGININDEED = new ReadOnlyBooleanWrapper();
    // representing the current input of the text field "username"

    /**
     * Constructor establishes the connection between the client's name and the LogIn Window
     */
    public LoginViewModel() {
        //create the connection if the username textField is not empty
        LOGININDEED.bind(USERNAME.isNotEmpty());
    }


    //getter Method to get the written name
    public String getUserName() {

        return USERNAME.get();
    }

    //string property helps to create the textField input
    public static StringProperty heroNameProperty() {
        return USERNAME;
    }

    //represents if the login is possible based on the condition that the username is not empty
    public static ReadOnlyBooleanProperty loginPossibleProperty() {
        return LOGININDEED.getReadOnlyProperty();
    }


}
