 
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
   
   
   void render() {
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
   
   
   float getZoomValue(int x) {
      if ((x >= minX) && (x <= (maxX - sliderWidth/2))) {
         sliderX = (int) (x - (sliderWidth/2));     
         if (sliderX < minX) { 
            sliderX = minX; 
         } 
         sliderValue = (x - minX) * valuePerX + minZoomValue;
      }     
      return sliderValue;
   }
   
   
   void updateZoomSlider(float value) {
      int tempX = (int) (value / valuePerX) + minX;
      if ((tempX >= minX) && (tempX <= (maxX+sliderWidth))) {
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
   
      boolean isZoomSliderEvent(int x, int y) {
      int slop = 50;  // number of pixels above or below slider that's acceptable.  provided for ease of use.
      int sliderLeft = (int) (sliderX - (sliderWidth/2)) - slop;
      int sliderRight = sliderX + sliderWidth + slop;
    //  return ((y >= sliderY + height - height/4) && (y <= (sliderY + height - height/4    + sliderHeight)) && (x >= sliderLeft)  && (x <= sliderRight) || draggingZoomSlider );
           return ((y >= sliderY + height - height/4 - sliderHeight/2) && (y <= (sliderY + height - height/4 + sliderHeight*2 )) && (x >= sliderLeft)  && (x <= sliderRight) || draggingZoomSlider );
   } 
}