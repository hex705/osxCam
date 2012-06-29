void mouseDragged() {

  if (keyPressed && key == CODED ) {  // if there is a key pressed AND its coded
    if (keyCode == 157) {            // AND the key is Apple Key ( command or butterfly )
      
      MOVE_ORIGIN = true;
         
      origin.x = mouseX ;
      origin.y = mouseY ;
      
    }
  }
} // end mouse dragged

void mouseReleased() {
  if (MOVE_ORIGIN) {
      origin.x = mouseX;
      origin.y = mouseY;
  }
  MOVE_ORIGIN = false;
}


