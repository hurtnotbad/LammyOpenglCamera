#extension GL_OES_EGL_image_external : require
varying highp vec2 textureCoordinate;

uniform samplerExternalOES vTexture;
uniform sampler2D maskTexture;
uniform highp float intensity;

void main(){

     vec4 textureColor = texture2D(vTexture, textureCoordinate);
     int flag = 0;
      // ceil  floor
     highp   float r = floor(textureColor.r * 255.0);
     highp   float g = floor(textureColor.g * 255.0);
     highp   float b = floor(textureColor.b * 255.0);


    r = r == 255.0? 254.0:r;
    g = g == 255.0? 254.0:g;
    b = b == 255.0? 254.0:b;

     highp   float row =(b - floor(b / 16.0)*16.0) * 256.0 + g;
     highp   float col = floor(b / 16.0) * 256.0 + r;



// 开始取像素的时候用的2、3种，发现都有像素偏差，不是x 偏一个就是 y偏一个，后来直接用第一种发现无问题，可能起始位置有一个边框
     gl_FragColor = texture2D(maskTexture,vec2((row+0.0)/4096.0,(col + 0.0)/4096.0) );
//     gl_FragColor = texture2D(maskTexture,vec2((row )/4095.0,(col)/4095.0) );
//     gl_FragColor = texture2D(maskTexture,vec2((row + 1.0 )/4096.0,(col+1.0)/4096.0) );

}