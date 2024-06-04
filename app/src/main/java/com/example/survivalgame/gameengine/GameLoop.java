package com.example.survivalgame.gameengine;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * The GameLoop class extends from the {@link Thread} class. It creates a thread that handles all game
 * updates and displays each frame on the screen.
 */
public class GameLoop extends Thread {
    public static final double MAX_UPS = 60.0;
    public static final double UPS_PERIOD = 1E+3 / MAX_UPS;
    private boolean isRunning = false, gameFinished = false, firstFrame = true;
    private final SurfaceHolder surfaceHolder;
    private final GameView game;
    private double averageUPS;
    private double averageFPS;

    public GameLoop(GameView game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    public void startLoop() {
        // We start the game with the isRunning on false, so as to let the tutorial be shown without
        // the game starting.

        start();
    }

    public void stopLoop() {
        isRunning = false;
    }

    @Override
    public void run() {
        super.run();

        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        // Game loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while (!gameFinished) {
            while (isRunning || firstFrame) {
                // Try to update and render the game
                try {
                    canvas = surfaceHolder.lockCanvas();

                    synchronized (surfaceHolder) {
                        game.update();
                        updateCount++;

                        game.draw(canvas);
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();

                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                            frameCount++;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Pause the game loop to avoid exceeding the target UPS
                elapsedTime = System.currentTimeMillis() - startTime;

                sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Skip frames to keep up with the target UPS
                while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                    game.update();
                    updateCount++;

                    elapsedTime = System.currentTimeMillis() - startTime;

                    sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                }

                // Calculate average UPS and FPS
                elapsedTime = System.currentTimeMillis() - startTime;

                if (elapsedTime >= 1000) {
                    averageUPS = (updateCount / (1E-3 * elapsedTime));
                    averageFPS = (frameCount / (1E-3 * elapsedTime));

                    // Reset all our counters
                    updateCount = 0;
                    frameCount = 0;

                    startTime = System.currentTimeMillis();
                }

                firstFrame = false;

                if (gameFinished) {
                    break;
                }
            }
        }
    }
}