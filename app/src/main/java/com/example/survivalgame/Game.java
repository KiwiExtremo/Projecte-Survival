package com.example.survivalgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.survivalgame.object.Bullet;
import com.example.survivalgame.object.Circle;
import com.example.survivalgame.object.Enemy;
import com.example.survivalgame.object.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Game class will manage all objects in the game, and will be responsible
 * for updating all states and rendering all objects to the screen.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Joystick joystick;
    private final Player player;
    private List<Enemy> enemyList = new ArrayList<>();
    private List<Bullet> bulletList = new ArrayList<>();
    private GameLoop gameLoop;
    private int joystickPointerId = 0;
    private int numberOfBulletsToFire = 0;

    public Game(Context context) {
        super(context);

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Create a new game loop
        gameLoop = new GameLoop(this, surfaceHolder);
        setFocusable(true);

        // Create a joystick
        // TODO make joystick appear where you click instead of in a fixed position
        joystick = new Joystick(275, 700, 100, 60);

        // Create a new player
        player = new Player(getContext(), joystick, 2 * 500, 500, 60);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Handle different touch event actions
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (joystick.getIsPressed()) { // Joystick was pressed right before this event -> fire a bullet
                    numberOfBulletsToFire++;

                } else if (joystick.isPressed(event.getX(), event.getY())) { // Joystick is pressed in this event -> isPressed() to true
                    // Store the event ID of the pressing of the joystick
                    joystickPointerId = event.getPointerId(event.getActionIndex());

                    joystick.setIsPressed(true);

                } else { // joystick was not pressed right before this event nor now -> fire a bullet
                    numberOfBulletsToFire++;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                // Joystick was pressed right before this event, and is now moved
                if (joystick.getIsPressed()) {
                    joystick.setActuator(event.getX(), event.getY());
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // Check if the lifted action was from an action that had an ACTION_DOWN event. If true, the current action is from lifting the joystick
                if (joystickPointerId == event.getPointerId(event.getActionIndex())) {
                    // Joystick has been lifted -> reset isPressed() and actuator
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                // if false, the action comes from firing a bullet, so nothing needs to be done

                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawUPS(canvas);
        drawFPS(canvas);

        joystick.draw(canvas);
        player.draw(canvas);

        for (Enemy enemy : enemyList) {
            enemy.draw(canvas);
        }

        for (Bullet bullet : bulletList) {
            bullet.draw(canvas);
        }
    }

    public void drawUPS(Canvas canvas) {
        double avgUPS = gameLoop.getAverageUPS();

        int color = ContextCompat.getColor(getContext(), R.color.green);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText(getContext().getString(R.string.canvas_text_ups, avgUPS), 100, 100, paint);
    }

    public void drawFPS(Canvas canvas) {
        double avgFPS = gameLoop.getAverageFPS();

        int color = ContextCompat.getColor(getContext(), R.color.green);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(50);

        canvas.drawText(getContext().getString(R.string.canvas_text_fps, avgFPS), 100, 200, paint);
    }

    public void update() {
        // Update state of each object in the game
        joystick.update();
        player.update();

        // Enemies are created dynamically here
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), player));
        }

        while (numberOfBulletsToFire > 0) {
            bulletList.add(new Bullet(getContext(), player));
            numberOfBulletsToFire--;
        }
        // Update state of each enemy
        for (Enemy enemy : enemyList) {
            enemy.update();
        }

        // Update state of each bullet
        for (Bullet bullet : bulletList) {
            bullet.update();
        }

        // Remove the current enemy if it is colliding with the player or a bullet (implicit iterator)
        enemyList.removeIf(enemy -> Circle.isColliding(enemy, player));

        Iterator<Enemy> enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Circle enemy = enemyIterator.next();

            if (Circle.isColliding(enemy, player)) {
                enemyIterator.remove();

                // Skip bullets collision detection with current enemy since it collided with the player
                continue;
            }

            Iterator<Bullet> bulletIterator = bulletList.iterator();
            while (bulletIterator.hasNext()) {
                Circle bullet = bulletIterator.next();

                // Remove current bullet if it collides with an enemy
                if (Circle.isColliding(bullet, enemy)) {
                    bulletIterator.remove();
                    enemyIterator.remove();

                    // Stop checking collision of current enemy with the rest of the bullets since it collided with curent bullet
                    break;
                }
            }
        }
import android.view.View;

public class Game extends View {
    public Game(Context context) {
        super(context);
    }
}