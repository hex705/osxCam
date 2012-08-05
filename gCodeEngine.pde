class gCodeEngine {

  String[] gCode_all = new String[1]; // file

  String[] gLine; 

  String[] eCode;

  String[] gCodeOrder = new String[1];



  PVector currentLoc;
  PVector[] gTargets;
  float gFeed, gDepth, gRapid, gPlungeRate, gTravelFeed, gTravelHeight, gStepDepth;
  int lineElementIndex;
  int lineNumber, lineCount;
  int lineNumberStep = 5;

  int[] gElements;
  int gMove;

  int gSteps= 1;

  boolean POLY_START_FLAG = true;


  boolean NUMBERLINES = true;
  boolean FIRSTLINE = true;

  int gCodeDirection = CCW;  // change back if messy

  gCodeEngine () {
    currentLoc = new PVector();
    gTargets =  new PVector[2];
    gElements = new int[3];
    lineCount = 0;
    currentLoc.x = -9999;
    currentLoc.y = -9999;
    gRapid = 400;
  }



  // methods 
  
  // fxn accepts the step number
  // then stores the depth per step locally in : gStepDepth
  // whichStep is contrller by ncFile
  
  void setThisStep ( int whichStep ) {
       gStepDepth = gDepth/gSteps * whichStep ;
       println( "gStepDepth " + gStepDepth);
  }
  
  void setGStepDepth ( float gSD ){
     gStepDepth = gSD; 
  }
  
  
  float getGStepDepth ( ){
     return gStepDepth; 
  }

  void setMasterRapid (float r ) {
    gRapid = r;
    println( "gRapid set to " + gRapid);
  }

  float getMasterRapid () {
    return gRapid;
  }

  void setMasterDepth (float d ) {

//    Iterator i = entities.values().iterator();
//
//    while (i.hasNext ()) {
//
//      Entity e = (Entity) i.next();
//      println("setting depth");
//      e.setDepth(d);
//    } // end while iteration;

    gDepth = d;
    println( "depth set to " + gDepth);
  }

  float  getMasterDepth () {
    return gDepth;
  }

  void setMasterFeed (float f ) {
    Iterator i = entities.values().iterator();

    while (i.hasNext ()) {

      Entity e = (Entity) i.next();
      println("setting depth");
      e.setDepth(f);
    } // end while iteration;

    gFeed = f;
    println( "feed set to " + gFeed);
  }

  float  getMasterFeed () {
    return gFeed;
  }

  void setTravelFeed (float f ) {
    gTravelFeed = f;
    println( "travel feed set to " + gTravelFeed);
  }

  float  getTravelFeed () {
    return gTravelFeed;
  }

  void setTravelHeight (float tH ) {
    gTravelHeight = tH;
    println( "travelHeight set to " + gTravelHeight);
  }

  float  getTravelHeight () {
    return gTravelHeight;
  }


  void setgCodeDirection(int d) {
    gCodeDirection = d;
  }

  void clearAll() {
    gCode_all  = new String[1]; // file
    gCodeOrder = new String[1];
    lineCount  = 0;
    currentLoc.x = -9999;
    currentLoc.y = -9999;
  }

  void setSteps (int s) {
    gSteps = s;
    println( "steps set to " + gSteps);
  }
  
  int  getSteps ( ) {
    return gSteps;
  }

  String[] getCode () {
    return gCode_all;
  }

  void addLines (String[] ss) {
    ; // need a way to add lines
  }

  void addLine (String s) {

    resetLine();

    if (NUMBERLINES) { 
      addLineNumberElement();
    }

    addLineElement (s);

    flushLineElements ();
  }

  void addBlank() {
    NUMBERLINES = false;
    addLine("");
    NUMBERLINES = true;
  }

  void addLabel(String s) {
    //NUMBERLINES = false;
    addLine("(" + s + ")");
    // NUMBERLINES = true;
  }

  void resetLine() {
    gLine = new String [1];
    gLine[0] = "";
    lineElementIndex = 0;
  } // end clear currentLine

  void addLineElement (String s) {    
    gLine = append (gLine, s);
  } // end add element

  void addLineNumberElement () {
    addLineElement("N" + (lineCount*lineNumberStep));
    lineCount++;
  } 


  void flushLineElements () {
    String theNewLine  = join (gLine, "  ");
    theNewLine         = trim(theNewLine);
    gCode_all          = append( gCode_all, theNewLine );
  }

  void setLineNumberStep (int l) {
    lineNumberStep = l;
  }

  int getLineNumberStep () {
    return lineNumberStep;
  }


  void addHeader () {
    gCode_all[lineCount] = "(file created with xCam)";
    
    String theSource = ("Source file : " +  fileName );
    addLabel ( theSource );
    int d = day();    // Values from 1 - 31
    int m = month();  // Values from 1 - 12
    int y = year();   // 2003, 2004, 2005, etc.
    int mm = minute();  // Values from 0 - 59
    int h = hour();    // Values from 0 - 23
    
    String theDate = ("Created on : "+ m +"/"+d+"/"+y +" @ "+h+":"+mm);
    addLabel(theDate); 
    
    
   
    
    addBlank();
    addLabel("header");
    addLine ("G21 (metric)");
    addLine ("G40 (cancel cutter comp)");
    addLine ("G01 F400 Z10.00 (raise cutter)");  // rapid to 10mm up
  }

  void addFooter() {
    addBlank();
    addLabel("footer");
    addLine ("G01  F400 Z30.00");  // rapid to 30mm up
    addLine ("G40");
    addLine ("M30 ");
    addLine("(end)");
  }



  void buildCode(Entity e, int pathTYPE) { // type = RAW or GOOFY
    // POLY_START_FLAG = true;
    // get the start end end points
    
    gTargets = e.getTargets( pathTYPE );

    // are we at the correct start location?
    //int pathOffset = 0;
    //if (pathTYPE == GOOFY ) { pathOffset = 2;}
    println("got back");
    checkStartPosition(e, gTargets[0]);

    println("got back again ");
    // add code for ENTITY
    addEntityGCode(e, pathTYPE ); // code direction 
    println("three");
    // reset the gTargets variable here
    // have to do this remotely in each entity type !!! -- shit look at line


      // reset current location
    currentLoc.x = gTargets[1].x;
    currentLoc.y = gTargets[1].y;
  }

  // this is a fudge until I resolve teh multi  step situation
  void resetEndTarget( PVector t) {
    gTargets[1].x = t.x;
    gTargets[1].y = t.y;
  }


  void addEntityGCode(Entity e, int pathType) {

    eCode = e.getNC(1, gStepDepth, pathType);
    // eCode = e.getNC(gSteps, gStepDepth, pathType);
    // eCode = e.getNC(gSteps, pathType);

    //println("got a string array for element gcode");
    //printArray (eCode, "code from element");

    for (int ls = 0; ls < eCode.length; ls ++ ) {
      addLine(eCode[ls]);
    }
    println("done entity add code");
    // checkDepthPlunge( e.getDepth(), e.getPlungeRate());
  } // end add Entity code




  void checkStartPosition( Entity e, PVector target) {

    float joinTolerance = 0.025;

    float xDiff = abs(target.x - currentLoc.x);
    float yDiff = abs(target.y - currentLoc.y);

    println("xDiff " + xDiff);
    println("yDiff " + yDiff);


    //if ( (target.y != currentLoc.y) || (target.y != currentLoc.y) ) {
    if ( ( yDiff > joinTolerance) || (xDiff > joinTolerance)) {

      addLabel ("Move to element start");
      addLine  ("G01  F" +e.feedFormat(gRapid)+ "  Z"+ e.coordFormat( gTravelHeight) );  // raise cutter

     // gFeed = gRapid;

      resetLine();


      if (NUMBERLINES) { 
        addLineNumberElement();
      }  

      addLineElement ("F" +e.feedFormat(gRapid)  );

      addLineElement ("X"+e.coordFormat(target.x));


      addLineElement ("Y"+e.coordFormat(target.y));


      flushLineElements( );
    } // end if
  } // end checkStartPosition


  void setCodeDirection ( int d) {
    gCodeDirection = d;
  }

  int getgCodeDirection () {
    return gCodeDirection;
  }

  void addtoOrder (String s) {
    gCodeOrder = append (gCodeOrder, s);
  } // end add
} // end class






//    void checkDepthPlunge(float _D, float _PR) {
//
//      if ( (_D != gDepth) ) {
//        // need to plunge to new depth
//        
//        resetLine();
//        
//        if (NUMBERLINES) { 
//          addLineNumberElement();
//        }
//        
//        if ( gPlungeRate != _PR) {
//          gPlungeRate = _PR;
//          addLineElement ("F"+feedFormat(gPlungeRate)); 
//        }
//          
//        if (_D != gDepth) { 
//          addLineElement ("Z"+coordFormat(_D3)); 
//          gDepth = _D;
//        }
//
//        flushLineElements( );
//
//        //      String theLineToAdd = join (gLine, "  ");
//        //      theLineToAdd = trim(theLineToAdd);
//        //      addLine( theLineToAdd );
//      }


//      if ( (f != gFeed ) ) {
//
//        resetLine();
//        
//        if (NUMBERLINES) { 
//          addLineNumberElement();
//        }
//
//        if (f != gFeed ) { 
//          addLineElement ("F"+f);
//          gFeed = f;
//        } // set feed rate if needed
//
//        if (d != gDepth) { 
//          addLineElement ("Z"+d); 
//          gDepth = d;
//        }
//
//        flushLineElements( );
//
//        //      String theLineToAdd = join (gLine, "  ");
//        //      theLineToAdd = trim(theLineToAdd);
//        //      addLine( theLineToAdd );
//      }
//      



//    } // end check plunge and depth

