void printArray (String[] s, String n) {

  println("\n\rthere are " + s.length+ " lines in the *" + n + "* section");

  for (int i=0; i < s.length; i++) {
    println(i +"  " +s[i]);
    println();
  }
}

void writeFile (String[] c) {

  String savePath = selectOutput("Save gCode");  // Opens file chooser
  if (savePath == null) {
    // If a file was not selected
    println("No output file was selected...");
  } 
  else {

    writeFile (savePath, c);
  }
}

void writeFile(String fileName, String[] content) {

  output = createWriter(fileName);
  for (int i = 0 ; i < content.length; i ++ ) { 
    output.println(content[i]);
  } // end for 
  output.flush();
  output.close();
  println("file " + fileName + " Written");
} // end writeFile

