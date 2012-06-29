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
  
 

