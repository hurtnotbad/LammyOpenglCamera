//#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
//uniform samplerExternalOES vTexture;
uniform sampler2D vTexture;

//放大镜的圆心
uniform float pointCenter[2];
//放大倍数
uniform float opinionSize;
// 放大镜的半径
uniform float r ;
void main()
 {

   vec4 nColor=texture2D(vTexture, textureCoordinate);
                       //放大镜效果
                        float dis=distance(vec2(textureCoordinate.x*2.0-1.0 ,(textureCoordinate.y*2.0 - 1.0)/0.5138),vec2(0.1,0.3));

                        //以r为中心进行放大，因为纹理是 旋转90°的，因此坐标x、y的方向改变了, textureCoordinate的xy变为yx，且正方向有变化
                        //圆心
                        vec2 o = vec2(pointCenter[0] , pointCenter[1]);
                        // 当前点到圆心的距离
                        float d = distance(textureCoordinate ,o);
//                        //半径
//                        float r = 0.25;
//                        // 放大倍数
//                        float opinionSize = 1.20;
                        if(d<r)
                        {
                            nColor = texture2D(vTexture,vec2(o.x + (textureCoordinate.x -o.x)/opinionSize , o.y + (textureCoordinate.y -o.y)/opinionSize));
                        }
                        gl_FragColor=nColor;
 }