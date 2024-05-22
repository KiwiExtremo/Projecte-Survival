package com.example.survivalgame.gameengine;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {
    public static final double MAX_UPS = 60.0;
    public static final double UPS_PERIOD = 1E+3 / MAX_UPS;
    private boolean isRunning = false;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private double averageUPS;
    private double averageFPS;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game = game;
        this.surfaceHolder = surfaceHolder;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    public void startLoop() {
        isRunning = true;

        start();
    }

    public void stopLoop() {
        isRunning = false;

        // wait for the thread to join
        try {
            join();

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
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
        while (isRunning) {
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
        }
    }
}
