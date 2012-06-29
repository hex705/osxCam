void addToHashmap(Entity e) {

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

ArrayList orderElements (HashMap h) {

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
     
   void searchForChildren (Entity pEntity) {
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
               
 
   Entity getNextParent() 
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
 
   Entity getSeed() {
       
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

Entity getLowX () {

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


Entity getHighX () {

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

void resetAggregated () {
  
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

