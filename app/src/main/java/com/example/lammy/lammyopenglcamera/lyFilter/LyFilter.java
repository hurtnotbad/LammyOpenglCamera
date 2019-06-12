package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.lammy.lammyopenglcamera.Utils.BufferUtil;
import com.example.lammy.lammyopenglcamera.Utils.Gl2Utils;
import com.example.lammy.lammyopenglcamera.Utils.LogUtil;
import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.helper.ShaderHelper;


import java.nio.FloatBuffer;

import static android.opengl.GLES20.glEnableVertexAttribArray;

public abstract class LyFilter {

    private static final String TAG="lyFilter";


    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    /**
     * 程序句柄
     */
    protected int program;
    public int getPrgram(){
        return program;
    }

//    //顶点坐标
    public float pos[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    // 因为 fbo，cameraFilter中 将纹理映射已经正确，因此这里可以 正确的一一对应即可
    public float coor[] = {
            0.0f , 1.0f,
            0.0f , 0.0f,
            1.0f , 1.0f,
            1.0f , 0.0f
    };

    public Context context;

    public LyFilter(Context context , int  vertexShaderRawId, int  fragmentShaderRawId){
        this.context = context;
        String vertexShaderString = ShaderHelper.readTextFileFromResourceRaw(context ,vertexShaderRawId );
        String fragmentShaderString = ShaderHelper.readTextFileFromResourceRaw(context ,fragmentShaderRawId );
        LogUtil.e(fragmentShaderString);
        program = ShaderHelper.buildProgram(vertexShaderString, fragmentShaderString);
        initUniforms();
    }
    public LyFilter(Context context , String vertexShaderAssetsPath, String fragmentShaderAssetsPath){
        this.context = context;
        String vertexShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,vertexShaderAssetsPath );
        String fragmentShaderString = ShaderHelper.readTextFileFromResourceAssets(context ,fragmentShaderAssetsPath );
        program = ShaderHelper.buildProgram(vertexShaderString, fragmentShaderString);
        initUniforms();
    }


    protected void onUseProgram(){
        GLES20.glUseProgram(program);
    }

    /**
     * 清除画布
     */
    protected void onClear(){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * 绑定默认纹理
     */
    int index = 0;
    public int type = 0;

    public int getType(){
        return type;
    }
    protected void onBindTexture() {

//        Bitmap bitmap = null;
//        try {
//            if(index % 2 == 0) {
//                bitmap = BitmapFactory.decodeStream(context.getAssets().open("lyfilter/lookup/purity.png"));
//            }else{
//                bitmap = BitmapFactory.decodeStream(context.getAssets().open("lyfilter/lookup/highkey.png"));
//            }
//            index++;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+type);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

//        ByteBuffer mBuffer = ByteBuffer.allocate(width * height * 4);
//        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mBuffer);
//        bitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//        bitmap.copyPixelsFromBuffer(mBuffer);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,mBuffer);
//        if(bitmap!=null&&!bitmap.isRecycled()){
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
//            bitmap.recycle();
//        }
        GLES20.glUniform1i(vTextureLocation,type);

//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
//        glUniform1i(vTextureLocation, 0);
    }


    public void deleteProgram() {
        GLES20.glDeleteProgram(program);
    }

    public float[] pointsMatrix = MatrixUtils.getOriginalMatrix();
    public int textureId ;
    public FloatBuffer vTextureCoordinateBuffer= BufferUtil.floatToBuffer(coor);
    public FloatBuffer vPositionBuffer = BufferUtil.floatToBuffer(pos);

    public int vPositionLocation;
    public int vTextureCoordinateLocation;
    public int vTextureLocation;
    public int vMatrixLocation;


    public abstract void  initUniforms();
//    public void  initUniforms(){
//        vPositionLocation = glGetAttribLocation(program , vPositionString);
//        vTextureCoordinateLocation = glGetAttribLocation(program , vTextureCoordinateString);
//        vTextureLocation = GLES20.glGetUniformLocation(program, vTextureString);
//        vMatrixLocation =  GLES20.glGetUniformLocation(program, vMatrixString);
//
//        initOtherUniform();
//    }
//    public void initBaseUniform(String vPositionString , String vTextureCoordinateString ,String vTextureString ,String vMatrixString){
//        this.vPositionString = vPositionString;
//        this.vTextureCoordinateString = vTextureCoordinateString;
//        this.vTextureString = vTextureString;
//        this.vMatrixString = vMatrixString;
//    }

    public void setUniforms(){
        GLES20.glUniformMatrix4fv(vMatrixLocation, 1, false, pointsMatrix, 0);
        GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 8, vPositionBuffer);
        GLES20.glVertexAttribPointer(vTextureCoordinateLocation, 2, GLES20.GL_FLOAT, false, 8,  vTextureCoordinateBuffer);

        setOtherUniform();
    }

    public abstract void setOtherUniform();

//    public void flipYPointsMatrix(){
//        MatrixUtils.flip(pointsMatrix,false,true);
//    }
//    public void flipY(){
//        float[] c  = MatrixUtils.getOriginalMatrix();
//        MatrixUtils.flip(c,false,true);
//        setPointsMatrix(c);
//    }

    public void setPointsMatrix(float[] projectMatrix) {
        this.pointsMatrix = projectMatrix;
    }
    public void setTextureId(int textureId){
        this.textureId = textureId;
    }

    public int getTextureId() {
        return textureId;
    }
    protected int width,height;
    public  void onSizeChanged(int width, int height){
        this.width =width;
        this.height = height;
    }


    public void draw(){
        onClear();
        onUseProgram();
        setUniforms();
        onBindTexture();

        glEnableVertexAttribArray(vPositionLocation);
        glEnableVertexAttribArray(vTextureCoordinateLocation);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(vPositionLocation);
        GLES20.glDisableVertexAttribArray(vTextureCoordinateLocation);
    }



}
