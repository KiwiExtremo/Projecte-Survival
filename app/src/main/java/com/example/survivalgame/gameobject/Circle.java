package com.example.survivalgame.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.R;

/**
 * Circle is an abstract class that inherits from the GameObject class, and it
 * implements a draw method to draw objects as circles. It has different draw methods, since different
 * objects will be drawn differently.
 */
public abstract class Circle extends GameObject {
    protected double radius;
    protected Paint outerPaint, whitePaint, blackPaint;

    public Circle(Context context, int color, double positionX, double positionY, double radius) {
        super(positionX, positionY);

        this.radius = radius;

        // Set color of the borders (outer and inner glow)
        outerPaint = new Paint();
        outerPaint.setColor(color);

        // Set color of the white middle glow
        whitePaint = new Paint();
        whitePaint.setColor(ContextCompat.getColor(context, R.color.white));

        // Set color of the black center
        blackPaint = new Paint();
        blackPaint.setColor(ContextCompat.getColor(context, R.color.black));
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

    /**
     * draw will draw a circle on the canvas, used for non-neon circles.
     * @param canvas the canvas on which the circles will be drawn
     */
    public void draw(Canvas canvas) {
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius, outerPaint);
    }

    /**
     * drawNeon will draw multiple circles on the canvas, in order to simulate a glowing neon circle.
     * @param canvas the canvas on which the circles will be drawn
     */
    public void drawNeon(Canvas canvas) {
        // Draw outer glow
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius, outerPaint);

        // Draw white center
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius - 5, whitePaint);

        // Draw inner glow
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius - 10, outerPaint);

        // Draw black center
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius - 15, blackPaint);
    }
}
