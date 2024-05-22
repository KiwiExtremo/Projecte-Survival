package com.example.survivalgame.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.survivalgame.R;

/**
 * The GameOver class is a panel that draws the "Game Over" message on the screen
 */
public class GameOver {

    private final Context context;
    private final int screenWidth;
    private final int screenHeight;

    public GameOver(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void draw(Canvas canvas) {
        float textX = screenWidth / 2;
        float textY = screenHeight / 2;

        Paint paint = new Paint();

        paint.setColor(context.getColor(R.color.game_over));

        float textSize = screenHeight / 10;
        paint.setTextSize(textSize);

        canvas.drawText(context.getString(R.string.game_game_over_text), textX, textY, paint);
    }
}
