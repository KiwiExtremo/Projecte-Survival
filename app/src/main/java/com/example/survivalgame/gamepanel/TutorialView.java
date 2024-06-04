package com.example.survivalgame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;

/**
 * The TutorialView class extends from the {@link View} class. It is used to show the game tutorial
 * to the user, and handles the different tutorial parts on the screen.
 */
public class TutorialView extends View {
    public static final int JOYSTICK = 1;
    public static final int HEALTHBAR = 2;
    public static final int SCORE = 3;
    public static final int DONE = 4;
    private Paint backgroundPaint, cutoutPaint;
    boolean drawOnLeft = true;
    private int tutorialToDraw;

    public TutorialView(Context context) {
        super(context);
        init();
    }

    public TutorialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getTutorialToDraw() {
        return tutorialToDraw;
    }

    public void setPositionToDraw(int tutorialToDraw) {
        this.tutorialToDraw = tutorialToDraw;
    }

    public boolean isDrawOnLeft() {
        return drawOnLeft;
    }

    public void setDrawOnLeft(boolean drawOnLeft) {
        this.drawOnLeft = drawOnLeft;
    }

    private void init() {
        tutorialToDraw = JOYSTICK;
        // Create a paint with the background color
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.background_tutorial));

        // Create a paint to cut out a transparent rectangle into the background color
        cutoutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cutoutPaint.setColor(ContextCompat.getColor(getContext(), R.color.transparent));
        cutoutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.background_tutorial));

        float rectMargin, rectLeft, rectTop, rectRight, rectBottom, cornerRadius;

        switch (tutorialToDraw) {
            case JOYSTICK:
                rectMargin = (float) (getWidth() * 0.03);
                rectBottom = getHeight() - rectMargin;
                rectTop = rectMargin * 3;

                if (drawOnLeft) {
                    rectLeft = rectMargin;
                    rectRight = (float) (getWidth() / 2) - rectMargin;

                } else {
                    rectLeft = getWidth() - rectMargin;
                    rectRight = (float) getWidth() / 2 + rectMargin;
                }

                break;

            case HEALTHBAR:
                rectMargin = (float) (getWidth() * 0.01);
                rectLeft = (float) (getWidth() * 0.69);
                rectRight = getWidth() - rectMargin;
                rectTop = rectMargin;
                rectBottom = (float) (getHeight() * 0.15);

                break;

            case SCORE:
                rectMargin = (float) (getWidth() * 0.01);
                rectLeft = rectMargin;
                rectRight = (float) (getWidth() * 0.25);
                rectTop = rectMargin;
                rectBottom = (float) (getHeight() * 0.13);

                break;

            case DONE:
            default:
                rectLeft = 0;
                rectRight = 0;
                rectTop = 0;
                rectBottom = 0;
        }

        cornerRadius = 50;

        canvas.drawRoundRect(rectLeft, rectTop, rectRight, rectBottom, cornerRadius, cornerRadius, cutoutPaint);
    }
}