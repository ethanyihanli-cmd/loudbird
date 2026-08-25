package com.macondo.loudbird;

import com.macondo.loudbird.controller.GameController;
import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private GameController controller;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(400, 600);
        GameModel model = new GameModel();
        GameView view = new GameView(canvas);

        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 400, 600);

        controller = new GameController(model, view, scene);

        primaryStage.setTitle("Loud Bird");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Closing mic...");
            controller.stopAudio();
        });

        primaryStage.show();
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
