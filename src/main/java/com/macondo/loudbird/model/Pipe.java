package com.macondo.loudbird.model;

public class Pipe {
    private int x;
    private int gapY;
    private int gapSize = 150;
    private int width = 60;
    private boolean scored = false;

    private int screenHeight = 600;

    public Pipe(int x, int gapY) {
        this.x = x;
        this.gapY = gapY;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getGapY() { return gapY; }
    public int getGapSize() { return gapSize; }
    public int getWidth() { return width; }

    public boolean isScored() { return scored; }
    public void setScored(boolean scored) { this.scored = scored; }

    public int getTopPipeBottom() {
        return gapY - gapSize / 2;
    }

    public int getBottomPipeTop() {
        return gapY + gapSize / 2;
    }

    public boolean isBirdPassing(int birdX, int birdY, int birdSize) {
        int birdLeft = birdX - birdSize/2;
        int birdRight = birdX + birdSize/2;
        int pipeLeft = x;
        int pipeRight = x + width;

        if (birdRight > pipeLeft && birdLeft < pipeRight) {
            int topPipeBottom = getTopPipeBottom();
            int bottomPipeTop = getBottomPipeTop();
            return birdY > topPipeBottom && birdY < bottomPipeTop;
        }
        return false;
    }


}
