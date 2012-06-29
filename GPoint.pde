// note a drill == point -- but we have a namespace issue (processing has a point already)

class GPoint extends Entity {

   int pDirection = CW;

  GPoint(String[] s_, float[] v_) {
    super(s_, v_);
    renderColor = color(255,0,0);
    setStartLoc ( coord[X1], coord[Y1] ) ;
    setEndLoc   ( coord[X1], coord[Y1] ) ;
    gElements[0] = 1;

    println("point constructed");
    type = "Point";
  }

  void display() {

    noStroke();
    fill(renderColor);
    ellipseMode(CENTER);
    ellipseMode(RADIUS);
    ellipse(coord[0], -coord[1], 2/zoom, 2/zoom ); //radius of 1 because we cant see point otherwise
  }
  
  int getEntityDirection () {
      return pDirection;
  }
  
  void flip() {
      // do nothing a point has only 1 dimension
  }
  
  void switchDirection() {
    // do nothing like flip
  }


  PVector[] getTargets (int pathType ) {
      targets[0] = startLoc;
      targets[1] = startLoc;
      return targets;
  }
  
  String[] getNC(int steps, int dir ) {  // type is prim or poly
   
    int newSize = steps*2 + 1;
    gcode = expand ( gcode, newSize );
   
        float depthPerStep = depth/steps;
        int c= 1;
      
        for (int s=0; s < steps*2; s=s+2 ) { 
            gcode[s] = ( "G01  F" + plungeRate ); // set the plunge rate
            gcode[s+1] = ("Z" + depthPerStep * c ) ; 
            
            c++;
            
        }
        
        gcode[newSize-1] = ("F200  Z" + travelHeight);
        
        
    return gcode;
    
      
  }  // end getNC
  
} // end drill 

