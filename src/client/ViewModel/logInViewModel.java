package client.ViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import client.Controller.Client;


public class logInViewModel {

    private StringProperty username;

    private PrintWriter out;

    public logInViewModel(PipedInputStream instream){
        out = new PrintWriter(new PipedOutputStream(instream), true);
        username = new SimpleStringProperty();

    }

    public void nameEntered(){
        out.println(username.get());
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
