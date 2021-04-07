package io.github.yueyinqiu.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener
{
    public interface CompassListener
    {
        void onNewAzimuth(double azimuth);
        void onGravityAccuracyChanged(int accuracy);
        void onMagnetismAccuracyChanged(int accuracy);
    }

    private CompassListener listener;

    private final SensorManager sensorManager;
    private final Sensor gravitySensor;
    private final Sensor magnetismSensor;

    public Compass(Context context) throws NoSuchSensorException
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetismSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (gravitySensor == null || magnetismSensor == null)
        {
            throw new NoSuchSensorException();
        }
    }

    public void start()
    {
        sensorManager.registerListener(
                this, gravitySensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(
                this, magnetismSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    public void setListener(CompassListener l)
    {
        listener = l;
    }

    private final float[] mGravity = new float[3];
    private final float[] mGeomagnetic = new float[3];

    private int gravityAccuracy = -1;
    private int magnetismAccuracy = -1;

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (listener == null)
            return;

        final float alpha = 0.97f;

        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                if(event.accuracy != gravityAccuracy)
                {
                    gravityAccuracy = event.accuracy;
                    listener.onGravityAccuracyChanged(gravityAccuracy);
                }
                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if(event.accuracy != magnetismAccuracy)
                {
                    magnetismAccuracy = event.accuracy;
                    listener.onMagnetismAccuracyChanged(magnetismAccuracy);
                }
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                break;
            default:
                return;
        }

        float[] r = new float[9];
        float[] i = new float[9];
        boolean success = SensorManager.getRotationMatrix(
                r, i, mGravity, mGeomagnetic);
        if (success)
        {
            float[] orientation = new float[3];
            SensorManager.getOrientation(r, orientation);
            double azimuth = Math.toDegrees(orientation[0]);
            azimuth = (azimuth + 360) % 360;
            listener.onNewAzimuth(azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
}