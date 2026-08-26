package com.macondo.loudbird.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameModel {
    private int birdX;
    private int birdY;
    private int birdSize = 30;
    private int screenWidth = 400;
    private int screenHeight = 600;

    private float currentLoudness = 0.0f;
    private float sensitivity = 0.7f;

    private int topPadding = 30;
    private int bottomPadding = 30;

    private List<Pipe> pipes;
    private Random random;
    private int pipeSpawnTimer = 0;
    private int pipeSpawnInterval = 120;

    private int pipeSpeed = 3;
    private int pipeGapMin = 100;
    private int pipeGapMax = 500;

    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;

    private boolean hitCooldown = false;
    private int hitCooldownTimer = 0;

    private int scorePopupTimer = 0;
    private boolean showScorePopup = false;

    public GameModel() {
        birdX = 80;
        birdY = screenHeight / 2;
        pipes = new ArrayList<>();
        random = new Random();
        loadHighScore();
    }

    public int getBirdX() { return birdX; }
    public int getBirdY() { return birdY; }
    public int getBirdSize() { return birdSize; }

    public void updateBirdPosition() {
        float volumeClamped = Math.min(currentLoudness * sensitivity, 1.0f);
        int newY = (int)((1.0f - volumeClamped) * (screenHeight - topPadding - bottomPadding) + topPadding);

        if (newY < topPadding) newY = topPadding;
        if (newY > screenHeight - bottomPadding) newY = screenHeight - bottomPadding;

        birdY = newY;
    }

    public void updatePipes() {
        if (gameOver) return;

        pipeSpawnTimer++;
        if (pipeSpawnTimer >= pipeSpawnInterval) {
            pipeSpawnTimer = 0;
            spawnPipe();
        }

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.setX(pipe.getX() - pipeSpeed);

            if (pipe.getX() + pipe.getWidth() < 0) {
                iterator.remove();
            }

        }

        if (hitCooldown) {
            hitCooldownTimer--;
            if (hitCooldownTimer <= 0) {
                hitCooldown = false;
            }
        }

        if (scorePopupTimer > 0) {
            scorePopupTimer--;
            if (scorePopupTimer <= 0) {
                showScorePopup = false;
            }
        }
    }

    private void spawnPipe() {
        int gapY = random.nextInt(pipeGapMax - pipeGapMin) + pipeGapMin;
        pipes.add(new Pipe(screenWidth, gapY));
    }

    public boolean checkPipeCollision() {
        if (gameOver || hitCooldown) return false;

        int halfSize = birdSize / 2;
        int birdLeft = birdX - halfSize;
        int birdRight = birdX + halfSize;
        int birdTop = birdY - halfSize;
        int birdBottom = birdY + halfSize;

        for (Pipe pipe : pipes) {
            int pipeLeft = pipe.getX();
            int pipeRight = pipe.getX() + pipe.getWidth();

            if (birdRight > pipeLeft && birdLeft < pipeRight) {
                int topPipeBottom = pipe.getTopPipeBottom();
                int bottomPipeTop = pipe.getBottomPipeTop();

                if (birdTop < topPipeBottom || birdBottom > bottomPipeTop) {
                    hitCooldown = true;
                    hitCooldownTimer = 10;
                    return true;
                }
            }
        }
        return false;
    }

    public void checkScoring() {
        for (Pipe pipe : pipes) {
            if (!pipe.isScored() && pipe.getX() + pipe.getWidth() < birdX - birdSize/2) {
                pipe.setScored(true);
                score++;

                showScorePopup = true;
                scorePopupTimer = 30;

                System.out.println("Score: " + score);

                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                    System.out.println("NEW HIGH SCORE: " + highScore + " !!!");
                }
            }
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.text"))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line.trim());
                System.out.println("Loaded high score: " + highScore);
            }
        } catch (IOException e) {
            System.out.println("No high score file found, starting fresh");
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(String.valueOf(highScore));
            System.out.println("Saved high score: " + highScore);
        } catch (IOException e) {
            System.err.println("Failed to save high score (HA): " + e.getMessage());
        }
        
    }

    public List<Pipe> getPipes() { return pipes; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver && score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }

    public boolean isShowScorePopup() { return showScorePopup; }
    public int getScorePopupTimer() { return scorePopupTimer; }

    public void resetGame() {
        birdY = screenHeight / 2;
        pipes.clear();
        score = 0;
        gameOver = false;
        pipeSpawnTimer = 0;
        hitCooldown = false;
        hitCooldownTimer = 0;
        showScorePopup = false;
        scorePopupTimer = 0;
        System.out.println("=== GAME RESET ===");
    }

    public float getCurrentLoudness() { return currentLoudness; }
    public void setCurrentLoudness(float loudness) {
        this.currentLoudness = loudness;
    }

    public void setSensitivity(float sens) {
        this.sensitivity = sens;
    }
    public float getSensitivity() { return sensitivity; }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public int getTopPadding() { return topPadding; }
    public int getBottomPadding() { return bottomPadding; }

    public int getPipeSpeed() { return pipeSpeed; }
    public void setPipeSpeed(int speed) { this.pipeSpeed = speed; }
    public int getPipeSpawnInterval() { return pipeSpawnInterval; }
    public void setPipeSpawnInterval(int interval) { this.pipeSpawnInterval = interval; }
}
