package com.example.survivalgame.object;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.GameLoop;
import com.example.survivalgame.R;

public class Bullet extends Circle {
    private static final double BULLET_RADIUS = 20;
    public static final double SPEED_PIXELS_PER_SECOND = 800.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;

    public Bullet(Context context, Player marksman) {
        super(
                context,
                ContextCompat.getColor(context, R.color.bullet),
                marksman.getPositionX(),
                marksman.getPositionY(),
                BULLET_RADIUS
        );

        velocityX = marksman.getDirectionX() * MAX_SPEED;
        velocityY = marksman.getDirectionY() * MAX_SPEED;
    }

    @Override
    public void update() {
        positionX += velocityX;
        positionY += velocityY;
    }
}
