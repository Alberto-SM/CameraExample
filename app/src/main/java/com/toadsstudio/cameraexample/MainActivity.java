package com.toadsstudio.cameraexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Camera mCamera;
    private FrameLayout mPreviewContainer;
    private CameraPreview mPreview;

    private final int CAMERA_PERMISSION_REQ_CODE = 1;
    private Boolean cameraPersmissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_main);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this);
        FrameLayout mPreviewContainer = (FrameLayout) findViewById(R.id.camera_preview);
        mPreviewContainer.addView(mPreview);
        //

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCameraPermission();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume(){
      super.onResume();
      Log.d(TAG, "onResume");
      if (cameraPersmissionGranted) initializeCamera();
    }


    // region Camera
    private void initializeCamera(){
        mCamera = getRearCameraInstance();
        mPreview.setCamera(mCamera);

    }

    public Camera getRearCameraInstance(){
        Camera camera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int nCameras = Camera.getNumberOfCameras();
        for (int i=0; i<nCameras; i++){
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) continue;
            try {
                camera = Camera.open(i);
                Log.d(TAG, "getRearCameraInstance: Cámara trasera abierta satisfactoriamente.");
            } catch (Exception e){
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return camera; // returns null if camera is unavailable
    }


    // endregion

    //region Permissions
    private void requestCameraPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ_CODE);
        } else{
            cameraPersmissionGranted = true;
            Log.d(TAG, "requestCameraPermission: Permiso de cámara aceptado.");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPersmissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: Permiso de cámara aceptado.");
                } else{
                    Toast.makeText(this, "Por favor, otorgue el permiso de acceso a la cámara y reinicie la aplicación.", Toast.LENGTH_LONG).show();
                }

        }

    }
    //endregion

}
