// note a drill == point -- but we have a namespace issue (processing has a point already)

class GLine extends Entity {

   int lineDirection = -1;
  
  GLine(String[] s_, float[] v_) {
    super(s_, v_);
    type = "Line";
    
    gElements[0] = 1;
    
    setEnds();

    println("Line constructed");
  }

  void setEnds() {

    setStartLoc( coord[X1], coord[Y1] ) ;
    setEndLoc  ( coord[X2], coord[Y2] ) ;
    //lineDirection = CCW;  // default -- sort engine will put you into CCW order
    
  }
  
  int getEntityDirection () {
      return lineDirection;
  }
  
  void flip() {
    switchDirection();
   // lineDirection = CCW;
  }
 
 void switchDirection() {
    
      PVector temp = new PVector();
      temp.set(startLoc);
   
      startLoc.set(endLoc);
      endLoc.set(temp);

      
     //  lineDirection = 1 - lineDirection;
  }  

  void display() {

    stroke(renderColor);
    line( coord[X1], -coord[Y1], coord[X2], -coord[Y2] );
    
  }
  
   PVector[] getTargets(int pathType ) {
     println("inside line targets");

    if ( (pathType == RAW )) {
      
    
      targets[tStart] = startLoc;  
      targets[tEnd]   = endLoc;
    }  

    if ( ( pathType == GOOFY )  ) {
     targets[tStart] = endLoc;  
     targets[tEnd]   = startLoc;
      
     
    } 

    return targets;
 }  // end getTargets

  
//  PVector[] getTargets(int gCodeDirection ) {
//    // note odd number of steps leads to errors in end Location 
//    // one step give correct for a line 
//    if (gCodeDirection == CW ) {
//      targets[0] = startLoc;
//      targets[1] = endLoc;
//  
//    } 
//    else {
//      targets[0] = endLoc;
//      targets[1] = startLoc;
//     
//    }
//     return targets;
//  }  // end getTargets


  String[] getNC(int steps, float depthPerStep, int pathTYPE) {  // type is prim or poly
    
    // i think direction is taken care of if I use targets
    PVector resetTarget = new PVector();
    int newSize = steps*2;
    gcode = expand ( gcode, newSize );
    
        // float depthPerStep = depth/steps;
        int c = 1;
        
        for (int s=0; s < steps*2; s=s+2) {
         
             gcode[s] = ( "G01  F" + plungeRate + "  Z" + (depthPerStep * c) ) ; 
            

            if (c%2 == 1) { 
               gcode[s+1] = ("F" + feed + "  X" + targets[1].x + "  Y" + targets[1].y);
               resetTarget.x = targets[1].x;
               resetTarget.y = targets[1].y;
            } else {  
               gcode[s+1] = ("F" + feed + "  X" + targets[0].x + "  Y" + targets[0].y);
               resetTarget.x = targets[0].x;
               resetTarget.y = targets[0].y;
            } // end if 
            
            c++;
           
             
        }
       
       println("return");
     
     gEngine.resetEndTarget(resetTarget) ;
    println("blab");
        
    return gcode;
      
  }  // end getNC
  
}// end CLASS LINE

