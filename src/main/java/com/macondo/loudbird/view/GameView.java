package com.macondo.loudbird.view;

import com.macondo.loudbird.model.GameModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameView {
    private Canvas canvas;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(135, 206, 235));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int x = model.getBirdX();
        int y = model.getBirdY();
        gc.setFill(Color.rgb(255, 200, 0));
        gc.fillOval(x - 15, y - 15, 30, 30);

        System.out.println("Bird drawn at Y: " + y);
    }
}
