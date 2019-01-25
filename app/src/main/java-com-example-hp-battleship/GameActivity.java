package com.example.hp.battleship;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.battleship.Logic.Game;
import com.example.hp.battleship.Logic.Point;

public class GameActivity extends AppCompatActivity implements MySensor.DevicePositionChangedListener {

    public final static String THE_WINNER = "winner";
    public final static String THE_DIFFICULTY = "difficulty";
    public final static String THE_SCORE = "score";
    public final static String THE_LOCATION_KEY = "location";
    public final static String THE_BUNDLE = "bundle";

    private final int COMPUTER_GRID_TILE_SIZE = 90;
    private final int PLAYER_GRID_TILE_SIZE = 60;

    private Game mGame;
    private GridView playerGrid, computerGrid;
    private TileAdapter computerAdapter, playerAdapter;
    private int difficulty, state, score;
    private boolean doOnce = true, gameIsOver = false;
    private String the_winner;

    private Intent serviceIntent;
    private MySensor.LocalBinder mBinder;
    private boolean isBound = false;

    private double[] mCoordinates;
    private LocationManager mLocationManager;
    LocationListener mLocationListener;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MySensor.LocalBinder) service;
            mBinder.registerListener(GameActivity.this);
            isBound = true;
            mBinder.getService().initSensorService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mCoordinates = new double[2];

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (location != null)
                    setCoordinates(location.getLatitude(), location.getLongitude());
                else
                    setCoordinates(0, 0);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        checkLocationPermission();

        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }

        else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        ((ProgressBar) findViewById(R.id.progresss_bar)).setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        Bundle extras = intent.getBundleExtra(MainActivity.THE_BUNDLE);

        if (extras != null) {
            difficulty = extras.getInt(MainActivity.THE_DIFFICULTY);
        }

        mGame = new Game(difficulty);

        computerGrid = (GridView) findViewById(R.id.enemy_grid);
        playerGrid = (GridView) findViewById(R.id.player_grid);

        computerAdapter = new TileAdapter(getApplicationContext(), mGame.getComputerBoard(), COMPUTER_GRID_TILE_SIZE);
        playerAdapter = new TileAdapter(getApplicationContext(), mGame.getPlayerBoard(), PLAYER_GRID_TILE_SIZE);

        computerGrid.setAdapter(computerAdapter);
        playerGrid.setAdapter(playerAdapter);

        computerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mGame.isPlayerTurn() && gameIsOver == false) {
                    int size = mGame.getPlayerBoard().getBoardSize();
                    int row = position / size;
                    int col = position % size;
                    Point point = new Point(row, col);
                    state = mGame.playTile(point, mGame.getComputerBoard());
                    if (mGame.isPlayerTurn())
                        return;
                }
                ((TileAdapter) computerGrid.getAdapter()).notifyDataSetChanged();
                if (mGame.checkIfWin(mGame.getComputerBoard())) {
                    the_winner = "player";
                    endTheGame(the_winner);
                }
                if (!mGame.isPlayerTurn() && gameIsOver == false) {
                    if (doOnce) {
                        doOnce = false;
                        ((TextView) findViewById(R.id.player_turn)).setText(R.string.computer_turn);
                        ((ProgressBar) findViewById(R.id.progresss_bar)).setVisibility(View.VISIBLE);
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int state = mGame.computerPlay();
                                        ((TileAdapter) playerGrid.getAdapter()).notifyDataSetChanged();
                                        ((ProgressBar) findViewById(R.id.progresss_bar)).setVisibility(View.INVISIBLE);
                                        if (state == 2) {
                                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            v.vibrate(500);
                                        }
                                        if (mGame.checkIfWin(mGame.getPlayerBoard())) {
                                            the_winner = "computer";
                                            endTheGame(the_winner);
                                        }
                                        ((TextView) findViewById(R.id.player_turn)).setText(R.string.player_turn);
                                        doOnce = true;
                                    }
                                });
                            }
                        });
                        t.start();
                    }
                }
            }
        });
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null)
                    setCoordinates(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceIntent = new Intent(this, MySensor.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void setCoordinates(double lat, double lng) {
        mCoordinates[0] = lat;
        mCoordinates[1] = lng;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound){
            mBinder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isBound){
            mBinder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isBound){
            mBinder.removeListeners();
            this.stopService(serviceIntent);
            this.unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isBound) {
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    private void endTheGame(String the_winner) {
        score = calculateScore();
        gameIsOver = true;
        Intent intent = new Intent(GameActivity.this, EndGameActivity.class);
        Bundle mBundle = new Bundle();

        mBundle.putInt(THE_DIFFICULTY, difficulty);
        mBundle.putString(THE_WINNER, the_winner);
        mBundle.putInt(THE_SCORE, score);

        mBundle.putDoubleArray(THE_LOCATION_KEY, mCoordinates);

        intent.putExtra(THE_BUNDLE, mBundle);
        startActivity(intent);

        finish();
    }

    private int calculateScore() {
        return mGame.getComputerBoard().getNumOfTilesLeft() * 100;
    }

    @Override
    public void devicePositionChanged() {
        if (mGame.isPlayerTurn() && gameIsOver == false) {
            Toast.makeText(GameActivity.this, "Oh No!!! Keep Your Phone Still!", Toast.LENGTH_LONG).show();
            mGame.getComputerBoard().ChangeAllCompShipsLocations();
            ((TileAdapter) computerGrid.getAdapter()).notifyDataSetChanged();
        }
    }

//    private void endTheGame(String the_winner) {
//        gameIsOver = true;
//        Intent intent = new Intent(GameActivity.this, EndGameActivity.class);
//        Bundle mBundle = new Bundle();
//
//        mBundle.putInt(THE_DIFFICULTY, difficulty);
//        mBundle.putString(THE_WINNER, the_winner);
//
//        intent.putExtra(THE_BUNDLE, mBundle);
//        startActivity(intent);
//
//        finish();
//    }

}

