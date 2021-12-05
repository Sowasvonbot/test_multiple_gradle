import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public abstract class LobbyProperties {


    public static final ObservableMap<Integer, Player> allPlayers =
            FXCollections.observableHashMap();



    public static void addPlayer(int id, String name) {
        Platform.runLater(
                () -> allPlayers.put(id, new Player(id, name, new SimpleStringProperty(""))));
    }

    public static void changeColor(int id, String color) {
        Platform.runLater(() -> allPlayers.get(id).color.set(color));
    }


    public record Player(int ID, String name, StringProperty color) {
    }



}
