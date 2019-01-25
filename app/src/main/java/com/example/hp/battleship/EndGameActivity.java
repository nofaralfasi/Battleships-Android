package com.example.hp.battleship;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v7.app.AlertDialog;


public class EndGameActivity extends AppCompatActivity {
    private String winningPlayer, difficultyName, userName;
    private ImageView imageViewTop, imageViewDown;
    private EditText input;
    private double[] mCoordinates;

    final static int HIGH_SCORE_MIN_BAR = 5500;

    private int difficulty,score;

    private int worstHighScore;
    private ScoreRecord highScoreRecord;
    private DataBaseHelper mDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        mCoordinates = new double[2];

        imageViewTop =  (ImageView)findViewById(R.id.endGamePictureTop);
        imageViewDown = (ImageView)findViewById(R.id.endGamePictureDown);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.endGame);

        Bundle extras = getIntent().getBundleExtra(GameActivity.THE_BUNDLE);

        if (extras!= null){
            difficulty = extras.getInt(GameActivity.THE_DIFFICULTY);
            winningPlayer = extras.getString(GameActivity.THE_WINNER);
            score = extras.getInt(GameActivity.THE_SCORE);
            mCoordinates = extras.getDoubleArray(GameActivity.THE_LOCATION_KEY);
        }

        switch (winningPlayer) {
            case "player":
                relativeLayout.setBackgroundResource(R.drawable.win);
                imageViewTop.setImageResource(R.drawable.win_text);
                imageViewDown.setImageResource(R.drawable.tressure);
                setDifficultyToString(difficulty);
                Log.i("Win mode:", "score: " + score);

                if (score > HIGH_SCORE_MIN_BAR) {

                    mDBHandler = DataBaseHelper.getInstance(this);

                    if (mDBHandler.isTableFull(difficultyName)){
                        worstHighScore = mDBHandler.getWorstRecord(difficultyName).getScore();
                        if (score > worstHighScore){
                            //delete worst record in table
                            mDBHandler.deleteLowestRecord(difficultyName);
                        }
                    }
                    new CountDownTimer(1500, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            createNewRecordDialog();
                        }
                    }.start();
                }
                break;
            case "computer":
                relativeLayout.setBackgroundResource(R.drawable.rain);
                imageViewTop.setImageResource(R.drawable.failed);
                imageViewDown.setImageResource(R.drawable.dead);
                break;
        }
    }

    public void createNewRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EndGameActivity.this);
        builder.setTitle("NEW RECORD!");
        builder.setMessage("Congrats! you set a new Record!");

        input = new EditText(this);
        input.setHint(R.string.new_record_placeholder);

        builder.setView(input);
        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userName = input.getText().toString();

                if (userName.length()!=0) {
                    highScoreRecord = new ScoreRecord(userName, score, mCoordinates[0], mCoordinates[1]);
                    mDBHandler.insertRecord(difficultyName, highScoreRecord);
                }
                goToHighScoreActivity();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToHighScoreActivity() {
        Intent intent = new Intent(EndGameActivity.this, ScoresListActivity.class);
        startActivity(intent);
    }

    public void rematchGame(View view){
        Intent intent = new Intent(EndGameActivity.this, GameActivity.class);
        intent.putExtra(Intent.EXTRA_INDEX, difficulty);
        startActivity(intent);
        finish();
    }

    public void goToMainMenu(View view){
        Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setDifficultyToString(int difficulty) {
        switch (difficulty){
            case 0:
                this.difficultyName = "EASY";
                break;
            case 1:
                this.difficultyName = "MEDIUM";
                break;
            case 2:
                this.difficultyName = "HARD";
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public String getDifficultyName() {
        return difficultyName;
    }
}
