
class GCircle extends Entity {

  int arcDirection = -1;

  GCircle(String[] s_, float[] v_) {  
    super(s_, v_);

    setStartLoc ( (coord[X1]-coord[R1]), coord[Y1] ) ;  // default position -- left side 
    setEndLoc   ( (coord[X1]-coord[R1]), coord[Y1] ) ;   // circles end where they start
    
    arcDirection = gEngine.getgCodeDirection();
    
    println("Circle constructed");
    type = "Circle";
  }


  void display() {

    ellipseMode(CENTER);
    ellipseMode(RADIUS); 
    stroke(renderColor);
    noFill();
    ellipse( coord[X1], -coord[Y1], coord[R1], coord[R1] );
  }

int getEntityDirection () {
  return arcDirection;
}

void flip() {
  switchDirection();
  arcDirection = CCW;
}

void switchDirection() {
    
      println("circle switchdirection");    
      arcDirection = 1 - arcDirection;
  } 



 PVector[] getTargets(int pathType ) {

      // circle starts and ends at same place 
      targets[0] = startLoc;
      targets[1] = endLoc;
      return targets;
   
  }  // end getTargets




String[] getNC(int steps, float depthPerStep, int dir) {  // type is prim or poly
                                      //  now add depth -- change steps to depth
    
    // i think direction is taken care of if I use targets

    int newSize = steps*2;
    gcode = expand ( gcode, newSize );
     
      i = coord[R1];
      j = 0;
    
        //float depthPerStep = depth/steps;
        int c= 1;
        
        for (int s=0; s < steps*2; s=s+2) {
          
            gcode[s] = ( "G01  F" + plungeRate + "  Z" + depthPerStep * c ) ; 

            if (arcDirection == CW) {
               gcode[s+1] = ("F" + feed + "  G02   X" + coordFormat(targets[1].x) + "  Y" + coordFormat(targets[1].y)+ "  I" + coordFormat(i)+ "  J" + coordFormat(j));
            } else {
               gcode[s+1] = ("F" + feed + "  G03   X" + coordFormat(targets[1].x) + "  Y" + coordFormat(targets[1].y) + "  I" + coordFormat(i) + "  J" + coordFormat(j));
            } // end if 
            
            c++;
             
        }
         
    return gcode;
    
      
  }  // end getNC


  
  String coordFormat (float f) {
     String sf = nf(f,3,3);
     return sf;
  }
  
  } // end circle

