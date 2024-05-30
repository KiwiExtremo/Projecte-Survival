package com.example.survivalgame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;
import com.example.survivalgame.gameengine.GameLoop;

public class Performance {
    private GameLoop gameLoop;
    private Context context;
    private Paint performancePaint, scorePaint, whitePaint;

    public Performance(Context context, GameLoop gameLoop) {
        this.context = context;
        this.gameLoop = gameLoop;
        Typeface tf = context.getResources().getFont(R.font.wave_family);

        performancePaint = new Paint();
        performancePaint.setTypeface(tf);
        performancePaint.setStyle(Paint.Style.STROKE);
        performancePaint.setColor(ContextCompat.getColor(context, R.color.green));
        performancePaint.setTextSize(50);
        performancePaint.setStrokeWidth(5);

        scorePaint = new Paint();
        scorePaint.setTypeface(tf);
        scorePaint.setStyle(Paint.Style.STROKE);
        scorePaint.setColor(ContextCompat.getColor(context, R.color.score));
        scorePaint.setTextSize(50);
        scorePaint.setStrokeWidth(5);

        whitePaint = new Paint();
        whitePaint.setTypeface(tf);
        whitePaint.setColor(ContextCompat.getColor(context, R.color.white));
        whitePaint.setTextSize(50);
    }

    public void draw(Canvas canvas, int currentScore) {
        drawUPS(canvas);
        drawFPS(canvas);
        drawScore(canvas, currentScore);
    }

    public void drawUPS(Canvas canvas) {
        double avgUPS = gameLoop.getAverageUPS();

        canvas.drawText(context.getString(R.string.canvas_text_ups, avgUPS), 100, 100, performancePaint);
        canvas.drawText(context.getString(R.string.canvas_text_ups, avgUPS), 100, 100, whitePaint);
    }

    public void drawFPS(Canvas canvas) {
        double avgFPS = gameLoop.getAverageFPS();

        canvas.drawText(context.getString(R.string.canvas_text_fps, avgFPS), 100, 200, performancePaint);
        canvas.drawText(context.getString(R.string.canvas_text_fps, avgFPS), 100, 200, whitePaint);
    }

    public void drawScore(Canvas canvas, int currentScore) {
        canvas.drawText(context.getString(R.string.canvas_text_score, currentScore), 500, 100, scorePaint);
        canvas.drawText(context.getString(R.string.canvas_text_score, currentScore), 500, 100, whitePaint);
    }
}
