package com.example.survivalgame.gameobject;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.Utils;
import com.example.survivalgame.gameengine.GameLoop;
import com.example.survivalgame.R;

/**
 * An enemy is a non-playable character that always move in the direction
 * of the player. The Enemy class extends from the Circle, which is
 * an extension of GameObject.
 */
public class Enemy extends Circle {
    private static final double ENEMY_RADIUS = 45;
    public static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND * 0.6;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private static final double SPAWNS_PER_MINUTE = 20;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE / 60;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS / SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;
    private static double timeToUpdateDirection = 0;
    private Player player;

    /**
     * Enemy is an overload constructor used for spawning enemies in random locations, outside of the
     * player's "safe area", but inside of the screen size.
     *
     * @param context context of the invoking activity.
     * @param screenHeight the phone's screen height at runtime.
     * @param screenWidth the phone's screen width at runtime.
     * @param player player object the enemy will chase.
     */
    public Enemy(Context context, int screenHeight, int screenWidth, Player player) {
        super(
                context,
                ContextCompat.getColor(context, R.color.enemy),
                1000,
                1000,
                ENEMY_RADIUS
        );

        int positionX, positionY;

        do {
            positionX = (int) (Math.random() * (screenWidth * 0.8));
            positionY = (int) (Math.random() * (screenHeight * 0.8));

        } while (Utils.getDistanceBetweenPoints(positionX, positionY, player.getPositionX(), player.getPositionY()) < Crosshair.ORBIT_RADIUS);

        this.positionX = positionX;
        this.positionY = positionY;

        this.player = player;
    }

    /**
     * readyToSpawn() checks if a new enemy should spawn based on the rate of spawns/minute.
     *
     * @return true if the enemy is ready to spawn, false otherwise.
     */
    public static boolean readyToSpawn() {
        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else {
            updatesUntilNextSpawn--;
            return false;
        }
    }

    /**
     * update() overrides the super method update(). It updates the enemy position based on its
     * direction. Additionally, every 60 game updates, the enemy will recalculate its direction to
     * chase after the closest player.
     */
    @Override
    public void update() {
        // TODO update velocity of enemy so it chases after closest player (in multiplayer)
        if (timeToUpdateDirection > 0) {
            timeToUpdateDirection--;

        } else {
            // Calculate vector from enemy to player
            double distanceToPlayerX = player.getPositionX() - positionX;
            double distanceToPlayerY = player.getPositionY() - positionY;

            // Calculate absolute distance between enemy and player
            double distanceToPlayer = GameObject.getDistanceBetweenObjects(this, player);

            // Calculate direction from enemy to player
            double directionX = distanceToPlayerX / distanceToPlayer;
            double directionY = distanceToPlayerY / distanceToPlayer;

            // Set velocity in direction of player
            if (distanceToPlayer > 0) {
                velocityX = directionX * MAX_SPEED;
                velocityY = directionY * MAX_SPEED;
            } else {
                velocityX = 0;
                velocityY = 0;
            }

            // Restart the timer to redirect the enemy
            timeToUpdateDirection += GameLoop.MAX_UPS;
        }

        // Update the position of the enemy
        positionX += velocityX;
        positionY += velocityY;
    }
}
