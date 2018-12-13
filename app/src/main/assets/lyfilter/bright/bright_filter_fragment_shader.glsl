
precision mediump float;
//varying vec2 textureCoordinate;
//uniform sampler2D vTexture;
//
//void main()
// {
//   gl_FragColor = texture2D( vTexture, textureCoordinate );
// }

varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float brightness;

void main()
{
   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);

   gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);
}