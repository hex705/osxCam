
class GArc extends Entity {


  float SA, EA, bulge;
  boolean ANGLE_FLAG = false;
  boolean SEMI = false;
  
  int arcDirection;
  
  
  int arcDirectionForCCW;


  GArc(String[] s_, float[] v_) {
    super(s_, v_);
    bulge = -9999;
    // processing draws arcs CW (DFX draws CCW)
    // angles are relative
    //calculateEndPoints();

    SA = 360 - v_[AS];  // start angle
    EA = 360 - v_[AE];  // end angle

    if (SA > EA) {

      EA += 360;
      ANGLE_FLAG = true;
    }  // processing draws arcs CW (DFX draws CCW)

    if ((EA-SA) == 180 ) {
      SEMI = true;
    }

    calculateEndPoints();

    println("Arc constructed");
    type = "Arc";
    println("arc direction at time of Entity construction " + arcDirection);
    
    
  } // end constructor


  // methods 
  void setBulge (float b ) {
    bulge = b;
  }

  void display() {

    ellipseMode(CENTER);
    ellipseMode(RADIUS); 
    stroke(renderColor);
    noFill();

    arc( coord[X1], -coord[Y1], coord[R1], coord[R1], radians(SA), radians(EA) ); 
    //stroke (255,0,0); // red == start
    // line (coord[X1], -coord[Y1], startLoc.x, -startLoc.y);

    // stroke (0, 255, 0);  // green = end
    // line (coord[X1], -coord[Y1], endLoc.x, -endLoc.y);
  } // end display
  
  int getEntityDirection () {
      return arcDirection;
  }

  
  void flip() {
    println("flipping arc");
    // this does not work 
    switchDirection();
    //arcDirection = CCW;

  }
  
  
  void switchDirection() {
    
    println("switching arc...");
 
      PVector temp = new PVector();
      temp.set(startLoc);
   
      startLoc.set(endLoc);
      endLoc.set(temp);

     arcDirection = 1 - arcDirection;
    println("inside switch -- arcDirection is now "+arcDirection);
    println( "new start = " + startLoc + " new end "+ endLoc);
  } 

  void calculateEndPoints() { 

    if (coord[SX] != 0 ) {

      // this is a poly ARC

      setStartLoc (coord[SX], coord[SY]);
      setEndLoc   (coord[EX], coord[EY]);

      bulge = coord[BLG];

      println("bulge: " + bulge);

      if ( (bulge != - 9999) ) {
        if (bulge < 0 ) { 
          // negative bulge from poly == CW
          arcDirection = CW;
        } 
        else { 
          // positive bulge from poly = CCW
          arcDirection = CCW;
          switchDirection();
        }
      }

      //what is our direction ??
      println( "in arc constructor %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      println("angles are ");
      println(" start " + coord[SX] +"  " + coord[SY]);
      println(" end   " + coord[EX] +"  " + coord[EY]);
      println("  ctr  " + coord[X1] +"  " + coord[Y1]);
      println(" angles " + SA + "   " +EA );
      println(" arcDir " + arcDirection);
      println( "semi " + SEMI);
    } 
    else {
      // bulge is -9999 --> we have a prim arc 

      // arcDirection = CCW;
      println(" we have a prim arc -- non poly $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
      stroke(255, 0, 0);

      println("ctr " +coord[X1] +", " +  coord[Y1]);
      println("SA  " + SA);
      println("EA  " + EA);   
      println("flg " + ANGLE_FLAG);

      if (SEMI) {

        // if SEMI -- work from fact that processing draw CW -- so a SEMI is CW

        setStartLoc( ( (coord[R1] * cos(radians(SA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(SA+180)) )  + coord[Y1] ) );

        setEndLoc  ( ( (coord[R1] * cos(radians(EA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(EA+180)) )  + coord[Y1] ) );

        arcDirection = CW;
      } // end if


      if (ANGLE_FLAG) {
        setStartLoc( ( (coord[R1] * cos(radians(EA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(EA+180)) )  + coord[Y1] ) );

        setEndLoc  ( ( (coord[R1] * cos(radians(SA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(SA+180)) )  + coord[Y1] ) );
        arcDirection = CCW;
      } 
      else {

        setStartLoc( ( (coord[R1] * cos(radians(SA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(SA+180)) )  + coord[Y1] ) );

        setEndLoc  ( ( (coord[R1] * cos(radians(EA)) )      + coord[X1] ), 
        ( (coord[R1] * sin(radians(EA+180)) )  + coord[Y1] ) );

        arcDirection = CW;
      } // end else
    } // end if

    println("ST " +startLoc.x + "  " + startLoc.y);
    println("END " +endLoc.x + "  " + endLoc.y);
    println( "semi " + SEMI);
    println("arc Dir " + arcDirection +"\n\r\n\r");
  } // end calculateEndPoints


  PVector[] getTargets(int pathType ) {

 if ( (pathType == RAW )) {
      targets[tStart] = startLoc;  
      targets[tEnd] = endLoc;
    }  

   if ( ( pathType == GOOFY )  ) {
      targets[tStart] = endLoc;  
      targets[tEnd] = startLoc;
      // arcDirection = 1 - arcDirection;
   } 

    return targets;
 }  // end getTargets


  String[] getNC(int steps, float depthPerStep, int pathType) {  // RAW or GOOFY
  
    

    // i think direction is taken care of if I use targets
    println("gcode path TYPE " + pathType);
    println("RAW arc direction " + arcDirection);
    // it possible to cut a CW arc on a CCW path 
    
    int newSize = steps*2;
    gcode = expand ( gcode, newSize );

   // float depthPerStep = depth/steps;
    int c = 1;

    for (int s=0; s < steps*2; s=s+2) {

      gcode[s] = ( "G01  F" + plungeRate + "  Z" + depthPerStep * c ) ; 
      
      // determine which way
      
      // if RAW == CW, then GOOFY == CCW
      
      // if RAW = CCW then GOOFY == CW
      
      // RAW == CW
      
      // RAW == CCW
      
      int thisArcDirection = -1;
      
      if (pathType == RAW) { thisArcDirection = arcDirection; }
      
      if (pathType == GOOFY) { 
          thisArcDirection = 1 - arcDirection;
      }
      
      
      if (thisArcDirection == CCW) { // if goofy then reverse start and end AND use GOOFY DIRECTION
      
          println("CCW - arc");
   
          // i = distance from start point to centre along X-axis
          // X1 = ctr.x
          i = coord[X1] - targets[tStart].x;
          // j= distance from the start point to the ctr along y-axis
          // Y1 = ctr.y
          j = coord[Y1] - targets[tStart].y ;
          
          gcode[s+1] = ("F" + feed + "  G03   X" + coordFormat(targets[tEnd].x) + "  Y" + coordFormat(targets[tEnd].y)
            + "  I" + coordFormat(i) + "  J" + coordFormat(j) );
         
        } 
        else {
          println("CW - arc");
         // i = distance from start point to centre along X-axis
          // X1 = ctr.x
          i = coord[X1] - targets[tStart].x;
          // j= distance from the start point to the ctr along y-axis
          // Y1 = ctr.y
          j = coord[Y1] - targets[tStart].y ;
          
          gcode[s+1] = ("F" + feed + "  G02   X" + coordFormat(targets[tEnd].x) + "  Y" + coordFormat(targets[tEnd].y)
            + "  I" + coordFormat(i) + "  J" + coordFormat(j) );
      } 
    
      c++;
    }

    return gcode;
  }  // end getNC
  
  
} // end class arc

