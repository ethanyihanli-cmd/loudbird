package com.macondo.loudbird.model;

public class GameModel {
    private int birdX;
    private int birdY;
    private int screenWidth = 400;
    private int screenHeight = 600;

    public GameModel() {
        birdX = 80;
        birdY = screenHeight / 2;
    }

    public int getBirdX() { return birdX; }
    public int getBirdY() { return birdY; }
    public void setBirdY(int y) { birdY = y; }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
}
