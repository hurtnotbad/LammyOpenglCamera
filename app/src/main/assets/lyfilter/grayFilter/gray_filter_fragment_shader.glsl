//#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
//uniform samplerExternalOES vTexture;
uniform sampler2D vTexture;
void main()
 {
   gl_FragColor = texture2D( vTexture, textureCoordinate );

   float  r = (gl_FragColor.r + gl_FragColor.g + gl_FragColor.b )/3.0;
   gl_FragColor = vec4(r , r , r , 1.0);
 }