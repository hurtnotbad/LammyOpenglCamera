package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

public class ChooseView extends RelativeLayout {
    public ChooseView(Context context) {
        super(context);
        init(context);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.choose_view, this);
        setTextViewColor(0);

    }


    private int current = -1;
    private int size = 2;
    private int downX =0;
    private int state = -1;// 0 为拍照 1 为 美颜
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int)event.getRawX();
//                LogUtil.e("ACTION_DOWN");
                if(state == -1) {
                    layout(getLeft() + getWidth() / 4, getTop(), getRight() + getWidth() / 4, getBottom());
                    state = 0;
                    setTextViewColor(state);
                }
                break;

            case MotionEvent.ACTION_MOVE:
//                LogUtil.e("ACTION_MOVE");
                int cX = (int)event.getRawX();
                int d = cX- downX;
                if(Math.abs(d)> getWidth()/4){
                    d = d > 0? getWidth()/2:-getWidth()/2;
                    if(state>0&& d > 0) {
                        layout(getLeft() + d, getTop(), getRight() + d, getBottom());
                        state--;
                        setTextViewColor(state);
                    }else if(state < size-1 && d < 0){
                        layout(getLeft() + d, getTop(), getRight() + d, getBottom());
                        state++;
                        setTextViewColor(state);
                    }
                    downX = cX;
                }
                break;


            case MotionEvent.ACTION_UP:
//                LogUtil.e("ACTION_UP");
                break;

            default:
                break;
        }
//            return super.onTouchEvent(event);
        return true;
    }

    private OnStateChangeListener onStateChangeListener;
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setTextViewColor(int state){
        TextView textView_take =findViewById(R.id.take_view);
        TextView textView_beauty =findViewById(R.id.beauty_view);
//        int chooseColor = 0x1C86EEff;
        int chooseColor = 0xff1C86EE;
        int unChooseColor = 0xffffffff;

        switch (state){
            case 0:
                textView_take.setTextColor(chooseColor);
                textView_beauty.setTextColor(unChooseColor);
                if(onStateChangeListener != null){
                    onStateChangeListener.onStateChange();
                }
                break;
            case 1:
                textView_take.setTextColor(unChooseColor);
                textView_beauty.setTextColor(chooseColor);
                if(onStateChangeListener != null){
                    onStateChangeListener.onStateChange();
                }
                break;
        }

    }

    public int  getState(){
        return state;
    }

    interface OnStateChangeListener{

        void onStateChange();
    }


}