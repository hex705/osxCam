// linear back plot

// add some comments to the file 

class BPElement {

  color plotColor;

  PVector first;
  PVector last;
  PVector data;

  BPElement (PVector _f, PVector _l, PVector _d, color _c) {
    println( "in parent element constructor");
    first.set(_f);
    println("a");
    last.set(_l);
    println("b");
    data.set(_d);
    
    println("inside here");
    plotColor = _c;
    
  }  // end constructor

  void display() {
    println("overwrite me");
  }
  
}// end class BPElement





  
class BPLine extends BPElement {
  
   BPLine (PVector _s, PVector _e, PVector _d, color _c) {
     super(_s, _e, _d, _c);
     println("line constructed");
   } // end constructor
   
   
   void display () {
     
       stroke(plotColor);
       line ( first.x, first.y, last.x,last.y ) ;
       
   } // end display
  
}  // end BPLine class


class BPArc extends BPElement {
  
  BPArc (PVector _s, PVector _e, PVector _d, color _c) {
     super (_s, _e, _d, _c);
   } // end constructor
  
  void display () {
     
       stroke(plotColor);
       line ( first.x, first.y, last.x, last.y ) ;
       println("bp line");
       
   } // end display
  
  
}  // end BPArc

