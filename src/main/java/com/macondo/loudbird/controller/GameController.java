package com.macondo.loudbird.controller;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import javax.sound.sampled.*;

public class GameController {
    private GameModel model;
    private GameView view;
    private AnimationTimer gameLoop;
    private AudioThread audioThread;
    private Scene scene;

    private int frameCounter = 0;
    private boolean restartPressed = false;
    private boolean micWorking = false;

    public GameController(GameModel model, GameView view, Scene scene) {
        this.model = model;
        this.view = view;
        this.scene = scene;
        setupKeyboardInput();
    }

    public void setupKeyboardInput() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                model.keyboardRestart();
            }
            if (e.getCode() == KeyCode.S) {
                float newSens = model.getSensitivity() + 0.1f;
                if (newSens > 1.5f) newSens = 0.3f;
                model.setSensitivity(newSens);
                System.out.println("Sensitivity: " + String.format("%.2f", newSens));
                }
        });
    }

    public void start() {
        try {
            audioThread = new AudioThread();
            audioThread.setDaemon(true);
            audioThread.start();

            try { Thread.sleep(500); } catch (InterruptedException e) {}

            if (model.getCurrentLoudness() > 0.01f) {
                micWorking = true;
                System.out.println("√ Microphone detected and working!");
            } else {
                System.out.println("⚠ Microphone detected but no sound. Try adjusting volume");
                micWorking = true;
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to open microphone: " + e.getMessage());
            System.err.println("Using fallback mode - bird will hover in middle");
            micWorking = false;
            model.setCurrentLoudness(0.5f);
        }

        System.out.println("Sensitivity: " + model.getSensitivity());
        System.out.println("Press SPACE or YELL to restart after game over");
        System.out.println("Press 'S' to change sensitivity");

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                model.checkVoiceRestart();

                if (!model.isGameOver()) {
                     model.updateBirdPosition();
                     model.updatePipes();
                     model.checkScoring();
                     model.checkPipeCollision();
                    } else {
                    model.updatePipes();
                }

                view.render(model);

                frameCounter++;
                if (frameCounter % 30 == 0 && !model.isGameOver()) {
                    float loudness = model.getCurrentLoudness();
                    if (micWorking) {
                        System.out.println("Mic: " + String.format("%.2f", loudness) +
                                " | Bird Y: " + model.getBirdY() +
                                " | Pipes: " + model.getPipes().size());
                    }
                }
            }
        };
        gameLoop.start();
    }

    public void stopAudio() {
        if (audioThread != null) {
            audioThread.stopRunning();
            try {
                audioThread.join(1000);
            } catch (InterruptedException e) {}
        }
    }

    private class AudioThread extends Thread {
        private volatile boolean running = true;
        private volatile boolean hasData = false;

        @Override
        public void run() {
            AudioFormat format = new AudioFormat(
                    44100.0f,
                    16,
                    1,
                    true,
                    true
            );

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = null;

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format, 4096);
                line.start();

                System.out.println("Audio line opened successfully");

                byte[] buffer = new byte[1024];
                int bytesRead;
                float rms;

                while (running) {
                    bytesRead = line.read(buffer, 0, buffer.length);
                    if (bytesRead < 0) break;

                    if (bytesRead > 0) {
                        hasData = true;
                        rms = calculateRMS(buffer, bytesRead);

                        if (rms > 1.0f) rms = 1.0f;
                        if (rms < 0.0f) rms = 0.0f;

                        model.setCurrentLoudness(rms);
                    }
                }
            } catch (LineUnavailableException e) {
                System.err.println("Microphone not available: " + e.getMessage());
                runFallbackMode();
            } catch (Exception e) {
                System.err.println("Audio error: " + e.getMessage());
                runFallbackMode();
            } finally {
                 if (line != null) {
                     line.stop();
                     line.close();
                 }
                 System.out.println("Audio thread stopped");
            }
        }

        private void runFallbackMode() {
            System.out.println("Running in fallback mode - use keyboard arrows to control bird");
            while (running) {
                try {
                    Thread.sleep(50);
                    model.setCurrentLoudness(0.4f);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        private float calculateRMS(byte[] audioData, int length) {
            double sum = 0.0;
            for (int i = 0; i < length - 1; i += 2) {
                int sample = ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
                sample = sample >> 8;
                double normalized = sample / 32768.0;
                sum += normalized * normalized;
            }
            double rms = Math.sqrt(sum / (length / 2));
            return (float)(rms * 2.0);
        }

        public void stopRunning() {
            running = false;
        }

        public boolean hasData() { return hasData; }
    }
}
