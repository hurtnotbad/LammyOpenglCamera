package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

public class FilterManager {
    private static FilterManager filterManager;
    private  FilterManager(){
    }

    public static FilterManager getInstance(){
        if(filterManager == null){
            synchronized (FilterManager.class){
                if(filterManager == null){
                    filterManager = new FilterManager();
                }
            }
        }
        return filterManager;
    }


    public  NoFilter noFilter;
    public  GrayFilter grayFilter;
    public  FaceColorFilter faceColorFilter;
    public  BeautyFilter beautyFilter;
    public  MagnifierFilter magnifierFilter;
    public  ZipPkmAnimationFilter zipPkmAnimationFilter;
    public  BrightFilter brightFilter;

    public void initAllFilter(Context context){
        if(noFilter == null){
            noFilter = new NoFilter(context);
        }
        if(grayFilter == null){
            grayFilter = new GrayFilter(context);
        }
        if(faceColorFilter == null){
            faceColorFilter = new FaceColorFilter(context);
        }
        if(beautyFilter == null){
            beautyFilter = new BeautyFilter(context);
        }
        if(magnifierFilter == null){
            magnifierFilter = new MagnifierFilter(context);
            magnifierFilter.setCenterPoint(new float[]{0.6f,0.5f});
            magnifierFilter.setOpinionSize(2f);
            magnifierFilter.setR(0.3f);
        }
        if(zipPkmAnimationFilter == null){
            zipPkmAnimationFilter = new ZipPkmAnimationFilter(context);
        }
        if(brightFilter == null){
            brightFilter = new BrightFilter(context);
            brightFilter.setBrightness(0.2f);
        }

    }


    public void drawFilters(int texttureId, int width, int height){
        noFilter.setTextureId(texttureId);
        grayFilter.setTextureId(texttureId);
        brightFilter.setTextureId(texttureId);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int offsetW = width /24;
        int offsetH = height/24;
        GLES20.glViewport(offsetW, 0+offsetH , width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(offsetW, height/3 +offsetH, width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(offsetW, height*2/3+offsetH , width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(width/3+offsetW,0+offsetH, width/4, height/4);
        grayFilter.drawNoClear();
        GLES20.glViewport(width/3+offsetW, height/3+offsetH , width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(width/3+offsetW, height*2/3+offsetH , width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(width*2/3+offsetW,0+offsetH, width/4, height/4);
        brightFilter.drawNoClear();
        GLES20.glViewport(width*2/3+offsetW, height/3 +offsetH, width/4, height/4);
        noFilter.drawNoClear();
        GLES20.glViewport(width*2/3+offsetW, height*2/3 +offsetH, width/4, height/4);
        noFilter.drawNoClear();


        GLES20.glViewport(0,0,width,height);
    }

}
