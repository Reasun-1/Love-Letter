package client.ViewModel;

import client.Controller.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;


public class logInViewModel {

    private StringProperty username;

    private Client client;

    public logInViewModel(Client client){
        this.client = client;
        username = new SimpleStringProperty();
    }

    public void nameEntered() {
        client.checkName(username.get());
    }

    public String getUserName() {

        return username.get();
    }

    public StringProperty heroNameProperty() {
        return username;
    }

    /*@Override
    public String toString() {
        return "LogInView{" +
                "The next client is: =" + username +
                ", client=" + client +
                '}';
    }*/

}
