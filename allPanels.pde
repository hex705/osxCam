
int filePanelxOffset = 20;

void drawPanels() {
    drawPanelBorders();
    displayLocation();
    displayFilename();
}


void drawPanelBorders(){
  
 strokeWeight(1);
 stroke(30);
 rect(canvasBorder, height/2+2, (width/2-canvasBorder),25);
 rect(width/2+canvasBorder, height/2+2, width/2-2*canvasBorder,25);
 stroke(30);
 rect(canvasBorder, height/2+30, width/2-canvasBorder,height/3);
 //gCodeBackground
 stroke(255);
 fill(0);
 rect(width/2+canvasBorder, height/2+30, width/2-2*canvasBorder,height/3); 
}

void displayLocation () {
  
  fill(255);
  textFont(font); 
  // print mouse position data
  if (mouseY < height/2) {
       text( ("X: " + ( ( mouseX-origin.x ) / zoom) )       , 15  , height/2+20); 
       text( ("Y: " + ( ( height/2 -(height/2-origin.y) ) - ( mouseY ) ) / zoom) , 135 , height/2+20); 
  } else {
       text( "X: ", 15, height/2+20); 
       text( "Y: ", 135, height/2+20); 
  } 
  
}// end display location

void displayFilename () {
  
  fill(255);
  textFont(font); 
  String message = "none";
  // print mouse position data
      if (fileName != "") {
      String[] t = splitTokens (fileName, "/");
       message = (t[t.length-1]);
      }
       text( message      , width/2 + 15  , height/2+20); 
  
  
}// end display location


