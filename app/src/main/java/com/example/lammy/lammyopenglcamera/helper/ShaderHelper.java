package com.example.lammy.lammyopenglcamera.helper;

import android.content.Context;
import android.content.res.Resources;

import com.example.lammy.lammyopenglcamera.Utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

//opengl es 2.0 静态导入


public class ShaderHelper {

    // 读取raw文件下shader文件为string
    public static String readTextFileFromResourceRaw(Context context , int resourceId){
        StringBuilder body = new StringBuilder();
        try{
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        }catch (IOException e){
            throw new RuntimeException("无法读取资源");
        }
        return body.toString();
    }
    // 读取assets文件下shader文件为string
    public static String readTextFileFromResourceAssets(Context context , String assetsPath){
        StringBuilder body = new StringBuilder();
        try{
            InputStream inputStream = context.getAssets().open(assetsPath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while((nextLine = bufferedReader.readLine()) != null){
                body.append(nextLine);
                body.append('\n');
            }
        }catch (IOException e){
            throw new RuntimeException("无法读取资源");
        }
        return body.toString();
    }
    // 读取assets文件下shader文件为string
    public static String uRes(Resources mRes, String path){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=mRes.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }


    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER ,shaderCode);
    }
    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER ,shaderCode);
    }
    private static int compileShader(int type ,String shaderCode) {

        final int shaderObjectId = glCreateShader(type);
        if(shaderObjectId == 0){
            LogUtil.e("创建着色器失败！");
            return 0;
        }
        //上传代码到着色器
        glShaderSource(shaderObjectId , shaderCode);
        //编译着色器
        glCompileShader(shaderObjectId);

        //取出编译状态
        final int[] compileStatus = new int[1];
        //将编译状态写入第0个元素
        glGetShaderiv(shaderObjectId , GL_COMPILE_STATUS , compileStatus , 0);


        // 编译失败
        if(compileStatus[0] == 0){
            LogUtil.e("compiling failed");
            // 输出编译日志
            LogUtil.e("result of compiling source :"+"\n" + shaderCode + "\n:"
                    + glGetShaderInfoLog(shaderObjectId));
            glDeleteShader(shaderObjectId);
            return 0;
        }else{
            LogUtil.e("shader compiling success");
        }

        return shaderObjectId;
    }

    // 将 顶点着色器和片着色器链接到一个program中
    public static int linkProgram(int vertexShaderId , int fragmentShaderId){
        final int programObjectId = glCreateProgram();

        if(programObjectId == 0){
            LogUtil.e("can not create new program !");
            return 0;
        }
        glAttachShader(programObjectId ,vertexShaderId);
        glAttachShader(programObjectId ,fragmentShaderId);

        glLinkProgram(programObjectId);

        final int[] linkState = new int[1];
        glGetProgramiv(programObjectId , GL_LINK_STATUS , linkState,0);



        if(linkState[0] == 0){
            LogUtil.e("linking of program failed !");
            // 输出编译日志
            LogUtil.e("result of linking program :"+"\n" + glGetProgramInfoLog(programObjectId));
            return 0;
        }else{
            LogUtil.e("linking of program success !");
        }

        return programObjectId;
    }


    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);
        final  int[] state = new int[1];
        glGetProgramiv(programObjectId , GL_VALIDATE_STATUS ,state , 0 );

        // 输出编译日志
        LogUtil.e("result of validating program :"+"\n" + state[0] + "\n: log:"
                + glGetProgramInfoLog(programObjectId));

        return state[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource , String fragmentShaderSource){
        int program;
        //compile the shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader , fragmentShader);

        if(LogUtil.debug)
        {
            ShaderHelper.validateProgram(program);
        }

        return program;
    }

}
