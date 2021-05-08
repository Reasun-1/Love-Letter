package client.viewmodel;

/**
 * logInVieModel Class represents the UI state and it should provide the properties included in the FXML
 * file of the LogIn Window, in order to bind with the LoginController
 * @author Rajna Fani
 * @version 1.0-SNAPSHOT
 */

import client.controller.Client;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;


public class logInViewModel {

    private static StringProperty userName = new SimpleStringProperty();

    // ReadOnlyBooleanWrapper logInIndeed checks if the input on the "username" textField is a word or empty
    private static ReadOnlyBooleanWrapper logInIndeed = new ReadOnlyBooleanWrapper();
    // representing the current input of the text field "username"

    private Client client;

    /**
     * Constructor establishes the connection between the client's name and the LogIn Window
     */
    public logInViewModel(){
        this.client = client;
        //create the connection if the username textField is not empty
        logInIndeed.bind(userName.isNotEmpty());
    }


    //checks if the name of the client is written
    public void nameEntered() {
        client.checkName(userName.get());
    }

    //getter Method to get the written name
    public String getUserName() {

        return userName.get();
    }

    //string property helps to create the textfield input
    public static StringProperty heroNameProperty() {
        return userName;
    }

    //represents if the login is possible based on the condition that the username is not empty
    public static ReadOnlyBooleanProperty loginPossibleProperty(){
        return logInIndeed.getReadOnlyProperty();
    }


}
