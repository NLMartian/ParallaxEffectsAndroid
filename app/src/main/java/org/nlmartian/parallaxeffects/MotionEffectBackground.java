package org.nlmartian.parallaxeffects;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Created by ctrip on 14-4-24.
 */
public class MotionEffectBackground extends FrameLayout implements SensorEventListener {

    private SensorManager mSensorManager;
    private final Handler mHandler;
    private ImageView mImageView;

    private float mMaxOffset = 45;
    private float mMovedOffsetX;
    private float mMovedOffsetY;

    public MotionEffectBackground(Context context) {
        this(context, null);
    }

    public MotionEffectBackground(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MotionEffectBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler = new Handler();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerGravitySensorListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        mSensorManager.unregisterListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.view_motioneffect, this);
        mImageView = (ImageView) view.findViewById(R.id.image);
    }

    public void registerGravitySensorListener() {
        if (mSensorManager != null) {
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        }
        Sensor gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void setResourceId(int id) {
        mImageView.setImageResource(id);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double gravityFactor = 0.8;
        float gX, gY, gZ;
        gX = sensorEvent.values[0];
        gY = sensorEvent.values[1];
        gZ = sensorEvent.values[2];
        double roll = Math.atan2(gX, gZ) * 180 / Math.PI;
        double pitch = Math.atan2(gY, Math.sqrt(gX * gX + gZ * gZ)) * 180 / Math.PI;

        Log.d("ANGLE", "roll:" + roll + " pitch:" + pitch);

        startImageOffsetAnimation(roll, pitch);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void startImageOffsetAnimation(double roll, double pitch) {
        float offsetX = calculateOffset(roll);
        mImageView.setTranslationX(offsetX -  mMovedOffsetX);
        Log.d("MOVE-X", (offsetX -  mMovedOffsetX) + "");
        mMovedOffsetX = offsetX;

        float offsetY = calculateOffset(pitch);
        mImageView.setTranslationY(offsetY - mMovedOffsetY);
        Log.d("MOVE-Y", (offsetY -  mMovedOffsetY) + "");
        mMovedOffsetY = offsetY;
    }

    private float calculateOffset(double angle) {
        if (angle > 45) {
            angle = 45;
        } else if (angle < -45) {
            angle = -45;
        }

        float offset = (float) (angle / 45 * mMaxOffset) * (-12f);

        Log.d("OFFSET", offset + "");
        return offset;
    }
}
