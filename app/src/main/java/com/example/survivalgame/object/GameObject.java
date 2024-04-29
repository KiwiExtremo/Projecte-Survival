package com.example.survivalgame.object;

import android.graphics.Canvas;

/**
 * The abstract GameObject class is the foundation of all world objects of the game.
 */
public abstract class GameObject {
    protected double positionX, positionY;
    protected double velocityX = 0, velocityY = 0;
    protected double directionX = 1, directionY = 0;

    public GameObject(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    protected static double getDistanceBetweenObjects(GameObject object1, GameObject object2) {
        return Math.sqrt(
                Math.pow(object2.getPositionX() - object1.getPositionX(), 2) +
                Math.pow(object2.getPositionY() - object1.getPositionY(), 2)
        );
    }

    public abstract void draw(Canvas canvas);
    public abstract void update();

    protected double getPositionX() {
        return positionX;
    }

    protected double getPositionY() {
        return positionY;
    }

    protected double getDirectionX() {
        return directionX;
    }

    protected double getDirectionY() {
        return directionY;
    }
}
