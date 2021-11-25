import utility.Tuple;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
        Tuple<BufferedReader,BufferedWriter> temp = readerWriterMap.remove(socket);
        try {
            temp.getX().close();
            temp.getY().close();
        } catch (IOException e){
            e.printStackTrace();
        }
        socketMap.remove(socket.getInetAddress().toString());
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Removed socket with address %s",socket.getInetAddress());

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
        getInstance().readerWriterMap.values().forEach((tuple) -> {
            try {
                tuple.getY().write(message + "\n");
                tuple.getY().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
