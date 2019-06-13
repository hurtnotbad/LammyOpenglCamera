package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import java.util.ArrayList;
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
        setFilterChooseFilter();
    }

    private ArrayList<LyFilter> filters = new ArrayList<>();
    private ArrayList<Point> filtersStartPoints = new ArrayList<>();

    private void getFiltersStartPoints( int width, int height){
        int offsetW = width /24;
        int offsetH = height/24;
        int size = filters.size();
        for(int i = 0; i < size; i ++){
            int index = i/9;
            int offX = offsetW + index * width + (i % 3) * width/3;
            int offY = offsetH + (2-(i % 9)/3) * height/3;
            filtersStartPoints.add(new Point(offX,offY));
        }

    }


    public  void setFilterChooseFilter(){
        filters.add(noFilter);
        filters.add(grayFilter);
        filters.add(brightFilter);
        filters.add(magnifierFilter);
        filters.add(faceColorFilter);
        filters.add(beautyFilter);
        filters.add(noFilter);
        filters.add(noFilter);
        filters.add(noFilter);
        filters.add(noFilter);
    }
    public void drawFilters(int textureId, int width, int height){

        if(filtersStartPoints.size() < filters.size()){
            getFiltersStartPoints( width,  height);
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int size = filters.size();
        for(int i = 0; i < size; i ++){
            LyFilter filter = filters.get(i);
            filter.setTextureId(textureId);
            GLES20.glViewport(filtersStartPoints.get(i).x, filtersStartPoints.get(i).y , width/4, height/4);
            filter.drawNoClear();
        }
        GLES20.glViewport(0,0,width,height);

//        noFilter.setTextureId(texttureId);
//        grayFilter.setTextureId(texttureId);
//        brightFilter.setTextureId(texttureId);
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        int offsetW = width /24;
//        int offsetH = height/24;
//        GLES20.glViewport(offsetW, 0+offsetH , width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(offsetW, height/3 +offsetH, width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(offsetW, height*2/3+offsetH , width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(width/3+offsetW,0+offsetH, width/4, height/4);
//        grayFilter.drawNoClear();
//        GLES20.glViewport(width/3+offsetW, height/3+offsetH , width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(width/3+offsetW, height*2/3+offsetH , width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(width*2/3+offsetW,0+offsetH, width/4, height/4);
//        brightFilter.drawNoClear();
//        GLES20.glViewport(width*2/3+offsetW, height/3 +offsetH, width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(width*2/3+offsetW, height*2/3 +offsetH, width/4, height/4);
//        noFilter.drawNoClear();
//        GLES20.glViewport(0,0,width,height);


    }

}
