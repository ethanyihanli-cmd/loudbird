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
        int size = 30;

        gc.setFill(Color.rgb(255, 200, 0));
        gc.fillOval(x - size/2, y - size/2, size, size);

        gc.setFill(Color.WHITE);
        gc.fillOval(x + 2, y - 8, 8, 8);
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5, y - 6, 4, 4);

        gc.setFill(Color.rgb(255, 140, 0));
        gc.fillPolygon(
                new double[]{x + size/2, x + size/2 + 8, x + size/2},
                new double[]{y - 2, y + 2, y + 6},
                3
        );

        System.out.println("Bird drawn at Y: " + y);
    }
}
