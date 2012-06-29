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

   HashMap getPolyEntities() {
	 return polyEntities;
   }  // end getPOly entities

   int getDDirection() {
     return drawnDirection;
   }

  String[] getPolyKeys () {
    return polyKeys;
  }


  void display() {

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
          
          strokeWeight(0.25);
          pE.display();

          // debug -- show lines for angles
          //          strokeWeight(0.25);
          //          stroke(0,200,0);
          //          line (ctrX,-ctrY, pE.getEndLoc().x, -pE.getEndLoc().y);
          
        }
      } // end if
    } // end for 
  
  } // end display
  
  void setDepth(float d) {

    for (int c = 0; c < polyKeys.length; c++ ) {

      if (polyEntities.containsKey(polyKeys[c])) {

        if (c != 0) {

          Entity pE = (Entity) polyEntities.get(polyKeys[c]);
              pE.setDepth(d);
        }
      }
    }

    
  } // end setDepth



  String[] orderPrimatives () {
    
    String[] theKeys = new String[howMany + 1 ];
    for (int c = 0; c < theKeys.length; c++ ) {
      String orderKey = ("polyKey" + c);
      theKeys[c] = orderKey;
      // println("the key order " + orderKey);
    }

    return theKeys;
  }

  int getDrawnDirection() {

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


    int buildPrimativesforPoly() {

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

