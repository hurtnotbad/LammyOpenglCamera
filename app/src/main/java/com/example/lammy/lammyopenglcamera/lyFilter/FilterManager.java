package com.example.lammy.lammyopenglcamera.lyFilter;

import android.content.Context;

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


    public static NoFilter noFilter;
    public static GrayFilter grayFilter;
    public static FaceColorFilter faceColorFilter;
    public static BeautyFilter beautyFilter;
    public static MagnifierFilter magnifierFilter;
    public static ZipPkmAnimationFilter zipPkmAnimationFilter;
    public static BrightFilter brightFilter;

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



}
