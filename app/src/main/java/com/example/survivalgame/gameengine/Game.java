package com.example.survivalgame.gameengine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.survivalgame.gameobject.Bullet;
import com.example.survivalgame.gameobject.Circle;
import com.example.survivalgame.gameobject.Crosshair;
import com.example.survivalgame.gameobject.Enemy;
import com.example.survivalgame.gameobject.Player;
import com.example.survivalgame.gamepanel.GameOver;
import com.example.survivalgame.gamepanel.Joystick;
import com.example.survivalgame.gamepanel.Performance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Game class will manage all objects in the game, and will be responsible
 * for updating all states and rendering all objects to the screen.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Joystick playerJoystick;
    private final Joystick aimJoystick;
    private final Player player;
    private final Crosshair crosshair;
    private List<Enemy> enemyList = new ArrayList<>();
    private List<Bullet> bulletList = new ArrayList<>();
    private GameLoop gameLoop;
    private int playerJoystickPointerId = -1;
    private int aimJoystickPointerId = -1;

    public int screenHeight, screenWidth, currentScore = 0;
    private boolean bulletReady = false;
    private boolean showPlayerJoystick = false;
    private boolean showAimJoystick = false;
    private GameOver gameOver;
    private Performance performance;


    public Game(Context context) {
        super(context);

        // Get screen sizes on runtime
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Create a new game loop
        gameLoop = new GameLoop(this, surfaceHolder);

    // Initialize the game panels
        gameOver = new GameOver(context, screenWidth, screenHeight);
        performance = new Performance(context, gameLoop);

        // Create the joysticks (they're set on approximate positions by default, but will move dynamically when used)
        playerJoystick = new Joystick((int) screenWidth / 4, (int) screenHeight / 2, 100, 60);
        aimJoystick = new Joystick((int) 3 * screenWidth / 4, (int) screenHeight / 2, 100, 60);

    // Initialize the game objects
        // Create a new player
        player = new Player(context, playerJoystick, (float) screenWidth / 2, (float) screenHeight / 2, screenHeight, screenWidth, 60);

        // Create a new crosshair
        crosshair = new Crosshair(context, player, aimJoystick, (float) screenWidth / 2, (float) screenHeight / 2);

        setFocusable(true);
    }

    public GameLoop getGameLoop() {
        return gameLoop;
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
                if (((float) screenWidth / 2 - screenWidth * 0.1) > eventX && !showPlayerJoystick && pointerId != aimJoystickPointerId) {
                    // left side of the screen was pressed -> playerJoystick position will be moved to event position
                    playerJoystick.setPositionX(eventX);
                    playerJoystick.setPositionY(eventY);

                    playerJoystickPointerId = pointerId;
                    playerJoystick.setIsPressed(true);

                    showPlayerJoystick = true;
                }

                if (((float) screenWidth / 2 + screenWidth * 0.1) < eventX && !showAimJoystick && pointerId != playerJoystickPointerId) {
                    // right side of the screen was pressed -> aimJoystick position will be moved to event position
                    aimJoystick.setPositionX(eventX);
                    aimJoystick.setPositionY(eventY);

                    aimJoystickPointerId = pointerId;
                    aimJoystick.setIsPressed(true);

                    showAimJoystick = true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                // A joystick was pressed right before this event, and is now moved
                if (playerJoystickPointerId == pointerId) {
                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    playerJoystick.setIsPressed(false);
                    playerJoystick.resetActuator();

                    playerJoystickPointerId = -1;

                    showPlayerJoystick = false;

                } else if (aimJoystickPointerId == pointerId) {
                    // Since this joystick is the crosshair, prepare a bullet
                    bulletReady = true;

                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    aimJoystick.setIsPressed(false);
                    aimJoystick.resetActuator();

                    aimJoystickPointerId = -1;

                    showAimJoystick = false;
                }
                break;
        }

        for (int iPointerIndex = 0; iPointerIndex < pointerCount; iPointerIndex++) {
            if (playerJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == playerJoystickPointerId) {
                // Joystick was pressed previously and is now moved
                playerJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));

            } else if (aimJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == aimJoystickPointerId) {
                // Joystick was pressed previously and is now moved
                aimJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));
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

        performance.draw(canvas, currentScore);

        crosshair.drawFilledNeon(canvas);
        player.drawNeon(canvas);

        if (showPlayerJoystick) {
            playerJoystick.draw(canvas);
        }
        if (showAimJoystick) {
            aimJoystick.draw(canvas);
        }

        for (Enemy enemy : enemyList) {
            enemy.drawNeon(canvas);
        }

        for (Bullet bullet : bulletList) {
            bullet.drawFilledNeon(canvas);
        }

        // When a player loses all their healthpoints, draw the Game Over
        if (player.getCurrentHealthPoints() <= 0) {
            gameOver.draw(canvas);
        }
    }

    public void update() {
        // Stop updating the game if the player is dead
        if (player.getCurrentHealthPoints() <= 0) {
            return;
        }

        // Update state of each object in the game
        playerJoystick.update();
        aimJoystick.update();

        player.update();
        crosshair.update();

        // Enemies are created dynamically here
        if (Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), screenHeight, screenWidth, player));
        }

        // Update state of each enemy
        for (Enemy enemy : enemyList) {
            enemy.update();
        }

        // Create bullet if ready
        if (bulletReady) {
            bulletList.add(new Bullet(getContext(), player, crosshair));
            bulletReady = false;
        }

        // Update state of each bullet
        for (Bullet bullet : bulletList) {
            bullet.update();
        }

        // Remove the current enemy if it is colliding with the player or a bullet
        // by iterating through all enemies and bullets
        Iterator<Enemy> enemyIterator = enemyList.iterator();
        while (enemyIterator.hasNext()) {
            Circle enemy = enemyIterator.next();

            if (Circle.isColliding(player, enemy)) {
                enemyIterator.remove();

                // Reduce the player's health points
                player.setHealthPoints(player.getCurrentHealthPoints() - 1);

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

                    currentScore += 1;
                    // Stop checking collision of current enemy with the rest of the bullets since it collided with curent bullet
                    break;
                }
            }
        }
    }

    public void pause() {
        gameLoop.stopLoop();
    }
}