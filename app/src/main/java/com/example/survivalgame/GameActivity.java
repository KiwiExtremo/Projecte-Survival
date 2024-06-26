package com.example.survivalgame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.example.survivalgame.gameengine.GameView;
import com.example.survivalgame.gamepanel.TutorialView;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private TextView tvLeftTutorial, tvRightTutorial, tvHPTutorial, tvScoreTutorial, tvPauseTutorial, tvDoneTutorial;
    private Button bNext;
    private Guideline guideLeft, guideMid, guideRight, guideMidLeft, guideMidRight, guideTop, guideBottom;
    private ConstraintLayout tutorialLayout;
    private FrameLayout parentLayout;
    private TutorialView tutorialView;
    private MediaPlayer mp;
    private int currentTutorial = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        gameView = findViewById(R.id.gameView);

        fetchViewsFromTutorial();
        checkToShowTutorial();

        startBGMusic();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mp.setLooping(true);
        mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameView.pause();

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
        }
    }

    private void startBGMusic() {
        mp = MediaPlayer.create(GameActivity.this, R.raw.synthwave_bg_music);

        mp.setLooping(true);
        mp.start();
    }
    public void showDialogGameOver(int endCode, int score) {
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
                gameView.getGameLoop().setGameFinished(true);
                finish();
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        });
    }

    private void showDialogPause() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_pause_title));
        builder.setMessage(getString(R.string.dialog_pause_body));

        // add the buttons
        builder.setPositiveButton(getString(R.string.dialog_pause_positive), (dialog, which) -> {
            // Resume the game state
            gameView.getGameLoop().setRunning(true);
        });

        builder.setNeutralButton(getString(R.string.dialog_pause_neutral), (dialog, which) -> {
            // Finish the run and save score
            // TODO save score to Firebase
            showDialogGameOver(0, 0);
        });

        builder.setNegativeButton(getString(R.string.dialog_pause_negative), (dialog, which) -> {
            // Display the game tutorial
            bOnClickShowTutorial();
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(dialog1 -> {
            // Do the same as negative button you touch outside of dialog bounds
            gameView.getGameLoop().setRunning(true);
        });
    }

    public void bOnClickNextTutorial(View view) {
        ConstraintLayout.LayoutParams joystickParams = (ConstraintLayout.LayoutParams) tvLeftTutorial.getLayoutParams();

        switch (currentTutorial) {
            // Show movement joystick tutorial
            case 0:
                tvLeftTutorial.setText(getString(R.string.text_view_tutorial_movement_joystick));

                // Set the position of the cutout rectangle
                tutorialView.setPositionToDraw(TutorialView.JOYSTICK);
                currentTutorial++;
                break;

            // Show movement action tutorial
            case 1:
                tvLeftTutorial.setText(getString(R.string.text_view_tutorial_movement));
                currentTutorial++;
                break;

            // Show aiming joystick tutorial
            case 2:
                // Switch tutorials shown
                tvLeftTutorial.setVisibility(View.INVISIBLE);
                tvRightTutorial.setVisibility(View.VISIBLE);

                // Move the tutorial cutout rectangle
                tutorialView.setDrawOnLeft(false);
                tutorialView.invalidate();

                currentTutorial++;
                break;

            // Show aiming action tutorial
            case 3:
                tvRightTutorial.setText(getString(R.string.text_view_tutorial_shooting));
                currentTutorial++;
                break;

            // Show shooting tutorial
            case 4:
                tvRightTutorial.setText(getString(R.string.text_view_tutorial_releasing));
                currentTutorial++;
                break;

            // Show healthbar tutorial
            case 5:
                // Switch tutorials shown
                tvRightTutorial.setVisibility(View.INVISIBLE);
                tvHPTutorial.setVisibility(View.VISIBLE);

                // Set the position of the cutout rectangle
                tutorialView.setPositionToDraw(TutorialView.HEALTHBAR);
                tutorialView.invalidate();

                currentTutorial++;
                break;

            // Show score tutorial
            case 6:
                // Switch tutorials shown
                tvHPTutorial.setVisibility(View.INVISIBLE);
                tvScoreTutorial.setVisibility(View.VISIBLE);

                // Set the position of the cutout rectangle
                tutorialView.setPositionToDraw(TutorialView.SCORE);
                tutorialView.invalidate();

                currentTutorial++;
                break;

            // Show pausing tutorial
            case 7:
                // Switch tutorials shown
                tvScoreTutorial.setVisibility(View.INVISIBLE);
                tvPauseTutorial.setVisibility(View.VISIBLE);

                // Set the position of the cutout rectangle
                tutorialView.setPositionToDraw(TutorialView.DONE);
                tutorialView.invalidate();

                currentTutorial++;
                break;

            // Show done message
            case 8:
                // Switch tutorials shown
                tvPauseTutorial.setVisibility(View.INVISIBLE);
                tvDoneTutorial.setVisibility(View.VISIBLE);

                bNext.setText(getString(R.string.button_tutorial_done));
                currentTutorial = -1;
                break;

            // close tutorial view
            case -1:
                parentLayout.removeView(tutorialLayout);

                // Start the game
                gameView.getGameLoop().setRunning(true);

                // update sharedPrefs to not show again
                break;

            default:
                // The user shouldn't ever end up here, so we do nothing
                break;
        }

    }

    private void checkToShowTutorial() {
        // TODO fetch from sharedprefs whether or not to show the tutorial
        boolean showTutorial = true;

        if (showTutorial) {
            // Do nothing, as the tutorial layout is shown by default
        } else {
            // Remove the tutorial layout
            parentLayout.removeView(tutorialLayout);

            // Start the game
            gameView.getGameLoop().setRunning(true);
        }
    }

    private void bOnClickShowTutorial() {
        // Add the tutorial layout to the parent layout again
        parentLayout.addView(tutorialLayout);

        // Restart the tutorial information
        currentTutorial = 0;
        tvLeftTutorial.setText(getString(R.string.text_view_tutorial_movement_joystick));
        tvDoneTutorial.setVisibility(View.INVISIBLE);
        tvLeftTutorial.setVisibility(View.VISIBLE);

        tutorialView.setPositionToDraw(TutorialView.JOYSTICK);
        tutorialView.setDrawOnLeft(true);
        tutorialView.invalidate();
    }

    private void fetchViewsFromTutorial() {
        tvLeftTutorial = findViewById(R.id.tvJoystickLeft);
        tvRightTutorial = findViewById(R.id.tvJoystickRight);
        tvHPTutorial = findViewById(R.id.tvHealthbar);
        tvScoreTutorial = findViewById(R.id.tvScore);
        tvPauseTutorial = findViewById(R.id.tvPause);
        tvDoneTutorial = findViewById(R.id.tvDone);

        tutorialView = findViewById(R.id.tutorialView);
        bNext = findViewById(R.id.bNext);

        guideLeft = findViewById(R.id.guidelineLeft);
        guideMid = findViewById(R.id.guidelineMiddle);
        guideRight = findViewById(R.id.guidelineRight);
        guideMidLeft = findViewById(R.id.guidelineMidLeft);
        guideMidRight = findViewById(R.id.guidelineMidRight);
        guideTop = findViewById(R.id.guidelineTop);
        guideBottom = findViewById(R.id.guidelineBottom);

        tutorialLayout = findViewById(R.id.tutorialLayout);
        parentLayout = findViewById(R.id.parentLayout);
    }

    @Override
    public void onBackPressed() {
        // Avoid closing the game when pressing the back button by disabling the call to the superclass.
        gameView.getGameLoop().setRunning(false);
        showDialogPause();
    }
}