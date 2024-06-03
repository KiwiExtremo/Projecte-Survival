package com.example.survivalgame.gameobject;

import android.content.Context;
import android.graphics.Canvas;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;
import com.example.survivalgame.gameengine.GameLoop;
import com.example.survivalgame.gamepanel.Joystick;

/**
 * A Player is the main character of the game, controllable by the user through a Joystick object.
 * The Player class is an extension of a {@link Circle}, which in turn extends from the {@link
 * GameObject} class.
 */
public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final int MAX_HEALTH_POINTS = 5;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Joystick joystick;
    private HealthBar healthBar;
    private int currentHealthPoints, screenHeight, screenWidth;

    public Player(Context context, Joystick joystick, double positionX, double positionY, int screenHeight, int screenWidth, double radius) {
        super(context, ContextCompat.getColor(context, R.color.player_yellow), positionX, positionY, radius);

        this.joystick = joystick;
        this.healthBar = new HealthBar(context, this);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.currentHealthPoints = MAX_HEALTH_POINTS;
    }

    /**
     * update() overrides the super method {@link GameObject#update()}. It updates the player's position
     * based on the joystick direction.
     */
    @Override
    public void update() {
        // Update velocity based on the actuator of the joystick
        velocityX = joystick.getActuatorX() * MAX_SPEED;
        velocityY = joystick.getActuatorY() * MAX_SPEED;

        // Update position based on current velocity
        positionX += velocityX;
        positionY += velocityY;
    }

    /**
     * drawNeon() calls the super method to handle the drawing of the player before drawing the
     * healthbar on the screen.
     *
     * @param canvas the canvas on which the circles will be drawn.
     */
    @Override
    public void drawNeon(Canvas canvas) {
        super.drawNeon(canvas);

        if (currentHealthPoints > 0) {
            healthBar.drawNeon(canvas, screenHeight, screenWidth);
        }
    }

    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    public void setCurrentHealthPoints(int newHealthPoints) {
        if (this.currentHealthPoints >= 0) {
            this.currentHealthPoints = newHealthPoints;
        }
    }
}
