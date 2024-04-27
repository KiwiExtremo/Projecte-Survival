package com.example.survivalgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * The Game class will manage all objects in the game, and will be responsible
 * for updating all states and rendering all objects to the screen.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private GameLoop gameLoop;
    private Context context;
    public Game(Context context) {
        super(context);
        this.context = context;

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Create a new game loop
        gameLoop = new GameLoop(this, surfaceHolder);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawUPS(canvas);
        drawFPS(canvas);

    }

    public void drawUPS(Canvas canvas) {
        double avgUPS = gameLoop.getAverageUPS();

        int color = ContextCompat.getColor(context, R.color.green);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText(context.getString(R.string.canvas_text_ups, avgUPS), 100, 100, paint);
    }

    public void drawFPS(Canvas canvas) {
        double avgFPS = gameLoop.getAverageFPS();

        int color = ContextCompat.getColor(context, R.color.green);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText(context.getString(R.string.canvas_text_fps, avgFPS), 100, 200, paint);
    }

    public void update() {
    }
}
