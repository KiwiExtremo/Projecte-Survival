package com.example.survivalgame.gameobject;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.gameengine.GameLoop;
import com.example.survivalgame.R;
import com.example.survivalgame.Utils;

public class Bullet extends Circle {
    private static final double BULLET_RADIUS = 20;
    public static final double SPEED_PIXELS_PER_SECOND = 800.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    // private final Crosshair crosshair;

    // Constructor with a crosshair // TODO make crosshair orbit around player at fixed distance
    /*
    public Bullet(Context context, Player marksman, Crosshair crosshair) {
        super(
                context,
                ContextCompat.getColor(context, R.color.bullet),
                marksman.getPositionX(),
                marksman.getPositionY(),
                BULLET_RADIUS
        );

        // this.crosshair = crosshair;

        velocityX = crosshair.getDirectionX() * MAX_SPEED;
        velocityY = crosshair.getDirectionY() * MAX_SPEED;
    }*/
    public Bullet(Context context, Player marksman, double directionX, double directionY) {
        super(
                context,
                ContextCompat.getColor(context, R.color.bullet),
                marksman.getPositionX(),
                marksman.getPositionY(),
                BULLET_RADIUS
        );

        double directionDistance = Utils.getDistanceBetweenPoints(0, 0, directionX, directionY);

        // Normalize speed of the bullet
        if (directionDistance != 0) {
            directionX = directionX / directionDistance;
            directionY = directionY / directionDistance;
        }

        velocityX = directionX * MAX_SPEED;
        velocityY = directionY * MAX_SPEED;
    }

    @Override
    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }
}
