import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {
        ServerSocket mainServerSocket = new ServerSocket(50000);

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String test = scanner.next();
                switch (test) {
                    case "stop", "quit" -> System.exit(0);
                    default -> System.err.printf("Unknown command: %s",test);
                }
            }
        }).start();

        while (true)
            new SocketThread(mainServerSocket.accept());

    }
}
