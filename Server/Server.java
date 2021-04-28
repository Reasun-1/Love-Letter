import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class Server {

    private volatile static Server server;
    // a set (here is a vector type) for the accepted ServerThreads
    protected static Hashtable<Integer, String> playerList = new Hashtable<Integer, String>();
    // a set for checking clients´ names
    protected static Hashtable<String, ServerThread> clientList = new Hashtable<String, ServerThread>();

    private Server() {
    }

    //Singleton with DCL (double-checked locking)
    public static Server getServer() {
        if (server == null) {
            synchronized (Server.class) {
                if (server == null) {
                    server = new Server();
                }
            }
        }
        return server;
    }

    //public static List<ServerThread> getThreads() {
      //  return threads;
    //}

    public void start() throws IOException {
        // create the server and define the port nr.
        ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        // accept the client request
        while (flag) {
            try {
                // when new client comes, will be put into the thread-set
                // with synchronized, there is only one thread at one time
                Socket clientSocket = server.accept();
                synchronized (clientList) {
                    ServerThread thread = new ServerThread(clientSocket);
                    new Thread(thread).start();
                }
                // several server threads will respond to the client requests

                //catch the exception
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
            }
        }
        //close the server
        server.close();
    }

    public static void main(String[] args) {
        try{
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Game createGame(){
        //create a new game
        return null;
    }

    public void sendTo(String clientName){
        //send message to Client clientName
    }

    public void addPlayer(String clientName){
        //add Player to PlayerList
        playerList.put(playerList.size(), clientName);
    }

    public void startGame(){
        //initiate the Gameplay
    }

    public void playCard(String cardName){
        //play the card cardName
    }

    public String choosePlayer(){
        //ask the active player to choose another player
        return "";
    }

    public String chooseCard(){
        //ask the active player to choose a card
        return "";
    }
}