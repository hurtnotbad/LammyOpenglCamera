package com.example.lammy.lammyopenglcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int requestCode = 100;
    private String permissions[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    /**
     * 发现在   定义了cameraview后，代码中立即修改glsurfaceview大小，无效，必须先手后修改
     * 或者可以在定义好尺寸后 在setcontentview 才生效
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        cameraView = findViewById(R.id.camera_view);
        requestPermissions();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.requestCode == requestCode) {

            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "获取权限失败", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            onDonePermissionGranted();
        }
    }

    private FboCameraRender fboCameraRender;

    private void onDonePermissionGranted() {

        CameraView cameraView = new CameraView(this);
        GLSurfaceView glSurfaceView = cameraView.findViewById(R.id.glSurfaceView);
        fboCameraRender = new FboCameraRender(this);
        fboCameraRender.setGlSurfaceView(glSurfaceView);
        setContentView(cameraView);

    }

    public void takePhoto(View view){
        fboCameraRender.takePhoto();
    }
    public void changeCamera(View view){
        fboCameraRender.changeCamera();
    }
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onDonePermissionGranted();
        } else {
            for (String permission : permissions) {
                int result = checkSelfPermission(permission);
                if (result == PackageManager.PERMISSION_DENIED) {
                    Log.e("lammy camera", "无权限" + permission);
                    // 没有权限就申请
                    requestPermissions(permissions, requestCode);
                    return;
                }
            }
            onDonePermissionGranted();
        }
    }


}


