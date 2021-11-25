package client;

import java.io.*;
import java.net.Socket;

import static java.util.Objects.*;

public class SocketConnection {

    private static SocketConnection instance;

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private SocketConnection(){
        try {
            socket = new Socket(Settings.getInstance().getSetting("server_ip"),50000);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public static SocketConnection getInstance() {
        if (isNull(instance))
            instance = new SocketConnection();
        return instance;
    }
}
