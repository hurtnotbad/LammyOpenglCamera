package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

public class CameraView extends RelativeLayout {
    public CameraView(Context context) {
        super(context); inflate(context);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs); inflate(context);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); inflate(context);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context);
    }


    private GLSurfaceView glSurfaceView;
    private void inflate(Context context){
        View view =LayoutInflater.from(context).inflate(R.layout.camera_view, this);

        glSurfaceView = findViewById(R.id.glSurfaceView);

//        addView(view);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    Size previewSize = new Size(0,0);
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(previewSize.getWidth()==0||previewSize.getHeight()==0){
            previewSize =CameraInterface.getCameraViewSize(this.getContext());
            RelativeLayout.LayoutParams  lp = (LayoutParams) glSurfaceView.getLayoutParams();
            lp.width =previewSize.getWidth();
            lp.height =previewSize.getHeight();
            glSurfaceView.setLayoutParams(lp);

//            glSurfaceView.layout(0,0,previewSize.getWidth(),previewSize.getHeight());
            LogUtil.e("0 onLayout.......   w = "+glSurfaceView.getWidth()+" h = "+ glSurfaceView.getHeight());
        }
        LogUtil.e("1 onLayout.......   w = "+glSurfaceView.getWidth()+" h = "+ glSurfaceView.getHeight());


    }

    public GLSurfaceView getGlSurfaceView() {
        return glSurfaceView;
    }
}
