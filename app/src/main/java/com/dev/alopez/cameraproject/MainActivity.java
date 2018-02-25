package com.dev.alopez.cameraproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private BroadcastReceiver broadcastReceiver;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private camera cam;

    CameraManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        takePictureButton = (Button) findViewById(R.id.btn_takepicture);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setup();

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureButton.setText(getResources().getString(R.string.capturing));
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 15 * 60000, pendingIntent);
            }
        });

        //Create cam manager
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // Orientation
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        cam = new camera(this, textureView, manager, (BatteryManager)getSystemService(BATTERY_SERVICE), rotation, TAG);

        assert takePictureButton != null;
    }

    private void setup()
    {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try{
                    //unlockScreen();  //
                    Thread.sleep(2000);
                    cam.takePicture();
                }catch (Exception e){
                    Log.e("ERROR: ", e.getMessage());
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(this.getPackageName()));
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this.getPackageName()), 0);
        alarmManager = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE));
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            cam.openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if(manager != null){
                cam.closeCamera();
            }
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        cam.startBackgroundThread();
        if (textureView.isAvailable()) {
            cam.openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        cam.closeCamera();
        cam.stopBackgroundThread();
    }
}