package com.example.survivalgame.gameengine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.survivalgame.object.Joystick;
import com.example.survivalgame.R;
import com.example.survivalgame.object.Bullet;
import com.example.survivalgame.object.Circle;
import com.example.survivalgame.object.Crosshair;
import com.example.survivalgame.object.Enemy;
import com.example.survivalgame.object.Joystick;
import com.example.survivalgame.object.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Game class will manage all objects in the game, and will be responsible
 * for updating all states and rendering all objects to the screen.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Joystick playerJoystick;
    private final Joystick crosshairJoystick;
    private final Player player;
    private final Crosshair crosshair;
    private List<Enemy> enemyList = new ArrayList<>();
    private List<Bullet> bulletList = new ArrayList<>();
    private GameLoop gameLoop;
    private int playerJoystickPointerId = -1;
    private int crosshairJoystickPointerId = -1;
    private boolean bulletReady = false;

    private double crosshairJoystickActuatorX, crosshairJoystickActuatorY;

    public Game(Context context) {
        super(context);

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Create a new game loop
        gameLoop = new GameLoop(this, surfaceHolder);

        // Create the joysticks
        // TODO make joysticks appear where you click instead of in a fixed position or set them at X distance from margins
        playerJoystick = new Joystick(275, 700, 100, 60);
        crosshairJoystick = new Joystick(1920 - 275, 700, 100, 60);

        // Create a new player
        player = new Player(getContext(), playerJoystick, 2 * 500, 500, 60);

        // Create a new crosshair
        crosshair = new Crosshair(getContext(), player, crosshairJoystick, 2 * 500, 600);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Handle different touch event actions
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        int pointerIdIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIdIndex);

        float eventX = event.getX(pointerIdIndex);
        float eventY = event.getY(pointerIdIndex);

        int pointerCount = event.getPointerCount();

        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (playerJoystick.isPressed(eventX, eventY)) {
                    // Joystick is pressed in this event -> isPressed() to true and store pointer ID
                    playerJoystickPointerId = pointerId;
                    playerJoystick.setIsPressed(true);

                } // TODO move playerJoystick to eventX and eventY if left side of screen is pressed

                if (crosshairJoystick.isPressed(eventX, eventY)) {
                    // Joystick is pressed in this event -> isPressed() to true and store the pointer ID
                    crosshairJoystickPointerId = pointerId;
                    crosshairJoystick.setIsPressed(true);
                } // TODO move crossJoystick to eventX and eventY if right side of screen is pressed
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                // Joystick was pressed right before this event, and is now moved
                if (playerJoystickPointerId == pointerId) {
                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    playerJoystick.setIsPressed(false);
                    playerJoystick.resetActuator();

                    playerJoystickPointerId = -1;

                } else if (crosshairJoystickPointerId == pointerId) {
                    // Since this joystick is the crosshair, prepare a bullet
                    bulletReady = true;

                    crosshairJoystickActuatorX = crosshairJoystick.getActuatorX();
                    crosshairJoystickActuatorY = crosshairJoystick.getActuatorY();

                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    crosshairJoystick.setIsPressed(false);
                    crosshairJoystick.resetActuator();

                    crosshairJoystickPointerId = -1;
                }
                break;
        }

        for (int iPointerIndex = 0; iPointerIndex < pointerCount; iPointerIndex++) {
            if (playerJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == playerJoystickPointerId) {
                // Joystick was pressed previously and is now moved
                playerJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));

            } else if (crosshairJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == crosshairJoystickPointerId) {
                // Joystick was pressed previously and is now moved
                crosshairJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));
            }
        }
        return true;
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

        crosshair.draw(canvas);
        player.draw(canvas);

        playerJoystick.draw(canvas);
        crosshairJoystick.draw(canvas);


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
        playerJoystick.update();
        crosshairJoystick.update();

        player.update();
        crosshair.update();

        // Enemies are created dynamically here
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), player));
        }

        // Update state of each enemy
        for (Enemy enemy : enemyList) {
            enemy.update();
        }

        // Create bullet if ready
        if (bulletReady) {
            bulletList.add(new Bullet(getContext(), player, crosshairJoystickActuatorX, crosshairJoystickActuatorY));
            bulletReady = false;
        }

        // Update state of each bullet
        for (Bullet bullet : bulletList) {
            bullet.update();
        }

        // Remove the current enemy if it is colliding with the player or a bullet
        // by iterating through all enemies and bullets
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
    }
}