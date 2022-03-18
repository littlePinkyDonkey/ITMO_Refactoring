

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].isEmpty()){
            System.out.println("Specify host address with args");
        } else {
            Client client = new Client(args[0]);
            client.launchClient();
        }
    }
}
