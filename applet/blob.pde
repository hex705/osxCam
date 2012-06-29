
class Blob {
  
  // blob has a raw direction 
 ArrayList blobElements = new ArrayList();    // arrayList of Entities  - can add at index
 boolean  IS_CLOSED = false;                  // nature of blob
  
 color closedColor = color( 0, 127, 127 ); // CYAN
 color openColor   = color( 127, 0, 127 ); // purple

 int rawDirection = -1;
 float minX, maxX, minY, maxY, ctrX, ctrY;
  

   Blob() {
    println("NEW BLOB CONSTRUCTED ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
  }
  
 
    
  void populateBlob( ArrayList aL ) {
        blobElements = aL;
   }
   
   void blobAdd ( Entity toAdd  ) {
        blobElements.add(toAdd); 
   }
    
  ArrayList getBlobEntities() {
    return blobElements;
  }
  
  
  void showBlob() {
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
  
  int getBlobLength( ) {
    return blobElements.size();
  }
  
 
  void isClosed() {
 
    
    int blobSize = blobElements.size();
    int matchCount = 0;
    float tolerance = 0.01;
    
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
  
  boolean getIsClosed () {
     return IS_CLOSED; 
  }
  
  
   int getRawDirection(){
      return rawDirection; 
   }
   
   void setRawDirection( int rD ) {
      rawDirection = rD ;
   }
  
   int calculateRawDirection() {
     
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
