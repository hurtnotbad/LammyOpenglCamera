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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private static final int requestCode = 100;
    private String permissions[] = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    /**
     * 发现在   定义了cameraview后，代码中立即修改glsurfaceview大小，无效，必须先手后修改
     * 或者可以在定义好尺寸后 在setcontentview 才生效，因此在camera_view 中写 相机界面
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
    private CameraView cameraView;
    private void onDonePermissionGranted() {

        cameraView = new CameraView(this);
        GLSurfaceView glSurfaceView = cameraView.findViewById(R.id.glSurfaceView);
        fboCameraRender = new FboCameraRender(this);
        fboCameraRender.setGlSurfaceView(glSurfaceView);
        setContentView(cameraView);
        initView();

    }

    public void takePhoto(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int second = (int)delayTime/1000;
                    for(int i = 0; i < second ; i ++){
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeView.setText((second - finalI) +"");
                            }
                        });
                        Thread.sleep(1000);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeView.setText("");
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fboCameraRender.takePhoto();
            }
        }).start();

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



    long delayTime = 0;
    int chooseColor = 0x5CACEEff;
    int unChooseColor = 0xffffffff;

    private void closeSetDelayTimeTake(){
        setTimeLayout.setVisibility(View.GONE);
        bt_closeSetTime.setVisibility(View.GONE);
        bt_openSetTime.setVisibility(View.VISIBLE);
    }

    public void closeSetDelayTimeTake(View view){
        closeSetDelayTimeTake();
    }

    private void openSetDelayTimeTake(){
        bt_closeSetTime.setVisibility(View.VISIBLE);
        bt_openSetTime.setVisibility(View.GONE);
        setTimeLayout.setVisibility(View.VISIBLE);
    }
    public void openSetDelayTimeTake(View view){
        openSetDelayTimeTake();
    }
    ImageButton bt_closeSetTime,bt_openSetTime;
    LinearLayout setTimeLayout;
    TextView tv_closeDelay ,tv_3SecondDelay,tv_5SecondDelay,tv_10SecondDelay;
    TextView timeView;
    CircleImageView imageView;
    private void initView(){

        imageView = cameraView.findViewById(R.id.gallery);
        setTimeLayout = cameraView.findViewById(R.id.set_time_layout);
        bt_closeSetTime = cameraView.findViewById(R.id.close_set_time_layout);
        bt_openSetTime = cameraView.findViewById(R.id.bt_open_set_time);

        timeView = cameraView.findViewById(R.id.time_view);

        tv_closeDelay = cameraView.findViewById(R.id.close_delay);
        tv_3SecondDelay = cameraView.findViewById(R.id.tv_3_delay);
        tv_5SecondDelay = cameraView.findViewById(R.id.tv_5_delay);
        tv_10SecondDelay = cameraView.findViewById(R.id.tv_10_delay);

        tv_closeDelay.setTextColor(chooseColor);

        // 默认是关闭 设置延迟拍照的
        setTimeLayout.setVisibility(View.GONE);
        bt_closeSetTime.setVisibility(View.GONE);
        bt_openSetTime.setVisibility(View.VISIBLE);

        tv_closeDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayTime = 0;
                tv_closeDelay.setTextColor(chooseColor);
                tv_3SecondDelay.setTextColor(unChooseColor);
                tv_5SecondDelay.setTextColor(unChooseColor);
                tv_10SecondDelay.setTextColor(unChooseColor);
                bt_openSetTime.setBackgroundResource(R.mipmap.bt_time_set);
            }
        });
        tv_3SecondDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayTime = 3000;
                tv_closeDelay.setTextColor(unChooseColor);
                tv_3SecondDelay.setTextColor(chooseColor);
                tv_5SecondDelay.setTextColor(unChooseColor);
                tv_10SecondDelay.setTextColor(unChooseColor);
                bt_openSetTime.setBackgroundResource(R.mipmap.bt_time_set_choose);
                closeSetDelayTimeTake();
            }
        });
        tv_5SecondDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayTime = 5000;
                tv_closeDelay.setTextColor(unChooseColor);
                tv_3SecondDelay.setTextColor(unChooseColor);
                tv_5SecondDelay.setTextColor(chooseColor);
                tv_10SecondDelay.setTextColor(unChooseColor);
                bt_openSetTime.setBackgroundResource(R.mipmap.bt_time_set_choose);
                closeSetDelayTimeTake();
            }
        });
        tv_10SecondDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayTime = 10000;
                tv_closeDelay.setTextColor(unChooseColor);
                tv_3SecondDelay.setTextColor(unChooseColor);
                tv_5SecondDelay.setTextColor(unChooseColor);
                tv_10SecondDelay.setTextColor(chooseColor);
                bt_openSetTime.setBackgroundResource(R.mipmap.bt_time_set_choose);
                closeSetDelayTimeTake();
            }
        });


    }



}


