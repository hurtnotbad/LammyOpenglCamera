package com.example.lammy.lammyopenglcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;


public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
    public CircleImageView(Context context) {

        super(context);
        LogUtil.e("CircleImageView construct" );
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LogUtil.e("CircleImageView construct" );
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        LogUtil.e("CircleImageView construct" );
    }




    private Paint mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mRawBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix = new Matrix();
    private float mBorderWidth = dip2px(2);
    private int mBorderColor = 0xFF0080FF;
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        Bitmap rawBitmap = getBitmap(drawable);
                    if(rawBitmap != null){
                        drawViewStyle1(canvas , rawBitmap);
                    } else {
                         super.onDraw(canvas);
                     }

    }

    private void drawViewStyle0(Canvas canvas , Bitmap rawBitmap ) {
        if (rawBitmap != null) {
            LogUtil.e("CircleImageView rawBitmap is not null");
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int viewMinSize = Math.min(viewWidth, viewHeight);
            float dstWidth = viewMinSize;
            float dstHeight = viewMinSize;
            if (mShader == null || !rawBitmap.equals(mRawBitmap)) {
                mRawBitmap = rawBitmap;
                //CLAMP：拉伸边缘, MIRROR：镜像, REPEAT：整图重复
                mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }
            if (mShader != null) {
                mMatrix.setScale(dstWidth / rawBitmap.getWidth(), dstHeight / rawBitmap.getHeight());
                mShader.setLocalMatrix(mMatrix);
            }
            mPaintBitmap.setShader(mShader);
            float radius = viewMinSize / 2.0f;
            canvas.drawCircle(radius, radius, radius, mPaintBitmap);

        }
    }

    private void drawViewStyle1(Canvas canvas ,  Bitmap rawBitmap ) {
                 if (rawBitmap != null){
                         int viewWidth = getWidth();
                         int viewHeight = getHeight();
                         int viewMinSize = Math.min(viewWidth, viewHeight);
                         float dstWidth = viewMinSize;
                         float dstHeight = viewMinSize;
                         if (mShader == null || !rawBitmap.equals(mRawBitmap)){
                                 mRawBitmap = rawBitmap;
                                 mShader = new BitmapShader(mRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                             }
                         if (mShader != null){
                                 mMatrix.setScale((dstWidth - mBorderWidth * 2) / rawBitmap.getWidth(), (dstHeight - mBorderWidth * 2) / rawBitmap.getHeight());
                                 mShader.setLocalMatrix(mMatrix);
                             }
                         mPaintBitmap.setShader(mShader);
                         mPaintBorder.setStyle(Paint.Style.STROKE);
                         mPaintBorder.setStrokeWidth(mBorderWidth);
                         mPaintBorder.setColor(mBorderColor);
                         float radius = viewMinSize / 2.0f;
                         canvas.drawCircle(radius, radius, radius - mBorderWidth / 2.0f, mPaintBorder);
                         canvas.translate(mBorderWidth, mBorderWidth);
                         canvas.drawCircle(radius - mBorderWidth, radius - mBorderWidth, radius - mBorderWidth, mPaintBitmap);

                 }

             }



    private Bitmap getBitmap(Drawable drawable){
                 if (drawable instanceof BitmapDrawable){
                         return ((BitmapDrawable)drawable).getBitmap();
                     } else if (drawable instanceof ColorDrawable){
                         Rect rect = drawable.getBounds();
                         int width = rect.right - rect.left;
                         int height = rect.bottom - rect.top;
                         int color = ((ColorDrawable)drawable).getColor();
                         Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                         Canvas canvas = new Canvas(bitmap);
                         canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
                         return bitmap;
                     } else {
                         return null;
                     }

             }


    private int dip2px(int dipVal)
    {
                 float scale = getResources().getDisplayMetrics().density;
                 return (int)(dipVal * scale + 0.5f);
    }

}
