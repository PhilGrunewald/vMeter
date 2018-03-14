package org.meter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;

public class vMeter extends Activity {
    private int minutesPerPic = 1;
    private static final String TAG = "vLog";
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    Camera camera;
    ScheduledFuture<?> beeperHandle;
    Parameters cameraParameters;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "started");
            setContentView(R.layout.activity_main);
            camera = Camera.open();
            cameraParameters = camera.getParameters();
            cameraParameters.setFlashMode(Parameters.FLASH_MODE_ON);
            //cameraParameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
            // cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParameters);
            takePicsPeriodically(60*minutesPerPic);
        }

    @Override
        protected void onDestroy() {
            super.onDestroy();
        }

    public void takePicsPeriodically(long period) {
        final Runnable beeper = new Runnable() {
            public void run() {
                camera.startPreview();
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        };
        beeperHandle = scheduler.scheduleAtFixedRate(beeper, period, period, TimeUnit.SECONDS);
        camera.stopPreview();
    }

    /* Camera Call backs */
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /** Save jpeg */
    PictureCallback jpegCallback = new PictureCallback() {
        private String fname;
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
            String dt = sdf.format(new Date());
            fname = "/sdcard/DCIM/" + dt + ".jpg";
            Log.d(TAG, "about to save "+fname);
            try {
                outStream = new FileOutputStream(fname);
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
        }	
    };
}
