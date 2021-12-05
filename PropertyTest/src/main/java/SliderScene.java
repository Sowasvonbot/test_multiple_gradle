import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SliderScene {

    private Scene scene;
    private GridPane gridPane;

    private ObservableList<PlayerBox> playerBoxes = FXCollections.observableArrayList();
    private static IntegerProperty size = new SimpleIntegerProperty(50);
    private static Random random = new Random();


    public SliderScene() {
        gridPane = new GridPane();
        IntegerProperty randomInt = new SimpleIntegerProperty();

        List<ObjectProperty<Paint>> randomPaintList = new ArrayList<>();

        randomInt.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < 0)
                return;
            randomPaintList.get(newValue.intValue() % randomPaintList.size()).setValue(
                    Color.rgb((oldValue.intValue() + newValue.intValue()) % 256,
                            255 - newValue.intValue() % 256,
                            newValue.intValue() * newValue.intValue() % 256));
        });


        Button button = new Button("Create new Scene");
        button.setOnAction(event -> {
            Stage newStage = new Stage();
            newStage.setScene(getSliderScene());
            newStage.show();
        });
        gridPane.add(button, 0, 0);
        int maxsize = 200;

        TextField integerInput = new TextField();
        integerInput.setText("50");
        integerInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[0-9]+[0-9]*$"))
                integerInput.setText(oldValue);
            size.set(Math.min(Integer.parseInt(integerInput.getText()), maxsize));

        });
        size.addListener((observable, oldValue, newValue) -> {
            integerInput.setText(Integer.toString(newValue.intValue()));
            randomInt.set(random.nextInt(randomPaintList.size() + 1) - 1);
        });

        new AnimationTimer() {
            @Override public void handle(long now) {
                for (int i = 0; i < random.nextInt(20)*20; i++) {
                    randomInt.set(random.nextInt(randomPaintList.size() + 1) - 1);
                }
            }
        }.start();

        Slider slider = new Slider(0, maxsize, 50);
        slider.valueProperty().bindBidirectional(size);
        gridPane.setOnScroll(event -> {
            int scrollSpeed = 1;
            if (event.isControlDown())
                scrollSpeed = 10;

            if (event.getDeltaY() == 0)
                return;
            size.set(size.getValue() + (event.getDeltaY() < 0 ? -1 : 1) * scrollSpeed);
            event.consume();
        });
        gridPane.add(slider, 0, 2);
        gridPane.add(integerInput, 0, 1);



        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                ObjectProperty<Paint> randomPaint = new SimpleObjectProperty(Color.BLACK);
                randomPaintList.add(randomPaint);
                Rectangle rectangle = new Rectangle();
                rectangle.heightProperty().bind(size);
                rectangle.widthProperty().bind(size);
                rectangle.fillProperty().bind(randomPaint);

                gridPane.add(rectangle, i + 3, j + 3);
            }
        }

        HBox playerBoxesHBox = new HBox();
        gridPane.add(playerBoxesHBox, 1, 1);
        playerBoxesHBox.setSpacing(10);
        gridPane.setPrefSize(400, 225);
        scene = new Scene(gridPane);

        this.playerBoxes.addListener((ListChangeListener<PlayerBox>) change -> {
            while (change.next()) {
                if (change.wasAdded())
                    playerBoxesHBox.getChildren().addAll(change.getAddedSubList().stream()
                            .map(PlayerBox::getGraphicElement).toList());
                if (change.wasRemoved())
                    playerBoxesHBox.getChildren().removeAll(
                            change.getRemoved().stream().map(PlayerBox::getGraphicElement)
                                    .toList());
            }
        });

        this.playerBoxes.addAll(LobbyProperties.allPlayers.values().stream()
                .map(player -> new PlayerBox(player.ID(), player.name(), player.color())).toList());

        LobbyProperties.allPlayers.addListener(
                (MapChangeListener<Integer, LobbyProperties.Player>) change -> {
                    if (change.wasRemoved())
                        this.playerBoxes.removeIf(playerBox -> playerBox.ID == change.getKey());

                    if (change.wasAdded())
                        this.playerBoxes.add(
                                new PlayerBox(change.getKey(), change.getValueAdded().name(),
                                        change.getValueAdded().color()));
                });



    }

    public Scene getScene() {
        return scene;
    }

    class PlayerBox {
        private int ID;
        private String name;
        private StringProperty color;

        private VBox graphicElement;

        public PlayerBox(int ID, String name, StringProperty color) {
            this.ID = ID;
            this.name = name;

            Text colorText = new Text();
            colorText.textProperty().bind(color);

            graphicElement = new VBox();
            graphicElement.setPadding(new Insets(0, 0, 10, 0));
            graphicElement.getChildren().addAll(new Text(name), colorText);

        }

        public void setColor(String color) {
            this.color.set(color);
        }

        public int getID() {
            return ID;
        }

        public Node getGraphicElement() {
            return graphicElement;
        }
    }

    public static Scene getSliderScene() {
        return new SliderScene().getScene();
    }
}
