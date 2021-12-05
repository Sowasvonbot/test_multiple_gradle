import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {

        new Thread(Main::simulateJSON).start();
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(SliderScene.getSliderScene());
        primaryStage.show();
    }


    private static void simulateJSON() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String[] line = scanner.nextLine().split(" ");

            switch (line[0]) {
                case "register" -> addPlayer(line);
                case "selectColor" -> selectColor(line);
                case "stop" -> System.exit(0);
                case "remove" -> remove(line);
            }
        }
    }

    private static void remove(String... args) {
        if (args.length != 2)
            return;
        try {
            int id = Integer.parseInt(args[1]);
            Platform.runLater(() -> LobbyProperties.allPlayers.remove(id));
        } catch (NumberFormatException e) {
            System.err.println("Unknown ID");
        }
    }

    private static void selectColor(String... args) {
        if (args.length != 3)
            return;
        try {
            int id = Integer.parseInt(args[1]);
            LobbyProperties.changeColor(id, args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Unknown ID");
        }
    }

    private static void addPlayer(String... args) {
        if (args.length != 3)
            return;
        try {
            int id = Integer.parseInt(args[1]);
            LobbyProperties.addPlayer(id, args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Unknown ID");
        }
    }

    @Override public void stop() throws Exception {
        System.exit(0);
    }
}
