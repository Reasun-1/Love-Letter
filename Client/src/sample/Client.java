package sample;

//test code
public class Client {
    public int num;
    public String name;
    public Client(int num, String name){
        this.num = num;
        this.name = name;
    }
    public void printName(){
        System.out.println(this.name);
    }

    public static void main(String[] args) {
        Client cl = new Client(1, "ee");
        cl.printName();
    }
}
