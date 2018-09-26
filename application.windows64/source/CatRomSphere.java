import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class CatRomSphere extends PApplet {

/*
 The sphere is drawn using the example from processing/example/texturesphere
 The green circle is drawn based on code from Andres Ruiz
 I dont claim anything other than I fused it all together

*/

PImage texmap;
PGraphics texture;

 float[] x = new float[3600];
 float[] y = new float[3600];

  // double GHA = Math.toRadians(162.3366651);
      double GHA = Math.toRadians(100);
// double dec = Math.toRadians(42); 
    double dec = Math.toRadians(-32);
 //    double dec = mouseY;
  double Be = Math.toRadians(35.4000f);
  double Le = Math.toRadians(26.452837f); 

 double[]vv = new double[3]; //, vy[3], vyz[3];
 double[]vy = new double[3];
 double[]vyz = new double[3];
 double[]wpt = new double[3600];
 float[]WPT = new float[3600];

 double[][]My = new double[3][3];

 double[][]Mz = new double[3][3];
 
 
 
 int sDetail = 65;  // Sphere detail setting
float rotationX = 0;
float rotationY = 0;
float velocityX = 0;
float velocityY = 0;
//float globeRadius = 600;
float pushBack = -400;
 
float globeRadius = 600;
float[] cx, cz, sphereX, sphereY, sphereZ;
float sinLUT[];
float cosLUT[];
float SINCOS_PRECISION = 0.5f;

int SINCOS_LENGTH = PApplet.parseInt(360.0f / SINCOS_PRECISION);

int [] colors = new int[7];

      float alt = radians(-77.79f);
//Controls controls;
boolean released = true;

Controls controls;
HorizontalControl controlX;
int showControls;
boolean draggingZoomSlider = false;

float zoom = -90.0f;
float tzoom = -100.0f;

public void setup() {
 
//fullScreen(P3D);


  
  noFill();   
     texmap = loadImage("world32k.jpg"); 
//  texmap = loadImage("alphatest.png");
  texture = createGraphics(texmap.width, texmap.height);
  controls = new Controls();
  controlX = new HorizontalControl();
  showControls = 1;    
  initStars();

  getPoints();
  initializeSphere(sDetail);
}

public void draw() {    
  background(0); 
 


  x = new float[3600];
  y = new float[3600];

  
    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY)) || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
        draggingZoomSlider = true;
       
zoom = controls.getZoomValue(mouseY);
 tzoom = controlX.getZoomValue(mouseX,mouseY);      
   //    tzoom = controlX.getZoomValue(mouseX);   
     

     // MousePress - Rotation Adjustment
  //   }/* else if (!draggingZoomSlider) {
      //  if (released != false){
    //     velocityX += (mouseY-pmouseY) * 0.01;
    //     velocityY -= (mouseX-pmouseX) * 0.01;
      //  }*/
     } 

//else if ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY)){
      //  draggingZoomSlider = true;
        
    //  tzoom = controlX.getZoomValue(mouseX);
      
           // MousePress - Rotation Adjustment
  else if (!draggingZoomSlider) {
        if (released == true){
         velocityX += (mouseY-pmouseY) * 0.01f;
         velocityY -= (mouseX-pmouseX) * 0.01f;
        // draggingZoomSlider = false;
        }
       
     } 


     
     

  }

  
//  println(degrees((float)alt));



 // initStars();
  renderGlobe();
  controls.updateZoomSlider(zoom);
  controlX.updateZoomSlider(tzoom);
    controls.render();

    controlX.render();
 

 
 

  
  
  

}

public void mouseReleased() {
   //released = false;
   // draggingZoomSlider = false;
if (released == true  && draggingZoomSlider == true){
        alt = (map(zoom,texture.height-texture.height,texture.height,radians(-90),radians(90)));
       // GHA = (map(tzoom, texture.width - texture.width, texture.width, radians(-180),radians(180)));
         GHA = (map(tzoom,texture.width - texture.width,texture.width, radians(-180),radians(180)));
        initStars();
          initializeSphere(sDetail);
          thread( "getPoints");
            renderGlobe();
}
draggingZoomSlider = false;
}

public boolean getPoints(){
  //  released = false;   
  Mz = Rz(Math.toRadians(360.0f) - GHA, Mz);
  
  My =  Ry(Math.toRadians(90.0f) - dec, My);
 
  int w = 0;

  for( double L0 = -180.0f; L0 <= 180.0f; L0 += .1f )
    {
    released = false;
    println("top of For LooP; " + released);
      vv =  VectorSpherical2Cartesian(alt,Math.toRadians(L0) );

      vy =  MatrixVecProd( My, vv, vy );

      vyz =  MatrixVecProd( Mz, vy, vyz );

      wpt[w] = C2ELat( vyz[0], vyz[1], vyz[2]);
      wpt[w+1] = C2ELon( vyz[0], vyz[1], vyz[2]);

      WPT = toFloatArray(wpt);

      x[w] = map(WPT[w+1],radians(-180) ,radians(180),texture.width, texture.width - texture.width);
      y[w] = map(WPT[w],radians(-90),(radians(90)),texture.height,texture.height - texture.height);

      texture.beginDraw();
      texture.point(x[w],y[w]);
      texture.noFill();
      texture.stroke(0,255,0);
      texture.strokeWeight(3);
      texture.beginShape();
      texture.curveVertex(x[w],y[w]);
      texture.curveVertex(x[w],y[w]);
      texture.endShape();
      texture.endDraw();
   released = true;
    }
    

      w++;

     // }
      println(released);
      println("Before return statement: " + released);
return released;
}


//Funcition to convert double[] to float[]
public float[] toFloatArray(double[] arr) {
  if (arr == null) return null;
  int n = arr.length;
  float[] ret = new float[n];
  for (int i = 0; i < n; i++) {
  ret[i] = (float)arr[i];
  }
  return ret;
}
// end of function to convert double[] to float[]

 public double[] VectorSpherical2Cartesian(double B, double L){
  
   double v[] = new double[3];
   v[0] = Math.cos(B) * Math.cos(L);
   v[1] = Math.cos(B) * Math.sin(L);
   v[2] = Math.sin(B);

   return(v);
   
 }

public double C2ELat( double x, double y, double z )
{
  double[]res = new double[3];
  res[0] = Math.sqrt( x*x+y*y+z*z);  //R
//*B = ASIN(z/(*R));
  res[1] = Math.atan2( z, Math.sqrt(x*x+y*y) ); //B
  res[2] = Math.atan2( y, x ); //L

  return (res[1]);

}

public double C2ELon( double x, double y, double z )
{
  double[]res = new double[3];
  res[0] = Math.sqrt( x*x+y*y+z*z);  //R
  res[1] = Math.atan2( z, Math.sqrt(x*x+y*y) ); //B
  res[2] = Math.atan2( y, x ); //L

  return (res[2]);

}
 

public double[] E2C( double B, double L, double R )
{
  double[]res = new double[3];
  
  res[0] = R*Math.cos((B))*Math.cos((L));
  res[1] = R*Math.cos((B))*Math.sin((L));
  res[2] = R*Math.sin((B));
 

 
  return(res);
}
 
 public double[][] Rx( double a, double[][] M ){

  M[0][0] = 1.0f;
  M[1][0] = 0.0f;
  M[2][0] = 0.0f;
  M[0][1] = 0.0f;
  M[1][1] = Math.cos(a); //Math.cos(Math.toRadians(a));
  M[2][1] = Math.sin(a); //Math.sin(Math.toRadians(a));
  M[0][2] = 0.0f;
  M[1][2] = -Math.sin(a); //-Math.sin(Math.toRadians(a));
  M[2][2] = Math.cos(a); //Math.cos(Math.toRadians(a));
  
  return(M);
}

public double[][] Ry( double a, double[][] M ){

  M[0][0] = Math.cos(a);
  M[1][0] = 0.0f;
  M[2][0] = -Math.sin(a);
  M[0][1] = 0.0f;
  M[1][1] = 1.0f;
  M[2][1] = 0.0f;
  M[0][2] = Math.sin(a);
  M[1][2] = 0.0f; 
  M[2][2] = Math.cos(a);
  
  return(M);
}

public double[][] Rz( double a, double[][] M ){

  M[0][0] = Math.cos(a); //Math.cos(a);
  M[1][0] = Math.sin(a);
  M[2][0] = 0.0f;
  M[0][1] = -Math.sin(a);
  M[1][1] = Math.cos(a);
  M[2][1] = 0.0f;
  M[0][2] = 0.0f; 
  M[1][2] = 0.0f; 
  M[2][2] = 1.0f;
  
  return(M);
}
 
public double[] MatrixVecProd( double[][] A, double[] v, double[] res ) {

  int i,j;
  int n = 3;

  for( i=0; i<n; i++ ) {
    res[i] = 0.0f;
    for( j=0; j<n; j++ ) {
    res[i] += A[i][j]*v[j];
   
  }
}

  return (res);
}


public void initStars(){
   
   texture.beginDraw();
   texture.background(texmap);
   texture.endDraw();
}



public void initializeSphere(int res)
{
  sinLUT = new float[SINCOS_LENGTH];
  cosLUT = new float[SINCOS_LENGTH];

  for (int i = 0; i < SINCOS_LENGTH; i++) {
    sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
    cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
  }

  float delta = (float)SINCOS_LENGTH/res;
  float[] cx = new float[res];
  float[] cz = new float[res];
  
  // Calc unit circle in XZ plane
  for (int i = 0; i < res; i++) {
    cx[i] = -cosLUT[(int) (i*delta) % SINCOS_LENGTH];
    cz[i] = sinLUT[(int) (i*delta) % SINCOS_LENGTH];
  }
  
  // Computing vertexlist vertexlist starts at south pole
  int vertCount = res * (res-1) + 2;
  int currVert = 0;
  
  // Re-init arrays to store vertices
  sphereX = new float[vertCount];
  sphereY = new float[vertCount];
  sphereZ = new float[vertCount];
  float angle_step = (SINCOS_LENGTH*0.5f)/res;
  float angle = angle_step;
  
  // Step along Y axis
  for (int i = 1; i < res; i++) {
    float curradius = sinLUT[(int) angle % SINCOS_LENGTH];
    float currY = -cosLUT[(int) angle % SINCOS_LENGTH];
    for (int j = 0; j < res; j++) {
      sphereX[currVert] = cx[j] * curradius;
      sphereY[currVert] = currY;
      sphereZ[currVert++] = cz[j] * curradius;
    }
    angle += angle_step;
  }
  sDetail = res;
}

// Generic routine to draw textured sphere
public void texturedSphere(float r, PGraphics t) {
  int v1,v11,v2;
  r = (r + 240 ) * 0.33f;
  beginShape(TRIANGLE_STRIP);
  texture(t);
  float iu=(float)(t.width-1)/(sDetail);
  float iv=(float)(t.height-1)/(sDetail);
  float u=0,v=iv;
  for (int i = 0; i < sDetail; i++) {
    vertex(0, -r, 0,u,0);
    vertex(sphereX[i]*r, sphereY[i]*r, sphereZ[i]*r, u, v);
    u+=iu;
  }
  vertex(0, -r, 0,u,0);
  vertex(sphereX[0]*r, sphereY[0]*r, sphereZ[0]*r, u, v);
  endShape();   
  
  // Middle rings
  int voff = 0;
  for(int i = 2; i < sDetail; i++) {
    v1=v11=voff;
    voff += sDetail;
    v2=voff;
    u=0;
    beginShape(TRIANGLE_STRIP);
    texture(t);
    for (int j = 0; j < sDetail; j++) {
      vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1++]*r, u, v);
      vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2++]*r, u, v+iv);
      u+=iu;
    }
  
    // Close each ring
    v1=v11;
    v2=voff;
    vertex(sphereX[v1]*r, sphereY[v1]*r, sphereZ[v1]*r, u, v);
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v+iv);
    endShape();
    v+=iv;
  }
  u=0;
  
  // Add the northern cap
  beginShape(TRIANGLE_STRIP);
  texture(t);
  for (int i = 0; i < sDetail; i++) {
    v2 = voff + i;
    vertex(sphereX[v2]*r, sphereY[v2]*r, sphereZ[v2]*r, u, v);
    vertex(0, r, 0,u,v+iv);    
    u+=iu;
  }
  vertex(sphereX[voff]*r, sphereY[voff]*r, sphereZ[voff]*r, u, v);
  endShape();
  
}


public void renderGlobe() {
 
  pushMatrix();
  translate(width/2, height/2, pushBack);
  pushMatrix();
  noFill();
  stroke(255,200);
  strokeWeight(2);
  smooth();
  popMatrix();
  lights();    
  pushMatrix();
  
        rotateX( radians(-rotationX) );
   
    rotateY( radians( - rotationY) );
  
  fill(200);
  noStroke();
  textureMode(IMAGE);  
  texturedSphere(globeRadius, texture);
  popMatrix();  
  popMatrix();
  rotationX += velocityX;
  rotationY += velocityY;
  velocityX *= 0.95f;
  velocityY *= 0.95f;

    
  
}
/*

 Kepler Visualization - Controls
 
 GUI controls added by Lon Riesberg, Laboratory for Atmospheric and Space Physics
 lon@ieee.org
 
 April, 2012
 
 Current release consists of a vertical slider for zoom control.  The slider can be toggled
 on/off by pressing the 'c' key.
 
 Slide out controls that map to the other key bindings is currently being implemented and
 will be released soon.
 
*/

class Controls {
   
   int barWidth;   
   int barX;                          // x-coordinate of zoom control
   int minY, maxY;                    // y-coordinate range of zoom control
   float minZoomValue, maxZoomValue;  // values that map onto zoom control
   float valuePerY;                   // zoom value of each y-pixel 
   int sliderY;                       // y-coordinate of current slider position
   float sliderValue;                 // value that corresponds to y-coordinate of slider
   int sliderWidth, sliderHeight;
   int sliderX;                       // x-coordinate of left-side slider edge                     
   
   Controls () {
      
      barX = 40;
      barWidth = 15;
 
      minY = 40;
      maxY = minY + height/3 - sliderHeight/2;
           
      minZoomValue = height - height;
      maxZoomValue = height;   // 300 percent
      valuePerY = (maxZoomValue - minZoomValue) / (maxY - minY);
      
      sliderWidth = 25;
      sliderHeight = 10;
      sliderX = (barX + (barWidth/2)) - (sliderWidth/2);      
      sliderValue = minZoomValue; 
      sliderY = minY;     
   }
   
   
   public void render() {

     // strokeWeight(1.5); 
        strokeWeight(1); 
    //  stroke(105, 105, 105);  // fill(0xff33ff99);
   //   stroke(0xff33ff99);  // fill(0xff33ff99);  0xffff0000
       stroke(0xffff0000);
      
      // zoom control bar
      fill(0, 0, 0, 0);
        
      rect(barX, minY, barWidth, maxY-minY);
      
      // slider
     // fill(105, 105, 105); //0x3300FF00
       fill(0xffff0000); // 0xff33ff99//0x3300FF00
      rect(sliderX, sliderY, sliderWidth, sliderHeight);
   }
   
   
   public float getZoomValue(int y) {
      if ((y >= minY) && (y <= (maxY - sliderHeight/2))) {
         sliderY = (int) (y - (sliderHeight/2));     
         if (sliderY < minY) { 
            sliderY = minY; 
         } 
         sliderValue = (y - minY) * valuePerY + minZoomValue;
      }     
      return sliderValue;
   }
   
   
   public void updateZoomSlider(float value) {
      int tempY = (int) (value / valuePerY) + minY;
      if ((tempY >= minY) && (tempY <= (maxY-sliderHeight))) {
         sliderValue = value;
         sliderY = tempY;
      }
   }
   
   
   public boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderTop = (int) (sliderY - (sliderHeight/2)) - slop;
      int sliderBottom = sliderY + sliderHeight + slop;
      return ((x >= sliderX) && (x <= (sliderX    + sliderWidth)) && (y >= sliderTop)  && (y <= sliderBottom) || draggingZoomSlider );
   } 
}
 
/*
I modified this so the slider is horizontal.  That gives me a vertical for
tweaking altitude and horizontal for right ascension/longitude
*/

/*

 Kepler Visualization - Controls
 
 GUI controls added by Lon Riesberg, Laboratory for Atmospheric and Space Physics
 lon@ieee.org
 
 April, 2012
 
 Current release consists of a vertical slider for zoom control.  The slider can be toggled
 on/off by pressing the 'c' key.
 
 Slide out controls that map to the other key bindings is currently being implemented and
 will be released soon.
 
*/

class HorizontalControl {
   
   int barHeight;   
   int barY;                          // y-coordinate of zoom control
   int minX, maxX;                    // x-coordinate range of zoom control
   float minZoomValue, maxZoomValue;  // values that map onto zoom control
   float valuePerX;                   // zoom value of each y-pixel 
   int sliderY;                       // y-coordinate of current slider position
   float sliderValue;                 // value that corresponds to y-coordinate of slider
   int sliderWidth, sliderHeight;
   int sliderX;                       // x-coordinate of left-side slider edge                     
   
   HorizontalControl () {
      
      barY = 15; //40;
      barHeight = 40; //15;
 
      minX = 40;
      maxX = minX + width/3 - sliderWidth/2;
           
      minZoomValue = texture.width - texture.width;
      maxZoomValue = texture.width;   // 300 percent
      valuePerX = (maxZoomValue - minZoomValue) / (maxX - minX);
      
      sliderWidth = 10; //25;
      sliderHeight = 25; //10;
     // sliderY = (barY + (barHeight/2)) - (sliderHeight/2);
      sliderY = (barY - (sliderHeight/2)) + (barHeight/2);
      sliderValue = minZoomValue; 
      sliderX = minX;     
   }
   
   
   public void render() {
       pushMatrix();


     // strokeWeight(1.5); 
        strokeWeight(1); 
    //  stroke(105, 105, 105);  // fill(0xff33ff99);
   //   stroke(0xff33ff99);  // fill(0xff33ff99);  0xffff0000
       stroke(0xffff0000);
      
      // zoom control bar
      fill(0, 0, 0, 0);
        
      rect(minX,barHeight + height - height/4,maxX-minX, barY );
     // rect(maxX-minX, barHeight/2,minX,barY + height - height/4 );
      
      // slider
     // fill(105, 105, 105); //0x3300FF00
       fill(0xffff0000); // 0xff33ff99//0x3300FF00

      rect(sliderX, sliderY + height - height/4 + sliderHeight/2 , sliderWidth, sliderHeight);

      popMatrix();
      
   }
   
   
   public float getZoomValue(int x, int y) {
      if ((x >= minX) && (x <= (maxX - sliderWidth/2)) && (y > (height - height/3))) {
         sliderX = (int) (x - (sliderWidth/2));     
         if (sliderX < minX) { 
            sliderX = minX; 
         } 
         sliderValue = (x - minX) * valuePerX + minZoomValue;
      }     
      return sliderValue;
   }
   
   
   public void updateZoomSlider(float value) {
      int tempX = (int) (value / valuePerX) + minX;
      
      if ( (tempX >= minX) && (tempX <= (maxX+sliderWidth))  ) {
         sliderValue = value;
         sliderX = tempX;
      }
   }
   
   
/*   boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderTop = (int) (sliderY - (sliderHeight/2)) - slop;
      int sliderBottom = sliderY + sliderHeight + slop;
      return ((x >= sliderX) && (x <= (sliderX    + sliderWidth)) && (y >= sliderTop)  && (y <= sliderBottom) || draggingZoomSlider );
   } */
   
      public boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderLeft = (int) (sliderX - (sliderWidth/2)) - slop;
      int sliderRight = sliderX + sliderWidth + slop;
    //  return ((y >= sliderY + height - height/4) && (y <= (sliderY + height - height/4    + sliderHeight)) && (x >= sliderLeft)  && (x <= sliderRight) || draggingZoomSlider );
           return ((y >= sliderY + height - height/4 - sliderHeight/2) && (y <= (sliderY + height - height/4 + sliderHeight*2 )) && (x >= sliderLeft )  && (x <= sliderRight ) || draggingZoomSlider );
   } 
}
  public void settings() {  size(400, 300, P3D);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "CatRomSphere" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
