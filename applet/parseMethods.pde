// takes original file and splits out tables
// feed in source array of strings, and KEYWORDS for BEGIN and END of needed table

//  http://processing.org/discourse/beta/num_1142535442.html


String[] getTable(String[] sourceArray, String tabelBegin, String tabelEnd) {

  int firstLine = -1;

  for (int i = 0; i < sourceArray.length; i++) {
    if (sourceArray[i].contains(tabelBegin)) {
      firstLine = i;
    }
  }

  if (firstLine == -1) {
    println("SECTION " + tabelBegin + " NOT FOUND.");
  }
  sourceArray = subset(sourceArray, firstLine + 1);

  int lastLine = -1;

  for (int i = 0; i < sourceArray.length; i++) {
    if (sourceArray[i].contains(tabelEnd)) {
      lastLine = i;
      break;
    }
  }

  if (lastLine == -1) {
    println("SECTION NOT TERMINATED at " + tabelEnd + ".");
  }
  return subset(sourceArray, 0, lastLine-1);
}


