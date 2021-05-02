package client.ViewModel;

import client.Controller.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

public class QuestionViewModel {

    private StringProperty questionMessage;
    private StringProperty answer;
    private Client client;

    public QuestionViewModel(Client client, String msg){
        this.client = client;
        questionMessage = new SimpleStringProperty(msg);
        answer = new SimpleStringProperty();
    }

    public void answerEntered(){
        client.sendMessage(answer.get());
    }

    public StringProperty getQuestionMessage(){
        return questionMessage;
    }

    public StringProperty getAnswer(){return answer;};
}

