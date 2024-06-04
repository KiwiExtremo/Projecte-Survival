package com.example.survivalgame.gameobject;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.gameengine.GameLoop;
import com.example.survivalgame.R;
import com.example.survivalgame.Utils;

/**
 * A Bullet is an object that is fired by a player, and it will destroy (and be destroyed) an enemy
 * when it collides with it.
 * The Bullet class is an extension of a {@link Circle}, which in turn inherits from the
 * {@link GameObject} class.
 */
public class Bullet extends Circle {
    private static final double BULLET_RADIUS = 20;
    public static final double SPEED_PIXELS_PER_SECOND = 800.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    public Bullet(Context context, Player marksman, Crosshair crosshair) {
        super(
                context,
                ContextCompat.getColor(context, R.color.player_yellow),
                marksman.getPositionX(),
                marksman.getPositionY(),
                BULLET_RADIUS
        );

        double directionDistance = Utils.getDistanceBetweenPoints(0, 0, crosshair.getPreviousCrosshairPositionX(), crosshair.getPreviousCrosshairPositionY());

        // Normalize speed of the bullet
        if (directionDistance != 0) {
            directionX = crosshair.getPreviousCrosshairPositionX() / directionDistance;
            directionY = crosshair.getPreviousCrosshairPositionY() / directionDistance;
        }

        velocityX = directionX * MAX_SPEED;
        velocityY = directionY * MAX_SPEED;
    }

    /**
     * Overrides the super method {@link Circle#update()}. It updates the bullet's position based on the
     * bullet's velocity.
     */
    @Override
    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }
}
