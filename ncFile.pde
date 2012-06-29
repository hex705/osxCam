// export nc file here

void makeGCode() {
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


void extractCode(Entity e, int whichWay) {
  
  
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

