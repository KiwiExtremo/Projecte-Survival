package com.example.survivalgame;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.gameengine.Game;

public class GameActivity extends AppCompatActivity {
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new Game(this);
        setContentView(game);

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        game.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
