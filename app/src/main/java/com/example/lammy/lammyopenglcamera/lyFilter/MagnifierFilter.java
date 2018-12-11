package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetAttribLocation;

public class MagnifierFilter extends LyFilter {
    public MagnifierFilter(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        super(context, vertexShaderRawId, fragmentShaderRawId);
    }

    public MagnifierFilter(Context context, String vertexShaderAssetsPath, String fragmentShaderAssetsPath) {
        super(context, vertexShaderAssetsPath, fragmentShaderAssetsPath);
    }

    public MagnifierFilter(Context context) {
        super(context, "lyfilter/magnifier/magnifier_filter_vertex_shader.glsl", "lyfilter/magnifier/magnifier_filter_fragment_shader.glsl");

    }


    private int centerPointLocation;
    private int rLocation;
    private int opinionSizeLocation;
    // 圆心
    private float[] centerPoint;
    // 半径
    private float r;
    // 缩放系数
    private float opinionSize;
    /**
     * *  centerPoint 放大镜的圆心 是纹理映射的坐标，因为相机获得数据是逆时针旋转90的，因此纹理映射旋转了90，x、y的位置换了。且 正方向也 有改变
     *  r 放大镜的半径 因为纹理映射范围是[0 , 1] 因此半径取值[0 , 0.5] ， 当取值1的时候，会整体放大，看不到放大镜
     *  opinionSize 放大系数，<1时候是缩小镜， >1时候是 放大镜
     */

    @Override
    public void initUniforms() {
        vPositionLocation = glGetAttribLocation(program , "vPosition");
        vTextureCoordinateLocation = glGetAttribLocation(program , "inputTextureCoordinate");
        vTextureLocation = GLES20.glGetUniformLocation(program, "vTexture");
        vMatrixLocation =  GLES20.glGetUniformLocation(program, "vMatrix");

        centerPointLocation =  GLES20.glGetUniformLocation(program, "pointCenter");
        rLocation =  GLES20.glGetUniformLocation(program, "r");
        opinionSizeLocation =  GLES20.glGetUniformLocation(program, "opinionSize");
        setArgs(new float[]{0.5f,0.5f} , 0.5f, 2);
    }

    @Override
    public void setOtherUniform() {
        GLES20.glUniform1f(rLocation , r);
        GLES20.glUniform1f(opinionSizeLocation , opinionSize);
        GLES20.glUniform1fv(centerPointLocation,2,centerPoint,0);
    }


    /**
     * *  centerPoint 按照纹理来的,纹理是[0f,1f] 放大镜的圆心 是纹理映射的坐标
     *  r 放大镜的半径 因为纹理映射范围是[0 , 1] 因此半径取值[0 , 0.5] ， 当取值1的时候，会整体放大，看不到放大镜
     *  opinionSize 放大系数，<1时候是缩小镜， >1时候是 放大镜
     */
    public void setArgs( float centerPoint[] , float r ,float opinionSize){

        this.centerPoint = centerPoint;
        this.r =r;
        this.opinionSize = opinionSize;
    }

    public float[] getCenterPoint(){
        return centerPoint;
    }
    public float  getR(){
        return r;
    }
    public float  getOpinionSize(){
        return opinionSize;
    }
    public void setCenterPoint(float centerPoint[]){
        this.centerPoint = centerPoint;
    }
    public void  setR( float r){
        this.r = r;
    }
    public void  setOpinionSize(float opinionSize){
        this.opinionSize = opinionSize;
    }

}
