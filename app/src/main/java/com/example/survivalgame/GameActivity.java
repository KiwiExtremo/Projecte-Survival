package com.example.survivalgame;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.survivalgame.gameengine.Game;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = new Game(this);
        setContentView(game);

        startBGMusic();
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

    private void startBGMusic() {
        mp = MediaPlayer.create(GameActivity.this, R.raw.synthwave_bg_music);

        mp.setLooping(true);
        mp.start();
    }
    private void showDialogGameOver(int endCode, int score) {
        runOnUiThread(() -> {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_game_over_title));

            if (endCode == 0) {
                builder.setMessage(getString(R.string.dialog_game_over_body_win, score));

            } else {
                builder.setMessage(getString(R.string.dialog_game_over_body_lose, score));
            }
            // add the buttons
            builder.setPositiveButton(getString(R.string.dialog_game_over_positive), (dialog, which) -> {
                game.getGameLoop().setGameFinished(true);
                finish();
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        });
    }

    private void showDialogGiveUp() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_give_up_title));
        builder.setMessage(getString(R.string.dialog_give_up_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_give_up_positive), (dialog, which) -> {
            // TODO save score to Firebase
            showDialogGameOver(0, 0);
        });

        builder.setNegativeButton(getString(R.string.dialog_give_up_negative), (dialog, which) -> {
            // Resume the game state
            game.getGameLoop().setRunning(true);
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(dialog1 -> {
            // Do the same as negative button you touch outside of dialog bounds
            game.getGameLoop().setRunning(true);
        });
    }

    @Override
    public void onBackPressed() {
        // Avoid closing the game when pressing the back button by disabling the call to the superclass.
        game.getGameLoop().setRunning(false);
        showDialogGiveUp();
    }
}
