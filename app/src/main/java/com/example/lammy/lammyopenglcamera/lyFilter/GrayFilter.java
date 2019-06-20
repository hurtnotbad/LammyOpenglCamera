package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;

public class GrayFilter extends LyFilter {
    int mmm = 1;
    public GrayFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public GrayFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }
    public GrayFilter(Context context) {
        super(context, NoFilterVertexShader, "lyfilter/grayFilter/gray_filter_fragment_shader.glsl");

    }

    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "inputTextureCoordinate");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
        vMatrixLocation =  GLES20.glGetUniformLocation(program, "vMatrix");
    }

    @Override
    public void setOtherUniform() {

    }


}
