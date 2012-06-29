void checkForPoly(DXFTable dT, String s) 
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

void createEntities( DXFTable dT ) 
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





float[] parseEntity (String[] s, int thisSize ) 
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
      tempE[X1] = float(s[entityElementCount + 1]);
      if (VERBOSE) { 
        println("found a 10: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 20")) {
      tempE[Y1] = float(s[entityElementCount + 1]);

      if (VERBOSE) { 
        println("found a 20: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 30")) {
      tempE[Z1] = float(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found a 30 : " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 11")) { 
      tempE[X2] = float(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found an 11: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 21")) {
      tempE[Y2] = float(s[entityElementCount + 1]);

      if (VERBOSE) {  
        println("found a 21: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 31")) {
      tempE[Z2] = float(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 31: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 40")) { 
      tempE[R1] = float(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 40: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 41")) { 
      tempE[R2] = float(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 41: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 50")) {
      tempE[AE] = float(s[entityElementCount + 1]);  // angle end
      if (VERBOSE) {  
        println("found a 50: " + s[entityElementCount + 1]);
      }
    }

    if (test.equals(" 51")) {
      tempE[AS] = float(s[entityElementCount + 1]);  // angle start
      if (VERBOSE) {  
        println("found a 51: " + s[entityElementCount + 1]);
      }
    }

    if ( (test.equals(" 42"))   && (thisSize == PRIMSIZE)  ) {   //  && (thisSize == PRIMSIZE)

      tempE[R3] = float(s[entityElementCount + 1]);
      if (VERBOSE) {  
        println("found a 42 && PRIMSIZE: " + s[entityElementCount + 1]);
      }
    }

    if ( (test.equals(" 42")) && (thisSize == POLYSIZE) ) {
      tempE[BG] = float(s[entityElementCount + 1]);
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

DXFTable expandLWPoly (String[] s) {

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


void explodePolys() {
  
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





