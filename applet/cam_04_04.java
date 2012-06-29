import processing.core.*; 
import processing.xml.*; 

import controlP5.*; 
import java.io.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class cam_04_04 extends PApplet {


//  http://processing.org/discourse/beta/num_1142535442.html

// document split into sections --
// each section rsides as subset of the main doc.
// last section is the one with the entities -- these become objects
// store objects in hash map 
// create an array to store the order of the objects -- this is a string array that stores the keys in order.
// objects must have a key (string)
// objects must have and id

// this one sorts -- aggrgates - blobs determines if open and find direction

//  now need to add multilayers


// cam_02_04 --  adding the arc code to polyline

// works for DXFR12 -- enhance parse for DXF!


// add file chooser for not in data folder AND -- 


ControlP5 gControl;



PrintStream      ps;             // used to redirect the standard output (global variable)

PVector origin;
PFont font;
// The font must be located in the sketch's 
// "data" directory to load successfully

boolean MOVE_ORIGIN = false;

gCodeEngine gEngine; //= new gCodeEngine();

public void setup() {
  //redirectStdOut("/data/myLog");
  size(1000, 800);
  
  background(0xffcccccc);

  origin = new PVector();
  origin.x = canvasBorder;
  origin.y = (height/2)-canvasBorder;

  gEngine = new gCodeEngine();
  // controllers
  setControllers();
  
  font = createFont("AppleGothic.vlw", 14); 
  println ("Begin process....");

  resetFileStatus() ;

  String[] lf = loadStrings("./data/lastFile.txt");

  if (lf != null) {
    fileName = lf[0];
  } 
  else {
    startUpSettings();
  }

  //noLoop();
} // end setup

public void draw() {


  //background(80);

  if ( !FILEOK ) { 
    
    if (fileName.equals("") ) {
      fileName = openNewFile();
    } 

    dxfRawFile = DXFImportFile(fileName);
    

    if ( (dxfRawFile != null) && (FILEOK) ) {
       getDXFTables();
    }
    
  } // end !FILEOK

  if (CREATE_ENTITIES && FILEOK) {

    checkForPoly(dxfEntities, "MAIN");
    createEntities ( dxfEntities ) ;
    println("at end of create entities the hash count is: " + hashCount );
    println("entities size "+ entities.size());
    explodePolys();  // find this at bottom of createEntities TAb
    println("entities size after explode polys: "+ entities.size());
    firstTime = true;
  } // end CREATE ENTITITES

  
  // draw canvas (black)
  fill(0);
  stroke(255);
  strokeWeight(2);
  float cH = (height/2)-canvasBorder*2;

  if (CANVAS) {
   // rect(canvasBorder, canvasBorder, (width-canvasBorder*2), (height/2)-canvasBorder*2);
  }  

  strokeWeight(1);

  // draw the entities
  pushMatrix();
  
	  translate(origin.x, origin.y );
	  scale(zoom); // 1px = 1 mm @ scale =1 
	  drawGrid();
	  drawOrigin();
	  drawEntities();

  popMatrix();

  drawMask();
  drawPanels();
  
  if (firstTime) {
     aggregate(); 
  }
  
  //SHOW_BACK_PLOT = false;
  if (SHOW_BACK_PLOT) {
   // backPlot();
  }
   

}  // end draw 

 public void getDXFTables() {
   
      println( "SHOULD HAVE A FILE" );
      vport = new DXFTable(dxfRawFile, "VPORT", "ENDSEC" ); 
      String[] vportSub = vport.getSubTable( " 12", " 43" );

      dxfEntities = new DXFTable(dxfRawFile, "ENTITIES", "ENDSEC");

      dxfEntities.parseEntities( "  0" );
      
      CREATE_ENTITIES = true;
    }


public void drawOrigin() {
  stroke(255, 0, 0);
  strokeWeight(0.5f);
  line(-5, 0, 5, 0);
  line(0, -5, 0, 5);
  
} // end origin


public void drawGrid() {
  // draw canvas grid here
  stroke(100);
  //println("start grid");
  strokeWeight(0.25f);
  // down and right
  
  for (int i = 0; i < width; i += gridSize) {
    for (int j = 0; j < height; j += gridSize ) {
      point(i,  j);
      point(i, -j);
      point(-i, j);
      point(-i,-j);
      
      //println("height " + height);
      if  ( (i % (gridSize * 10) ) == 0 ) { 
        line(  i, -height,  i,  height ) ;
        line( -i, -height, -i,  height ) ;
      }
      if  ( (j % (gridSize *10) ) == 0 ) { 
        line(-width, -j, (width) , -j) ;
        line(-width,  j, (width) ,  j) ;
      }
    } // end for j
  } //   end for i

} // end draw grid

public void   drawMask(){
  fill(125,130,130);
  noStroke();
   rect(0,0,width,canvasBorder-1);
   rect(0,0,canvasBorder-1,height);
   rect(width-canvasBorder+1,0,width-canvasBorder+1,height);
   rect(0,height/2-canvasBorder+1,width,height);
   
}

public void drawEntities () {

  
  Iterator i = entities.values().iterator();

  while (i.hasNext ()) {

    Entity e = (Entity) i.next();
    // e.printSource();
    // e.printVerticies();
    if (SHOWENDPOINTS) {
      e.showEndPoints();
    }
    
    if (e.isEnabled()) {
       // println("key " + e.getMyKey());
        e.display();
    }
  } // end while iteration
  
} // end draw entities


public String[] DXFImportFile(String _fileName) { 

  // get the file -- each line in array

  String[] rawFile = loadStrings(_fileName); //folderPath }
 
  // check that file was found and loaded    
  if (rawFile != null) {   
    println( "\n\rFile loaded successfully" );
    FILEOK = true;
  } 
  else { 
    println("Error: File not loaded." );
    fileName ="";
    FILEOK = false;
    
  }

  return rawFile;
} // end DXF import



public String openNewFile () {
  
 

  String loadPath = selectInput("Select Drawing");

  if (loadPath == null ) {

    String[] lf = loadStrings("./data/lastFile.txt");
      if (lf != null) {
        loadPath = lf[0];
      } 
      else {
        startUpSettings(); 
      }
    println ("Error:  No file  selected");
    
   } 
     else 
  {  

    String[] lastFile = new String[1];
    lastFile[0] = loadPath;
    writeFile("./data/lastFile.txt", lastFile);
  }

  return loadPath;
}


public void resetFileStatus() {
  hashCount = 0 ;
  entities = new HashMap();
  aggregateList = new ArrayList();
  blobList = new ArrayList();
  CREATE_ENTITIES = true;
  fileName = "";
  FILEOK = false;
}

public void startUpSettings() {
  CREATE_ENTITIES = false;
  FILEOK = true;
}




// DXFTable extractDXFTable(String[] s, String start, String end) {


//        viewcenterX = float(vportSub[0]);
//        viewcenterY = float(vportSub[2]);
//        viewWidthY = float(vportSub[28]);
//        aspectRatio = float(vportSub[30]);
//        viewWidthX = viewWidthY * aspectRatio;
//        fixedheight = viewWidthX;
//        //zoom = fixedheight / viewWidthY;
//        FILEOK = true;
//        UPDATE = true;

// } // end of if s -- from very top
//return FILEOK;
// }

public void aggregate() {
  
      // form the aggregates
      sortedAggregates = orderElements( entities );  // this is a list of raw aggregates --search tree and place elements inside BLOB object
   
      
      for (int i = 0; i < sortedAggregates.size(); i ++ ) {
        
            Aggregate a = (Aggregate) sortedAggregates.get(i);
            println("building blob from aggregate " + i);
            Blob b= new Blob();
            
            b.populateBlob ( a.getNodes() );
            
            blobList.add(b);       
      } // end for 
      
      // transpose aggragates into blobs
      // check blob of entity direction -- ie, if the path is CCW, the netities must line up CCW
      for (int bb= 0; bb< blobList.size(); bb++ ) {
        Blob bbb = (Blob) blobList.get(bb);
       
        bbb.isClosed();
         bbb.showBlob();
          println("raw direction " + bbb.calculateRawDirection() );
        
      } // end for blob list out 
      firstTime = false;
      
      
  } // end aggregate



//re-direct standard output to a logfile
public void redirectStdOut(String filename) throws IOException {
   FileOutputStream fos =
     new FileOutputStream(filename,true);
   BufferedOutputStream bos =
     new BufferedOutputStream(fos, 1024);
   ps =
     new PrintStream(bos, false);

   System.setOut(ps);
 }
/*

control panels

move travel setting from element to gENgine 

in poly make sure start and ends match -- so you go around 1x only. 
in poly's make sure the cutter goes down once


*	sort order has a problem
*	if a line is stored with end points reversed then it won; close r connec to blob
*	so we need o check both end points -- firt check the direction -- store it in DXF order
*	pick an object 
*	
*	
Add: ISCLOSED?
*/
int X1 = 0;
int Y1 = 1;
int Z1 = 2;

int X2 = 3;
int Y2 = 4;
int Z2 = 5;

int R1 = 6;
int R2 = 7;
int R3 = 8;
int ANG = 8;

int SX = 7;
int SY = 8;
int EX = 9;
int EY = 10;

int BLG = 11;

int AS = 4; //arc start
int AE = 5; //arc end 

int BG = 3; //bulge for poly line arc  -- used to be 3

int POLYSIZE = 4;
int PRIMSIZE = 12;

int PRIM = 1;
int POLY =2;

int CW = 0;
int CCW = 1;
int GOOFY = 2;
int RAW = 0;


int arcCount=0;

boolean openFIRSTTIME = true;
boolean makencFIRSTTIME = true;

boolean SHOWENDPOINTS = false;
boolean SHOWSEGMENTS= false;

boolean VERBOSE =false;
boolean SHOW_CONTROL_FONTS = true;

PrintWriter output;

String fileName = ""; // "simpleArcRaw.dxf"

float zoom = 1;
boolean CANVAS = true;
HashMap entities;
int hashCount = 0;
String entityKey = "entKey";
int entityCount = 0;  // combine entityKey and entityCount to create the entityKey

ArrayList sortedAggregates = new ArrayList();

ArrayList sortList = new ArrayList();
ArrayList goalList = new ArrayList();
ArrayList blobList = new ArrayList();


String[]   dxfRawFile;
String[]   tildeEntity;
String[][] ent;
String[][] code;

DXFTable dxfEntities;
DXFTable vport;

float viewcenterX = 0;
float viewcenterY = 0;
float viewWidthX  = 1;
float viewWidthY  = 1;
float aspectRatio = 1;


float maxX, minX, maxY, minY;
float canvasBorder = 5;
float gridSize = 10;

float fixedheight=0;

boolean UPDATE = true;
boolean CREATE_ENTITIES = false;
boolean FILEOK = false;

int TOOLPATH    = 1;
int DIVOTTHINGY = 2;
float defaultDepth = 2;

// for mouse display 
  float tempY = 0;
  float tempX = 0;
  
  // for sorting the elements into structures
   
   int  aggregateCount;
   ArrayList aggregateList;
   HashMap sortHash;
   int sortCount = 0;
   String[] subparentList;
 
   
   
   int LEFT = 0;
   int RIGHT = 1;
   
   
   boolean firstTime = true;
   
   
   // backplot shit
   
   ArrayList backPlotElements = new ArrayList();
   
   boolean SHOW_BACK_PLOT = false;
  
 


// control events 

// placement constants 


int leftBlock_LEFT  = 15;
int rightBlock_LEFT = 20;

int block_TOP = 0 ; 

int blockSPACE  = 15;
int blockHEIGHT = 25;

// set height of a controller with formula 
// where <order> is order from top  - can add gaps with space
// block_TOP + blockHEIGHT + <order>


public void setControllers() {

  gControl = new ControlP5(this);

  ControlFont cfv = new ControlFont(createFont("Arial", 14));
  ControlFont cfc = new ControlFont(createFont("Arial", 16));



  PImage[] gOpen = {
    loadImage("openFile_norm.png"), loadImage("openFile_over.png"), loadImage("openFile_clck.png")
    };

    gControl.addButton("openFile")
      .setValue(1)
        .setPosition( (width - 115 ), height/2+310)
          .setImages(gOpen)
            .updateSize()
              ;

  PImage[] gNC = {
    loadImage("makeNC_norm.png"), loadImage("makeNC_over.png"), loadImage("makeNC_clck.png")
    };

    gControl.addButton("makeNC")
      .setValue(2)
        .setPosition( leftBlock_LEFT, height/2+310)
          .setImages(gNC)
            .updateSize()
              ;




  // sliders 

  gControl.addSlider("Steps")
    .setRange(1, 10)
      .setValue(1)
        .setNumberOfTickMarks(11)
          .snapToTickMarks(true)
            .showTickMarks(false)
              .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 2) + (blockSPACE *0) ) )
                .setSize(100, 20);


  gControl.addSlider("Zoom")
    .setRange(0, 10)
      .setValue(1)
        .setPosition( 300, (height/2 + 5 ) )
          .setSize(100, 20);



  gControl.addSlider("Travel_Height")
    .setRange(0, 15)
      .setValue(3)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 5) + (blockSPACE *2) ) )
          .setSize(100, 20);

  gControl.addSlider("Travel_Feed")
    .setRange(0, 300)
      .setValue(300)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 6) + (blockSPACE *2) ) )
          .setSize(100, 20);

  gControl.addSlider("Master_Feed")
    .setRange(0, 400)
      .setValue(75)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 3) + (blockSPACE *1) ) )
          .setSize(100, 20);

  gControl.addSlider("Master_Depth")
    .setRange(0, -10)
      .setValue(-2)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 4) + (blockSPACE *1) ) )
          .setSize(100, 20);

  gControl.addSlider("Rapid_Feed")
    .setRange(200, 400)
      .setValue(400)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 7) + (blockSPACE *3) ) )
          .setSize(100, 20);

  gControl.addToggle("C-CW             CW")
    .setValue(true) 
      .setMode(ControlP5.SWITCH)
        .setPosition(rightBlock_LEFT, (height/2 + block_TOP + (blockHEIGHT * 8) + (blockSPACE *4) ) )
          .setSize(100, 20);



  if (SHOW_CONTROL_FONTS) {

    gControl.controller("Steps").valueLabel().setControlFont(cfv);
    gControl.controller("Steps").captionLabel().setControlFont(cfc);

    gControl.controller("Zoom").valueLabel().setControlFont(cfv); 
    gControl.controller("Zoom").captionLabel().setControlFont(cfc);
  }
}

public void controlEvent(ControlEvent theEvent) {

  String s = theEvent.getController().getName();
  int v = (int) theEvent.getController().getValue();
  //println(s);
  if (s.equals("C-CW             CW")) {
    // call the direction function
    pathDirection (v);
  }
}

public void openFile(int theValue) {
  if (openFIRSTTIME) {
    println("open startup");
    openFIRSTTIME = false;
  } 
  else {
    println("a button event (openfile): "+theValue);
    resetFileStatus();
  }
}


public void makeNC ( int theValue ) {
  if (makencFIRSTTIME) {
    println("make startup");
    makencFIRSTTIME = false;
  } 
  else {
    println("a button event (makeNC): "+ theValue);
    makeGCode();
  }
}

public void Steps (int v) {
  v = floor(v);
  // gControl.controller("Steps").setValue(v);
  gEngine.setSteps (v);
}

//public void numSteps(int v) {
//  println("v "  + v);
//     v = floor(v);
//    gControl.controller("Steps2").setValue(v);
//    gEngine.setSteps (v);
//}

public void Zoom (float  z) { 
  zoom = z;
}

public void Master_Feed (int  mF) { 
  gEngine.setMasterFeed(mF);
}

public void Master_Depth (float  d) { 
  //gEngine.setMasterDepth( d );
}


public void Rapid_Feed (int  rF) { 
  gEngine.setMasterRapid( rF );
}

public void Travel_Feed (int  tF) { 
  gEngine.setTravelFeed( tF );
}

public void Travel_Height (int  tH) { 
  tH = floor(tH);
  // gControl.controller("Travel_Height").setValue(tH);
  gEngine.setTravelHeight( tH); 
  //zoom = z;
}

public void pathDirection (int  gDir) { 
  println("toggle toggled == " + gDir);
    gEngine.setgCodeDirection( gDir );
  //bT = floor(bT);
  //gControl.controller("BINARY_test").setValue(bT);
  // gEngine.setTravelHeight( tH); 
  //zoom = z;
}

class DXFTable {

  String[] tableSource;
  String[][] tableEntities;
  Boolean ENTITIES_SET = false;

  int tableLength;
  String oldType = "";



  // constructors
  DXFTable (String[] sourceArray) {
    // for polyLine (i think) 
    tableSource = sourceArray;
  }

  DXFTable (String[] sourceArray, String tableBegin, String tableEnd) {

    tableSource = getTable(sourceArray, tableBegin, tableEnd);
    tableLength = tableSource.length;
  } // end constructor 


  // function for the Vport -- can handle this better later
  public String[] getSubTable (String tableBegin, String tableEnd) { 
    return getTable ( tableSource, tableBegin, tableEnd);
  }


  public String[][] getTableEntities() {
    return tableEntities;
  }


  public int getTableEntitiesLength() {
    return tableEntities.length;
  }

  public void disableElement (int e ) {
    oldType = tableEntities[e][1];
    tableEntities[e][1] = "disAbled";
  }


  public void enableElement (int e ) {
    tableEntities[e][1] = oldType;
  }

  public void expandTable (String[][] newEntities) {

    // tableEntities = splice (tableEntities, newEntities, 0); // tableEntities.length-1
  }

  public void parseEntities ( String target  ) {
    println("beginning the PARSE ENTITIES ROUTINE in DXFTable"); 
    int numEntities = 0;

    // find all the entity start points
    for (int i = 0; i < tableSource.length; i++) {
      if (tableSource[i].contains( target )) {  // marks the start of an entity
        tableSource[i] = "startEntity";
        numEntities ++;
      } // end if
    } // end for

    String joindxf;
    joindxf = join(tableSource, "~");
    println("joined up dxf:\n\r " + joindxf);

    // split at ~ -- this seems excessive
    tildeEntity = split(joindxf, "startEntity");
    printArray(tildeEntity, "tildeEntity FROM dxfTable.parseEntities"); 
    println();

    tableEntities = new String[numEntities + 1][]; 

    for (int i = 0; i <= numEntities; i++) {

      tableEntities[i] = split(tildeEntity[i], "~");
    } // end for

    // } // end read

    ENTITIES_SET = true;
  } // end parseForEntities

  public void printEntities () {

    if ( ENTITIES_SET ) {
      for (int i=0; i < tableEntities.length; i++) {
        println(tableEntities[i]);
      }
    } 
    else {
      println("cannot print ENTITIES not YET created");
    }
  }

  public void printSource () {
    printSource ( "");
  }

  public void printSource (String id) {

    println("outputting table " + id );
    for (int i = 0; i < tableLength; i ++ ) {
      print ( tableSource[i] + " " );
    }
  } // end print source
  

} // end class 
// ****  


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
  public void setBulge (float b ) {
    bulge = b;
  }

  public void display() {

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
  
  public int getEntityDirection () {
      return arcDirection;
  }

  
  public void flip() {
    println("flipping arc");
    // this does not work 
    switchDirection();
    //arcDirection = CCW;

  }
  
  
  public void switchDirection() {
    
    println("switching arc...");
 
      PVector temp = new PVector();
      temp.set(startLoc);
   
      startLoc.set(endLoc);
      endLoc.set(temp);

     arcDirection = 1 - arcDirection;
    println("inside switch -- arcDirection is now "+arcDirection);
    println( "new start = " + startLoc + " new end "+ endLoc);
  } 

  public void calculateEndPoints() { 

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


  public PVector[] getTargets(int pathType ) {

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


  public String[] getNC(int steps, int pathType) {  // RAW or GOOFY
  
    

    // i think direction is taken care of if I use targets
    println("gcode path TYPE " + pathType);
    println("RAW arc direction " + arcDirection);
    // it possible to cut a CW arc on a CCW path 
    
    int newSize = steps*2;
    gcode = expand ( gcode, newSize );

    float depthPerStep = depth/steps;
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


  public void display() {

    ellipseMode(CENTER);
    ellipseMode(RADIUS); 
    stroke(renderColor);
    noFill();
    ellipse( coord[X1], -coord[Y1], coord[R1], coord[R1] );
  }

public int getEntityDirection () {
  return arcDirection;
}

public void flip() {
  switchDirection();
  arcDirection = CCW;
}

public void switchDirection() {
    
      println("circle switchdirection");    
      arcDirection = 1 - arcDirection;
  } 



 public PVector[] getTargets(int pathType ) {

      // circle starts and ends at same place 
      targets[0] = startLoc;
      targets[1] = endLoc;
      return targets;
   
  }  // end getTargets




public String[] getNC(int steps, int dir) {  // type is prim or poly
    
    // i think direction is taken care of if I use targets

    int newSize = steps*2;
    gcode = expand ( gcode, newSize );
     
      i = coord[R1];
      j = 0;
    
        float depthPerStep = depth/steps;
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


  
  public String coordFormat (float f) {
     String sf = nf(f,3,3);
     return sf;
  }
  
  } // end circle


// note a drill == point -- but we have a namespace issue (processing has a point already)

class GEllipse extends Entity {

  float longWidth, shortWidth, angle, SA, EA;
  PVector apex;

  GEllipse(String[] s_, float[] v_) {
    super(s_, v_);
    SA =  TWO_PI - v_[R2] ;  // start angle
    EA =  TWO_PI - v_[R3] ;  // end angle
    println("angle BEFORE is = " + (EA-SA) );
    if ( SA < EA) {
      EA -= TWO_PI;
    }
    println("angle AFTERis = " + (EA-SA) );

    buildEllipse();

    println("Ellipse constructed");
    type = "Ellipse";
  } // end constructor

  // methods 
  public void buildEllipse() {
    println("print verticies");
    printVerticies();

    apex = new PVector();

    apex.x = coord[X1] + coord[X2];
    apex.y = coord[Y1] + coord[Y2];

    float tDist = dist(coord[X1], coord[Y1], apex.x, apex.y);
    println("distance from center to apex = " + tDist );
    longWidth = tDist ;  // radius
    shortWidth = longWidth * coord[R1] ;

    println("long width " + longWidth);
    println("short width " + shortWidth);
    // there needs to be an angle here.


    pushMatrix();
    translate(coord[X1], -coord[Y1]);
    angle = atan2( -coord[Y2], coord[X2] );

    //if (angle > PI) { angle -=PI;} // keeps angle in upper half and positive

    popMatrix();


    print ("angle " + degrees (angle));

    println("print verticies from DISPLAY");
    printVerticies();
  } // end build ellipse


  public void display() {


    ellipseMode(CENTER);
    ellipseMode(RADIUS);
    stroke(renderColor);
    noFill();
    //stroke(255, 0, 0);
    ellipse(coord[X1], -coord[Y1], 1, 1);
    ellipse(apex.x, -apex.y, 1, 1);

    pushMatrix();
    //translate(canvasBorder, (height/2)-canvasBorder);
    translate(coord[X1], -coord[Y1]);
    rotate(-angle);
    stroke(0, 255, 0);  
    //ellipse ( 0, 0, longWidth, shortWidth); 
    stroke(255, 0, 0);
    //  arc( 0, 0, longWidth, shortWidth, coord[R2] - coord[ANG], coord[R3] - coord[ANG]);
    arc( 0, 0, longWidth, shortWidth, SA -angle, EA - angle); 
    popMatrix();
  } // end display
  
} // end CLASS ellipse

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

  public void setEnds() {

    setStartLoc( coord[X1], coord[Y1] ) ;
    setEndLoc  ( coord[X2], coord[Y2] ) ;
    //lineDirection = CCW;  // default -- sort engine will put you into CCW order
    
  }
  
  public int getEntityDirection () {
      return lineDirection;
  }
  
  public void flip() {
    switchDirection();
   // lineDirection = CCW;
  }
 
 public void switchDirection() {
    
      PVector temp = new PVector();
      temp.set(startLoc);
   
      startLoc.set(endLoc);
      endLoc.set(temp);

      
     //  lineDirection = 1 - lineDirection;
  }  

  public void display() {

    stroke(renderColor);
    line( coord[X1], -coord[Y1], coord[X2], -coord[Y2] );
    
  }
  
   public PVector[] getTargets(int pathType ) {
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


  public String[] getNC(int steps, int pathTYPE) {  // type is prim or poly
    
    // i think direction is taken care of if I use targets
    PVector resetTarget = new PVector();
    int newSize = steps*2;
    gcode = expand ( gcode, newSize );
    
        float depthPerStep = depth/steps;
        int c= 1;
        
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

  public void display() {

    noStroke();
    fill(renderColor);
    ellipseMode(CENTER);
    ellipseMode(RADIUS);
    ellipse(coord[0], -coord[1], 2/zoom, 2/zoom ); //radius of 1 because we cant see point otherwise
  }
  
  public int getEntityDirection () {
      return pDirection;
  }
  
  public void flip() {
      // do nothing a point has only 1 dimension
  }
  
  public void switchDirection() {
    // do nothing like flip
  }


  public PVector[] getTargets (int pathType ) {
      targets[0] = startLoc;
      targets[1] = startLoc;
      return targets;
  }
  
  public String[] getNC(int steps, int dir ) {  // type is prim or poly
   
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

// note a drill == point -- but we have a namespace issue (processing has a point already)

class GPoly extends Entity {
  int vertexCount;
  int elementCount;
  String[] polyKeys; 
  String FIRST;
  String LAST;
  // poly is a hashmap of primatives 
  // -- this should be done recursively but for now .. 
  HashMap polyEntities;
  String polyKey = "polyKey";
  boolean isPOLY = true;
  int howMany;
  float minX, maxX, minY, maxY, ctrX, ctrY;
  boolean isCW = true;

  int drawnDirection;

  GPoly(String[] s_, float[] v_) {

    super(s_, v_);
    elementCount = 0;
    vertexCount = v_.length / 4;
    polyEntities = new HashMap(); 
    howMany = buildPrimativesforPoly();
    println("Poly with " + vertexCount + " verticies constructed");


    type = "Poly";
    polyKeys = orderPrimatives();
    FIRST = polyKeys[0];
    LAST  = polyKeys[polyKeys.length-1];
    drawnDirection = getDrawnDirection();
    
    // TO DO --> set DIRECTION
  }

   public HashMap getPolyEntities() {
	 return polyEntities;
   }  // end getPOly entities

   public int getDDirection() {
     return drawnDirection;
   }

  public String[] getPolyKeys () {
    return polyKeys;
  }


  public void display() {

    for (int c = 0; c < polyKeys.length; c++ ) {

      if (polyEntities.containsKey(polyKeys[c])) {

        if (c != 0) {

          Entity pE = (Entity) polyEntities.get(polyKeys[c]);

          if (SHOWSEGMENTS) {
            stroke(random(255), 0, random(255));
          }

          if (SHOWENDPOINTS) {

            pE.showEndPoints();
          }

          if (drawnDirection == CW) { stroke(0, 255, 0);} else { stroke(255, 0, 0);}
          
          strokeWeight(0.25f);
          pE.display();

          // debug -- show lines for angles
          //          strokeWeight(0.25);
          //          stroke(0,200,0);
          //          line (ctrX,-ctrY, pE.getEndLoc().x, -pE.getEndLoc().y);
          
        }
      } // end if
    } // end for 
  
  } // end display
  
  public void setDepth(float d) {

    for (int c = 0; c < polyKeys.length; c++ ) {

      if (polyEntities.containsKey(polyKeys[c])) {

        if (c != 0) {

          Entity pE = (Entity) polyEntities.get(polyKeys[c]);
              pE.setDepth(d);
        }
      }
    }

    
  } // end setDepth



  public String[] orderPrimatives () {
    
    String[] theKeys = new String[howMany + 1 ];
    for (int c = 0; c < theKeys.length; c++ ) {
      String orderKey = ("polyKey" + c);
      theKeys[c] = orderKey;
      // println("the key order " + orderKey);
    }

    return theKeys;
  }

  public int getDrawnDirection() {

    float startX, startY, midX, midY, endX, endY;

    minX =   10000;
    maxX =  -10000;
    minY =   10000;
    maxY =  -10000;

    // first, find max and min
    for (int c = 0; c < polyKeys.length; c++ ) {

      if (polyEntities.containsKey(polyKeys[c])) {

        Entity pE = (Entity) polyEntities.get(polyKeys[c]);

        float eX = pE.getEndLoc().x;
        float eY = pE.getEndLoc().y;

        if (eX < minX ) { 
          minX = eX;
        }
        if ( eX > maxX ) { 
          maxX = eX;
        }
        if (eY < minY ) { 
          minY = eY;
        }
        if ( eY > maxY ) { 
          maxY = eY;
        }
      } // end ifs
      ctrX = ( maxX + minX ) / 2;
      ctrY = ( maxY + minY ) / 2;

      // println ("minX " + minX + " minY " + minY + "  maxX " + maxX  + " maxY  " + maxY);
      
    } // end for 


    ellipse(0, 0, 4, 4);
    // go through and get the atan2 angle of end point to ctr
    pushMatrix();
    translate(ctrX, ctrY);

    float[] angles = new float[polyKeys.length];

    for (int c = 0; c < polyKeys.length; c++ ) {

      if (polyEntities.containsKey(polyKeys[c])) {

        Entity pE = (Entity) polyEntities.get(polyKeys[c]);

        float eX = pE.getEndLoc().x;
        float eY = pE.getEndLoc().y;

        if (pE.getType().contains("Line") ) {
          angles[c] = atan2(eY-ctrY, eX-ctrX) + TWO_PI;
        } 
        else {  
          angles[c] = -99;
        }
      }// end if
    } // end for
    popMatrix();

    // now calculate the direction
    int angleTotal = 0;
    println("angles ");

    for (int d=2;d<angles.length; d++  ) {
      println (angles[d]) ; 

      if ( ( angles[d] != -99 ) && (angles[d -1] != 99 ) ) {
        float test = angles[d] - angles[d-1];
        if ( test > 0 ) angleTotal++;
        if ( test < 0 ) angleTotal--;
      }
    } // end for 


    println("angleTotal = " + angleTotal);
    if (angleTotal < 0) { 
      println("direction is CCW");
      return CCW ;
    } 
    else {
      println("direction is CW");
      return CW;
    }
  }  // end is CW


    public int buildPrimativesforPoly() {

    int polyEntityValue = 0;
    println();
    println("length ^^^^^ " +coord.length);
    for (int v = 0; v < coord.length-4; v+=4 ) { // for each vertex PAIR build a primative OBJ

      polyEntityValue += 1;
      String thisKey = polyKey + polyEntityValue;
      println("polyentity key " + thisKey);
      // polyKeys = append (polyKeys, thisKey);
      if ( polyEntities.containsKey(thisKey) ) {
        println(" error creating new object -- this key is already assigned ");
        println(" ************************* -- **************************** ");
        break;
      } 
      else {
        elementCount ++;
        String s[]  = new String[1];
        s[0] = "polyPrimative";

        if ( coord[v+BG] == 0 ) {  // if bulge is 0 (zero)
          // we have a line
          //println("creating a poly line cause thats all we know how to do:");
          float[] tempV = new float[6];

          // create vertex array
          tempV[X1] = coord[X1+v];    //  x position of start point
          tempV[Y1] = coord[Y1+v];    //  y position of start point
          tempV[Z1] = coord[Z1+v];    //  z position of start point

          tempV[X2] = coord[X2+v+1];    //  x position of end  point
          tempV[Y2] = coord[Y2+v+1];    //  y position of end  point
          tempV[Z2] = coord[Z2+v+1];    //  z position of end  point

          GLine gL = new GLine(s, tempV);
          polyEntities.put( thisKey, gL );
        } 
        else {
          // bulge was NOT 0 so now we make an arc
          println("CREATING ARC DATA IN GPoly");
          PVector p1, p2, p3, c;

          float bulge = coord[BG+v] ;

          p1 = new PVector(coord[X1+v], coord[Y1+v], coord[Z1+v]);   // point1
          p2 = new PVector(coord[X2+v+1], coord[Y2+v+1], coord[Z2+v+1]); // point 2

          p3 = new PVector( ((p1.x+p2.x )/2), ((p1.y+p2.y )/2), 0 );
          c  =  new PVector (0, 0, 0);

          float twoQ = sqrt ( sq ( p2.x - p1.x ) + sq ( p2.y - p1.y )  );

          float theta = 4 * atan ( bulge ) ;
          float halfTheta  = theta /2;

          float sTheta = sin(halfTheta);

          float r = abs(twoQ / (2 * sTheta));

          println("twoQ = " + twoQ + "    r= " + r);

          println("bulge = " + bulge);

          float xyDist = sqrt ( sq(r) - sq (twoQ/2) ) ;

          float xDist = xyDist * ( (p1.y - p2.y) / (twoQ) );
          float yDist = xyDist * ( (p2.x - p1.x) / (twoQ) );

          // bulge 0 --> 1 means minor curve
          // bulge > 1 means majpor curve

          // sign tells direction

          // bulge range ::  -inf --> -1 --> 0 --> 1 --> inf
          if ( ( bulge > - 1 && bulge < 0 )  || bulge > 1 ) { 
            // negative minor bulge or major positive bulge
            c.x = p3.x - xDist;
            c.y = p3.y - yDist;
          } 
          else {
            // positive minor bulge or major negative bulge
            c.x = p3.x + xDist;
            c.y = p3.y + yDist;
          }// end if bulge

          println(" centre point " + c);
          println(" p1 " + p1 + "\tp2 " + p2);
          // get angles;
          // shows all the angles and lines
          pushMatrix();
          //  println("HERE");

          translate(canvasBorder, (height/2)-canvasBorder);
          translate(c.x, -c.y);

          // println("translate");
          // println(" p1 " + p1 + "\tp2 " + p2);
          //fill(255, 0, 0);
          //ellipse(0, 0, 5, 5);
          // stroke(255);
          // strokeWeight(3);
          float ea = degrees(atan2(p1.y-c.y, p1.x-c.x)); 
          // rotate(radians(ea));
          //  println("ea = " + ea);

          // line (0, 0, p1.x-c.x, -p1.y+c.y);
          float sa = degrees(atan2(p2.y-c.y, p2.x-c.x));
          //  println("sa = " + sa);
          // rotate(radians(sa));
          // line (0, 0, p2.x-c.x, -p2.y+c.y);
          strokeWeight(1);
          popMatrix();

          if (bulge < 0 ) {
            float ta = sa;
            sa = ea;
            ea = ta;
          }
          float[] tempV = new float[12];
          // create vertex array
          tempV[X1] = c.x;    //  x position of point
          tempV[Y1] = c.y;    //  y position of point
          tempV[Z1] = c.z;    // z position =  depth
          tempV[R1] = r;   // radius

          tempV[AS] = sa;
          tempV[AE] = ea;

          tempV[SX] = p1.x;
          tempV[SY] = p1.y;

          tempV[EX] = p2.x;
          tempV[EY] = p2.y;

          tempV[BLG] = bulge;



          GArc gA = new GArc(s, tempV);
          //gA.setBulge(bulge);
          polyEntities.put( thisKey, gA );
        } // end line or arc else
      } // end key if
    } // end for loop for entities.
    println("build poly is done ");
    return polyEntityValue;
  } // end build primatives for poly
  
  
  
}// end class ****

/*


based on concordance shiffman :

http://www.shiffman.net/2006/01/31/mmm-binary-trees/


see this for great explanation of trees

http://math.hws.edu/javanotes/c9/s4.html

*/


class Aggregate {
  
	 Node root;
	
	 Aggregate() {
		root = null;
	}
	
// methods

	public int insert (Entity e)
	{	
      	          int c = 0;
		  Node newNode = new Node(e);
		  if (root == null) 
		  { 
                       root = newNode;
	               c = 2;
		  } else {
	               c = root.insertNode( newNode );
	          }
            return c;
	}

        // search tree for a string -- in this case an entity hash-key
//        boolean contains ( Entity entityToFind ) 
//        {
//          if (root == null) 
//          {
//            return false;
//          } else {
//            return root.branchesContains ( root , entityToFind ) ;
//          }  
//          
//        } // end contains
        
         // search tree for a string -- in this case an entity hash-key
        public int traverseInsert ( Entity parentToFind, Entity childToInsert ) 
        {
          if (root == null) 
          {
            return 99;
          } else {
            return root.traverseInsert ( root , parentToFind, childToInsert ) ;
          }  
          
        } // end contains

	// Start the recursive traversing of the tree
	public void print(){
	   if (root != null)
	         root.printNodes();
	   }

       // Start the recursive traversing of the tree
	public ArrayList getNodes(){
           ArrayList a = new ArrayList();
	   if (root != null) {
                //a.add(root.nodeEntity);
	        a = root.getNodes( a );
	   }  
          return a;
        }

	
} // end aggregate
	
	
      class Node {
		
	    Entity nodeEntity;
            String entityKey;
	    Node left;
            Node right;
            int count;
		
	    Node ( Entity _e ) {
		   nodeEntity = _e;
                   entityKey = _e.getMyKey();
		   left  = null;
		   right = null;
	    } // end node constructor 
		
		
          // node methods 
          		
		// Inserts a new node as a descendant of this node
		// If both spots are full, keep on going. . .
	public int insertNode(Node newNode)  {
  
	            // we are in a node (PARENT) it has an entity
                    // println("inside NODE");
		     //println(nodeEntity.getMyKey()); // prints the parent key 
		    // println(newNode.nodeEntity.getMyKey()); // prints the new node key

	         int compareValue = nodesConnected (nodeEntity, newNode.nodeEntity );  // parent, child 

		   // if new element END is touching the start of PARENT
		   if ( compareValue  < 0 )
		   {
		      // if the spot is empty (null) insert here
		      // otherwise keep going!
		      if (left == null) left = newNode;
		      else left.insertNode(newNode);
                     
		   }
		   // if new word is alphabeticall after
		   else if (compareValue > 0)
		   {
		      // if the spot is empty (null) insert here
		      // otherwise keep going!
		      if (right == null) right = newNode;
		      else right.insertNode(newNode);

		   // if new word is the same
		   } else {
		      // We'd do something here if we wanted to when the Strings are equal
		      // For example, increment a counter
                      
		   }

                    return compareValue;
                    
		}  // end insert node 


          // this is node insert -- NOT tree insert !!!
          
         public int traverseInsert (Node thisNode, Entity entityToFind, Entity childToInsert ) 
         {
           
           int returnCode= -99 ;

           if ( thisNode == null ) {
             return returnCode;
           }
           
           // check to see if disabled -- break here if it is
           if ( ! thisNode.nodeEntity.isEnabled() ) {
             println("Entity disabled");
             return -999;
           }
            
           
           if ( thisNode.nodeEntity == entityToFind ) 
           {
             // found the node that will act as parent
               Node newNode = new Node(childToInsert);
               returnCode = thisNode.insertNode( newNode );
       
             return returnCode;  // insert outcome
             
           }   // end if
           
           // see if it is left -- if this is true
           // if it is not -- tehn check right side -- if it is then quit
          
           int isLeft = traverseInsert(thisNode.left, entityToFind, childToInsert );
         
           // ugly but still only search half the tree - some of the time
           if ( isLeft == 0 || isLeft == -1 || isLeft == 1 ) {
             // not on left -- try right
              
               return isLeft;
            } else {
                // println("searching right...");
              int isRight = traverseInsert(thisNode.right, entityToFind, childToInsert );
              if (isRight == 0 || isRight == -1 || isRight == 1 ) 
              {
                  return isRight;
              } else {
                  println("PARENT ENTITY NOT FOUND");
                  return isRight;
              }
               
            }//end else 
        
         }
         
         
//         boolean branchesContains (Node thisNode, Entity entityToFind ) 
//         {
//           
//           if ( thisNode == null ) {
//             return false;
//           }
//           
//           if ( thisNode.nodeEntity == entityToFind ) 
//           {
//             // found the node that will act as parent
//             println ("branches parent found  ");
//             println ("Attempting to insert CHILD");
//             return true;
//           }   // end if
//           
//           // see if it is left -- if this is true
//           // if it is not -- tehn check right side -- if it is then quit
//           println("searching left ...");
//           boolean isLeft = branchesContains (thisNode.left, entityToFind );
//           
//           // ugly but still only searc half the tree
//           
//           if ( isLeft ) {
//             // not on left -- try right
//               println("...................... ** PARENT found on left ** ");
//               return isLeft;
//            } else {
//              println("searching right...");
//              boolean isRight = branchesContains (thisNode.right, entityToFind );
//              if (isRight) 
//              {
//                  println("...................... ** PARENT found on right **");
//                  return isRight;
//              } else {
//                  println("PARENT ENTITY NOT FOUND");
//                  return isRight;
//              }
//               
//            }//end else 
//        
//         }
         

        public void printNodes()  {
          
                   if (left != null) left.printNodes();
                   
                   println( "node key " + nodeEntity.getMyKey() ); //+ " left " + left.nodeEntity.getMyKey() + "right " + right.nodeEntity.getMyKey());
                   
                   if (right != null) right.printNodes();
                }
                
         public ArrayList getNodes(ArrayList a){

                   if (left != null) left.getNodes( a );
                   
                   println( "node key " + nodeEntity.getMyKey() ); //+ " left " + left.nodeEntity.getMyKey() + "right " + right.nodeEntity.getMyKey());
                   a.add(nodeEntity);
                   
              
                   if (right != null) right.getNodes( a );
                   return (a);
                }
      
      
      public int nodesConnected (Entity parent, Entity child) {
            
                int c = 0; // not connected -- do nothing
                float tolerance = 0.0100f;

                
                PVector childStart  = child.getStartLoc();
                PVector childEnd    = child.getEndLoc();
                PVector parentStart = parent.getStartLoc();
                PVector parentEnd   = parent.getEndLoc();
                
              println( "parent " + parent.getMyKey() + " child " + child.getMyKey());
              
                
                // parent OK -- child is reversed
                if ( 
                      (   ( abs( parentStart.x - childStart.x) < tolerance )   &&  ( abs( parentStart.y - childStart.y ) < tolerance )   ) 
                
                    || 
                    
                      (  (  abs( parentEnd.x - childEnd.x   ) < tolerance  )   &&   ( abs( parentEnd.y - childEnd.y   ) < tolerance   )   ) 
                      
                    )  
                    
                   {
                     // child is "facing wrong way"  flip the child
                     println("flipping child");
                     child.flip();
                     // have to write flip for  line, arc, circle
                     // point does nothing -- it will never be here.
                     
                     childStart  = child.getStartLoc();
                     childEnd    = child.getEndLoc();
                     parentStart = parent.getStartLoc();
                     parentEnd   = parent.getEndLoc();
                       
                   }
       
                
                  if (  ( abs( parentStart.x - childEnd.x  ) < tolerance )   &&  ( abs( parentStart.y - childEnd.y   ) < tolerance )   ) 
                       {
                           c=-1;       
                       }
                  
                        
                
                  if (  ( abs( parentEnd.x - childStart.x ) < tolerance )   &&  ( abs( parentEnd.y - childStart.y ) < tolerance )     ) 
            
                        {
                           c=1;       
                        }
                
                return c;
                
          } // end connected ?
		
		
    } // end NODE class


	


int filePanelxOffset = 20;

public void drawPanels() {
    drawPanelBorders();
    displayLocation();
    displayFilename();
}


public void drawPanelBorders(){
  
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

public void displayLocation () {
  
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

public void displayFilename () {
  
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


// linear back plot

class BPElement {

  int plotColor;

  PVector first;
  PVector last;
  PVector data;

  BPElement (PVector _f, PVector _l, PVector _d, int _c) {
    println( "in parent element constructor");
    first.set(_f);
    println("a");
    last.set(_l);
    println("b");
    data.set(_d);
    
    println("inside here");
    plotColor = _c;
    
  }  // end constructor

  public void display() {
    println("overwrite me");
  }
  
}// end class BPElement





  
class BPLine extends BPElement {
  
   BPLine (PVector _s, PVector _e, PVector _d, int _c) {
     super(_s, _e, _d, _c);
     println("line constructed");
   } // end constructor
   
   
   public void display () {
     
       stroke(plotColor);
       line ( first.x, first.y, last.x,last.y ) ;
       
   } // end display
  
}  // end BPLine class


class BPArc extends BPElement {
  
  BPArc (PVector _s, PVector _e, PVector _d, int _c) {
     super (_s, _e, _d, _c);
   } // end constructor
  
  public void display () {
     
       stroke(plotColor);
       line ( first.x, first.y, last.x, last.y ) ;
       println("bp line");
       
   } // end display
  
  
}  // end BPArc



public void createBackPlot() {
  
  SHOW_BACK_PLOT = false;
  backPlotElements = new ArrayList();
  
  boolean SKIP = false;
  
  fill(255);
  ellipse (100,100,100,100);
  
  String lastGCode = "";
  boolean lineNumbers = false;
  PVector bpCurrent= new PVector(0,0,0);
  PVector bpLast = new PVector(0,0,0);
  PVector bpData = new PVector(0,0,0);

  
  float currentZ = 99;
  float currentFeed =0;
  
  int plotColor = color (0);
  int travel   = color (100);
  int arc02    = color (255,0,0);
  int arc03    = color (0,0,255);
  int lin01    = color (255,0,255);
  
  boolean bpLINE = false;
  boolean bpARC = false;
  
  strokeWeight(0.5f);
    
  
  String[] backPlotCode = gEngine.getCode();
  

  // go through each line of the gCode created
  for (int i = 0; i < backPlotCode.length; i++ ) {
        //  println("raw line just read " + backPlotCode[i]);
        
        if ( backPlotCode[i].equals("") ) {
          println("blank line");
          SKIP = true;
        }
    
        println("backplot line:: " + backPlotCode[i]);
        
        // split each line into words -- gcode fragments
        String[] bpWords = splitTokens (backPlotCode[i], " " );
       
       
       // check each word for meaning
       for (int j = 0; j < bpWords.length; j++ ) {
         
           println("word :" +bpWords[j]) ; 
           
           // ignore comments -- skip line once a comment is found
           if (bpWords[j].charAt(0) == '(' ) { 
                println("comment");
                SKIP = true;
                break;
           }
           
           // see if lines are numbered -- if so skip first word
           if (bpWords[j].charAt(0) == 'N' ) {
                lineNumbers = true;
                println("numbers on ");
           }
           
           if (bpWords[j].charAt(0) == 'Z' ) {
                bpCurrent.z = splitWord (bpWords[j], 'Z') ;
                println("currentHeight = " + bpCurrent.z);
            
           }
           
           if (bpWords[j].charAt(0) == 'X' ) {
                bpCurrent.x = splitWord (bpWords[j], 'X') ;
                println("currentX = " + bpCurrent.x);
                bpLINE = true;
           }
           
           if (bpWords[j].charAt(0) == 'Y' ) {
                bpCurrent.y = splitWord (bpWords[j], 'Y') ;
                println("currentY = " + bpCurrent.y);
                bpLINE = true;
           }
           
           if (bpWords[j].charAt(0) == 'I' ) {
                bpData.x = splitWord (bpWords[j], 'I') ;
                println("currentI = " + bpData.x);
                bpARC = true;
           }
           
           if (bpWords[j].charAt(0) == 'J' ) {
                bpData.y = splitWord (bpWords[j], 'J') ;
                println("currentJ = " + bpData.y);
                bpARC = true;
           }
           
           // got a speed change 
           if (bpWords[j].charAt(0) == 'F' ) {
                currentFeed = splitWord (bpWords[j], 'F') ;
                println("speed change");   
                      
           } // end G code fragment

           if (bpWords[j].charAt(0) == 'G' ) {

                     if (bpWords[j].equals("G01")) {
                        
                     }
             
           } // end G code fragment
           
           
       } // end words
       
       // do the drawing here
       
//     if (bpCurrent.z > 0){
//                               plotColor = travel;
//     }
//     
//      if (bpLINE) {
//               plotColor = lin01;
//      }
//     if (bpARC) {
//               plotColor = arc02;
//           } // end z;
    

    if ( !SKIP ) {
         println("adding");
         
         if (bpARC == true ) {  
           println ("add arc element");
              // BPElement bpE = new BPArc( bpLast, bpCurrent, bpData, plotColor);
               backPlotElements.add( new BPArc( bpLast, bpCurrent, bpData, plotColor) );
               bpLINE = false;
           }
          
           if (bpLINE == true){
               println ("add line element");
               backPlotElements.add( new BPLine ( bpLast, bpCurrent, bpData, plotColor ));
           }
           
        
           bpLast.set(bpCurrent); 
    }  // end skip

      
       println("bpLast.x = "  + bpLast.x);
       println("************* line drawn");
  
      SKIP = false;
      
      bpLINE = false;
      bpARC = false;   
      
  } // end for 
 
      SHOW_BACK_PLOT = true;
      println("done!!!");
      
} // end backplot



public float splitWord (String s, char t  ) {
   
    String[] parts = split(s,t);   
    return PApplet.parseFloat( parts[1] );
}


public void backPlot() {
  //println("aa");
  //println("size " + backPlotElements.size());
  
  for (int i = 0; i < backPlotElements.size(); i ++ ) {
    
    println("inside");
    BPElement bp = (BPElement) backPlotElements.get(i);
    bp.display();
    
  }
}
  

class Blob {
  
  // blob has a raw direction 
 ArrayList blobElements = new ArrayList();    // arrayList of Entities  - can add at index
 boolean  IS_CLOSED = false;                  // nature of blob
  
 int closedColor = color( 0, 127, 127 ); // CYAN
 int openColor   = color( 127, 0, 127 ); // purple

 int rawDirection = -1;
 float minX, maxX, minY, maxY, ctrX, ctrY;
  

   Blob() {
    println("NEW BLOB CONSTRUCTED ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
  }
  
 
    
  public void populateBlob( ArrayList aL ) {
        blobElements = aL;
   }
   
   public void blobAdd ( Entity toAdd  ) {
        blobElements.add(toAdd); 
   }
    
  public ArrayList getBlobEntities() {
    return blobElements;
  }
  
  
  public void showBlob() {
     // printArray(blobElements,"blob"); 
     
     println("Show BLOB \n\rblob size: " + blobElements.size());
     for (int i = 0; i < blobElements.size(); i ++ ) {
       
     	 Entity current = (Entity) blobElements.get(i) ;
     
	 print("key " + current.getMyKey() );
	 PVector s = current.getStartLoc();
	 PVector e = current.getEndLoc();
       
	 println("\tSX " + s.x + "\tSY " + s.y + "\tEX " + e.x + "\tEY " + e.y);

     }  // end for 
     println("blob is closed ? " + IS_CLOSED );
  }
  
  public int getBlobLength( ) {
    return blobElements.size();
  }
  
 
  public void isClosed() {
 
    
    int blobSize = blobElements.size();
    int matchCount = 0;
    float tolerance = 0.01f;
    
      Entity first = (Entity) blobElements.get(0);
      Entity last =  (Entity) blobElements.get(blobSize-1);  // last element 
      
      IS_CLOSED = false;
      
      if ( 
              (  (abs(first.getStartLoc().x - last.getEndLoc().x ) < tolerance)  )
           && (  (abs(first.getStartLoc().y - last.getEndLoc().y ) < tolerance)  )
         ) 
         
         {
        
           IS_CLOSED = true;
        
         } // end if 
 
      
  }
  
  public boolean getIsClosed () {
     return IS_CLOSED; 
  }
  
  
   public int getRawDirection(){
      return rawDirection; 
   }
   
   public void setRawDirection( int rD ) {
      rawDirection = rD ;
   }
  
   public int calculateRawDirection() {
     
     if ( IS_CLOSED ) {
              
          float startX, startY, midX, midY, endX, endY;
      
          minX =   10000;
          maxX =  -10000;
          minY =   10000;
          maxY =  -10000;
      
          // first, find max and min
          for (int c = 0; c < blobElements.size(); c++ ) {

              Entity pE = (Entity) blobElements.get(c);
      
              float eX = pE.getEndLoc().x;
              float eY = pE.getEndLoc().y;
      
              if (eX < minX ) { 
                minX = eX;
              }
              if ( eX > maxX ) { 
                maxX = eX;
              }
              if (eY < minY ) { 
                minY = eY;
              }
              if ( eY > maxY ) { 
                maxY = eY;
              }
           
            } // end for 
           
            ctrX = ( maxX + minX ) / 2;
            ctrY = ( maxY + minY ) / 2;
      
             // println ("minX " + minX + " minY " + minY + "  maxX " + maxX  + " maxY  " + maxY);
             // println( "ctrX " + ctrX + " ctrY " + ctrY );
           
             
           pushMatrix();
             
               translate(origin.x, origin.y );
               ellipse(ctrX,-ctrY, 2, 2);
              
           popMatrix();
           
          // go through and get the atan2 angle of end point to ctr
          pushMatrix();
          
            translate(origin.x, origin.y );
            translate(ctrX,-ctrY);
          
            fill(255,255,0);
            ellipse(0,0,3,3);
          
            float[] angles = new float[ blobElements.size() ];
      
      
          for (int c = 0; c < blobElements.size(); c++ ) {
      
              Entity pE = (Entity) blobElements.get(c);
      
              float eX = pE.getEndLoc().x;
              float eY = pE.getEndLoc().y;
              
              stroke(0,255,255);
              strokeWeight(2);
              line (0,0,eX-ctrX,-eY+ctrY);
      
                if ( pE.getType().contains("Line") || pE.getType().contains("Arc") ) {
                  angles[c] = atan2(-eY+ctrY, eX-ctrX) + TWO_PI;
                } 
                else {  
                  angles[c] = -99;
                }
          
          } // end for
          popMatrix();
      
          // now calculate the direction
          int angleTotal = 0;
          println("angles ");
      
          for (int d=2;d<angles.length; d++  ) {
            println (angles[d]) ; 
      
            if ( ( angles[d] != -99 ) && (angles[d -1] != 99 ) ) {
              float test = angles[d] - angles[d-1];
              if ( test > 0 ) angleTotal++;
              if ( test < 0 ) angleTotal--;
            }
          } // end for 
      
      
          println("angleTotal = " + angleTotal);
          if (angleTotal < 0) { 
            println("direction is CCW");
            setRawDirection ( CCW ) ;
            return CCW ;
          } 
          else {
            println("direction is CW");
            setRawDirection ( CW ) ;
            return CW;
          }
        
   } else {
     // open figure
     setRawDirection (-1);
       return -1;
   }
   
 } // end fxn
            
     
  
} // end class
public void checkForPoly(DXFTable dT, String s) 
{
  println("CHECK FOR POLY >>> ");
  println("Table is " + s ) ;

  String e[][] = dT.getTableEntities();
  // for each entity extract the data
  for (int i = 1; i < e.length; i++) 
  {
    if (e[i][1].contains("LWPOLYLINE")) 
    { 
      DXFTable t = expandLWPoly( e[i] ) ;
      createEntities (t);
      dT.disableElement(i);
    } // end if
  }// end for
} // end function

public void createEntities( DXFTable dT ) 
{

  String e[][] = dT.getTableEntities();

  println("\n\rcreating entity objects from DXFTable ");
  println("ent length " + e.length);

  //println("\n\r\n\rAll the entities in the table ");
  //dT.printEntities();
  print("\n\r\n\r");


  // for each entity extract the data
  for (int i = 1; i < e.length; i++) {

    float[] tempV = new float[PRIMSIZE];


    if (e[i][1].contains("POLYLINE") ) {

          //printArray(e[i], "e[i]");
          println( "we ARE splitting a poly" );
          println("the poly has " +e.length  + " elements");
          tempV = new float[POLYSIZE];
          float[]   tempP = new float[4];
          String[]  tempS = e[i];
    
          int offset = 0;
          i++;
          //println("ei1 " + e[i][1]);
              while ( (e[i][1].contains ("VERTEX")) ) 
              {
                  //println("ei[1]= " + e[i][1] + " length is " + e[i].length + " i= " + i);
                  // println("got a vertex");
          
                  tempS = concat( tempS, e[i]); // add this vertex to the source
          
                  tempV = parseEntity( e[i], POLYSIZE ); // entity and offset -- offset only for poly
                  tempP =  concat( tempP, tempV); // add tempV onto the poly
          
                  i++;
              } // loops until all vertices added
    
          tempP = subset ( tempP, 4);
    
          GPoly gY = new GPoly(tempS, tempP);
          //entities.put( thisKey, gY );
          addToHashmap(gY);
    }// end poly

    tempV = parseEntity ( e[i], PRIMSIZE ); // entity and offset -- offset only for poly

    if (e[i][1].contains("POINT")) 
    {
      GPoint p = new GPoint(e[i], tempV);
      addToHashmap(p);
      //entities.put( thisKey, p );
    }

    if (e[i][1].contains("LINE")) 
    {
      GLine gL = new GLine(e[i], tempV);
      addToHashmap(gL);
      //entities.put( thisKey, gL );
    }

    if (e[i][1].contains("ARC")) 
    {
      GArc gA = new GArc(e[i], tempV);
      addToHashmap(gA);
      //entities.put( thisKey, gA );
    }

    if (e[i][1].contains("CIRCLE")) 
    {     
      GCircle gC = new GCircle(e[i], tempV);
      addToHashmap(gC);
    } // end circle

    if (e[i][1].contains("ELLIPSE")) 
    {     
      GEllipse gC = new GEllipse(e[i], tempV);
      addToHashmap(gC);
    } // end circle
    //  } // end check length of e[i]
    
  } // for loop

  CREATE_ENTITIES = false;
} // end creat entitites





public float[] parseEntity (String[] s, int thisSize ) 
{

  if (VERBOSE) { 
    println("got to the parseEntity function");
  }
  int entityElementCount = 0;
  int tempCount = 0;
  float[] tempE = new float[thisSize];
  if ( thisSize == POLYSIZE ) { 
    if (VERBOSE) { 
      println("setting BG to zero");
    }
    tempE[BG] = 0 ;
  }

  while ( entityElementCount < s.length ) {
    // println("s[entityElementCount] *" + s[entityElementCount] + "* \t\telementCOUnt " + entityElementCount  + "  s.len " + s.length);
    // look at each element
    // find the elements that matter to the drawing
    String test = s[entityElementCount];
    // println("test string = " + test);
    if (test.equals(" 5")) { 

      //   println("found an ID: $$$$$$$$$$$$$$$$$" + s[entityElementCount + 1]);
    }
    if (test.equals(" 10")) { 
      tempE[X1] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) { 
        println("found a 10: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 20")) {
      tempE[Y1] = PApplet.parseFloat(s[entityElementCount + 1]);

      if (VERBOSE) { 
        println("found a 20: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 30")) {
      tempE[Z1] = PApplet.parseFloat(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found a 30 : " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 11")) { 
      tempE[X2] = PApplet.parseFloat(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found an 11: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 21")) {
      tempE[Y2] = PApplet.parseFloat(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found a 21: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 31")) {
      tempE[Z2] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 31: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 40")) { 
      tempE[R1] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 40: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 41")) { 
      tempE[R2] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 41: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 50")) {
      tempE[AE] = PApplet.parseFloat(s[entityElementCount + 1]);  // angle end
      if (VERBOSE) {  
        println("found a 50: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 51")) {
      tempE[AS] = PApplet.parseFloat(s[entityElementCount + 1]);  // angle start
      if (VERBOSE) {  
        println("found a 51: " + s[entityElementCount + 1]);
      }
    }

    if ( (test.equals(" 42"))   && (thisSize == PRIMSIZE)  ) {   //  && (thisSize == PRIMSIZE)

      tempE[R3] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 42 && PRIMSIZE: " + s[entityElementCount + 1]);
      }
    }

    if ( (test.equals(" 42")) && (thisSize == POLYSIZE) ) {
      tempE[BG] = PApplet.parseFloat(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 42 && POLYSIZE: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals("") ) {
      if (VERBOSE) {
        println("blank");
      }
    }

    entityElementCount++;
  }  // end while

  return tempE;
} // end parseEntity 


//void expandLWPoly (DXFTable dT) {
//  
// for (int c = 0; c < dT.getTableEntitiesLength(); c++ )  {
//    
//      if (dT.tableEntities[c][1].contains("LWPOLYLINE")) { 
//        
//          dT.tableEntities[c][0] = "  0"; 
//          dT.tableEntities[c][1] = "POLYLINE";
//        
//          String[] prefix = { "  0", "VERTEX" };
//          for (int q = dT.tableEntities[c].length-1; q > 0 ; q--) {
//            if (dT.tableEntities[c][q].contains( " 10" )) {  // marks the start of a vertex
//              println("c " + c + " val " + dT.tableEntities[c][q]);
//              dT.tableEntities[c] = splice( dT.tableEntities[c], prefix, q );
//            } // end if
//          } // end for
//
//          DXFTable tempPoly = new DXFTable ( dT.tableEntities[c] );
//          tempPoly.parseEntities( "  0" );
//          // tempPoly.printEntities();
//          createEntities (tempPoly);
//        } // end if contains 
//        
//    for (int i = 0; i < dT.getTableEntitiesLength(); i ++ ) {
//            if (dT.tableEntities[i][1].contains("LWPOLYLINE")) { expandLWPoly(dT);}
//   } 
//
//  } // end table
//  
//  
//  
//}
//

public DXFTable expandLWPoly (String[] s) {

  DXFTable tempPoly = new DXFTable(s);

  print("A POLY HAS BEEN FOUND >>>>   ");
  println("begin expand poly");
  if (s[1].contains("LWPOLYLINE")) { 
    s[1] = trim(s[1]);

    s[0] = "  0"; 
    s[1] = "POLYLINE";
    String[] suffix = { 
      "  0", "SEQEND"
    };
    s = splice( s, suffix, s.length );
    String[] prefix = { 
      "  0", "VERTEX", "  8", "0"
    };
    for (int q = s.length-1; q > 0 ; q--) {
      if (s[q].contains( " 10" )) {  // marks the start of a vertex
        if (VERBOSE) {
          println("q " + q + " val " + s[q]);
        }
        s = splice( s, prefix, q );
      } // end if
    } // end for

    tempPoly = new DXFTable ( s );
    tempPoly.parseEntities( "  0" );
    // println("\n\r\n\rprinting the entites just created INSIDE expandPOLY \n\r");
    // tempPoly.printEntities();
  } // end if contains
  return tempPoly;
} // end table


public void explodePolys() {
  
        println("Explode Polys");
  
         ArrayList explodedPrims = new ArrayList(); 
	
	 Iterator i = entities.values().iterator();

	  while (i.hasNext ()) {

	    Entity e = (Entity) i.next();

		if (e.getType().contains("Poly")) {
			// here you need to add a way to get all the the poly netities into the main entity list
			println("got hashing to here");
                        HashMap polyPrims= new HashMap();

                            polyPrims = e.getPolyEntities();

                            Iterator j = polyPrims.values().iterator();
                            while (j.hasNext ()) {
                      
                      	        Entity ppE = (Entity) j.next();
                                explodedPrims.add(ppE);
                                
                                //addToHashmap(pE);
                            } // end indside
  
			e.disable();
		} // end if poly
		
	  } // end while iteration;

        // now all polyPrims are in an array list -- iterate the list and add the prims to entitites
        for (int eps = 0; eps < explodedPrims.size(); eps++ ) {
          Entity eX = (Entity) explodedPrims.get(eps);
          addToHashmap(eX);
        }

	
}  // end explode polys





class Entity {

  int renderColor = color(255,0,0);
  String[] source;
  String[] gcode;
  float[]  coord;
  float depth, feed, travelHeight, travelFeed, plungeRate, i, j, k;
  String type ="";
  
  boolean isAggregated = false;

  int[] gElements;

  PVector startLoc, endLoc;
  
  PVector[] targets;
  int tStart     =  0;
  int tEnd       =  1;
  int goofyStart =  2;
  int goofyEnd   =  3;
  
  String myKey = "";
  
  boolean IS_ENABLED = true;  // enabled / disabled when agregating
  int      AGG_INDEX = -1; // which agregate ar you ?
  int      LEAF_DIRECTION = -1;
  
  int rawDirection = -1;
  
  Entity() {
	myKey = "";
     startLoc = new PVector();
     endLoc   = new PVector();
  }

  Entity(String[] s_, float[] v_) { // source string (_s) from DXF file, array of verticies

    source = s_;
    coord  = v_;
    depth  = -2.0f;
    feed   = 200;
    travelFeed = 200;
    travelHeight = 3;
    plungeRate = 75;

    startLoc = new PVector();
    endLoc   = new PVector();
    targets =  new PVector[4];

    gcode = new String[1];
    gElements = new int[3];

  } // end constrcutor
  
 
  public void flip() {
    println(" overwrite");
  }
  
  public void setAggIndex(int i ) {
    AGG_INDEX = i ;
  }
  
  public int getAggIndex() {
    return AGG_INDEX;
  }
  
  public void setLeafDirection(int d ) {
    LEAF_DIRECTION = d ;
  }
  
  public int getLeafDirection() {
    return LEAF_DIRECTION;
  }
  
  
  public void setAggregated(Boolean state){
    isAggregated = state;
  }
  
  public Boolean getAggregated(){
    return isAggregated;
  }
  
  public void setColor(int c) {
     renderColor = c; 
  }
  
  public int getColor() {
    return renderColor;
  }
  
  public int getEntityDirection () {
      println("getDirection parent");
      return -1;
  }
  
  public void switchDirection() {
    
      println("parent circle switch direction");    

  } 

  
  
   public HashMap getPolyEntities() {
         HashMap hm = new HashMap();
	 return hm;
   }  // end getPOly entities

    public void disable() {
	IS_ENABLED = false;
    }

	public void enable() {
		IS_ENABLED = true;
	}

	public void setState(boolean s) {
		IS_ENABLED = s;
	}

	public boolean getState() {
		return IS_ENABLED;
	}

	public boolean isEnabled() {
	  return IS_ENABLED;
	}

	public void setMyKey(String mK) {
	   myKey = mK; 
	}

	public String getMyKey() {
	   return myKey; 
	}

	public int getDDirection() {
	     return -1;
	}
 
  public int[] getElements () {
    return gElements;
  }


  public PVector[] getTargets (int i) {
    return targets;
  }
  
  public HashMap getPrimatives() {
    HashMap h = new HashMap();
    return h;
  }

  public String[] getPolyKeys () {
    //  println("from parent");
    return source;
  }


  public String getType() {
    return type;
  }

  public float getFeed() {
    return feed;
  } // get


  public void setFeed( float f ) {
    feed = f;
  }

  public float getDepth() {
    return depth;
  } // get


  public void setDepth( float d ) {
    depth = d;
  }

  public float getPlungeRate () {
    return plungeRate;
  }

  public float getTravelFeed() {
    return travelFeed;
  }

  public void setTravelFeed( float t ) {
    travelFeed = t ;
  }
  
  public float getTravelHeight() {
    return travelHeight;
  }

  public void setTravelHeight( float t ) {
    travelHeight = t ;
  }

  public float getPlunge() {
    return plungeRate;
  }


  public void setStartLoc(float x, float y) {
    getStartLoc().x = x;
    getStartLoc().y = y;
  } // end getFIrst NC

  public void setEndLoc(float x, float y) {
    endLoc.x = x;
    endLoc.y = y;
  } // end getFIrst NC


  public PVector getStartLoc() {
    return ( startLoc );
  }

  public PVector getEndLoc() {
    return ( endLoc );
  }

  public void showEndPoints () {

    fill(0, 255, 0);
    noStroke();
    ellipse( startLoc.x, -startLoc.y, 1, 1 );

    noFill();
    stroke( 255, 0, 0);
    strokeWeight(0.5f);
    ellipse( endLoc.x, -endLoc.y, 1, 1 ); 

    stroke(255);
  }

  public String[] getNC (int i, int j) {
    String[] ss = {
      "this is an NC line from PARENT ENTITY", "really"
    };
    return ss;
  } // end addNC


  // print array passed to object
  public void printSource () {
    println("ENTITY SUPER printing source");
    printArray(source, "source");  // utility function
  } 

  public void printVerticies() {
    println("ENTITY SUPER printing verticies");

    for (int j = 0; j < coord.length; j ++) {
      println(coord[j]);
    }
  }

  // need to have all methods here -- kind of like prototypes for subClasses
  public void display() {
    println("superDisplay");
  } // end display
  
  public String coordFormat (float f) {
     String sf = nf(f,3,3);
     return sf;
  }
  
  public String feedFormat (float f) {
     String sf = nf(f,3,0);
     return sf;
  }
  
  
//  int connectedTo ( Entity child ) {
//            
//                int c = 0; // not connected -- do nothing
//    
//                PVector childStart = child.getStartLoc();
//                PVector childEnd   = child.getEndLoc();
//                
//                if (  ( (startLoc.x == childEnd.x)   && ( startLoc.y == childEnd.y   ) ) 
//                   || ( (startLoc.x == childStart.x) && ( startLoc.y == childStart.y ) )  ) 
//                   {
//                     c=-1;       
//                   }
//                  
//                  if (  ( (endLoc.x == childStart.x) && ( endLoc.y == childStart.y ) )
//                     || ( (endLoc.x == childEnd.x  ) && ( endLoc.y == childEnd.y   ) ) 
//                  {
//                     c=1;       
//                  }
//                
//                return c;
//                
//  } // end connected ?
 
    
} // end class

class gCodeEngine {

  String[] gCode_all = new String[1]; // file

  String[] gLine; 

  String[] eCode;

  String[] gCodeOrder = new String[1];



  PVector currentLoc;
  PVector[] gTargets;
  float gFeed, gDepth, gRapid, gPlungeRate, gTravelFeed, gTravelHeight;
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

  public void setMasterRapid (float r ) {
    gDepth = r;
  }

  public float getMasterRapid () {
    return gRapid;
  }

  public void setMasterDepth (float d ) {

    Iterator i = entities.values().iterator();

    while (i.hasNext ()) {

      Entity e = (Entity) i.next();
      println("setting depth");
      e.setDepth(d);
    } // end while iteration;


    gDepth = d;
  }

  public float  getMasterDepth () {
    return gDepth;
  }

  public void setMasterFeed (float f ) {
    gFeed = f;
  }

  public float  getMasterFeed () {
    return gFeed;
  }

  public void setTravelFeed (float f ) {
    gTravelFeed = f;
  }

  public float  getTravelFeed () {
    return gTravelFeed;
  }

  public void setTravelHeight (float tH ) {
    gTravelHeight = tH;
  }

  public float  getTravelHeight () {
    return gTravelHeight;
  }


  public void setgCodeDirection(int d) {
    gCodeDirection = d;
  }

  public void clearAll() {
    gCode_all  = new String[1]; // file
    gCodeOrder = new String[1];
    lineCount  = 0;
    currentLoc.x = -9999;
    currentLoc.y = -9999;
  }

  public void setSteps (int s) {
    gSteps = s;
  }
  
  public int  getSteps ( ) {
    return gSteps;
  }

  public String[] getCode () {
    return gCode_all;
  }

  public void addLines (String[] ss) {
    ; // need a way to add lines
  }

  public void addLine (String s) {

    resetLine();

    if (NUMBERLINES) { 
      addLineNumberElement();
    }

    addLineElement (s);

    flushLineElements ();
  }

  public void addBlank() {
    NUMBERLINES = false;
    addLine("");
    NUMBERLINES = true;
  }

  public void addLabel(String s) {
    //NUMBERLINES = false;
    addLine("(" + s + ")");
    // NUMBERLINES = true;
  }

  public void resetLine() {
    gLine = new String [1];
    gLine[0] = "";
    lineElementIndex = 0;
  } // end clear currentLine

  public void addLineElement (String s) {    
    gLine = append (gLine, s);
  } // end add element

  public void addLineNumberElement () {
    addLineElement("N" + (lineCount*lineNumberStep));
    lineCount++;
  } 


  public void flushLineElements () {
    String theNewLine  = join (gLine, "  ");
    theNewLine         = trim(theNewLine);
    gCode_all          = append( gCode_all, theNewLine );
  }

  public void setLineNumberStep (int l) {
    lineNumberStep = l;
  }

  public int getLineNumberStep () {
    return lineNumberStep;
  }


  public void addHeader () {
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

  public void addFooter() {
    addBlank();
    addLabel("footer");
    addLine ("G01  F400 Z30.00");  // rapid to 30mm up
    addLine ("G40");
    addLine ("M30 ");
    addLine("(end)");
  }



  public void buildCode(Entity e, int pathTYPE) { // type = RAW or GOOFY
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
  public void resetEndTarget( PVector t) {
    gTargets[1].x = t.x;
    gTargets[1].y = t.y;
  }


  public void addEntityGCode(Entity e, int pathType) {

    eCode = e.getNC(gSteps, pathType);

    //println("got a string array for element gcode");
    //printArray (eCode, "code from element");

    for (int ls = 0; ls < eCode.length; ls ++ ) {
      addLine(eCode[ls]);
    }
    println("done entity add code");
    // checkDepthPlunge( e.getDepth(), e.getPlungeRate());
  } // end add Entity code




  public void checkStartPosition( Entity e, PVector target) {

    float joinTolerance = 0.01f;

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


  public void setCodeDirection ( int d) {
    gCodeDirection = d;
  }

  public int getgCodeDirection () {
    return gCodeDirection;
  }

  public void addtoOrder (String s) {
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

public void addToHashmap(Entity e) {

  hashCount++;
  // create the hashmap key for this entity
  String thisKey = entityKey + hashCount;
  println("thisKey " + thisKey);

  // make sure it is unique -- if not throw error
  if ( entities.containsKey(thisKey) ) {
    println(" error creating new object -- this key is already assigned ");
    println(" ************************* -- **************************** ");
  } 
  else {
    gEngine.addtoOrder(thisKey);
    entities.put( thisKey, e );
    //Entity t = Entity.get(thisKey);
    e.setMyKey(thisKey);
  } // end if else
  
}// end hashadd


// send a hashmap in and get a sorted ArrayList out
// try for opened or closed ?

public ArrayList orderElements (HashMap h) {

  println("\n\r\n\rOrdering the Elements with gCodeEngine");
   
  // setup for ordering 
  resetAggregated();               //make sure all entities are included in ordering
  sortHash      = new HashMap(h); 
  aggregateList = new ArrayList();
  subparentList = new String[1];

  // set counters
  sortCount = sortHash.size(); // count elemtns removed
  aggregateCount = -1;        // count aggregates created
  
  println("number of entities to search " + sortCount);
  
  // get firstParent
  Entity parentEntity = getNextParent();
   

      // now iterate the hash and see if anyone can be added to the aggregate
     // compare focusEntity to everyone else in the hash
     int loopCount = 0;
     
            while ((sortCount > 0 ) ) {
             
               println("looking for aggragate entities");
               println(" new parent is " + parentEntity.getMyKey());
               searchForChildren ( parentEntity ) ;
               
               // println("should have a full aggregate now");
               // if you get to here it means that we have built an entity
               // MATCH_FLAG = false;
             
               println("get a new seed");
               
               parentEntity = getNextParent();
               println(" new seed is " + parentEntity.getMyKey());
       
            }  // end while --> end of the hashMap to search 
            
          
          // output all of the aggregates
          for (int aa= 0; aa< aggregateList.size(); aa++ ) {
            Aggregate aaa = (Aggregate) aggregateList.get(aa);
            println("aggregate " + aa);
            aaa.print();
            
           
          }
         
    return aggregateList;
    
 } // end order elements
 
     // new function for searching for children 
     
   public void searchForChildren (Entity pEntity) {
            int s = -99;
 
            Aggregate testAggregate = (Aggregate) aggregateList.get( aggregateCount );
            
             //boolean MATCH_FLAG = false;
             
             Iterator itr = sortHash.values().iterator();
             
               while ( itr.hasNext () ) {
               //subParent ="";
               
               Entity childEntity = (Entity) itr.next();  // yes, get item g from sort list
               
                  if (childEntity  != pEntity) {
                    
                    if ( childEntity.isEnabled() == false ) {
                      sortCount--;
                      
                    } else {
                    
                        if  (childEntity.getAggregated() == false  ) {
                          
                           
                         // s = testAggregate.insert ( childEntity  ) ;  // extend this to first find parent -- try to insert and take action according to outcome
                         s = testAggregate.traverseInsert (pEntity, childEntity);
                         println( " inside addToAggregate -- > got " + s + " entity " + childEntity.getMyKey());
                         
                         println("s ===== " +s);
                         
                          // outcome can be -1 >left, 1 . right, 2 > root,  0 = no match 
                          switch (s) {
                              case 2:
                                  println("root added");
                                  sortCount--;
                                  break;
                                  
                              case 1:
                                  println("added right");
                                  childEntity.setAggregated(true);
                                  sortCount--;
                                  //MATCH_FLAG = true;
                                    subparentList = append (subparentList, childEntity.getMyKey() ) ;
                                 // subParent = childEntity.getMyKey() ;
                                  //Entity decendantToParent = (Entity) sortHash.get(subParent);
                                  
                                  // searchForChildren ( childEntity );
                                  break;
                                  
                              case -1:
                                  println("added left");
                                  childEntity.setAggregated(true);
                                  sortCount--;
                                  //MATCH_FLAG = true;
                                    subparentList = append (subparentList, childEntity.getMyKey() ) ;
                                   // subParent = childEntity.getMyKey() ;
                                   // searchForChildren ( childEntity );
                                  break;
                                    
                              case 0:
                                  println("0 = NO match");
                                  break;
                                
                              case -99:
                                 println("error");
                                 break;
                            
                          } // end switch
                          println();
                          println();
                        } // end if 
                    } // end if enabled
                  } // end if 

               } // end of loop through the hashMap
              
    } // end search for children
               
 
   public Entity getNextParent() 
   {
       int len = subparentList.length;
       println("len ++ " + len);
       Entity p = new Entity();
        if ( len > 1 )
        { 
            if (sortHash.containsKey(subparentList[len-1])) 
            {
              p = (Entity) sortHash.get(subparentList[len-1]);
              subparentList = shorten ( subparentList );
              println("subparentlength = " + subparentList.length);
            }  
        } else { 
           p = getSeed();
        }
        println("************    new PARENT      ******************************************* " + p.getMyKey());
       return p;
   }
 
   public Entity getSeed() {
       
        println("\n\r^^^^^^^^^^^^^^^^^^^^Creating new seed for aggregate ^^^^^^^^^^^^^^^^^^^^^^^" );
        Aggregate a = new Aggregate();
        aggregateList.add(a);
        
        //Entity seedEntity = getHighX ();
         Entity seedEntity = getLowX ();
        seedEntity.setAggregated(true);
        
        int outCome = a.insert(seedEntity);  // become root of aggregate
        sortCount--;
        //println("sortCount inside getSeed " + sortCount);
        aggregateCount++;
          println(" creating a new aggregate " ) ;
          println(" first key " + seedEntity.getMyKey());
          println("first add = (hope for a 2) " + outCome);
          println();
          println();
     return seedEntity;
   
  }

public Entity getLowX () {

  float lowestX = 99999999;

  Entity lowE = new Entity();

  Iterator it = sortHash.values().iterator();

  while (it.hasNext () ) {
    Entity e = (Entity) it.next();
  
    if ( (e.isEnabled()) && (e.getAggregated() == false ) ) {  // remove polys from the list

      if (e.getStartLoc().x < lowestX) {
        lowestX = e.getStartLoc().x;
        lowE = e;
      } // end if lowest
    } // end if enabled
  } // end while -- iterator for hashmap 

  return lowE;
} // end get lowest


public Entity getHighX () {

  float highestX = -99999999;

  Entity highE = new Entity();

  Iterator it = sortHash.values().iterator();

  while (it.hasNext () ) {
    Entity e = (Entity) it.next();
  
    if ( (e.isEnabled()) && (e.getAggregated() == false ) ) {  // remove polys from the list

      if (e.getStartLoc().x > highestX) {
        highestX = e.getStartLoc().x;
        highE = e;
      } // end if lowest
    } // end if enabled
  } // end while -- iterator for hashmap 

  return highE;
} // end get lowest
//
//
//

public void resetAggregated () {
  
    Iterator i = entities.values().iterator();
    while (i.hasNext ()) {
        Entity e = (Entity) i.next();
        e.setAggregated(false);
        e.setAggIndex(-1);
        e.setLeafDirection(-1);
    }
  
} // end draw entities

//
//int addToAggregate (HashMap sHash, Aggregate agg) {
//
//     int s=0;
//     Iterator itr = sHash.values().iterator();
//     while ( itr.hasNext () ) {
//
//       
//   Entity compareEntity = (Entity) itr.next();  // yes, get item g from sort list
//   
//   s = agg.insert ( compareEntity ) ;
//   
//   println( " inside addToAggregate -- > got " + s);
//     } // end while
//  
//  return s;
//} // end add to aggregate
////
////
////
//
//
////String[] getTouchList (HashMap sHash, Entity testEntity) 
////{
////
////  String[] s = new String[1];
////  s[0] = "touchlist";
////
////  Iterator itr = sHash.values().iterator();
////
////  while (itr.hasNext ()) {
////
////    compareEntity = (Entity) itr.next();  // yes, get item g from sort list
////
////      while (compareEntity != testEntity) {
////
////      ARE_CONNECTED = areConnected( testEntity, compareEntity);
////      if (ARE_CONNECTED == 1 ) { 
////        // two entities touch -- add them to the touchList
////        s = append ( s, testEntity.getMyKey() ); 
////        s = append ( s, "CW");
////      }  // end if
////    } // end while
////  }// end while 
////
////  return s;
////} // end touchList
////
//
//
//
////int areConnected( Entity e1 ) {
////
////  int c = 0;
////
////  Entity defaultEntity = new Entity();
////  // PVector defaultStart = new PVector(99999,999999);
////  // PVector defaultEnd   = new PVector(99999,999999);
////  defaultEntity.setStartLoc(PVector(99999, 999999));
////  defaultEntity.setEndLoc(PVector(99999, 999999));
////
////  c = areConnected (e1, defaultEntity);
////
////  return c;
////}
////
////int areConnected (Entity first, Entity next) {
////
////  int c = 0; // not 
////  PVector firstS = first.getStartLoc();
////  PVector firstE = first.getEndLoc();
////
////  PVector nextS = next.getStartLoc();
////  PVector nextE = next.getEndLoc();
////
////  if (  ( (firstE.x == nextS.x) && ( firstE.y == nextS.y ) )
////    || ( (firstS.x == nextE.x) && ( firstS.y == nextE.y ) ) 
////    || ( (firstE.x == nextE.x) && ( firstE.y == nextE.y ) ) 
////    || ( (firstS.x == nextS.x) && ( firstS.y == nextS.y ) )  ) 
////  {
////    c=1;
////  }
////
////  println("gSX " + firstS.x + " gSY " +  firstS.y + " gEX " + firstE.x + " gEY " + firstE.y) ;
////  println("cSX " + nextS.x  + " cSY " +  nextS.y  + " cEX " + nextE.x  + " cEY " + nextE.y ) ;
////  println();
////
////
////  return c;
////} // end connected ?
//
////    
////    ArrayList orderElements (HashMap h) {
////
////      println("Ordering the Elements with gCodeEngine");
////      
////      // setup for ordering 
////      HashMap sortHash = new HashMap(h); 
////      int ARE_CONNECTED; 
////      String[] touchList = new String[1];
////
////	  Entity focusEntity;
////      Entity compareEntity;
////
////	  // set up the blob list -- all non-empty drawings have at least 1 blob
////      int blobCount = 0;
////      blobList = new ArrayList();
////      Blob b   = new Blob();    		// make a blob
////      blobList.add(b);          	   // add it to the list of blobs
////     
////
////      // seed the sort and the first blob
////      String lowXID = getLowX (sortHash);
////      Entity focusEntity = (Entity) sortHash.get(lowXID);  
////      
////      b.addElementToBlob( focusEntity, "CW");             	// add the element to the new blob
////      sortHash.remove(lowXID);                         		// remove the element from the hash
////
////    	println("ordering .... " ) ;
////	println("the current entity order by hash" ) ;
////	
////        while (sortHash.size() > 0) {
////	
////			// get a starting entity
////			// call it foucs entity
////			// pass that entity to a loop that iterates teh hash for touching 	
////			touchList = new String[1];
////	// recusrion here ? 
////								     
////		    touchList = getTouchList ( sortHash, focusEntity ) ;    //  get the list 
////
////          	// need to extract here
////			
////			if (touchList.length > 1) {									// if we have found a touching element
////			
////          		Blob blb = (Blob) blobList.get(blobCount);				// get current blob
////     
////          		println("start adding to blob and removing from hash");
////
////          		for (int r = 1; r < removeKeys.length; r=r+2) {
////          
////		            Entity bAdd = (Entity) sortHash.get(removeKeys[r]); // get entity to add to current blob
////		            blb.addElementToBlob(bAdd, removeKeys[r+1]);		// add the entity to current blob
////					
////		            sortHash.remove(removeKeys[r]);						// remove the entites from the hash
////		
////					// now test each entity on the list for touching elements
////					// this is a recursion  -- figure it out
////					
////		        }
////         
////    
////          } else {
////          //if ( !MATCH_FLAG ) {
////            // when noOne matches we have to create and start a new blob -- it begins with the next element from the sort list.
////            
////           // println("checked whole list, no match");
////           // must be end of blob
////           println("Through sortLIST, no match" ) ;
////           
////           
////           
////           // println("blob count       " + blobCount);
////           // println("size of sortList " + sortList.size());
////           // println("size of goalList " + goalList.size());
////           
////           Blob bE = new Blob();                   // NO MATCH -- make a new Blob object
////           blobList.add(bE);                       // add new blob to teh list
////           blobCount++;
////           
////           println("new blob");
////           
////          // Blob blb = (Blob) blobList.get(blobCount);
////           lowestX = 999999;
////           Iterator getLow = sortHash.values().iterator();
////           while (getLow.hasNext ()) {
////              
////              Entity ee = (Entity) getLow.next();
////              if (ee.isEnabled() ) {  // remove polys from the list
////                
////                  if (ee.getStartLoc().x < lowestX) {
////                    lowestX = ee.getStartLoc().x;
////                    lowXID = ee.getMyKey();
////                    println("lowest X " +lowestX); 
////                  } // end if lowest
////                
////             } // end if enabled
////                
////           } // end while
////           
////           Entity tEE = (Entity) sortHash.get(lowXID);  //  get the first element from the sortList -- this is the start POINT for next blob
////    
////            boolean success = (bE.addElementToBlob(tEE,"CW"));               // add the element to teh new blob as well.
////            if (success) {
////                   goalList.add(tEE);                      // add that element to the goalList
////                   sortHash.remove(lowXID);                     // remove the element from the sort list
////                   println("adjusted lists");
////            }
////            
////          } // end create a blob
////        
////          
////          MATCH_FLAG = false;  // reset for next Loop
////         //s OTHER_FLAG = false;
////          
////
////           
////          //possible to get here with NO MATCH -- now who do we add ?
////          // add FIrst on sortList ie sortList(0) 
////     
////        g++;
////    }  // end for
////            
////      // println("got to end of looping");
////      
////      // println("at end of looping we have "+ sortList.size()+" entities in sort");
////      // println("at end of looping we have "+ goalList.size()+" entities in goal");
////     
////  
////       //  println("matchCOunt " + matchCount);
////       int c = 0 ; 
////       
////         println("sortedList");
////            for (int t = 0; t < goalList.size()-1; t++ ) {
////             Entity p = (Entity) goalList.get(t);
////             println(p.myKey);
////             c++;
////             
////            }
////      println("count " + c) ;
////      
////      for (int bbb= 0 ; bbb < blobList.size(); bbb++ ) { 
////        println("\n\rblob " + bbb);
////        Blob bb = (Blob) blobList.get(bbb);
////        bb.showBlob();
////        bb.isClosed();
////        println();
////      }
////      
////      return goalList;
////      
////    } // end order elements
////
////
////    
////    String getLowX (HashMap lowest) {
////      
////     int lowestX = 99999999;
////     String lowXid = "";
////      
////     Iterator it = lowest.values().iterator();
////           
////            while (it.hasNext ()) {
////              Entity e = (Entity) it.next();
////              
////              if (e.isEnabled() ) {  // remove polys from the list
////                 
////                  if (e.getStartLoc().x < lowestX) {
////                    lowestX = e.getStartLoc().x;
////                    lowXid = e.getMyKey();
////                   
////                  } // end if lowest
////                
////             } // end if enabled
////
////           } // end while -- iterator for hashmap 
////           return lowXid;
////    } // end get lowest
////    
////    
////
////
////	String[] getTouchList (HashMap, sHash, Entity testEntity) 
////		{
////		
////			String[] s = new String[1];
////			s[0] = "touchlist";
////			
////      		Iterator itr = sHash.values().iterator();
////
////	        while (itr.hasNext ()) {
////					
////				compareEntity = (Entity) itr.next();  // yes, get item g from sort list
////				
////				while (compareEntity != testEntity) {
////					
////					ARE_CONNECTED = areConnected( testEntity, compareEntity);
////					if (ARE_CONNECTED == 1 ) { 
////							// two entities touch -- add them to the touchList
////							s = append ( s, testEntity.getMyKey() ); 
////							s = append ( s, "CW");	
////			    	}  // end if 
////			
////				} // end while
////				
////			    
////            }// end while 
////					
////			return s;
////					
////		} // end touchList
////		
////		
////		
////		
////	int areConnected( Entity e1) {
////	      
////	       int c = 0;
////
////	       Entity defaultEntity = new Entity();
////	       // PVector defaultStart = new PVector(99999,999999);
////	       // PVector defaultEnd   = new PVector(99999,999999);
////	       defaultEntity.setStartLoc(PVector(99999,999999));
////	       defaultEntity.setEndLoc(PVector(99999,999999));
////
////	       c = areConnected (e1,defaultEntity);
////
////	       return c;
////
////	  }
////    
////     int areConnected (Entity first, Entity next) {
////            
////            int c = 0; // not 
////            PVector firstS = first.getStartLoc();
////            PVector firstE = first.getEndLoc();
////            
////            PVector nextS = next.getStartLoc();
////            PVector nextE = next.getEndLoc();
////            
////            if (  ( (firstE.x == nextS.x) && ( firstE.y == nextS.y ) )
////               || ( (firstS.x == nextE.x) && ( firstS.y == nextE.y ) ) 
////               || ( (firstE.x == nextE.x) && ( firstE.y == nextE.y ) ) 
////               || ( (firstS.x == nextS.x) && ( firstS.y == nextS.y ) )  ) 
////              {
////                 c=1;       
////              }
////            
////			      println("gSX " + firstS.x + " gSY " +  firstS.y + " gEX " + firstE.x + " gEY " + firstE.y) ;
////                  println("cSX " + nextS.x  + " cSY " +  nextS.y  + " cEX " + nextE.x  + " cEY " + nextE.y ) ;
////                  println();
////			
////
////            return c;
////            
////       } // end connected ?
////            
////    
////    
////  
////  
////  
////  

public void mouseDragged() {

  if (keyPressed && key == CODED ) {  // if there is a key pressed AND its coded
    if (keyCode == 157) {            // AND the key is Apple Key ( command or butterfly )
      
      MOVE_ORIGIN = true;
         
      origin.x = mouseX ;
      origin.y = mouseY ;
      
    }
  }
} // end mouse dragged

public void mouseReleased() {
  if (MOVE_ORIGIN) {
      origin.x = mouseX;
      origin.y = mouseY;
  }
  MOVE_ORIGIN = false;
}


// export nc file here

public void makeGCode() {
  println("\n\r\n\r^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
  println("making gcode");
  // create engine 
  gEngine.clearAll();

  gEngine.addHeader();
  
    print("inside ncFIle -- toolpath direction = ");
    if ( gEngine.gCodeDirection == CW) {
        println("CW"); 
      }else { 
        println("CCW");
      }
 // so we get to here we have a counterClockWise path
 // the entitties arein correct order
 
  // addBody elements
  // iterate all the elements 
  
  for (int blobs = 0; blobs < blobList.size(); blobs++ ) {
    println("inside ncFIle -- extracting blob elements");
     gEngine.addBlank();
     
     Blob thisBlob = (Blob) blobList.get(blobs);
     
     ArrayList blobEntities = thisBlob.getBlobEntities();
       
     
     // do each blob as many times as there are steps.
     for (int step = 0; step < gEngine.getSteps(); step++ ) {
     
             println("gCOde dir = " + gEngine.gCodeDirection);
             println("blob direction = " + thisBlob.getRawDirection());
       
           
            if ( thisBlob.getRawDirection() == -1 ) {
              
              println("OPEN FIGURE we are supposed to go with the RAW direction &&&&&&&&&&&&&&&&&&&&&&");
                 
                 // element go into an aggregate in CCW order 
                 for (int b = 0; b < blobEntities.size();  b ++) {
                   println("OPEN for loop");
                     Entity nextElementInBlob = (Entity) blobEntities.get(b);
                         println("no change :: RAW"); 
                         extractCode(nextElementInBlob, RAW );
                 } // end for 
                 
            } else {
              
            if ( gEngine.gCodeDirection != thisBlob.getRawDirection() ) {
              
               println("GOOFY : we are supposed to SWITCH THE DIRECTION here   $$$$$$$$$$$$$$$$$$$$$$$");
               println(blobEntities.size());
               
               // read array backwards
               for (int b = blobEntities.size()-1; b >= 0;  b--) {
                 Entity nextElementInBlob = (Entity) blobEntities.get(b);
                       println("switching to :: GOOFY");                      
                       extractCode( nextElementInBlob, GOOFY );
   
               } // end for 
             
             } else {  // the directions do not match 
               
                 println("RAW we are supposed to go with the RAW direction &&&&&&&&&&&&&&&&&&&&&&");
                 
                 // element go into an aggregate in CCW order 
                 for (int b = 0; b < blobEntities.size();  b ++) {
                   println("RAW for loop");
                     Entity nextElementInBlob = (Entity) blobEntities.get(b);
                         println("no change :: RAW"); 
                         extractCode(nextElementInBlob, RAW );
                 } // end for 
                 
             } // end else
            } // end open 
             
     } // end for steps

  }
 
  
  // change this to add gCode from the entities

  gEngine.addFooter();

  writeFile(gEngine.getCode());
  
 // createBackPlot();
  
}  // end  make code 


public void extractCode(Entity e, int whichWay) {
  
  
     gEngine.addLabel ("Element: " + e.getType());
     println("Element: " + e.getType());


//    if (e.getType().equals("Poly")) {
//      
//      float oldDirection = gEngine.gCodeDirection;
//      gEngine.setgCodeDirection(e.getDDirection());
//
//      println("gEngine ADD POLY");
//      // extract the poly primatives
//      HashMap h = e.getPrimatives();
//      String[] order = e.getPolyKeys();  // need to be sure !!
//
//      for (int c = 0; c < order.length; c++ ) {
//
//        if (h.containsKey(order[c])) {
//
//          if (c != 0) {
//            Entity primativeFromPoly = (Entity) h.get(order[c]);
//            gEngine.buildCode(primativeFromPoly, POLY);
//          } // end if
//        } // end if
//       
//      } // end for each poly
//      gEngine.setgCodeDirection(gEngine.gCodeDirection);
//    } 
//    else {
  // primative 
  
     // println("gEngine ADD primative");
     
      gEngine.buildCode(e, whichWay);
    
  
  
  }  




//} // end class

// takes original file and splits out tables
// feed in source array of strings, and KEYWORDS for BEGIN and END of needed table

//  http://processing.org/discourse/beta/num_1142535442.html


public String[] getTable(String[] sourceArray, String tabelBegin, String tabelEnd) {

  int firstLine = -1;

  for (int i = 0; i < sourceArray.length; i++) {
    if (sourceArray[i].contains(tabelBegin)) {
      firstLine = i;
    }
  }

  if (firstLine == -1) {
    println("SECTION " + tabelBegin + " NOT FOUND.");
  }
  sourceArray = subset(sourceArray, firstLine + 1);

  int lastLine = -1;

  for (int i = 0; i < sourceArray.length; i++) {
    if (sourceArray[i].contains(tabelEnd)) {
      lastLine = i;
      break;
    }
  }

  if (lastLine == -1) {
    println("SECTION NOT TERMINATED at " + tabelEnd + ".");
  }
  return subset(sourceArray, 0, lastLine-1);
}


public void printArray (String[] s, String n) {

  println("\n\rthere are " + s.length+ " lines in the *" + n + "* section");

  for (int i=0; i < s.length; i++) {
    println(i +"  " +s[i]);
    println();
  }
}

public void writeFile (String[] c) {

  String savePath = selectOutput("Save gCode");  // Opens file chooser
  if (savePath == null) {
    // If a file was not selected
    println("No output file was selected...");
  } 
  else {

    writeFile (savePath, c);
  }
}

public void writeFile(String fileName, String[] content) {

  output = createWriter(fileName);
  for (int i = 0 ; i < content.length; i ++ ) { 
    output.println(content[i]);
  } // end for 
  output.flush();
  output.close();
  println("file " + fileName + " Written");
} // end writeFile

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "cam_04_04" });
  }
}
