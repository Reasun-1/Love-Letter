package src.sample;

import java.io.InputStream;

public class ViewModel {

    private InputStream stream;

    //This will be the Viewmodel class for the client
    //Needs implementation of:
    // - errorMessage(String msg): Print the error message in a Pop-Up Window
    // - welcomeMessage(String name): Open the chat window and print "Welcome " + name
    // - getName(): Ask the user for a name
    // - receiveMessage(String msg): Print the received message in the chat window
    // - getInputStream(): Hand over the InputStream (must be a variable, messages which should be sent can be handed over into this stream)

    public void errorMessage(String msg){

    }

    public void welcomeMessage(String name){

    }

    public String getName(){
        return "Fluffige_Flugenten";
    }

    public void receiveMessage(String msg){

    }

    public InputStream getInputStream(){
        return stream;
    }
}
