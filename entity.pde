class Entity {

  color renderColor = color(255,0,0);
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
    depth  = -2.0;
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
  
 
  void flip() {
    println(" overwrite");
  }
  
  void setAggIndex(int i ) {
    AGG_INDEX = i ;
  }
  
  int getAggIndex() {
    return AGG_INDEX;
  }
  
  void setLeafDirection(int d ) {
    LEAF_DIRECTION = d ;
  }
  
  int getLeafDirection() {
    return LEAF_DIRECTION;
  }
  
  
  void setAggregated(Boolean state){
    isAggregated = state;
  }
  
  Boolean getAggregated(){
    return isAggregated;
  }
  
  void setColor(color c) {
     renderColor = c; 
  }
  
  color getColor() {
    return renderColor;
  }
  
  int getEntityDirection () {
      println("getDirection parent");
      return -1;
  }
  
  void switchDirection() {
    
      println("parent circle switch direction");    

  } 

  
  
   HashMap getPolyEntities() {
         HashMap hm = new HashMap();
	 return hm;
   }  // end getPOly entities

    void disable() {
	IS_ENABLED = false;
    }

	void enable() {
		IS_ENABLED = true;
	}

	void setState(boolean s) {
		IS_ENABLED = s;
	}

	boolean getState() {
		return IS_ENABLED;
	}

	boolean isEnabled() {
	  return IS_ENABLED;
	}

	void setMyKey(String mK) {
	   myKey = mK; 
	}

	String getMyKey() {
	   return myKey; 
	}

	int getDDirection() {
	     return -1;
	}
 
  int[] getElements () {
    return gElements;
  }


  PVector[] getTargets (int i) {
    return targets;
  }
  
  HashMap getPrimatives() {
    HashMap h = new HashMap();
    return h;
  }

  String[] getPolyKeys () {
    //  println("from parent");
    return source;
  }


  String getType() {
    return type;
  }

  float getFeed() {
    return feed;
  } // get


  void setFeed( float f ) {
    feed = f;
  }

  float getDepth() {
    return depth;
  } // get


  void setDepth( float d ) {
    depth = d;
  }

  float getPlungeRate () {
    return plungeRate;
  }

  float getTravelFeed() {
    return travelFeed;
  }

  void setTravelFeed( float t ) {
    travelFeed = t ;
  }
  
  float getTravelHeight() {
    return travelHeight;
  }

  void setTravelHeight( float t ) {
    travelHeight = t ;
  }

  float getPlunge() {
    return plungeRate;
  }


  void setStartLoc(float x, float y) {
    getStartLoc().x = x;
    getStartLoc().y = y;
  } // end getFIrst NC

  void setEndLoc(float x, float y) {
    endLoc.x = x;
    endLoc.y = y;
  } // end getFIrst NC


  PVector getStartLoc() {
    return ( startLoc );
  }

  PVector getEndLoc() {
    return ( endLoc );
  }

  void showEndPoints () {

    fill(0, 255, 0);
    noStroke();
    ellipse( startLoc.x, -startLoc.y, 1, 1 );

    noFill();
    stroke( 255, 0, 0);
    strokeWeight(0.5);
    ellipse( endLoc.x, -endLoc.y, 1, 1 ); 

    stroke(255);
  }

  String[] getNC (int i, int j) {
    String[] ss = {
      "this is an NC line from PARENT ENTITY", "really"
    };
    return ss;
  } // end addNC


  // print array passed to object
  void printSource () {
    println("ENTITY SUPER printing source");
    printArray(source, "source");  // utility function
  } 

  void printVerticies() {
    println("ENTITY SUPER printing verticies");

    for (int j = 0; j < coord.length; j ++) {
      println(coord[j]);
    }
  }

  // need to have all methods here -- kind of like prototypes for subClasses
  void display() {
    println("superDisplay");
  } // end display
  
  String coordFormat (float f) {
     String sf = nf(f,3,3);
     return sf;
  }
  
  String feedFormat (float f) {
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

