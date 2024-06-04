package com.example.survivalgame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;

/**
 * The healthBar class displays the player's health on the screen.
 */
public class HealthBar {

    private final Player player;
    private int hpWidth, hpHeight, hpMargin, hpCorners;
    private Paint borderPaint, healthPaint, whitePaint, blackPaint;

    public HealthBar(Context context, Player player) {
        this.player = player;
        hpWidth = 500;
        hpHeight = 80;
        hpMargin = 50;
        hpCorners = 15;

        // Create border paint
        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.healthbar_border);

        borderPaint.setColor(borderColor);

        // Create health paint
        this.healthPaint = new Paint();
        int healthColor = ContextCompat.getColor(context, R.color.healthbar_color);

        healthPaint.setColor(healthColor);

        // Set color of the white middle glow
        whitePaint = new Paint();
        whitePaint.setColor(ContextCompat.getColor(context, R.color.white));

        // Set color of the black center
        blackPaint = new Paint();
        blackPaint.setColor(ContextCompat.getColor(context, R.color.transparent));
        blackPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /**
     * Handles the drawing of the health bar, drawing a rounded rectangle and then drawing vertical
     * lines to divide the health bar into individual health points.
     *
     * @param canvas the canvas on which the circles will be drawn.
     * @param screenHeight the height of the screen.
     * @param screenWidth  the witdh of the screen.
     */
    public void drawNeon(Canvas canvas, int screenHeight, int screenWidth) {
        float healthPointPercent = (float) player.getCurrentHealthPoints() / Player.MAX_HEALTH_POINTS;

        // Get the health coordinates
        float borderLeft, borderTop, borderRight, borderBottom;

        borderLeft = screenWidth - (hpWidth * healthPointPercent) - hpMargin;
        borderRight = screenWidth - hpMargin;
        borderBottom = (float) hpHeight + hpMargin;
        borderTop =  hpMargin;

        // Draw the outer glow, the white middle part, the inner glow, and the black center
        canvas.drawRoundRect(borderLeft, borderTop, borderRight, borderBottom, hpCorners, hpCorners, healthPaint);
        canvas.drawRoundRect(borderLeft + 5, borderTop + 5, borderRight - 5, borderBottom - 5, hpCorners, hpCorners, whitePaint);
        canvas.drawRoundRect(borderLeft + 10, borderTop + 10, borderRight - 10, borderBottom - 10, hpCorners, hpCorners, healthPaint);
        canvas.drawRoundRect(borderLeft + 15, borderTop + 15, borderRight - 15, borderBottom - 15, hpCorners, hpCorners, blackPaint);

        // Draw the segments dividing each health point
        for (int i = 0; i < player.getCurrentHealthPoints(); i++) {
            if (i != 0 && player.getCurrentHealthPoints() != 0) {
                float segmentBorder = borderLeft + (i * ((float) hpWidth / Player.MAX_HEALTH_POINTS));

                canvas.drawRect(segmentBorder - 7, borderTop + 15, segmentBorder + 8, borderBottom - 15, healthPaint);
                canvas.drawRect(segmentBorder - 2, borderTop + 5, segmentBorder + 3, borderBottom - 5, whitePaint);
            }
        }
    }
}
