import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {
        ServerSocket mainServerSocket = new ServerSocket(50000);

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                switch (scanner.next()) {
                    case "stop", "quit" -> System.exit(0);
                }
            }
        }).start();

        while (true)
            new SocketThread(mainServerSocket.accept());

    }
}
