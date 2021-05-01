package client.ViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;


public class logInViewModel {

    private StringProperty username;

    private PrintWriter out;

    public logInViewModel(PipedInputStream instream) throws IOException {
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

    /*@Override
    public String toString() {
        return "LogInView{" +
                "The next client is: =" + username +
                ", client=" + client +
                '}';
    }*/

}
