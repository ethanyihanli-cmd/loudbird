package com.macondo.loudbird.model;

public class GameModel {
    private int birdX;
    private int birdY;
    private int screenWidth = 400;
    private int screenHeight = 600;

    private float currentLoudness = 0.0f;
    private float sensitivity = 0.7f;

    private int topPadding = 30;
    private int bottomPadding = 30;

    public GameModel() {
        birdX = 80;
        birdY = screenHeight / 2;
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

    public void setBirdY(int y) { birdY = y; }

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
}
