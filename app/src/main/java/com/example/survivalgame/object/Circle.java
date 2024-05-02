package com.example.survivalgame.object;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Circle is an abstract class that inherits from the GameObject class, and it
 * implements a draw method to draw objects as circles.
 */
public abstract class Circle extends GameObject {
    protected double radius;
    protected Paint paint;

    public Circle(Context context, int color, double positionX, double positionY, double radius) {
        super(positionX, positionY);

        this.radius = radius;

        // Set color of the circle
        paint = new Paint();
        paint.setColor(color);
    }

    private double getRadius() {
        return radius;
    }

    /**
     * isColliding checks if two circle objects are colliding based on their positions and radii
     * @param object1 first object to check collision
     * @param object2 second object to check collision
     * @return returns true if the passed objects are colliding (they are overlapping). Returns
     * false otherwise.
     */
    public static boolean isColliding(Circle object1, Circle object2) {
        double distance = getDistanceBetweenObjects(object1, object2);
        double distanceToCollision = object1.getRadius() + object2.getRadius();

        return distance < distanceToCollision;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius, paint);
    }
}
