package com.macondo.loudbird.controller;

import com.macondo.loudbird.model.GameModel;
import com.macondo.loudbird.view.GameView;
import javafx.animation.AnimationTimer;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;

public class GameController {
    private GameModel model;
    private GameView view;
    private AnimationTimer gameLoop;
    private AudioThread audioThread;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        try {
            audioThread = new AudioThread();
            audioThread.setDaemon(true);
            audioThread.start();
            System.out.println("Microphone started - yell at your computer!");
        } catch (Exception e) {
            System.err.println("Failed to open min: " + e.getMessage());
            e.printStackTrace();
            model.setCurrentLoudness(0.5f);
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                view.render(model);
            }
        };
        gameLoop.start();
    }

    private class AudioThread extends Thread {
        private volatile boolean running = true;

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

                byte[] buffer = new byte[1024];
                int bytesRead;
                float rms;

                while (running) {
                    bytesRead = line.read(buffer, 0, buffer.length);
                    if (bytesRead < 0) break;

                    rms = calculateRMS(buffer, bytesRead);

                    if (rms > 1.0f) rms = 1.0f;
                    if (rms < 0.0f) rms = 0.0f;

                    model.setCurrentLoudness(rms);

                    if (rms > 0.1f) {
                        System.out.println("Loudness: " + String.format("%.3f", rms));
                    }
                }
            } catch (LineUnavailableException e) {
                System.err.println("Mic not available: " + e.getMessage());
                model.setCurrentLoudness(0.5f);
            } finally {
                 if (line != null) {
                     line.stop();
                     line.close();
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

        public void stopAudio() {
            if (audioThread != null) {
                audioThread.stopRunning();
            }
        }
    }
}
