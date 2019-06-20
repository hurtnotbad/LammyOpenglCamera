package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniform1f;

public class BrightFilter extends LyFilter{


    public BrightFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public BrightFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }
    public BrightFilter(Context context) {
        super(context,  NoFilterVertexShader ,  "lyfilter/bright/bright_filter_fragment_shader.glsl" );
    }




    private int brightnessLocation;
    private float brightness;

    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program ,  "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program ,  "inputTextureCoordinate" );
        vTextureLocation = GLES20.glGetUniformLocation(program,  "inputImageTexture" );

        brightnessLocation =  GLES20.glGetUniformLocation(program,  "brightness" );
    }


    @Override
    public void setOtherUniform() {
        glUniform1f(brightnessLocation , brightness);
    }


    /**
     * brightness value ranges from -1.0 to 1.0, with 0.0 as the normal level
     * recommend value ranges[-0.5 , 0.5]
     */
    public void setBrightness(float brightness){
        this.brightness = brightness;
    }

}
