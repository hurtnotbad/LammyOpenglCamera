package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.lammy.lammyopenglcamera.Utils.BufferUtil;
import com.example.lammy.lammyopenglcamera.Utils.EasyGlUtils;
import com.example.lammy.lammyopenglcamera.Utils.LogUtil;
import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.helper.FBOHelper;
import com.example.lammy.lammyopenglcamera.helper.ShaderHelper;
import com.example.lammy.lammyopenglcamera.helper.TextureHelper;


import java.nio.FloatBuffer;

import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniform1i;

public  class CameraFilter {


    private static final String TAG="CameraFilter";

    public CameraFilter(Context context , int  vertexShaderRawId, int  fragmentShaderRawId){
        String vertexShaderString = ShaderHelper.readTextFileFromResourceRaw(context ,vertexShaderRawId );
        String fragmentShaderString = ShaderHelper.readTextFileFromResourceRaw(context ,fragmentShaderRawId );
        LogUtil.e(fragmentShaderString);
        program = ShaderHelper.buildProgram(vertexShaderString, fragmentShaderString);
        initUniforms();

    }
    public CameraFilter(Context context , String vertexShaderAssetsPath, String fragmentShaderAssetsPath){
        String vertexShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,vertexShaderAssetsPath );
        String fragmentShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,fragmentShaderAssetsPath );
        program = ShaderHelper.buildProgram(vertexShaderString, fragmentShaderString);
        initUniforms();

    }
    public CameraFilter(Context context){
        String vertexShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,"lyfilter/cameraFilter/no_filter_vertex_shader.glsl" );
        String fragmentShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,"lyfilter/cameraFilter/no_filter_fragment_shader.glsl" );
        program = ShaderHelper.buildProgram(vertexShaderString, fragmentShaderString);
        initUniforms();
    }

    /**
     * 程序句柄
     */
    protected int program;

    public int getPrgram(){
        return program;
    }

    //顶点坐标
    private float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    private float[] coordCameraFront={
            1f,1f,
            0f,1f,
            1f,0f,
            0f,0f
    };
    private float[] coordCameraBack={
            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f
    };

    private int vPositionLocation;
    private int vTextureCoordinateLocation;
    private int vTextureLocation;


    private void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "inputTextureCoordinate");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
        onCreate();
    }

    protected void setUniforms() {
        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 8, vPositionBuffer);
        GLES20.glVertexAttribPointer(vTextureCoordinateLocation, 2, GLES20.GL_FLOAT, false, 8,vTextureCoordinateBuffer );

    }

    public void onSizeChanged(int width, int height) {
        if(this.width!=width||this.height!=height){
            this.width=width;
            this.height=height;
            LogUtil.e("width = " + width);
            LogUtil.e("height = " + height);
            //创建FrameBuffer和Texture
            deleteFrameBuffer();
           // GLES20.glGenFramebuffers(1, fFrame, 0);
            FBOHelper.createFrameBuffer(width, height, fFrame, fRender,outTextureId);
            //TextureHelper.genTexturesWithParameter(1, outTextureId,0,GLES20.GL_RGBA,width,height);

        }
    }


    /**
     * 启用顶点坐标和纹理坐标进行绘制
     */
    int index= 0;
    public void draw(){
        // 在切换相机的时候，切换的瞬间容易看到，纹理映射，旋转前的图像，因此，卡顿21帧不绘制
        if(isChangeCamera){
            if(index > 40){
                index = 0;
                isChangeCamera = false;
            }else{
                index ++;
                return;
            }
        }


        boolean a=GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if(a){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }

        if(mSurfaceTexture!=null){
            mSurfaceTexture.updateTexImage();
        }

        FBOHelper.bindFrameTexture(fFrame[0],outTextureId[0],fRender[0]);


//        //6. 检测是否绑定从成功
//        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)!= GLES20.GL_FRAMEBUFFER_COMPLETE) {
//            Log.e("zzz", "glFramebufferTexture2D error  ");
//        }else{
//            Log.e("zzz", "glFramebufferTexture2D success " );
//        }

        OnDraw();
        FBOHelper.unBindFrameBuffer();

        if(a){
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }


    }

    private void OnDraw(){
        onClear();
        onUseProgram();
        setUniforms();
        onBindTexture();
        glEnableVertexAttribArray(vPositionLocation);
        glEnableVertexAttribArray(vTextureCoordinateLocation);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(vPositionLocation);
        GLES20.glDisableVertexAttribArray(vTextureCoordinateLocation);
    }

    private FloatBuffer vPositionBuffer =  BufferUtil.floatToBuffer(pos);
    private FloatBuffer vTextureCoordinateBuffer;
    private int width , height;
    private SurfaceTexture mSurfaceTexture;

    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }

    private float[] get90RotationMatrix(){

//       // 目标矩阵 进行
//        float []projectionMatrix = MatrixUtils.getOriginalMatrix();
//        float[] modelMatrix = new float[16];
//        // 将modelMatrix 置为单位矩阵
//        Matrix.setIdentityM(modelMatrix , 0);
//        //移动
//        Matrix.translateM(modelMatrix , 0, 0f , 0f ,-3f);
//        // 旋转 90
//        Matrix.rotateM(modelMatrix , 0, 90 ,0f , 0f ,1f);
//        //先 移动 后  旋转，然后 2个 矩阵相乘 赋值给temp
//        float[] temp = new float[16];
//        Matrix.multiplyMM(temp , 0 , projectionMatrix , 0 , modelMatrix , 0);
//        //temp 值拷贝给 projectionMatrix , 其实 可以直接temp, 这里 防止目标矩阵是成员变量
//        System.arraycopy(temp , 0 , projectionMatrix , 0 , temp.length);


        //目标矩阵
        float []pointsMatrix = MatrixUtils.getOriginalMatrix();

        float[] modelMatrix = MatrixUtils.getOriginalMatrix();
        Matrix.rotateM(modelMatrix , 0, 90 ,0f , 0f ,1f);
        // 先 移动 后  旋转，然后 2个 矩阵相乘 赋值给temp
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp , 0 , pointsMatrix , 0 , modelMatrix , 0);
        //temp 值拷贝给 projectionMatrix
        System.arraycopy(temp , 0 , pointsMatrix , 0 , temp.length);

        return pointsMatrix;
    }


    public SurfaceTexture getmSurfaceTexture() {
        return mSurfaceTexture;
    }

//    public void setPositionBuffer(FloatBuffer vPosition){
//        this.vPositionBuffer = vPosition;
////        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 8, vPosition);
//    }
//    public void setTextureCoorBuffer(FloatBuffer textureCoordinate){
//        this.vTextureCoordinateBuffer = textureCoordinate;
////        GLES20.glVertexAttribPointer(vTextureCoordinateLocation, 2, GLES20.GL_FLOAT, false, 8, textureCoordinate);
//    }
//
//    public FloatBuffer getTextureCoordinateBuffer() {
//        return vTextureCoordinateBuffer;
//    }

//    public void setMatrix(float[] projectMatrix){
//        this.pointsMatrix = projectMatrix;
//        if(projectMatrix != null) {
//            this.pointsMatrix = projectMatrix;
//        }else{
//            this.pointsMatrix = MatrixUtils.getOriginalMatrix();
//        }
//    }

    /**
     * 绑定默认纹理
     */
    private int fFrame[] = new int[1];
    private int[] fRender = new int[1];
    private int textureId[] = new int[1];
    private int outTextureId[] = new int[1];

    public int getOutTextureId(){
        return outTextureId[0];
}

    private String cameraId = "0";
    public String getCameraId() {
        return cameraId;
    }

    private boolean isChangeCamera = false;
    public void setCameraId(String cameraId){
        isChangeCamera = true;
        this.cameraId  = cameraId;
        if(cameraId.equals("0")) {
            vTextureCoordinateBuffer = BufferUtil.floatToBuffer(coordCameraBack);
        }else if(cameraId.equals("1")){
            vTextureCoordinateBuffer = BufferUtil.floatToBuffer(coordCameraFront);
        }else{
            LogUtil.e("please set the cameraId first !");
        }
    }


    protected void onUseProgram(){
        GLES20.glUseProgram(program);
    }

    protected void onBindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId[0]);
        glUniform1i(vTextureLocation, 0);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    public void deleteProgram() {
        GLES20.glDeleteProgram(program);
    }

    private void createOesTexture(){
        GLES20.glGenTextures(1,textureId,0);
    }


    private void onCreate(){
        createOesTexture();
        mSurfaceTexture=new SurfaceTexture(textureId[0]);
    }

    //取消绑定Texture
    private void unBindFrame() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, outTextureId, 0);
    }



}
