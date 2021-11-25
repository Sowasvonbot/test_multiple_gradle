import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class SocketThread {


    private Thread myThread;


    public SocketThread(Socket socket) {
        myThread = new Thread(() -> {
            BufferedReader myReader = SocketHolder.registerSocket(socket).getX();
            try {
                while (!socket.isClosed()) {

                    System.out.println("Start to listen");
                    String line = myReader.readLine();
                    if (line == null)
                        socket.close();
                    System.out.println(line);
                    SocketHolder.broadcast(socket.getInetAddress().toString() + ": " + line);
                }
                SocketHolder.getInstance().remove(socket);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                SocketHolder.getInstance().remove(socket);
            }

        });
        myThread.start();



    }
}
