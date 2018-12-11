/*
 * Created by Wuwang on 2017/3/24
 * Copyright © 2017年 深圳哎吖科技. All rights reserved.
 */
package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.lammy.lammyopenglcamera.Utils.MatrixUtils;
import com.example.lammy.lammyopenglcamera.Utils.ZipPkmReader;

import java.nio.ByteBuffer;



/**
 * Description:
 */
public class ZipPkmAnimationFilter extends LyFilter {

    private boolean isPlay=false;
    private ByteBuffer emptyBuffer;
    private int width,height;
    private int type= MatrixUtils.TYPE_CENTERINSIDE;
    public static final int TYPE=0x01;

    private NoFilter mBaseFilter;

    private int[] texture;

    private ZipPkmReader mPkmReader;
    private int mGlHAlpha;


    public ZipPkmAnimationFilter(Context context) {
        super(context, "shader/pkm_mul.vert","shader/pkm_mul.frag");
        texture=new int[2];
        createEtcTexture(texture);
        setTextureId(texture[0]);

        mBaseFilter = new NoFilter(context);
        mPkmReader=new ZipPkmReader(context.getAssets());

        setAnimation("assets/etczip/cc.zip");
    }




    @Override
    protected void onClear() {

    }

    @Override
    public void onSizeChanged(int width, int height) {
        emptyBuffer= ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(width,height));
        this.width=width;
        this.height=height;
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }


    @Override
    protected void onBindTexture() {
        ETC1Util.ETC1Texture t=mPkmReader.getNextTexture();
        ETC1Util.ETC1Texture tAlpha=mPkmReader.getNextTexture();
//        Log.e("lammy","is ETC null->"+(t==null));
//        Log.e("lammy","is tAlpha null->"+(tAlpha==null));
        if(t!=null&&tAlpha!=null){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+type);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0, GLES20.GL_RGB, GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,t);
            GLES20.glUniform1i(vTextureLocation,type );

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1+type);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0, GLES20.GL_RGB, GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,tAlpha);
            GLES20.glUniform1i(mGlHAlpha,type + 1);
        }else{
            if(mPkmReader!=null){
                mPkmReader.close();
                mPkmReader.open();
            }
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+type);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0, GLES20.GL_RGB, GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES20.glUniform1i(vTextureLocation,type);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1+type);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[1]);
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D,0,0, GLES20.GL_RGB, GLES20
                    .GL_UNSIGNED_SHORT_5_6_5,new ETC1Util.ETC1Texture(width,height,emptyBuffer));
            GLES20.glUniform1i(mGlHAlpha,1+type);
            isPlay=false;
        }
    }

    @Override
    public void initUniforms() {
        vPositionLocation= GLES20.glGetAttribLocation(program, "vPosition");
        vTextureCoordinateLocation=GLES20.glGetAttribLocation(program,"vCoord");
        vMatrixLocation=GLES20.glGetUniformLocation(program,"vMatrix");
        vTextureLocation=GLES20.glGetUniformLocation(program,"vTexture");

    }

    @Override
    public void setOtherUniform() {
        mGlHAlpha= GLES20.glGetUniformLocation(program,"vTextureAlpha");
    }

    @Override
    public void draw() {
        if(getTextureId()!=0){
            mBaseFilter.setTextureId(getTextureId());
            mBaseFilter.setPointsMatrix(pointsMatrix);
            mBaseFilter.draw();
        }
        GLES20.glViewport(0 ,height/6 *5,width/6,height/6);

        // 动画生成纹理，在制作的时候就已经是flipY的，因此其实这里不需要flipY,但是添加到groupFilter的时候在调用draw之前，已经flipY了，然后现在 2次调用flipY根本没有区别，除非在
        //本来的pointMatrix 下再flipY，因此这里调用flipYPointsMatrix
//        LogUtil.e("pointsMatrix = " +Arrays.toString(  pointsMatrix));
        flipYPointsMatrix();
        super.draw();
        GLES20.glViewport(0,0,width,height);
    }

    private int baseTextureId = getTextureId();
    public void setBaseTextureId( int baseTextureId){
        this.baseTextureId = baseTextureId;
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


    public void setAnimation(String path){
        mPkmReader.setZipPath(path);
        mPkmReader.open();
    }


    @Override
    protected void finalize() throws Throwable {
        if(mPkmReader!=null){
            mPkmReader.close();
        }
        super.finalize();
    }

    private void createEtcTexture(int[] texture){
        //生成纹理
        GLES20.glGenTextures(2,texture,0);
        for (int i=0;i<texture.length;i++){
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[i]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
        }
    }

}
