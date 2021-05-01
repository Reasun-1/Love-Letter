package client.ViewModel;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

public class ChatRoomViewModel {
    private PrintWriter out;
    private TextArea chatInput;
    private StringProperty messageOutput;

    public ChatRoomViewModel(PipedInputStream instream) throws IOException {
        out = new PrintWriter(new PipedOutputStream(instream), true);
        chatInput = new TextArea();
        messageOutput = new SimpleStringProperty();
    }

    public StringProperty getChatInput(){
        return chatInput.textProperty();
    }

    public StringProperty getMessageOutput(){
        return messageOutput;
    }

    public void updateChat(String msg){
        chatInput.appendText(msg);
    }

    public void sendMessage(){
        out.println(messageOutput.get());
    }

}
