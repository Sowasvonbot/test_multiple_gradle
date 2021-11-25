package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Main extends Application {

    TextFlow textFlow;

    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setMinHeight(600);
        primaryStage.setResizable(false);
        BorderPane pane = new BorderPane();
        pane.setMinHeight(600);

        pane.setPadding(new Insets(10, 10, 10, 10));
        Scene testScene = new Scene(pane);


        textFlow = new TextFlow();

        new Thread(() -> {
            BufferedReader reader = SocketConnection.getInstance().getReader();
            while (true) {
                try {
                    Text text = new Text(reader.readLine() + "\n");
                    Platform.runLater(() -> textFlow.getChildren().add(text));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        textFlow.prefHeight(100);
        ScrollPane pain = new ScrollPane(textFlow);
        pain.maxHeight(100);
        pain.vvalueProperty().bind(textFlow.heightProperty());

        pain.setBackground(new Background(new BackgroundFill(null, null, null)));
        pane.setCenter(pain);
        textFlow.setPrefWidth(300);
        pane.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));


        pane.prefHeight(100);
        pane.prefHeightProperty().bind(textFlow.heightProperty());


        TextField textField = new TextField();
        textField.setPrefWidth(300);


        Button changeTextButton = new Button("Change text");
        changeTextButton.setOnAction((event) -> {
            sendMessage(textField.getText());
            textField.clear();
        });

        textField.setOnAction(event -> {
            sendMessage(textField.getText());
            textField.clear();
        });

        pane.setBottom(new HBox(textField, changeTextButton));
        primaryStage.setScene(testScene);
        primaryStage.show();
    }

    private void sendMessage(String text) {
        BufferedWriter writer = SocketConnection.getInstance().getWriter();
        try {
            writer.write(text+"\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override public void stop() throws Exception {
        System.exit(0);
    }
}
