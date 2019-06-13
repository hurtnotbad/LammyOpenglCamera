package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaScannerConnection;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.view.SurfaceHolder;
import com.example.lammy.lammyopenglcamera.Utils.LogUtil;
import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.helper.FBOHelper;
import com.example.lammy.lammyopenglcamera.helper.TextureHelper;
import com.example.lammy.lammyopenglcamera.lyFilter.CameraFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.FilterManager;
import com.example.lammy.lammyopenglcamera.lyFilter.GroupFilter;
import com.example.lammy.lammyopenglcamera.lyFilter.LyFilter;
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
    private FilterManager filterManager;

    public FboCameraRender(Context context) {
        this.context = context;
        cameraInterface = new CameraInterface(context);
        filterManager =  FilterManager.getInstance();
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



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initFilter();

        mSurfaceTexture = cameraFilter.getmSurfaceTexture();
        cameraInterface.setSurfaceTexture(mSurfaceTexture);
        cameraInterface.openCamera();
        cameraFilter.setCameraId(cameraInterface.getCameraId());
//        filterManager.getFlitersStartPoints(cameraFilter.getWidth(), cameraFilter.getHeight());
    }


    public CameraFilter cameraFilter;
    public GroupFilter groupFilter;
    public LyFilter showFilter;

    private void initFilter(){
        filterManager.initAllFilter(context);
        cameraFilter = new CameraFilter(context);
        showFilter = FilterManager.getInstance().noFilter;
        groupFilter = new GroupFilter(cameraFilter);
//        groupFilter.addFilter(filterManager.zipPkmAnimationFilter);
//        groupFilter.addFilter(FilterManager.noFilter);
//        groupFilter.addFilter(FilterManager.grayFilter);
    }




    public void addFilter(LyFilter lyFilter){
        if(groupFilter!=null) {
            groupFilter.addFilter(lyFilter);
        }
    }

    public void removeAllFilter(){
        if(groupFilter!=null) {
            groupFilter.removeAllFilter();
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        if(groupFilter == null){
            LogUtil.e("groupFilter is null");

        }
        groupFilter.onSizeChanged(width, height);
        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1,mExportFrame,0);
        TextureHelper.genTexturesWithParameter(1,mExportTexture,0,GLES20.GL_RGBA,width, height);
    }


    private boolean isChooseFilter = false;
    public void setChooseFilter(boolean isChoose){
        isChooseFilter = isChoose;
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        if(!isChooseFilter){
            groupFilter.draw();
            showFilter.setTextureId(groupFilter.getOutTexture());
            showFilter.draw();
            callbackIfNeeded();
        }
        else{
            cameraFilter.draw();
            int textureId = cameraFilter.getOutTextureId();
            filterManager.drawFilters(textureId, cameraFilter.getWidth(),cameraFilter.getHeight() );
        }

    }


    private boolean isShoot = false;
    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportRender = new int[1];
    private int[] mExportTexture = new int[1];
    private void callbackIfNeeded() {
        if ( isShoot) {
            int width = cameraFilter.getWidth();
            int height = cameraFilter.getHeight();
            ByteBuffer data = ByteBuffer.allocate(width * height*4);
            GLES20.glViewport(0, 0, width,height);

            FBOHelper.bindFrameTexture(mExportFrame[0],mExportTexture[0], mExportRender[0]);
            // 因为纹理坐标 和 图片 坐标是山下颠倒得，因此保存图片得时候，得上下颠倒后保存
            showFilter.flipY();
            showFilter.draw();
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            onFrame(data.array() ,width,height ,0);
            isShoot = false;
            FBOHelper.unBindFrameBuffer();
            // 保存了图片 恢复反转
            showFilter.setPointsMatrix(MatrixUtils.getOriginalMatrix());
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

            // 通知图库
            MediaScannerConnection.scanFile(context, new String[]{jpegName}, null, null);
            lastImageTakenPath = jpegName;
            if(onPhotoTakenListener != null){
                onPhotoTakenListener.onPhotoTaken();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private  onPhotoTakenListener onPhotoTakenListener;

    public void setOnPhotoTakenListener(onPhotoTakenListener onPhotoTakenListener){
        this.onPhotoTakenListener = onPhotoTakenListener;
    }

    private String lastImageTakenPath;
    public void takePhoto(){
        isShoot = true;
    }


    public String getLastImageTakenPath(){
        return lastImageTakenPath;
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    public void changeCamera(){
        cameraInterface.changeCamera();
        cameraFilter.setCameraId(cameraInterface.getCameraId());
    }

    interface onPhotoTakenListener{

        void onPhotoTaken();
    }

}
