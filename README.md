 



CatRomSphere
Processing code of spherical globe and circles of equal altitude.  No special libraries needed.  Move the vertical slider to adjust the altitude angle, move the horizontal slider to adjust the right ascension/longitude.  Click anywhere other than the sliders and the circle of equal altitude is computed and drawn in green on the 3D globe.  It uses the processing function thread() to compute the points of the circle seperately from the draw() thread.  This is a quick and dirty way of threading with the risk of inconsistent results.  In other words, if it draws a mess, its because it uses thread() incorrectly.  It now uses thread() correctly which means that if it is drawing then you cant rotate the globe.  Rotating the globe does not cause it to redraw anymore.  The only way to redraw is to change a slider.

The application.windows64 directory is a standalone export of the application.  To use it, you dont need Processing, since the directory includes everything needed to run the program.  Download that directory and click on the CatRomSphere.exe file. 
