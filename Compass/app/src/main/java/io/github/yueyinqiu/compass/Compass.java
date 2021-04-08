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
        void onAccelerationAccuracyChanged(int accuracy);
        void onMagnetismAccuracyChanged(int accuracy);
    }

    private CompassListener listener;

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final Sensor magnetismSensor;

    public Compass(Context context) throws NoSuchSensorException
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetismSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (accelerometer == null || magnetismSensor == null)
        {
            throw new NoSuchSensorException();
        }
    }

    public void start()
    {
        sensorManager.registerListener(
                this, accelerometer,
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

    private final float[] acceleration = new float[3];
    private final float[] magnetism = new float[3];

    private int accelerationAccuracy = -1;
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
                if(event.accuracy != accelerationAccuracy)
                {
                    accelerationAccuracy = event.accuracy;
                    listener.onAccelerationAccuracyChanged(accelerationAccuracy);
                }
                acceleration[0] = alpha * acceleration[0] + (1 - alpha)
                        * event.values[0];
                acceleration[1] = alpha * acceleration[1] + (1 - alpha)
                        * event.values[1];
                acceleration[2] = alpha * acceleration[2] + (1 - alpha)
                        * event.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if(event.accuracy != magnetismAccuracy)
                {
                    magnetismAccuracy = event.accuracy;
                    listener.onMagnetismAccuracyChanged(magnetismAccuracy);
                }
                magnetism[0] = alpha * magnetism[0] + (1 - alpha)
                        * event.values[0];
                magnetism[1] = alpha * magnetism[1] + (1 - alpha)
                        * event.values[1];
                magnetism[2] = alpha * magnetism[2] + (1 - alpha)
                        * event.values[2];
                break;
            default:
                return;
        }

        float[] r = new float[9];
        float[] i = new float[9];
        boolean success = SensorManager.getRotationMatrix(
                r, i, acceleration, magnetism);
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