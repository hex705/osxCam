
// note a drill == point -- but we have a namespace issue (processing has a point already)

class GEllipse extends Entity {

  float longWidth, shortWidth, angle, SA, EA;
  PVector apex;

  GEllipse(String[] s_, float[] v_) {
    super(s_, v_);
    SA =  TWO_PI - v_[R2] ;  // start angle
    EA =  TWO_PI - v_[R3] ;  // end angle
    println("angle BEFORE is = " + (EA-SA) );
    if ( SA < EA) {
      EA -= TWO_PI;
    }
    println("angle AFTERis = " + (EA-SA) );

    buildEllipse();

    println("Ellipse constructed");
    type = "Ellipse";
  } // end constructor

  // methods 
  void buildEllipse() {
    println("print verticies");
    printVerticies();

    apex = new PVector();

    apex.x = coord[X1] + coord[X2];
    apex.y = coord[Y1] + coord[Y2];

    float tDist = dist(coord[X1], coord[Y1], apex.x, apex.y);
    println("distance from center to apex = " + tDist );
    longWidth = tDist ;  // radius
    shortWidth = longWidth * coord[R1] ;

    println("long width " + longWidth);
    println("short width " + shortWidth);
    // there needs to be an angle here.


    pushMatrix();
    translate(coord[X1], -coord[Y1]);
    angle = atan2( -coord[Y2], coord[X2] );

    //if (angle > PI) { angle -=PI;} // keeps angle in upper half and positive

    popMatrix();


    print ("angle " + degrees (angle));

    println("print verticies from DISPLAY");
    printVerticies();
  } // end build ellipse


  void display() {


    ellipseMode(CENTER);
    ellipseMode(RADIUS);
    stroke(renderColor);
    noFill();
    //stroke(255, 0, 0);
    ellipse(coord[X1], -coord[Y1], 1, 1);
    ellipse(apex.x, -apex.y, 1, 1);

    pushMatrix();
    //translate(canvasBorder, (height/2)-canvasBorder);
    translate(coord[X1], -coord[Y1]);
    rotate(-angle);
    stroke(0, 255, 0);  
    //ellipse ( 0, 0, longWidth, shortWidth); 
    stroke(255, 0, 0);
    //  arc( 0, 0, longWidth, shortWidth, coord[R2] - coord[ANG], coord[R3] - coord[ANG]);
    arc( 0, 0, longWidth, shortWidth, SA -angle, EA - angle); 
    popMatrix();
  } // end display
  
} // end CLASS ellipse

