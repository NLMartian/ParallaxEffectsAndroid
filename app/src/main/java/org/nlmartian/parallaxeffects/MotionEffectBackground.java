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
    private final Handler mHandler;
    private ImageView mImageView;

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
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.view_motioneffect, this);
        mImageView = (ImageView) view.findViewById(R.id.image);
    }

    private void registerGravitySensorListener() {
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
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
        if (Math.abs(roll) > Math.abs(pitch)) {
            mImageView.setTranslationX(calculateOffset(roll) - mImageView.getTranslationX());
        } else {
            mImageView.setTranslationY(calculateOffset(pitch) - mImageView.getTranslationY());
        }
    }

    private float calculateOffset(double angle) {
        float offset = (float) (angle / 10 + 2);
        if (offset > 20) {
            offset = 20;
        } else if (offset < -20) {
            offset = -20;
        }

        Log.d("OFFSET", offset + "");
        return offset;
    }
}
