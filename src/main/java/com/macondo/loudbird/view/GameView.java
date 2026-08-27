package com.macondo.loudbird.view;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.model.Pipe;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

        drawVolumeMeter(gc, model);

        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillRect(0, 0, canvas.getWidth(), 50);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(24));
        gc.fillText("Score: " + model.getScore(), 15, 35);

        gc.setFill(Color.rgb(255, 215, 0));
        gc.setFont(Font.font(18));
        gc.fillText("Best: " + model.getHighScore(), 140, 33);

        if (model.isShowScorePopup()) {
            int timer = model.getScorePopupTimer();
            int yOffset = 50 - timer;
            int alpha = (int)(255 * (timer / 30.0));

            gc.setFill(Color.rgb(255, 255, 100, alpha / 255.0));
            gc.setFont(Font.font(32));
            gc.fillText("+1", canvas.getWidth() - 80, 80 - yOffset);
        }

        if (model.isGameOver()) {
            gc.setFill(Color.rgb(0, 0, 0, 0.6));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(48));
            gc.fillText("GAME OVER", canvas.getWidth()/2 - 120, canvas.getHeight()/2 - 60);

            gc.setFont(Font.font(24));
            gc.fillText("Score: " + model.getScore(), canvas.getWidth()/2 - 50, canvas.getHeight()/2);

            if (model.getScore() >= model.getHighScore() && model.getScore() > 0) {
                gc.setFill(Color.rgb(255, 215, 0));
                gc.setFont(Font.font(22));
                gc.fillText("* NEW HIGH SCORE! *", canvas.getWidth()/2 - 100, canvas.getHeight()/2 + 45);
            } else {
                gc.setFill(Color.rgb(200, 200, 200));
                gc.setFont(Font.font(18));
                gc.fillText("High Score: " + model.getHighScore(), canvas.getWidth()/2 - 70, canvas.getHeight()/2 + 45);
            }

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(16));

            if (model.canRestart()) {
                gc.setFill(Color.rgb(100, 255, 100));
                gc.fillText("Press SPACE or YELL to restart!", canvas.getWidth()/2 - 110, canvas.getHeight()/2 + 90);
            } else {
                gc.setFill(Color.rgb(200, 200, 200));
                gc.fillText("Restarting in " + (model.getRestartCooldown() / 60 + 1) + "s...",
                        canvas.getWidth()/2 - 80, canvas.getHeight()/2 + 90);
            }
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.setFont(Font.font(11));
        gc.fillText("Sensitivity: " + String.format("%.2f", model.getCurrentLoudness()), 5, canvas.getHeight() - 5);
    }

    private void drawVolumeMeter(GraphicsContext gc, GameModel model) {
        float loudness = model.getCurrentLoudness();

        int meterX = (int)canvas.getWidth() - 30;
        int meterY = 60;
        int meterWidth = 20;
        int meterHeight = 200;
        int meterPadding = 5;

        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillRect(meterX - meterPadding, meterY - meterPadding,
                meterWidth + meterPadding * 2, meterHeight + meterPadding * 2);

        gc.setStroke(Color.rgb(255, 255, 255, 0.3));
        gc.setLineWidth(1);
        gc.strokeRect(meterX - meterPadding, meterY - meterPadding,
                meterWidth + meterPadding * 2, meterHeight + meterPadding * 2);

        int fillHeight = (int)(loudness * meterHeight);
        int fillY = meterY + meterHeight - fillHeight;

        Color fillColor;
        if (loudness < 0.3f) {
            fillColor = Color.rgb(0, 200, 0);
        } else if (loudness < 0.6f) {
            fillColor = Color.rgb(255, 200, 0);
        } else {
            fillColor = Color.rgb(255, 50, 50);
        }

        gc.setFill(fillColor);
        gc.fillRect(meterX, fillY, meterWidth, fillHeight);

        gc.setFill(Color.rgb(255, 255, 255, 0.1));
        gc.fillRect(meterX, meterY, meterWidth, meterHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(12));
        gc.fillText("VOLUME", meterX - 10, meterY - 10);

        gc.setFont(Font.font(14));
        gc.setFill(Color.rgb(200, 200, 200));
        String percent = String.format("%.of%%", loudness * 100);
        gc.fillText(percent, meterX - 5, meterY + meterHeight + 20);

        gc.setStroke(Color.rgb(255, 255, 255, 0.2));
        gc.setLineWidth(1);
        for (int i = 0; i <= 4; i++) {
            int yPos = meterY + (i * meterHeight / 4);
            gc.strokeLine(meterX - 3, yPos, meterX, yPos);
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
