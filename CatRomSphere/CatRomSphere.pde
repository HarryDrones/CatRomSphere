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
  double Be = Math.toRadians(35.4000);
  double Le = Math.toRadians(26.452837); 

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
float SINCOS_PRECISION = 0.5;

int SINCOS_LENGTH = int(360.0 / SINCOS_PRECISION);

color [] colors = new color[7];

      float alt = radians(-77.79f);
//Controls controls;
boolean released = false;

Controls controls;
HorizontalControl controlX;
int showControls;
boolean draggingZoomSlider = false;

float zoom = -90.0f;
float tzoom = -100.0f;

void setup() {
 size(400, 300, P3D);
//fullScreen(P3D);


  smooth();
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

void draw() {    
  background(0); 
 


  x = new float[3600];
  y = new float[3600];

  
    if (mousePressed) {
     if( (showControls == 1) && (controls.isZoomSliderEvent(mouseX, mouseY))){ // || ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY))) {
        draggingZoomSlider = true;
       
zoom = controls.getZoomValue(mouseY);
       
   //    tzoom = controlX.getZoomValue(mouseX);   
     

     // MousePress - Rotation Adjustment
     }/* else if (!draggingZoomSlider) {
      //  if (released != false){
         velocityX += (mouseY-pmouseY) * 0.01;
         velocityY -= (mouseX-pmouseX) * 0.01;
      //  }
     } */

else if ( showControls == 1 && controlX.isZoomSliderEvent(mouseX,mouseY)){
      //  draggingZoomSlider = true;
        
      tzoom = controlX.getZoomValue(mouseX);
      
           // MousePress - Rotation Adjustment
 } else if (!draggingZoomSlider) {
        if (released = true){
         velocityX += (mouseY-pmouseY) * 0.01;
         velocityY -= (mouseX-pmouseX) * 0.01;
        }
       
     } 


     
     

  }

  
  println(degrees((float)alt));



 // initStars();
  renderGlobe();
  controls.updateZoomSlider(zoom);
  controlX.updateZoomSlider(tzoom);
    controls.render();

    controlX.render();
 

 
 

  
  
  

}

void mouseReleased() {
   //released = false;
    draggingZoomSlider = false;
if (released == true){
        alt = (map(zoom,texture.height-texture.height,texture.height,radians(-90),radians(90)));
       // GHA = (map(tzoom, texture.width - texture.width, texture.width, radians(-180),radians(180)));
         GHA = (map(tzoom,texture.width - texture.width,texture.width, radians(-180),radians(180)));
        initStars();
          initializeSphere(sDetail);
          thread( "getPoints");
            renderGlobe();
}
}

boolean getPoints(){
    released = false;   
  Mz = Rz(Math.toRadians(360.0) - GHA, Mz);
  
  My =  Ry(Math.toRadians(90.0) - dec, My);
 
  int w = 0;

  for( double L0 = -180.0; L0 <= 180.0; L0 += .1 )
    {
    
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
   
    }
      w++;
      if (w == 3600){
         released = true;
      }
return released;
}


//Funcition to convert double[] to float[]
float[] toFloatArray(double[] arr) {
  if (arr == null) return null;
  int n = arr.length;
  float[] ret = new float[n];
  for (int i = 0; i < n; i++) {
  ret[i] = (float)arr[i];
  }
  return ret;
}
// end of function to convert double[] to float[]

 double[] VectorSpherical2Cartesian(double B, double L){
  
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

  M[0][0] = 1.0;
  M[1][0] = 0.0;
  M[2][0] = 0.0;
  M[0][1] = 0.0;
  M[1][1] = Math.cos(a); //Math.cos(Math.toRadians(a));
  M[2][1] = Math.sin(a); //Math.sin(Math.toRadians(a));
  M[0][2] = 0.0;
  M[1][2] = -Math.sin(a); //-Math.sin(Math.toRadians(a));
  M[2][2] = Math.cos(a); //Math.cos(Math.toRadians(a));
  
  return(M);
}

public double[][] Ry( double a, double[][] M ){

  M[0][0] = Math.cos(a);
  M[1][0] = 0.0;
  M[2][0] = -Math.sin(a);
  M[0][1] = 0.0;
  M[1][1] = 1.0;
  M[2][1] = 0.0;
  M[0][2] = Math.sin(a);
  M[1][2] = 0.0; 
  M[2][2] = Math.cos(a);
  
  return(M);
}

public double[][] Rz( double a, double[][] M ){

  M[0][0] = Math.cos(a); //Math.cos(a);
  M[1][0] = Math.sin(a);
  M[2][0] = 0.0;
  M[0][1] = -Math.sin(a);
  M[1][1] = Math.cos(a);
  M[2][1] = 0.0;
  M[0][2] = 0.0; 
  M[1][2] = 0.0; 
  M[2][2] = 1.0;
  
  return(M);
}
 
public double[] MatrixVecProd( double[][] A, double[] v, double[] res ) {

  int i,j;
  int n = 3;

  for( i=0; i<n; i++ ) {
    res[i] = 0.0;
    for( j=0; j<n; j++ ) {
    res[i] += A[i][j]*v[j];
   
  }
}

  return (res);
}


void initStars(){
   
   texture.beginDraw();
   texture.background(texmap);
   texture.endDraw();
}



void initializeSphere(int res)
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
void texturedSphere(float r, PGraphics t) {
  int v1,v11,v2;
  r = (r + 240 ) * 0.33;
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


void renderGlobe() {
 
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
  velocityX *= 0.95;
  velocityY *= 0.95;

    
  
}