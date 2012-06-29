
void createBackPlot() {
  
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
  
  color plotColor = color (0);
  color travel   = color (100);
  color arc02    = color (255,0,0);
  color arc03    = color (0,0,255);
  color lin01    = color (255,0,255);
  
  boolean bpLINE = false;
  boolean bpARC = false;
  
  strokeWeight(0.5);
    
  
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



float splitWord (String s, char t  ) {
   
    String[] parts = split(s,t);   
    return float( parts[1] );
}


void backPlot() {
  //println("aa");
  //println("size " + backPlotElements.size());
  
  for (int i = 0; i < backPlotElements.size(); i ++ ) {
    
    println("inside");
    BPElement bp = (BPElement) backPlotElements.get(i);
    bp.display();
    
  }
}
  
