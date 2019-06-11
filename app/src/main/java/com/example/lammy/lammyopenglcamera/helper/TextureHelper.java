package com.example.lammy.lammyopenglcamera.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;


public class TextureHelper {

    public  static int loadTexture(Context context , int resourceId){

        // 创建纹理
        final  int[] textureObjectIds = new int[1];
        glGenTextures(1,textureObjectIds ,0);

        if(textureObjectIds[0] == 0){
            LogUtil.e("could't generate a new opengl texture object ");
            return 0;
        }

        final  BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources() , resourceId , options);
        if(bitmap == null){
            LogUtil.e("加载纹理图片"+ resourceId+"失败");
            glDeleteTextures(1 , textureObjectIds , 0);
            return  0;
        }

        glBindTexture(GL_TEXTURE_2D , textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GLUtils.texImage2D(GL_TEXTURE_2D , 0 , bitmap , 0);
        bitmap.recycle();
        // 生成mip贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        // 解除纹理绑定，传入0即是 解除
        glBindTexture(GL_TEXTURE_2D , 0);
        return textureObjectIds[0];
    }
    public  static int loadTexture(Bitmap bitmap){

        // 创建纹理
        final  int[] textureObjectIds = new int[1];
        glGenTextures(1,textureObjectIds ,0);

        if(textureObjectIds[0] == 0){
            LogUtil.e("could't generate a new opengl texture object ");
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D , textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D , GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        GLUtils.texImage2D(GL_TEXTURE_2D , 0 , bitmap , 0);
        bitmap.recycle();
        // 生成mip贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        // 解除纹理绑定，传入0即是 解除
        glBindTexture(GL_TEXTURE_2D , 0);
        return textureObjectIds[0];
    }



    public static void useTexParameter(){
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static void useTexParameter(int gl_wrap_s,int gl_wrap_t,int gl_min_filter,
                                       int gl_mag_filter){
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,gl_wrap_s);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,gl_wrap_t);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,gl_min_filter);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,gl_mag_filter);
    }

    public static void genTexturesWithParameter(int size,int[] textures,int start,
                                                int gl_format,int width,int height){
        GLES20.glGenTextures(size, textures, start);
        for (int i = 0; i < size; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0,gl_format, width, height,
                    0, gl_format, GLES20.GL_UNSIGNED_BYTE, null);
            useTexParameter();
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
    }


    public static int createOESTexture(int tex[]) {
        //int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }
}
