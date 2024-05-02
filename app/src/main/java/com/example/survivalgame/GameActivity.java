package com.example.survivalgame;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.gameengine.Game;

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Game(this));

        // TODO fix this
        // Set window to full screen, hiding the status' bars.
//        Window window = getWindow();
//
//        WindowInsetsController insetsController = window.getInsetsController();
//
//        if (insetsController != null) {
//            insetsController.hide(WindowInsets.Type.statusBars());
//            insetsController.setSystemBarsBehavior(BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//        }
    }
}
