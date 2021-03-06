import utility.Tuple;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

public class SocketHolder {

    private static SocketHolder instance;

    private Map<String, Socket> socketMap;
    private Map<Socket, Tuple<BufferedReader, BufferedWriter>> readerWriterMap;

    private SocketHolder() {
        socketMap = new HashMap<>();
        readerWriterMap = new HashMap<>();

    }

    public void remove(Socket socket) {
        Tuple<BufferedReader, BufferedWriter> temp = readerWriterMap.remove(socket);
        try {
            temp.getX().close();
            temp.getY().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketMap.remove(socket.getInetAddress().toString());
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Removed socket with address %s", socket.getInetAddress());

    }

    private Tuple<BufferedReader, BufferedWriter> registerSocketIntern(Socket socket) {
        System.out.println(socket.getInetAddress().toString());
        if (readerWriterMap.containsKey(socket))
            return readerWriterMap.get(socket);

        BufferedReader reader;
        BufferedWriter writer;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
        socketMap.put(socket.getInetAddress().toString(), socket);
        readerWriterMap.put(socket, new Tuple<>(reader, writer));
        return readerWriterMap.get(socket);
    }

    public static void broadcast(String message) {
        getInstance().socketMap.keySet().forEach(name -> sendMessageToUser(name, message));
    }

    public static void sendMessageToUser(Set<String> user, String message) {
        for (String name : user) {
            getInstance().sendMessageUser(name, message);
        }
    }

    public static void sendMessageToUser(String user, String message) {
        getInstance().sendMessageUser(user, message);
    }

    private void sendMessageUser(String user, String message) {
        if (!socketMap.containsKey(user))
            throw new IllegalArgumentException("User: " + user + " unknown");
        BufferedWriter writer = this.readerWriterMap.get(socketMap.get(user)).getY();
        try {
            writer.write(message+"\n");
            writer.flush();
        } catch (IOException e) {
            remove(socketMap.get(user));
            throw new RuntimeException("User " + user + " has lost connection");
        }
    }



    public static SocketHolder getInstance() {
        if (isNull(instance))
            instance = new SocketHolder();
        return instance;
    }

    public static Tuple<BufferedReader, BufferedWriter> registerSocket(Socket socket) {
        return getInstance().registerSocketIntern(socket);
    }


}
