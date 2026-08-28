package com.macondo.loudbird.view;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.model.Pipe;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView {
    private Canvas canvas;
    private int flashCounter = 0;
    private boolean flashRed = false;

    private List<Cloud> clouds;
    private Random random;
    private int cloudTimer = 0;

    private Image peterGif;
    private boolean gifLoaded = false;

    private String[] lyrics = {
            "Surfin' bird!",
            "A-well-a, everybody's heard about the bird",
            "Bird, bird, bird, b-bird's the word",
            "A-well-a, bird, bird, bird, the bird is the word",
            "A-well-a, bird, bird, bird, well, the bird is the word",
            "A-well-a, bird, bird, b-bird's the word",
            "A-well-a, bird, brid, brid, b-bird's the word",
            "A-well-a, bird, bird, bird, well, the bird is the word",
            "A- well-a, bird, bird, b-bird's the word",
            "A-well-a, don't you know about the bird?",
            "Well, everybody knows that the bird is the word!",
            "A-well-a, bird, bird, b-bird's the word",
            "A-well-a, everybody's heard about the bird",
            "Bird, bird, bird, b-bird's the word",
            "A-well-a, bird, bird, bird, b-bird's hte word",
            "A-well-a, bird, bird, bird, b-bird's the word",
            "A-well-a, bird, bird, b-bird's the word",
            "A-well-a. bird, bird, bird, b-bird's hte word",
            "A-well-a, bird, bird, bird, b-bird's the word",
            "A-well-a, bird, bird, bird, b-bird's the word",
            "A-well-a, bird, bird, bird, b-bird's the word",
            "A-well-a, don't you konw baout hte bird?",
            "Well, everybody's talking about the bird!",
            "A-well-a, bird, bird, b-bird's teh word",
            "A-well-a, bird (Surfin' bird)"
    };
    private int currentLyricIndex = 0;
    private int lyricTimer = 0;
    private int lyricsInterval = 120;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.random = new Random();
        this.clouds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clouds.add(new Cloud(
                    random.nextInt((int)canvas.getWidth()),
                    random.nextInt(200) + 20,
                    30 + random.nextInt(50),
                    15 + random.nextInt(20)
            ));
        }

        try {
            java.net.URL gifUrl = getClass().getResource("/peter-griffin.gif");
            if (gifUrl != null) {
                peterGif = new Image(gifUrl.toExternalForm());
                gifLoaded = true;
                System.out.println("√ Peter Griffin GIF loaded from file!");
            } else {
                peterGif = new Image("file:peter-griffin.gif");
                gifLoaded = true;
                System.out.println("√ Peter Griffin GIF loaded from file!");
            }
        } catch (Exception e) {
            System.out.println("❌ Could not load Peter Griffin GIF: " + e.getMessage());
            System.out.println("  Place peter-griffin.gif in the project root or resources folder");
            gifLoaded = false;
        }
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        LinearGradient skyGradient = new LinearGradient(
                0, 0, 0, canvas.getHeight(),
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(135, 206, 235)),
                new Stop(0.5, Color.rgb(100, 180, 220)),
                new Stop(1, Color.rgb(200, 220, 240))
        );
        gc.setFill(skyGradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        updateClouds();
        for (Cloud cloud : clouds) {
            drawCloud(gc, cloud);
        }

        drawGround(gc);

        for (Pipe pipe : model.getPipes()) {
            drawPipe(gc, pipe);
        }

        drawBird(gc, model);

        drawVolumeMeter(gc, model);

        drawScoreUI(gc, model);

        drawSingalongLyrics(gc);

        drawPeterGriffin(gc);

        if (model.isGameOver()) {
            drawGameOver(gc, model);
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.setFont(Font.font(10));
        String micStatus = "Mic: " + String.format("%.2f", model.getCurrentLoudness());
        gc.fillText(micStatus, 5, canvas.getHeight() - 5);

        double dotX = 70;
        double dotY = canvas.getHeight() - 8;
        if (model.getCurrentLoudness() > 0.05f) {
            gc.setFill(Color.rgb(0, 255, 0));
            gc.fillOval(dotX, dotY - 3, 6, 6);
        } else {
            gc.setFill(Color.rgb(100, 100, 100));
            gc.fillOval(dotX, dotY - 3, 6, 6);
        }
    }

    private void drawSingalongLyrics(GraphicsContext gc) {
        lyricTimer++;
        if (lyricTimer >= lyricsInterval) {
            lyricTimer = 0;
            currentLyricIndex = (currentLyricIndex + 1) % lyrics.length;
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillRoundRect(canvas.getWidth()/2 - 200, 55, 400, 45, 10, 10);

        gc.setStroke(Color.rgb(255, 255, 255, 0.15));
        gc.setLineWidth(1);
        gc.strokeRoundRect(canvas.getWidth()/2 - 200, 55, 400, 45, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.setFont(Font.font("Arial", 20));
        gc.fillText(lyrics[currentLyricIndex], canvas.getWidth()/2 + 1, 88);

        gc.setFill(Color.rgb(255, 255, 255, 0.3));
        gc.setFont(Font.font("Arial", 9));
        gc.fillText("🎵 Sing along 🎵", canvas.getWidth()/2, 70);

        int totalLyrics = lyrics.length;
        int visibleDots = 15;
        int startIndex = Math.min(0, currentLyricIndex - visibleDots/2);
        int endIndex = Math.min(totalLyrics, startIndex + visibleDots);

        double dotSpacing = (canvas.getWidth() - 40) / visibleDots;
        double dotY = 105;

        for (int i = startIndex; i < endIndex; i++) {
            double dotX = 20 + (i - startIndex) * dotSpacing + dotSpacing/2;
            if (i == currentLyricIndex) {
                gc.setFill(Color.rgb(255, 255, 100, 0.8));
                gc.fillOval(dotX - 3, dotY - 3, 6, 6);
            } else {
                gc.setFill(Color.rgb(255, 255, 255, 0.2));
                gc.fillOval(dotX - 2, dotY - 2, 4, 4);
            }
        }
    }

    private void drawPeterGriffin(GraphicsContext gc) {
        if (!gifLoaded || peterGif == null) {
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRoundRect(10, canvas.getHeight() - 110, 100, 100, 10, 10);
            gc.setFill(Color.rgb(255, 255, 255, 0.5));
            gc.setFont(Font.font("Arial", 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Peter Griffin", 60, canvas.getHeight() - 80);
            gc.fillText("Coming soon", 60, canvas.getHeight() - 60);
            return;
        }

        double gifSize = 120;
        double gifX = 15;
        double gifY = canvas.getHeight() - gifSize - 55;

        gc.setFill(Color.rgb(0, 0, 0, 0.2));
        gc.fillOval(gifX + 10, gifY + gifSize - 5, gifSize - 20, 15);

        gc.drawImage(peterGif, gifX, gifY, gifSize, gifSize);

        gc.setStroke(Color.rgb(255, 255, 255, 0.15));
        gc.setLineWidth(1);
        gc.strokeRect(gifX, gifY, gifSize, gifSize);

        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillRoundRect(gifX, gifY + gifSize - 20, gifSize, 20, 5, 5);
        gc.setFill(Color.rgb(255, 255, 255, 0.6));
        gc.setFont(Font.font("Arial", 9));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("🎵 Peter Griffin 🎵", gifX + gifSize/2, gifY + gifSize - 6);
    }

    private void updateClouds() {
        cloudTimer++;
        for (Cloud cloud : clouds) {
            cloud.x += 0.3;
            if (cloud.x > canvas.getWidth() + 50) {
                cloud.x = -cloud.width - 50;
                cloud.y = random.nextInt(200) + 20;
                cloud.width = 30 + random.nextInt(50);
                cloud.height = 15 + random.nextInt(20);
            }
        }
    }

    private void drawCloud(GraphicsContext gc, Cloud cloud) {
        gc.setFill(Color.rgb(255, 255, 255, 0.3));
        gc.fillOval(cloud.x, cloud.y, cloud.width, cloud.height);
        gc.fillOval(cloud.x + cloud.width * 0.3, cloud.y - cloud.height * 0.3,
                cloud.width * 0.6, cloud.height * 0.7);
        gc.fillOval(cloud.x - cloud.width * 0.2, cloud.y + cloud.height * 0.2,
                cloud.width * 0.5, cloud.height * 0.5);
    }

    private void drawGround(GraphicsContext gc) {
        int groundY = (int)canvas.getHeight() - 50;

        LinearGradient grassGrad = new LinearGradient(
                0, groundY, 0, canvas.getHeight(),
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(60, 180, 60)),
                new Stop(1, Color.rgb(40, 120, 40))
        );
        gc.setFill(grassGrad);
        gc.fillRect(0, groundY, canvas.getWidth(), 50);

        gc.setStroke(Color.rgb(50, 160, 50, 0.5));
        gc.setLineWidth(1);
        for (int i = 0; i < canvas.getWidth(); i += 3) {
            int height = 3 + random.nextInt(5);
            gc.strokeLine(i, groundY, i + 1, groundY - height);
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.1));
        gc.fillRect(0, groundY, canvas.getWidth(), 3);
    }

    private void drawBird(GraphicsContext gc, GameModel model) {
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

        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillOval(x - size / 2 + 3, y - size / 2 + 3, size, size);

        Color bodyColor = flashRed ? Color.RED : Color.rgb(255, 200, 0);
        LinearGradient birdGrad = new LinearGradient(
                x - size / 2, y - size / 2, x + size / 2, y + size / 2,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, flashRed ? Color.rgb(255, 100, 100) : Color.rgb(255, 220, 50)),
                new Stop(1, flashRed ? Color.rgb(200, 0, 0) : Color.rgb(200, 150, 0))
        );
        gc.setFill(birdGrad);
        gc.fillOval(x - size / 2, y - size / 2, size, size);

        if (!flashRed) {
            gc.setFill(Color.rgb(200, 160, 0, 0.5));
            gc.fillOval(x - 5, y - 2, 18, 12);
        }

        if (!flashRed) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x + 4, y - 8, 10, 10);
            gc.setFill(Color.BLACK);
            gc.fillOval(x + 7, y - 6, 5, 5);
            gc.setFill(Color.WHITE);
            gc.fillOval(x + 9, y - 8, 2, 2);
        }

        if (!flashRed) {
            gc.setFill(Color.rgb(255, 140, 0));
            gc.fillPolygon(
                    new double[]{x + size / 2 - 2, x + size / 2 + 10, x + size / 2 - 2},
                    new double[]{y - 4, y + 2, y + 8},
                    3
            );
            gc.setStroke(Color.rgb(200, 100, 0));
            gc.setLineWidth(1);
            gc.strokeLine(x + size / 2 - 2, y + 2, x + size / 2 + 8, y + 2);
        }
    }

    private void drawPipe(GraphicsContext gc, Pipe pipe) {
        int x = pipe.getX();
        int width = pipe.getWidth();
        int gapY = pipe.getGapY();
        int gapSize = pipe.getGapSize();

        LinearGradient pipeGrad = new LinearGradient(
                x, 0, x + width, 0,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 180, 40)),
                new Stop(0.3, Color.rgb(0, 220, 60)),
                new Stop(0.7, Color.rgb(0, 200, 50)),
                new Stop(1, Color.rgb(0, 150, 30))
        );

        int topHeight = gapY - gapSize/2;
        gc.setFill(pipeGrad);
        gc.fillRect(x, 0, width, topHeight);

        gc.setFill(Color.rgb(0, 160, 40));
        gc.fillRect(x - 5, topHeight - 20, width + 10, 20);
        gc.setFill(Color.rgb(0, 200, 50));
        gc.fillRect(x - 3, topHeight - 18, width + 6, 16);

        gc.setFill(Color.rgb(255, 255, 255, 0.1));
        gc.fillRect(x + 2, 0, 5, topHeight);
        gc.fillRect(x + 2, topHeight - 20, 5, 20);

        int bottomY = gapY + gapSize/2;
        int bottomHeight = (int)canvas.getHeight() - bottomY - 50;

        gc.setFill(pipeGrad);
        gc.fillRect(x, bottomY, width, bottomHeight);

        gc.setFill(Color.rgb(0, 160, 40));
        gc.fillRect(x - 5, bottomY, width + 10, 20);
        gc.setFill(Color.rgb(0, 200, 50));
        gc.fillRect(x - 3, bottomY + 2, width + 6, 16);

        gc.setFill(Color.rgb(255, 255, 255, 0.1));
        gc.fillRect(x + 2, bottomY, 5, bottomHeight);
        gc.fillRect(x + 2, bottomY, 5, 20);

        gc.setStroke(Color.rgb(0, 120, 20, 0.3));
        gc.setLineWidth(1);
        gc.strokeRect(x, 0, width, topHeight);
        gc.strokeRect(x, bottomY, width, bottomHeight);
    }

    private void drawVolumeMeter(GraphicsContext gc, GameModel model) {
                float loudness = model.getCurrentLoudness();

                int meterX = (int)canvas.getWidth() - 35;
                int meterY = 60;
                int meterWidth = 22;
                int meterHeight = 200;
                int meterPadding = 8;

                gc.setFill(Color.rgb(0, 0, 0, 0.5));
                gc.fillRoundRect(meterX - meterPadding, meterY - meterPadding,
                        meterWidth + meterPadding * 2, meterHeight + meterPadding * 2, 8, 8);

                gc.setStroke(Color.rgb(255, 255, 255, 0.1));
                gc.setLineWidth(1);
                gc.strokeRoundRect(meterX - meterPadding, meterY - meterPadding,
                        meterWidth + meterPadding * 2, meterHeight + meterPadding * 2, 8, 8);

                gc.setFill(Color.rgb(30, 30, 50, 0.8));
                gc.fillRect(meterX, meterY, meterWidth, meterHeight);

                int fillHeight = (int)(loudness * meterHeight);
                int fillY = meterY + meterHeight - fillHeight;

                Color fillColor;
                if (loudness < 0.3f) {
                    fillColor = Color.rgb(0, 220, 0);
                } else if (loudness < 0.6f) {
                    fillColor = Color.rgb(255, 220, 0);
                } else {
                    fillColor = Color.rgb(255, 50, 50);
                }

                LinearGradient fillGrad = new LinearGradient(
                        0, fillY, 0, meterY + meterHeight,
                        false, CycleMethod.NO_CYCLE,
                        new Stop(0, fillColor.brighter()),
                        new Stop(1, fillColor.darker())
                );

                gc.setFill(fillColor);
                gc.fillRect(meterX + 2, fillY, meterWidth - 4, fillHeight);

                gc.setFill(Color.rgb(255, 255, 255, 0.15));
                gc.fillRect(meterX + 2, meterY, meterWidth - 4, Math.min(10, fillHeight));

                gc.setFill(Color.WHITE);
                gc.setFont(Font.font(10));
                gc.fillText("VOL", meterX + 3, meterY - 12);

                gc.setFont(Font.font(13));
                gc.setFill(Color.rgb(220, 220, 220));
                String percent = String.format("%.0f%%", loudness * 100);
                gc.fillText(percent, meterX - 2, meterY + meterHeight + 25);

                gc.setStroke(Color.rgb(255, 255, 255, 0.15));
                gc.setLineWidth(1);
                for (int i = 0; i <= 4; i++) {
                    int yPos = meterY + (i * meterHeight / 4);
                    gc.strokeLine(meterX - 3, yPos, meterX, yPos);
                }
    }

    private void drawScoreUI(GraphicsContext gc, GameModel model) {
        gc.setFill(Color.rgb(0, 0, 0, 0.35));
        gc.fillRect(0, 0, canvas.getWidth(), 55);

        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillText("Score: " + model.getScore(), 16, 37);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 28));
        gc.fillText("Score: " + model.getScore(), 15, 35);

        gc.setFont(Font.font("Arial", 18));
        gc.setFill(Color.rgb(255, 215, 0));
        gc.fillText("* Best: " + model.getHighScore(), 160, 33);

        if (model.isShowScorePopup()) {
            int timer = model.getScorePopupTimer();
            int yOffset = 60 - timer * 2;
            int alpha = (int) (255 * (timer / 30.0));

            gc.setFill(Color.rgb(255, 255, 100, alpha / 255.0));
            gc.setFont(Font.font("Arial", 36));
            gc.fillText("+1", canvas.getWidth() - 80, 80 - yOffset);

            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                int dist = 20 + (30 - timer);
                int px = (int) (canvas.getWidth() - 60 + Math.cos(angle) * dist);
                int py = (int) (70 - yOffset + Math.sin(angle) * dist);
                gc.setFill(Color.rgb(255, 255, 100, (alpha / 255.0) * 0.5));
                gc.fillOval(px, py, 3, 3);
            }
        }
    }

    private void drawGameOver(GraphicsContext gc, GameModel model) {
        gc.setFill(Color.rgb(0, 0, 0, 0.65));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(Color.rgb(30, 30, 50, 0.8));
            gc.fillRoundRect(canvas.getWidth()/2 - 160, canvas.getHeight()/2 - 120, 320, 220, 15, 15);
            gc.setStroke(Color.rgb(255, 255, 255, 0.1));
            gc.setLineWidth(2);
            gc.strokeRoundRect(canvas.getWidth()/2 - 160, canvas.getHeight()/2 - 120, 320, 220, 15, 15);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", 42));
            gc.fillText("GAME OVER", canvas.getWidth()/2 - 130, canvas.getHeight()/2 - 70);

            gc.setFont(Font.font("Arial", 22));
            gc.setFill(Color.rgb(200, 200, 200));
            gc.fillText("Score: " + model.getScore(), canvas.getWidth()/2 - 50, canvas.getHeight()/2 - 20);

            if (model.getScore() >= model.getHighScore() && model.getScore() > 0) {
                gc.setFill(Color.rgb(255, 215, 0));
                gc.setFont(Font.font("Arial", 20));
                gc.fillText("* NEW HIGH SCORE! *", canvas.getWidth()/2 - 110, canvas.getHeight()/2 + 30);
            } else {
                gc.setFill(Color.rgb(180, 180, 180));
                gc.setFont(Font.font("Arial", 18));
                gc.fillText("Best: " + model.getHighScore(), canvas.getWidth()/2 - 60, canvas.getHeight()/2 + 30);
            }

            gc.setFont(Font.font("Arial", 16));
            if (model.canRestart()) {
                gc.setFill(Color.rgb(100, 255, 100));
                gc.fillText("Press SPACE or YELL to restart!", canvas.getWidth()/2 - 130, canvas.getHeight()/2 + 75);
            } else {
                gc.setFill(Color.rgb(180, 180, 180));
                gc.fillText("Restarting in " + (model.getRestartCooldown() / 60 + 1) + "s...",
                        canvas.getWidth()/2 - 90, canvas.getHeight()/2 + 75);
            }
        }

        private class Cloud {
            float x, y;
            float width, height;

            Cloud(float x, float y, float width, float height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }
        }
    }
