package com.example.survivalgame.gameobject;

import android.content.Context;

public class NonPlayerCharacter extends Circle {

    public NonPlayerCharacter(Context context, int color, double positionX, double positionY, double radius) {
        super(context, color, positionX, positionY, radius);
    }

    @Override
    public void update() {

    }
}
