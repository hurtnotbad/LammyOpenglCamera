package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;

public class NoFilter extends LyFilter{

    public NoFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public NoFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }
    public NoFilter(Context context) {
        super(context, "lyfilter/noFilter/no_filter_vertex_shader.glsl", "lyfilter/noFilter/no_filter_fragment_shader.glsl");
    }


    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "inputTextureCoordinate");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
    }


    @Override
    public void setOtherUniform() {

    }




}
