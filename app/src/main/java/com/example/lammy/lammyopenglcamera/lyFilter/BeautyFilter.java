package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;

public class BeautyFilter extends LyFilter {
    public BeautyFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public BeautyFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }
    public BeautyFilter(Context context) {
        super(context, "lyfilter/beautyFilter/beauty.vert", "lyfilter/beautyFilter/beauty.frag");

    }

    private int iternumLocation;
    private int aaCoefLocation; //参数
    private int mixCoefLocation; //混合系数
    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "vCoord");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
        vMatrixLocation = GLES20.glGetUniformLocation(program, "vMatrix");

        iternumLocation = GLES20.glGetUniformLocation(program, "iternum");
        aaCoefLocation = GLES20.glGetUniformLocation(program, "aaCoef");
        mixCoefLocation = GLES20.glGetUniformLocation(program, "mixCoef");
        setBeautyProgress(0);
    }

    @Override
    public void setOtherUniform() {

    }



    public void setBeautyProgress(int progress) {
        switch (progress){
            case 1:
                setBeautyProgress(1,0.19f,0.54f);
                break;
            case 2:
                setBeautyProgress(2,0.29f,0.54f);
                break;
            case 3:
                setBeautyProgress(3,0.17f,0.39f);
                break;
            case 4:
                setBeautyProgress(3,0.25f,0.54f);
                break;
            case 5:
                setBeautyProgress(4,0.13f,0.54f);
                break;
            case 6:
                setBeautyProgress(4,0.19f,0.69f);
                break;
            default:
                setBeautyProgress(0,0f,0f);
                break;
        }
    }

    private void setBeautyProgress(int iternum,float aaCoef,float mixCoef){
        GLES20.glUniform1f(iternumLocation,iternum);
        GLES20.glUniform1f(aaCoefLocation,aaCoef);
        GLES20.glUniform1f(mixCoefLocation,mixCoef);
    }
}
