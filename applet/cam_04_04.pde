
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

import controlP5.*;
ControlP5 gControl;

import java.io.*;

PrintStream      ps;             // used to redirect the standard output (global variable)

PVector origin;
PFont font;
// The font must be located in the sketch's 
// "data" directory to load successfully

boolean MOVE_ORIGIN = false;

gCodeEngine gEngine; //= new gCodeEngine();

void setup() {
  //redirectStdOut("/data/myLog");
  size(1000, 800);
  
  background(#cccccc);

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

void draw() {


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

 void getDXFTables() {
   
      println( "SHOULD HAVE A FILE" );
      vport = new DXFTable(dxfRawFile, "VPORT", "ENDSEC" ); 
      String[] vportSub = vport.getSubTable( " 12", " 43" );

      dxfEntities = new DXFTable(dxfRawFile, "ENTITIES", "ENDSEC");

      dxfEntities.parseEntities( "  0" );
      
      CREATE_ENTITIES = true;
    }


void drawOrigin() {
  stroke(255, 0, 0);
  strokeWeight(0.5);
  line(-5, 0, 5, 0);
  line(0, -5, 0, 5);
  
} // end origin


void drawGrid() {
  // draw canvas grid here
  stroke(100);
  //println("start grid");
  strokeWeight(0.25);
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

void   drawMask(){
  fill(125,130,130);
  noStroke();
   rect(0,0,width,canvasBorder-1);
   rect(0,0,canvasBorder-1,height);
   rect(width-canvasBorder+1,0,width-canvasBorder+1,height);
   rect(0,height/2-canvasBorder+1,width,height);
   
}

void drawEntities () {

  
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


String[] DXFImportFile(String _fileName) { 

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



String openNewFile () {
  
 

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


void resetFileStatus() {
  hashCount = 0 ;
  entities = new HashMap();
  aggregateList = new ArrayList();
  blobList = new ArrayList();
  CREATE_ENTITIES = true;
  fileName = "";
  FILEOK = false;
}

void startUpSettings() {
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

void aggregate() {
  
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
void redirectStdOut(String filename) throws IOException {
   FileOutputStream fos =
     new FileOutputStream(filename,true);
   BufferedOutputStream bos =
     new BufferedOutputStream(fos, 1024);
   ps =
     new PrintStream(bos, false);

   System.setOut(ps);
 }
