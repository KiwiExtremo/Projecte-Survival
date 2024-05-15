package com.example.survivalgame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;

/**
 * The healthBar class displays the player's health on the screen.
 */
public class HealthBar {

    private final Player player;
    private int hpWidth, hpHeight, hpMargin;
    private Paint borderPaint, healthPaint;

    public HealthBar(Context context, Player player) {
        this.player = player;
        hpWidth = 100;
        hpHeight = 20;
        hpMargin = 2;

        // Create border paint
        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.healthbar_border);

        borderPaint.setColor(borderColor);

        // Create health paint
        this.healthPaint = new Paint();
        int healthColor = ContextCompat.getColor(context, R.color.healthbar_color);

        healthPaint.setColor(healthColor);
    }

    public void draw(Canvas canvas) {
        float playerX = (float) player.getPositionX();
        float playerY = (float) player.getPositionY();

        float distanceToPlayer = 30;

        float healthPointPercent = player.getCurrentHealthPoints() / player.MAX_HEALTH_POINTS;
        //
        // Draw the border
        float borderLeft, borderTop, borderRight, borderBottom;
        borderLeft = playerX - hpWidth / 2;
        borderRight = playerX + hpWidth / 2;
        borderBottom = playerY - distanceToPlayer;
        borderTop = borderBottom - hpHeight;

        canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, borderPaint);

        // Draw the health
        float healthLeft, healthTop, healthRight, healthBottom, healthWidth, healthHeight;
        healthWidth = hpWidth + 2 * hpMargin;
        healthHeight = hpWidth - 2 * hpMargin;

        healthLeft = borderLeft + hpMargin;
        healthRight = healthLeft + healthWidth * healthPointPercent;
        healthBottom = borderBottom - hpMargin;
        healthTop = healthBottom - healthHeight;

        canvas.drawRect(healthLeft, healthTop, healthRight, healthBottom, healthPaint);
    }
}
