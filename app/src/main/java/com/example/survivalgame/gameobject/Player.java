package com.example.survivalgame.gameobject;

import android.content.Context;
import android.graphics.Canvas;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.gameengine.GameLoop;
import com.example.survivalgame.R;
import com.example.survivalgame.Utils;
import com.example.survivalgame.gamepanel.Joystick;

/**
 * A Player is the main character of the game, controllable by the user through a Joystick object.
 * The Player class is an extension of a Circle, which in turn inherits from the GameObject class.
 */
public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final int MAX_HEALTH_POINTS = 10;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Joystick joystick;
    private HealthBar healthBar;
    private int currentHealthPoints;

    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);

        this.joystick = joystick;
        this.healthBar = new HealthBar(context, this);
        this.currentHealthPoints = MAX_HEALTH_POINTS;
    }

    @Override
    public void update() {
        // Update velocity based on the actuator of the joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        // Update position based on current velocity
        positionX += velocityX;
        positionY += velocityY;

        // Update direction of player
        if (velocityX != 0 || velocityY != 0) {
            // Normalize velocity to get direction
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityY);

            directionX = velocityX / distance;
            directionY = velocityY / distance;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        healthBar.draw(canvas);

    }

    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    public void setHealthPoints(int currentHealthPoints) {
        if (currentHealthPoints >= 0) {
            this.currentHealthPoints = currentHealthPoints;
        }
    }
}
