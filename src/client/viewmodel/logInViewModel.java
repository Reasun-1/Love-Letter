package client.viewmodel;

import client.controller.Client;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class logInViewModel {

    private static StringProperty userName = new SimpleStringProperty();

    // ReadOnlyBooleanWrapper logInIndeed checks if the input on the "username" textField is a word or empty
    private static ReadOnlyBooleanWrapper logInIndeed = new ReadOnlyBooleanWrapper();
    // representing the current input of the text field "username"

    private Client client;

    public logInViewModel(){
        this.client = client;
        //create the connection if the username textField is not empty
        logInIndeed.bind(userName.isNotEmpty());
    }



    public void nameEntered() {
        client.checkName(userName.get());
    }

    public String getUserName() {

        return userName.get();
    }

    public static StringProperty heroNameProperty() {
        return userName;
    }

    //represents if the login is possible based on the condition that the username is not empty
    public static ReadOnlyBooleanProperty loginPossibleProperty(){
        return logInIndeed.getReadOnlyProperty();
    }

    /*@Override
    public String toString() {
        return "LogInView{" +
                "The next client is: =" + username +
                ", client=" + client +
                '}';
    }*/

}
