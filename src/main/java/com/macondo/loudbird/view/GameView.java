package com.macondo.loudbird.view;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.model.Pipe;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameView {
    private Canvas canvas;
    private int flashCounter = 0;
    private boolean flashRed = false;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(135, 206, 235));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Pipe pipe : model.getPipes()) {
            drawPipe(gc, pipe);
        }

        int x = model.getBirdX();
        int y = model.getBirdY();
        int size = model.getBirdSize();

        if (model.isGameOver()) {
            flashCounter++;
            if (flashCounter % 10 < 5) {
                flashRed = true;
            } else {
                flashRed = false;
            }
        } else {
            flashRed = false;
            flashCounter = 0;
        }

        Color bodyColor = flashRed ? Color.RED : Color.rgb(255, 200, 0);
        gc.setFill(bodyColor);
        gc.fillOval(x - size/2, y - size/2, size, size);

        if (!flashRed) {
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
        }

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(24));
        gc.fillText("Score: " + model.getScore(), 10, 30);

        if (model.isGameOver()) {
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(48));
            gc.fillText("GAME OVER", canvas.getWidth()/2 - 120, canvas.getHeight()/2 - 30);

            gc.setFont(javafx.scene.text.Font.font(20));
            gc.fillText("Final Score: " + model.getScore(), canvas.getWidth()/2 - 60, canvas.getHeight()/2 + 30);

            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("Press SPACE to restart", canvas.getWidth()/2 - 80, canvas.getHeight()/2 + 70);
        }
    }

    private void drawPipe(GraphicsContext gc, Pipe pipe) {
        int x = pipe.getX();
        int width = pipe.getWidth();
        int gapY = pipe.getGapY();
        int gapSize = pipe.getGapSize();

        int topHeight = gapY - gapSize/2;
        gc.setFill(Color.rgb(0, 200, 50));
        gc.fillRect(x, 0, width, topHeight);

        gc.setFill(Color.rgb(0, 180, 40));
        gc.fillRect(x - 5, topHeight - 20, width + 10, 20);

        int bottomY = gapY + gapSize/2;
        int bottomHeight = (int)canvas.getHeight() - bottomY;
        gc.setFill(Color.rgb(0, 200, 50));
        gc.fillRect(x, bottomY, width, bottomHeight);

        gc.setFill(Color.rgb(0, 180, 40));
        gc.fillRect(x - 5, bottomY, width + 10, 20);

        gc.setStroke(Color.rgb(0, 150, 30));
        gc.setLineWidth(2);
        gc.strokeRect(x, 0, width, topHeight);
        gc.strokeRect(x, bottomY, width, bottomHeight);
    }
}
