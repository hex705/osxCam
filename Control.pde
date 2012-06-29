
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

