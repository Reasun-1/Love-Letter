package client.viewmodel;

import client.controller.Client;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

