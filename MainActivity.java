package com.example.hp.battleship;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;


public class MainActivity extends AppCompatActivity {
    public static final String[] DIFFICULTY = {"Easy", "Medium", "Hard"};
    public final static String THE_BUNDLE = "bundle";
    public final static String THE_DIFFICULTY = "difficulty";

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private NumberPicker mPicker = null;
    private ImageButton mStartButton;
    private ImageButton mInstructionsButton;
    private int theDifficulty;
    private Bundle mBundle;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        mPicker = (NumberPicker) findViewById(R.id.difficultyPickerPicker);
        mPicker.setMinValue(0);
        mPicker.setMaxValue(DIFFICULTY.length - 1);
        mPicker.setDisplayedValues(DIFFICULTY);

        mStartButton = (ImageButton) findViewById(R.id.startButton);
        mInstructionsButton = (ImageButton) findViewById(R.id.instructions_button);
    }

    public void startGame(View view) {
        theDifficulty = mPicker.getValue();
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        mBundle = new Bundle();
        mBundle.putInt(THE_DIFFICULTY, theDifficulty);
        intent.putExtra(THE_BUNDLE , mBundle);
        startActivity(intent);
    }

    public void goToInstructions(View view){
        Intent intent = new Intent(MainActivity.this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void showHighScore(View view){
        Intent intent = new Intent(MainActivity.this, ScoresListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}








