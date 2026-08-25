package com.macondo.loudbird.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameModel {
    private int birdX;
    private int birdY;
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

    public GameModel() {
        birdX = 80;
        birdY = screenHeight / 2;
        pipes = new ArrayList<>();
        random = new Random();
    }

    public int getBirdX() { return birdX; }
    public int getBirdY() { return birdY; }

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
    }

    private void spawnPipe() {
        int gapY = random.nextInt(pipeGapMax - pipeGapMin) + pipeGapMin;
        pipes.add(new Pipe(screenWidth, gapY));
    }

    public boolean checkPipeCollision() {
        int birdSize = 30;
        int birdLeft = birdX - birdSize/2;
        int birdRight = birdX + birdSize/2;

        for (Pipe pipe : pipes) {
            int pipeLeft = pipe.getX();
            int pipeRight = pipe.getX() + pipe.getWidth();

            if (birdRight > pipeLeft && birdLeft < pipeRight) {
                int topPipeBottom = pipe.getTopPipeBottom();
                int bottomPipeTop = pipe.getBottomPipeTop();

                if (birdY - birdSize/2 < topPipeBottom || birdY + birdSize/2 > bottomPipeTop) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkScoring() {
        int birdSize = 30;
        for (Pipe pipe : pipes) {
            if (!pipe.isScored() && pipe.setX() + pipe.getWidth() < birdX - birdSize/2) {
                pipe.setScored(true);
                score++;
                System.out.println("Score: " + score);
            }
        }
    }

    public List<Pipe> getPipes() { return pipes; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public void resetGame() {
        birdY = screenHeight / 2;
        pipes.clear();
        score = 0;
        gameOver = false;
        pipeSpawnTimer = 0;
        System.out.println("Game reset!");
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
}
