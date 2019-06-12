attribute vec4 vPosition;
attribute vec2 vCoord;
varying vec2 aCoord;

void main(){
aCoord =vec2(vCoord.x ,1.0 - vCoord.y) ;
gl_Position = vPosition;
//gl_Position = vec4(vPosition.r,  -vPosition.g ,vPosition.b,vPosition.a);
}