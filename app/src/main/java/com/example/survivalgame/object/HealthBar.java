package com.example.survivalgame.object;

import android.graphics.Canvas;

/**
 * The healthBar class displays the player's health on the screen.
 */
public class HealthBar {

    private final Player player;

    public HealthBar(Player player) {
        this.player = player;
    }

    public void draw(Canvas canvas) {
        float playerX = (float) player.getPositionX();
        float playerY = (float) player.getPositionY();

        float distanceToPlayer = 30;

        float healthPointPercent = player.getCurrentHealthPoints() / player.MAX_HEALTH_POINTS;
        //
        // Draw the border
        // canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, borderPaint);

        // Draw the health
        // canvas.drawRect(healthLeft, healthTop, healthRight, healthBottom, healthPaint);
    }
}
