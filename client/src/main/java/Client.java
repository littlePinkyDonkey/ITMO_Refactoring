import service.EndpointService;

import java.util.Scanner;

public class Client {
    private EndpointService endpointService;

    public Client(final String uri) {
        this.endpointService = new EndpointService(uri);
    }

    public void launchClient() {

        Scanner scanner = new Scanner(System.in);
        String line;
        System.out.println(endpointService.execute("help"));

        System.out.print("login: ");
        String login = scanner.nextLine();
        System.out.print("password: ");
        String password = scanner.nextLine();
        System.out.println(endpointService.auth(login, password));
        do {
            System.out.print("Enter command: ");
            line = scanner.nextLine();
            System.out.println(endpointService.execute(line));
        } while (!line.equals("exit"));
        endpointService.exit();
    }

}
