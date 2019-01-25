package com.example.hp.battleship;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class MySensor extends Service implements SensorEventListener {

    private final IBinder mBinder = new LocalBinder();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnometer;
    private float[] mAccelerometerReading= new float[3];
    private float[] mMagnometerReading= new float[3];
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientationAngles = new float[3];
    private float mAzimut, mPitch, mRoll;
    private ArrayList<DevicePositionChangedListener> mPositionListeners = new ArrayList<>();
    private DevicePositionChangedListener tempListener;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void initSensorService(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (mAccelerometer != null && mMagnometer != null){
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mMagnometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            showDeviceNotSupportSensorsMessage();
        }
    }

    public void showDeviceNotSupportSensorsMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
        builder.setTitle("Sensor Settings");
        builder.setMessage("Device Not Support Sensor Service");
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mAccelerometerReading, 0, event.values.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mMagnometerReading, 0, event.values.length);
                break;
            default:
                return;
        }
        if (mAccelerometerReading != null && mMagnometerReading != null) {

            boolean success = SensorManager.getRotationMatrix(mRotationMatrix, null  , mAccelerometerReading, mMagnometerReading);
            if (success) {
                SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
                mAzimut = mOrientationAngles[0];
                mPitch = mOrientationAngles[1];
                mRoll = mOrientationAngles[2];
                if (mPitch > 0){
                    for (int i = 0; i < mPositionListeners.size(); i++)
                        if (mPositionListeners.get(i) != null) {
                            mPositionListeners.get(i).devicePositionChanged();
                            tempListener = mPositionListeners.get(i);
                            mPositionListeners.remove(i);
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    mPositionListeners.add(tempListener);
                                }
                            }.start();
                        }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public class LocalBinder extends Binder {

        public MySensor getService() {
            return MySensor.this;
        }

        public void registerListener(DevicePositionChangedListener listener){
            mPositionListeners.add(listener);
        }

        public void removeListeners(){
            mPositionListeners.clear();

        }
    }

    public interface DevicePositionChangedListener {
        void devicePositionChanged();
    }
}
