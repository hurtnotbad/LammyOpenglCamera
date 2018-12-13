package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniform1f;

public class SharpenFilter extends LyFilter {
    public SharpenFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public SharpenFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }
    public SharpenFilter(Context context) {
        super(context,  "lyfilter/sharpen/sharpen_filter_vertex_shader.glsl" ,  "lyfilter/sharpen/sharpen_filter_fragment_shader.glsl" );
    }

    private float imageWidthFactor;
    private float imageHeightFactor;
    private float sharpness;
    private int imageWidthFactorLocation;
    private int imageHeightFactorLocation;
    private int sharpnessLocation;

    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program ,  "position");
        vTextureCoordinateLocation = glGetAttribLocation(program ,  "inputTextureCoordinate" );
        vTextureLocation = GLES20.glGetUniformLocation(program,  "inputImageTexture" );
        vMatrixLocation =  GLES20.glGetUniformLocation(program,  "vMatrix" );

        imageWidthFactorLocation =  GLES20.glGetUniformLocation(program,  "imageWidthFactor" );
        imageHeightFactorLocation =  GLES20.glGetUniformLocation(program,  "imageHeightFactor" );
        sharpnessLocation =  GLES20.glGetUniformLocation(program,  "sharpness" );
    }


    @Override
    public void setOtherUniform() {
        glUniform1f(imageWidthFactorLocation , 1f/width);
        glUniform1f(imageHeightFactorLocation , 1f/height);
        glUniform1f(imageHeightFactorLocation , sharpness);
    }
    /**
     * @sharpness: from -4.0 to 4.0, with 0.0 as the normal level
     */

    public void setSharpness(float sharpness){
        this.sharpness = sharpness;
    }

}
