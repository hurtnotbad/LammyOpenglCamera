package com.example.lammy.lammyopenglcamera.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

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


}
