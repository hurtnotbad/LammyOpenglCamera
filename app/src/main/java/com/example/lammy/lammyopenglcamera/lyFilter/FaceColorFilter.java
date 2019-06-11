package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.lammy.lammyopenglcamera.helper.TextureHelper;


import java.io.IOException;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniform1f;

public class FaceColorFilter extends LyFilter {


    public FaceColorFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public FaceColorFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }

    public FaceColorFilter(Context context) {
        super(context, "lyfilter/FaceColorFilter/faceColor.vert", "lyfilter/FaceColorFilter/faceColor.frag");
        try {
            bitmap = BitmapFactory.decodeStream(context.getAssets().open("lyfilter/FaceColorFilter/purity.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 之前在initUniforms 中调用maskTextures ，发现 maskTextures 为空，原因在于
        // initUniforms是 重写父类方法，initUniforms在父类初始化中调用，此时maskTextures为空，子类未创建
        TextureHelper.genTexturesWithParameter(1,maskTextures,0, GLES20.GL_RGBA,512,512);
    }


    private int intensityLocation;
    private float intensity;
    private int maskTextureLocation;
    private  int[] maskTextures = new int[1];
    private Bitmap bitmap;
    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "vCoord");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
        maskTextureLocation = GLES20.glGetUniformLocation(program, "maskTexture");
        vMatrixLocation =  GLES20.glGetUniformLocation(program, "vMatrix");
        intensityLocation =  GLES20.glGetUniformLocation(program, "intensity");
//        try {
//             bitmap = BitmapFactory.decodeStream(context.getAssets().open("lyfilter/FaceColorFilter/purity.png"));
//             if(maskTextures == null){
//                 LogUtil.e("maskTextures  is null");
//                 maskTextures = new int[1];
//             }
//            EasyGlUtils.genTexturesWithParameter(1,maskTextures,0, GLES20.GL_RGBA,512,512);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public void setOtherUniform() {
        glUniform1f(intensityLocation ,intensity );
        setMask();
    }


    /**
     *
     * @param intensity 美肤 强度 [0,1]
     */
    public void setIntensity(float intensity){
        this.intensity = intensity;
    }



    // 绑定纹理得在draw中每次调用，不然颜色不正确
    public void setMask(){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + type + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, maskTextures[0]);
            if(bitmap!=null&&!bitmap.isRecycled()){
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
                bitmap.recycle();
            }
            GLES20.glUniform1i(maskTextureLocation, type + 1);

    }

}
