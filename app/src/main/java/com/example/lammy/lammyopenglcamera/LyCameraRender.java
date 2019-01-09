package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;
import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.lyFilter.CameraFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.GrayFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.NoFilter;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glViewport;

/**
 * 简单的 将 摄像头 纹理转化为 2d texture，然后渲染出来
 */
public class LyCameraRender implements GLSurfaceView.Renderer{


    private Context context;
    private SurfaceTexture mSurfaceTexture;
    private GLSurfaceView glSurfaceView;
    private CameraInterface cameraInterface;


    public LyCameraRender(Context context){
        this.context = context;
        cameraInterface = new CameraInterface(context);
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView){
        this.glSurfaceView = glSurfaceView;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
//        glSurfaceView.getHolder().addCallback(null);
    }


    private CameraFilter cameraFilter;
    private GrayFilter grayFilter;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        cameraFilter = new CameraFilter(context);
        mSurfaceTexture = cameraFilter.getmSurfaceTexture();
        cameraInterface.setSurfaceTexture(mSurfaceTexture);
        cameraInterface.openCamera();

        noFilter = new NoFilter(context);
        grayFilter = new GrayFilter(context);

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        glViewport(0, 0, width, height);
        cameraFilter.onSizeChanged(width , height);
    }

    NoFilter noFilter;

    @Override
    public void onDrawFrame(GL10 gl) {

        cameraFilter.setCameraId(cameraInterface.getCameraId());
        cameraFilter.draw();


        noFilter.setTextureId(cameraFilter.getOutTextureId());
        noFilter.flipY();
        noFilter.draw();

        grayFilter.setTextureId(cameraFilter.getOutTextureId());
        grayFilter.flipY();
        grayFilter.draw();



    }

}
