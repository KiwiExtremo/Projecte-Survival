package com.example.survivalgame.object;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.GameLoop;
import com.example.survivalgame.R;
import com.example.survivalgame.Utils;

/**
 * A Crosshair is an object that is used to know where the bullets will be fired.
 * The Crosshair class is an extension of a Circle, which in turn inherits from the GameObject class.
 */
public class Crosshair extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 1000.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    public static final int orbitRadius = 100;
    private final Joystick joystick;

    public Crosshair(Context context, Joystick joystick, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.crosshair), positionX, positionY, radius);

        this.joystick = joystick;
    }

    // TODO move crosshair to the same relative position as the actuator, but centered on the player's position
    @Override
    public void update() {
        // Update velocity based on the actuator of the joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        // Update position based on current velocity
        positionX += velocityX;
        positionY += velocityY;

        // Update direction of crosshair
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);

            directionX = velocityX / distance;
            directionY = velocityY / distance;
        }
    }
}
