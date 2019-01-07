package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import android.util.DisplayMetrics;
import android.util.Size;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import com.example.lammy.lammyopenglcamera.Utils.EasyGlUtils;
import com.example.lammy.lammyopenglcamera.Utils.LogUtil;
import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.lyFilter.BeautyFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.BrightFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.CameraFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.FaceColorFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.GrayFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.GroupFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.LyFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.MagnifierFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.NoFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.ZipPkmAnimationFilter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glViewport;

/**
 * 将摄像头 转为2d纹理，并且 可以添加多个filter  渲染，并且可以拍照
 */

public class FboCameraRender implements GLSurfaceView.Renderer {


    private Context context;
    private SurfaceTexture mSurfaceTexture;
    private GLSurfaceView glSurfaceView;
    private CameraInterface cameraInterface;


    public FboCameraRender(Context context) {
        this.context = context;
        cameraInterface = new CameraInterface(context);
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(this);
        glSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(cameraInterface!=null && cameraFilter!= null) {
                    cameraInterface.openCamera();
                    LogUtil.e("lammylog surfaceCreated");
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(cameraInterface!=null && cameraFilter!= null) {
//                    cameraInterface.closeCamera();
                    LogUtil.e("lammylog surfaceCreated");
                }
            }
        });
    }


    private CameraFilter cameraFilter;
    private GrayFilter grayFilter;
    private GroupFilter groupFilter;
    private LyFilter showFilter;
    private FaceColorFilter faceColorFilter;
    private BeautyFilter beautyFilter;
    private MagnifierFilter magnifierFilter;
    private ZipPkmAnimationFilter zipPkmAnimationFilter;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        setFilter();
//        cameraFilter = new CameraFilter(context);
//        mSurfaceTexture = cameraFilter.getmSurfaceTexture();
//        cameraInterface.setSurfaceTexture(mSurfaceTexture);
//        cameraInterface.openCamera();
//        cameraFilter.setCameraId(cameraInterface.getCameraId());
//
//        noFilter = new NoFilter(context);
//        grayFilter = new GrayFilter(context);
//        faceColorFilter = new FaceColorFilter(context);
//        faceColorFilter.setIntensity(1f);
//        beautyFilter = new BeautyFilter(context);
//        beautyFilter.setBeautyProgress(3);
//        magnifierFilter = new MagnifierFilter(context);
//        magnifierFilter.setCenterPoint(new float[]{0.6f,0.5f});
//        magnifierFilter.setOpinionSize(2f);
//        magnifierFilter.setR(0.3f);
//        zipPkmAnimationFilter=new ZipPkmAnimationFilter(context);
//
//        BrightFilter brightFilter = new BrightFilter(context);
//        brightFilter.setBrightness(0.2f);
//
//
//        groupFilter = new GroupFilter(cameraFilter);
////        groupFilter.addFilter(noFilter);
////        groupFilter.addFilter(grayFilter);
////        groupFilter.addFilter(faceColorFilter);
////        groupFilter.addFilter(beautyFilter);
////        groupFilter.addFilter(magnifierFilter);
////        groupFilter.addFilter(zipPkmAnimationFilter);
//        groupFilter.addFilter(brightFilter);

        showFilter = new NoFilter(context);

    }



    public void setFilter(){
        cameraFilter = new CameraFilter(context);
        mSurfaceTexture = cameraFilter.getmSurfaceTexture();
        cameraInterface.setSurfaceTexture(mSurfaceTexture);
        cameraInterface.openCamera();
        cameraFilter.setCameraId(cameraInterface.getCameraId());

        groupFilter = new GroupFilter(cameraFilter);
//        groupFilter.addFilter(new GrayFilter(context));
        groupFilter.addFilter(new ZipPkmAnimationFilter(context));
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {


//        Size size = cameraInterface.getCameraViewSize();
//        width = size.getWidth();
//        height = size.getHeight();
        LogUtil.e("onSurfaceChanged width = " + width);
        LogUtil.e("onSurfaceChanged height = " + height);


//        height =cameraInterface.previewSize.getWidth();
//        width = cameraInterface.previewSize.getHeight();

        glViewport(0, 0, width, height);

        if(groupFilter == null){
            LogUtil.e("groupFilter is null");

        }
        groupFilter.onSizeChanged(width, height);
        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1,mExportFrame,0);
        EasyGlUtils.genTexturesWithParameter(1,mExportTexture,0,GLES20.GL_RGBA,width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        groupFilter.draw();
        showFilter.setTextureId(groupFilter.getOutTexture());
        showFilter.flipY();
        showFilter.draw();
        callbackIfNeeded();
    }


    private boolean isShoot = false;
    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];
    private void callbackIfNeeded() {
        if ( isShoot) {
            int width = cameraFilter.getWidth();
            int height = cameraFilter.getHeight();
            ByteBuffer data = ByteBuffer.allocate(width * height*4);
            GLES20.glViewport(0, 0, width,height);
            EasyGlUtils.bindFrameTexture(mExportFrame[0],mExportTexture[0]);

            // showFilter 绘制了一次了，已经正了，所以 矩阵得 置为单位矩阵
            showFilter.setPointsMatrix(MatrixUtils.getOriginalMatrix());
            showFilter.draw();
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            onFrame(data.array() ,width,height ,0);
            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            // 保存了图片 恢复反转
            showFilter.flipY();
        }
    }
    public void onFrame(final byte[] bytes, final int width, final int height , long time) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                ByteBuffer b=ByteBuffer.wrap(bytes);
                bitmap.copyPixelsFromBuffer(b);
                saveBitmap(bitmap);
                bitmap.recycle();
            }
        }).start();
    }
    //图片保存
    public void saveBitmap(Bitmap b){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

        File file = new File(path+"lammyPhoto");
        if(!file.exists()){
            file.mkdirs();
        }
        long takeTime = System.currentTimeMillis();
        final String jpegName=file.getAbsolutePath()+ "/"+ takeTime +".jpg";
        LogUtil.e("jpegName = "+jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void takePhoto(){
        isShoot = true;
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    public void changeCamera(){
        cameraInterface.changeCamera();
        cameraFilter.setCameraId(cameraInterface.getCameraId());
    }
}
