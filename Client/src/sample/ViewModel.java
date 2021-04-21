package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ViewModel {

    private InputStream stream = System.in;

    //This will be the Viewmodel class for the client
    //Needs implementation of:
    // - errorMessage(String msg): Print the error message in a Pop-Up Window
    // - welcomeMessage(String name): Open the chat window and print "Welcome " + name
    // - getName(): Ask the user for a name
    // - receiveMessage(String msg): Print the received message in the chat window
    // - getInputStream(): Hand over the InputStream (must be a variable, messages which should be sent can be handed over into this stream)

    public void errorMessage(String msg){
        System.out.println(msg);
    }

    public void welcomeMessage(String name){
        System.out.println("Welcome " + name);
    }

    public String getName(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String name = null;
        try {
            name = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }


    public void receiveMessage(String msg){

    }

    public InputStream getInputStream(){
        return System.in;
    }
}
