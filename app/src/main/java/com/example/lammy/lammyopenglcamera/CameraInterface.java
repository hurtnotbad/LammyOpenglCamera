package com.example.lammy.lammyopenglcamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Surface;
import android.view.WindowManager;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

import java.util.Arrays;

/**
 * Created by lammy on 2018/9/17.
 */

public class CameraInterface {

    private String cameraID = 0+"";
    public static Size previewSize ;
    private  Context context;
    private SurfaceTexture surfaceTexture;

    public CameraInterface(Context context){
        this.context =context;
        getPreviewSize();
    }
    public void setSurfaceTexture(SurfaceTexture surfaceTexture){
        this.surfaceTexture =surfaceTexture;
        startCameraThread();

    }
    private Handler mCameraHandler;
    private HandlerThread mCameraThread;
    public void startCameraThread() {
        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    private void getPreviewSize(){
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics
                .SCALER_STREAM_CONFIGURATION_MAP);
        //获取相机支持的size
        Size size[] =  map.getOutputSizes(ImageFormat.JPEG);
        previewSize = size[0];
        LogUtil.e("onSurfaceChanged cameraWidth0 = " + previewSize.getWidth());
        LogUtil.e("onSurfaceChanged cameraHeight0= " + previewSize.getHeight());
    }

//    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreViewBuilder;
    private CameraCaptureSession mCameraSession;
    private CameraCharacteristics mCameraCharacteristics;
    public boolean openCamera(){
        closeCamera();
         final CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        try {
            cameraManager.openCamera(cameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    mCameraDevice = cameraDevice;
                    try {

                        mPreViewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                        mCameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
                        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics
                                .SCALER_STREAM_CONFIGURATION_MAP);
                        //获取相机支持的size
                        Size size[] =  map.getOutputSizes(ImageFormat.JPEG);
                        previewSize = size[0];
                        surfaceTexture.setDefaultBufferSize(previewSize.getWidth() , previewSize.getHeight());
                        Surface surface = new Surface(surfaceTexture);
                        mPreViewBuilder.addTarget( surface);

                        mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                mCameraSession = cameraCaptureSession;
                                startPreview();
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                            }
                        }, mCameraHandler);

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private void startPreview(){
        if(mCameraSession == null||mCameraDevice == null) {
            openCamera();
        }
        try {
            mPreViewBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            mPreViewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_USE_SCENE_MODE);
            mPreViewBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);
            mPreViewBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE);
            mPreViewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            mPreViewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            mCameraSession.setRepeatingRequest(mPreViewBuilder.build(), null, mCameraHandler);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera() {
        try {

            if (null != mCameraSession) {
                mCameraSession.close();
                mCameraSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
//            if (null != mImageReader) {
//                mImageReader.close();
//                mImageReader = null;
//            }
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
//            mCameraOpenCloseLock.release();
        }
    }


    public int getRotateDegree(Context context ) {
        CameraManager manager = (CameraManager) context.getSystemService(Context
                .CAMERA_SERVICE);
        try {
            mCameraCharacteristics = manager.getCameraCharacteristics(cameraID);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int displayRotation = wm.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (displayRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int senseOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        return   (senseOrientation - degrees + 360) % 360;
    }

    public String  getCameraId(){
        return cameraID;
    }

    public void changeCamera(){
        if(cameraID.equals("1")){
            this.cameraID = "0";
        }else if(cameraID.equals("0")){
            this.cameraID = "1";
        }
      openCamera();
    }

}
