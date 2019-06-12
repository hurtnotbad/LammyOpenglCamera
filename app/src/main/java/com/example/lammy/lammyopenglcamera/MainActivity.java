package com.example.lammy.lammyopenglcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lammy.lammyopenglcamera.lyFilter.BeautyFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.FaceColorFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.FilterManager;
import com.example.lammy.lammyopenglcamera.lyFilter.GroupFilter;

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;

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
        fboCameraRender.setOnPhotoTakenListener(onPhotoTakenListener);
        setContentView(cameraView);
        initView();

    }

    // 是否取消
   static boolean isCancel = false;
    // 在拍照中
   static boolean isTaking = false;
    public void takePhoto(View view){
        if(isTaking){
            isCancel = true;
            return;
        }
        isTaking = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int second = (int)delayTime/1000;
                    for(int i = 0; i < second ; i ++){
                        if(isCancel){
                            break;
                        }
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeView.setText((second - finalI) +"");
                                bt_takePhoto.setBackgroundResource(R.mipmap.cancel_take);
                            }
                        });
                        Thread.sleep(1000);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeView.setText("");
                            bt_takePhoto.setBackgroundResource(R.drawable.bt_take_photo);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isCancel) {
                    fboCameraRender.takePhoto();
                }
                isTaking = false;
                isCancel = false;
            }
        }).start();

    }
    private Bitmap lastImageTaken;
    private FboCameraRender.onPhotoTakenListener onPhotoTakenListener = new FboCameraRender.onPhotoTakenListener() {
        @Override
        public void onPhotoTaken() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap  = BitmapFactory.decodeFile(fboCameraRender.getLastImageTakenPath());
                    imageView.setImageBitmap(bitmap);

                }
            });
        }
    };

    public void changeCamera(View view){
        fboCameraRender.changeCamera();
    }
    public void chooseFilter(View view){
        fboCameraRender.setChooseFilter(true);
        bt_takePhoto.setEnabled(false);
    }
    public void closeChooseFilter(){
        fboCameraRender.setChooseFilter(false);
        bt_takePhoto.setEnabled(true);
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
    ImageButton bt_closeSetTime,bt_openSetTime,bt_takePhoto;
    LinearLayout setTimeLayout;
    TextView tv_closeDelay ,tv_3SecondDelay,tv_5SecondDelay,tv_10SecondDelay;
    TextView timeView;
    CircleImageView imageView;
    private void initView(){

//        fboCameraRender.setOnPhotoTakenListener(onPhotoTakenListener);
        imageView = cameraView.findViewById(R.id.gallery);
        bt_takePhoto = cameraView.findViewById(R.id.takePhoto_bt);

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String path = fboCameraRender.getLastImageTakenPath();
                if(path==null){
                    return;
                }
                File file = new File(path);
                Uri contentUri = getUriForFile(getApplicationContext(), "com.lammy.fileprovider", file);
                // 这种绝对路径后去uri FileUriExposedException ，必须要用 file_provider的形式
                //Uri contentUri = Uri.fromFile(file);
              //打开指定的一张照片
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "image/*");
                startActivity(intent);
            }
        });

        final ChooseView chooseView = cameraView.findViewById(R.id.choose_view);
        final SeekBar seekBar = cameraView.findViewById(R.id.beauty_seek_bar);
        seekBar.setVisibility(View.INVISIBLE);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               FilterManager.faceColorFilter.setIntensity(progress/100f);
               FilterManager.beautyFilter.setBeautyProgress(progress/20 + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        chooseView.setOnStateChangeListener(new ChooseView.OnStateChangeListener() {
            @Override
            public void onStateChange() {
                    int state = chooseView.getState();
                synchronized (GroupFilter.class) {
                    if (state == 0) {
                        closeChooseFilter();
                        seekBar.setVisibility(View.INVISIBLE);
                        fboCameraRender.removeAllFilter();
                        fboCameraRender.addFilter(FilterManager.zipPkmAnimationFilter);
                    } else if (state == 1) {
                        closeChooseFilter();
                        fboCameraRender.removeAllFilter();
                        seekBar.setVisibility(View.VISIBLE);
                        fboCameraRender.addFilter(FilterManager.zipPkmAnimationFilter);
                        fboCameraRender.addFilter(FilterManager.faceColorFilter);
                        fboCameraRender.addFilter(FilterManager.beautyFilter);
                    }
                }
            }
        });

    }



}
/**
 *
 * 添加文件file_provider，来源：https://blog.csdn.net/shenwd_note/article/details/74278884
 *
 *
 1、 在AndroidManifest.xml中加上
 <provider
 android:name="android.support.v4.content.FileProvider"
 android:authorities="com.mydomain.fileprovider"
 android:exported="false"
 android:grantUriPermissions="true">
 <meta-data
 android:name="android.support.FILE_PROVIDER_PATHS"
 android:resource="@xml/file_provider_paths" />
 </provider>

 2 、在res/xml 文件中创建file_provider_paths.xml 文件

 <paths xmlns:android="http://schemas.android.com/apk/res/android">
 <files-path name="my_images" path="images/"/>
 </paths>

 name为标识，file-path 表示前缀路径，path接着file-path 的路径

 内部的element可以是files-path，cache-path，external-path，external-files-path，external-cache-path
 分别对应Context.getFilesDir()，Context.getCacheDir()，Environment.getExternalStorageDirectory()，Context.getExternalFilesDir()，Context.getExternalCacheDir()等几个方法

 使用Uri

 File imagePath = new File(Context.getFilesDir(), "images");
 File newFile = new File(imagePath, "default_image.jpg");
 Uri contentUri = getUriForFile(getContext(), "com.mydomain.fileprovider", newFile);

 以上是百度查的资料

 使用中遇到的问题又查了官网：

 加上provider 时不能build；需要在provider的meta-data中加上tools:replace="android:resource"，顶部加上xmlns:tools="http://schemas.android.com/tools"
 <provider
 android:name="android.support.v4.content.FileProvider"
 android:authorities="com.shen.snote.fileprovider"
 android:exported="false"
 android:grantUriPermissions="true">
 <meta-data
 tools:replace="android:resource"
 android:name="android.support.FILE_PROVIDER_PATHS"
 android:resource="@xml/file_provider_paths" />
 </provider>
 使用Uri时 日志显示权限拒绝，需要在使用时添加代码intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);或addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
 //打开指定的一张照片
 Intent intent = new Intent();
 intent.setAction(android.content.Intent.ACTION_VIEW);
 intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
 intent.setDataAndType(uriForFile, "image/*");
 startActivity(intent);
 ---------------------
 作者：ShenWandong
 来源：CSDN
 原文：https://blog.csdn.net/shenwd_note/article/details/74278884
 版权声明：本文为博主原创文章，转载请附上博文链接！

 *
 */

