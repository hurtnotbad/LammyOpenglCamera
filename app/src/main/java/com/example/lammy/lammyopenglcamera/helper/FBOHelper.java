package com.example.lammy.lammyopenglcamera.helper;

import android.opengl.GLES20;

public class FBOHelper {

    //创建FrameBuffer
    public static boolean createFrameBuffer(int width, int height,int[]fFrame, int[] fRender, int[] fTexture) {
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, fRender, 0);

        // 生成纹理
        TextureHelper.genTexturesWithParameter(fTexture.length, fTexture, 0, GLES20.GL_RGBA, width, height );
        // 创建 renderbuffer，分配空间
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(status==GLES20.GL_FRAMEBUFFER_COMPLETE){
            unBindFrameBuffer();
            return true;
        }
        unBindFrameBuffer();
        return false;
    }

    public static void bindFrameTexture(int frameBufferId,int textureId, int fRenderId){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, fRenderId);

    }

    public static void unBindFrameBuffer(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    public static void deleteFrameBuffer(int[] fRender, int[] fFrame, int[] fTexture) {
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }
}
