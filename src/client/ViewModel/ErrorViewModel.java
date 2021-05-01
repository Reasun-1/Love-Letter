package client.ViewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

public class ErrorViewModel {

    private StringProperty errorMessage;

    public ErrorViewModel(String msg){
        errorMessage = new SimpleStringProperty(msg);
    }

    public StringProperty getErrorMessage(){
        return errorMessage;
    }

}
