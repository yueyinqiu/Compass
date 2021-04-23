package io.github.yueyinqiu.compass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.lqr.picselect.LQRPhotoSelectUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements Compass.CompassListener
{
    private Compass compass;
    private ImageView arrowView;
    private float currentAzimuth;

    private void MakeToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //region image
    private boolean setupImage()
    {
        String imageUri;
        try (FileInputStream inputStream = openFileInput("imagePath"))
        {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            imageUri = bufferedReader.readLine();
        }
        catch (FileNotFoundException ignore)
        {
            imageUri = null;
        }
        catch (IOException e)
        {
            MakeToast("44162282: Cannot read settings files.");
            return false;
        }
        if(imageUri == null)
            arrowView.setImageResource(R.mipmap.skin_default);
        else
        {
            Uri uri = Uri.parse(imageUri);
            arrowView.setImageURI(uri);
        }
        return true;
    }

    LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

    private boolean setupPhotoSelectUtils()
    {
        this.mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(
                this, (outputFile, outputUri) -> {
            arrowView.setImageURI(outputUri);
            try (FileOutputStream outputStream = openFileOutput(
                    "imagePath", Context.MODE_PRIVATE))
            {
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(outputUri.toString());
                bufferedWriter.flush();
            }
            catch (IOException e)
            {
                MakeToast("ecf11c80: Cannot write settings files.");
            }
        }, false);
        return true;
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrowView = findViewById(R.id.imageView);

        if (!setupTitleComponent() ||
                !setupImage() ||
                !setupImageLocking() ||
                !setupCompass() ||
                !setupPhotoSelectUtils())
            this.finish();
    }

    //region menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.view_repo)
        {
            Uri uri = Uri.parse("https://github.com/yueyinqiu/Compass");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        else if (id == R.id.select_image)
        {
            final String[] t = new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    };

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                requestPermissions(t, 233);
            mLqrPhotoSelectUtils.selectPhoto();
        }
        return true;
    }
    //endregion

    //region compass
    private boolean setupCompass()
    {
        try
        {
            compass = new Compass(this);
        }
        catch (NoSuchSensorException e)
        {
            MakeToast("Not supported for this device.");
            return false;
        }
        compass.setListener(this);
        return true;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (!imageLocked)
            compass.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!imageLocked)
            compass.stop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!imageLocked)
            compass.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (!imageLocked)
            compass.stop();
    }

    @Override
    public void onNewAzimuth(double azimuth)
    {
        float azi = (float) azimuth;
        Animation an = new RotateAnimation(-currentAzimuth, -azi,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = azi;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    @Override
    public void onAccelerationAccuracyChanged(int accuracy)
    {
        titleComponents.accelerationAccuracy = accuracy;
        refreshTitle();
    }

    @Override
    public void onMagnetismAccuracyChanged(int accuracy)
    {
        titleComponents.magnetismAccuracy = accuracy;
        refreshTitle();
    }
    //endregion

    //region title
    TitleComponents titleComponents;
    private void refreshTitle()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(titleComponents.title);
        if(titleComponents.imageLocked)
        {
            builder.append(" - Locked");
        }
        else
        {
            builder.append(" - G");
            builder.append(titleComponents.accelerationAccuracy + 1);
            builder.append("/4 M");
            builder.append(titleComponents.magnetismAccuracy + 1);
            builder.append("/4");
        }
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(builder);
    }
    private boolean setupTitleComponent()
    {
        titleComponents = new TitleComponents();
        titleComponents.title = getString(R.string.app_name);
        titleComponents.accelerationAccuracy = -1;
        titleComponents.magnetismAccuracy = -1;
        titleComponents.imageLocked = false;
        refreshTitle();
        return true;
    }
    //endregion

    //region lock
    private boolean imageLocked;
    private boolean setupImageLocking()
    {
        imageLocked = false;
        return true;
    }
    public void triggerLocking(View view)
    {
        imageLocked = !imageLocked;
        if(imageLocked)
            compass.stop();
        else
            compass.start();

        titleComponents.imageLocked = imageLocked;
        refreshTitle();
    }
    //endregion
}
