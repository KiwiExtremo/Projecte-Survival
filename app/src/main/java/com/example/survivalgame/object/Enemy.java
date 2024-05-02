package com.example.survivalgame.object;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.survivalgame.GameLoop;
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
    private Player player;

    public Enemy(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.enemy), positionX, positionY, radius);
        this.player = player;
    }

    /**
     * Enemy is an overload constructor used for spawning enemies in random locations
     * @param context context of the invoking activity
     * @param player player object the enemy will chase
     */
    public Enemy(Context context, Player player) {
        // TODO change 1000 to max screen size - 20% margins
        // TODO check that the spawn point is not too close to the player
        super(
                context,
                ContextCompat.getColor(context, R.color.enemy),
                Math.random() * 1000,
                Math.random() * 1000,
                ENEMY_RADIUS
        );

        this.player = player;
    }

    /**
     * readyToSpawn checks if a new enemy should spawn based on the decided max number of enemies,
     * and the rate of spawns/minute.
     * @return
     */
    public static boolean readyToSpawn() {
        if (updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true;
        } else
            updatesUntilNextSpawn--;
        return false;
    }

    @Override
    public void update() {
        // TODO update velocity of enemy so it chases after players
        // Calculate vector from enemy to player
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        // Calculate absolute distance between enemy and player
        double distanteToPlayer = GameObject.getDistanceBetweenObjects(this, player);

        // Calculate direction from enemy to player
        double directionX = distanceToPlayerX / distanteToPlayer;
        double directionY = distanceToPlayerY / distanteToPlayer;

        // Set velocity in direction of player
        if (distanteToPlayer > 0) {
            velocityX = directionX * MAX_SPEED;
            velocityY = directionY * MAX_SPEED;
        } else {
            velocityX = 0;
            velocityY = 0;
        }

        // Update the position of the enemy
        positionX += velocityX;
        positionY += velocityY;
    }
}
