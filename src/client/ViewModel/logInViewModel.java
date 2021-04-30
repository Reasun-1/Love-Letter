package client.ViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import client.Controller.Client;


public class logInViewModel {

    private StringProperty username;

    private Client client;

    public logInViewModel(Client client){
        this.client = client;
        username = new SimpleStringProperty();
    }

    public String getUserName() {

        return username.get();
    }

    public StringProperty heroNameProperty() {
        return username;
    }

    @Override
    public String toString() {
        return "LogInView{" +
                "The next client is: =" + username +
                ", client=" + client +
                '}';
    }

}
