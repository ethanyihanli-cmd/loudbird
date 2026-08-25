package com.macondo.loudbird.view;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.model.Pipe;
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

        for (Pipe pipe : model.getPipes()) {
            drawPipe(gc, pipe);
        }

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

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(24));
        gc.fillText("Score: " + model.getScore(), 10, 30);

        if (model.isGameOver()) {
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(36));
            gc.fillText("GAME OVER", canvas.getWidth()/2 - 100, canvas.getHeight()/2 - 20);
            gc.setFont(javafx.scene.text.Font.font(18));
            gc.fillText("Press SPACE to restart", canvas.getWidth()/2 - 85, canvas.getHeight()/2 + 30);
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
