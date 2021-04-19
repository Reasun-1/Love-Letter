import sample.Client;

//test code
public class Server {
    public static void main(String[] args) {
        System.out.println("i server");
        Client cl = new Client(1, "client1");
        cl.printName();


    }

}
