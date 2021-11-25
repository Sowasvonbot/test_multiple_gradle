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
import java.util.Set;

import static java.util.Objects.isNull;

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

        Button connect = new Button("Connect");

        TextField ipInput = new TextField("localhost");
        pane.setTop(new HBox(ipInput,connect));

        connect.setOnAction(event->{
            Settings.getInstance().setSetting("server_ip", ipInput.getText());
            ipInput.clear();
            new Thread(() -> {
                try {
                    BufferedReader reader = SocketConnection.getInstance().getReader();
                    connect.setDisable(true);
                    while (SocketConnection.isAlive()) {
                        try {
                            String textString = reader.readLine();
                            if(isNull(textString)) {
                                SocketConnection.disconnect();
                                Platform.runLater(() -> textFlow.getChildren().add(new Text("Error connecting")));
                            }
                            else {
                                Text text = new Text(textString + "\n");
                                Platform.runLater(() -> textFlow.getChildren().add(text));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> textFlow.getChildren().add(new Text("Error connecting")));
                        }
                    }
                } catch (RuntimeException e){
                    Platform.runLater(() -> textFlow.getChildren().add(new Text("Error connecting")));
                }
                connect.setDisable(false);
            }).start();
        });




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
            sendMessage(textField);
        });

        textField.setOnAction(event -> {
            sendMessage(textField);
        });

        pane.setBottom(new HBox(textField, changeTextButton));
        primaryStage.setScene(testScene);
        primaryStage.show();
    }

    private void sendMessage(TextField textField){
        try{
            SocketConnection.getInstance().sendMessage(textField.getText());
            textField.clear();
        } catch (RuntimeException e){
            textFlow.getChildren().add(new Text(e.getMessage()));
        }
    }

    @Override public void stop() throws Exception {
        System.exit(0);
    }
}
