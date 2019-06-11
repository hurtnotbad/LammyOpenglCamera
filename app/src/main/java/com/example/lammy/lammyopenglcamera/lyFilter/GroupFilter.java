package com.example.lammy.lammyopenglcamera.lyFilter;

import com.example.lammy.lammyopenglcamera.helper.FBOHelper;
import java.util.ArrayList;

public class GroupFilter {

    private  ArrayList<LyFilter> lyFilters = new ArrayList<>();

    private CameraFilter cameraFilter;
    //创建离屏buffer
    private int fTextureSize = 2;
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[fTextureSize];
    private int width , height;

    public GroupFilter(CameraFilter cameraFilter){
        this.cameraFilter = cameraFilter;
        width = cameraFilter.getWidth();
        height = cameraFilter.getHeight();
        FBOHelper.createFrameBuffer(width,height,fFrame,fRender,fTexture);
    }

    public void setCameraFilter(CameraFilter cameraFilter){
        this.cameraFilter = cameraFilter;
    }

    public void addFilter(LyFilter lyFilter){
        lyFilters.add(lyFilter);
    }
    public void removeAllFilter(){
        lyFilters.clear();
    }
    private int textureIndex=0;
    public void draw(){
        cameraFilter.draw();

        textureIndex=0;
        for(LyFilter filter:lyFilters){

            FBOHelper.bindFrameTexture(fFrame[0], fTexture[textureIndex%2], fRender[0]);
            if(textureIndex ==0){
                filter.setTextureId(cameraFilter.getOutTextureId());
            }else{
                filter.setTextureId(fTexture[(textureIndex - 1)%2]);
            }
                // 因为绘制到buffer，会导致
                filter.onSizeChanged(width , height);
                filter.flipY();
                filter.draw();
                FBOHelper.unBindFrameBuffer();
                textureIndex++;
        }

    }
    public int getOutTexture() {
        if (lyFilters.size() == 0) {
            return cameraFilter.getOutTextureId();
        } else {
            return  fTexture[(textureIndex - 1) % 2];
        }
    }


    public void onSizeChanged(int width, int height) {
        this.width=width;
        this.height=height;
        cameraFilter.onSizeChanged(width , height);
        FBOHelper.createFrameBuffer(width,height,fFrame,fRender,fTexture);
    }



}
