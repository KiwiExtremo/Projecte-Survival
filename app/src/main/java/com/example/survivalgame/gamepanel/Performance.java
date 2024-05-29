package com.example.survivalgame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;
import com.example.survivalgame.gameengine.GameLoop;

public class Performance {
    private GameLoop gameLoop;
    private Context context;

    public Performance(Context context, GameLoop gameLoop) {
        this.context = context;
        this.gameLoop = gameLoop;
    }

    public void draw(Canvas canvas, int currentScore) {
        drawUPS(canvas);
        drawFPS(canvas);
        drawScore(canvas, currentScore);
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

    public void drawScore(Canvas canvas, int currentScore) {
        int color = ContextCompat.getColor(context, R.color.white);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText(context.getString(R.string.canvas_text_score, currentScore), 500, 100, paint);
    }
}
