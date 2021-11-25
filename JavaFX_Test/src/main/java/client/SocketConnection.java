package client;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

import static java.util.Objects.*;

public class SocketConnection {

    private static SocketConnection instance;

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private SocketConnection() throws RuntimeException{
        try {
            socket = new Socket(Settings.getInstance().getSetting("server_ip"), 50000);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e){
            throw new RuntimeException("Error in connection");
        }
    }

    public void sendJsonObject(JSONObject jsonObject) {
        sendMessage(jsonObject.toString());
    }

    public void sendMessage(String text) {
        try {
            writer.write(text + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public BufferedReader getReader() {
        return reader;
    }

    public static SocketConnection getInstance() {
        if (isNull(instance))
            instance = new SocketConnection();
        return instance;

    }

    public static void disconnect(){
        try {
            getInstance().socket.close();
            instance = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAlive(){
        return !getInstance().socket.isClosed();
    }
}
