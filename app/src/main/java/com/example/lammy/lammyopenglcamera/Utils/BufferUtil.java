package com.example.lammy.lammyopenglcamera.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Created by lenovo on 2016/9/6.
 */
public class BufferUtil {

    public static FloatBuffer floatToBuffer(float[] a) {
        FloatBuffer mBuffer;
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        //数组排序用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        mBuffer = mbb.asFloatBuffer();
        mBuffer.put(a);
        mBuffer.position(0);
        return mBuffer;
    }
    public static IntBuffer intToBuffer(int[] a) {
        IntBuffer mBuffer2;
        //先初始化buffer，数组的长度*4，因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        //数组排序用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        mBuffer2 = mbb.asIntBuffer();
        mBuffer2.put(a);
        mBuffer2.position(0);
        return mBuffer2;
    }

    public static ShortBuffer shortToBuffer(short[] shortDatas) {
        ByteBuffer dlb = ByteBuffer.allocateDirect(shortDatas.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = dlb.asShortBuffer();
        shortBuffer.put(shortDatas);
        shortBuffer.position(0);
        return  shortBuffer;
    }


}