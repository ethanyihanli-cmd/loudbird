package com.macondo.loudbird.controller;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.view.GameView;
import javafx.animation.AnimationTimer;

public class GameController {
    private GameModel model;
    private GameView view;
    private AnimationTimer gameLoop;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                view.render(model);
            }
        };
        gameLoop.start();
    }
}
