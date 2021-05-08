package client.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorViewModel {

    private StringProperty errorMessage;

    public ErrorViewModel(String msg){

        errorMessage = new SimpleStringProperty(msg); //
    }

    public StringProperty getErrorMessage(){

        return errorMessage;
    }
}
