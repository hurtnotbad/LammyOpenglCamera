attribute vec4 vPosition;
attribute vec2 vCoord;
uniform mat4 vMatrix;

varying vec2 textureCoordinate;

void main(){
    gl_Position = vPosition*vMatrix;
    textureCoordinate = vCoord;
}