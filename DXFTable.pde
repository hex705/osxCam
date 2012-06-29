class DXFTable {

  String[] tableSource;
  String[][] tableEntities;
  Boolean ENTITIES_SET = false;

  int tableLength;
  String oldType = "";



  // constructors
  DXFTable (String[] sourceArray) {
    // for polyLine (i think) 
    tableSource = sourceArray;
  }

  DXFTable (String[] sourceArray, String tableBegin, String tableEnd) {

    tableSource = getTable(sourceArray, tableBegin, tableEnd);
    tableLength = tableSource.length;
  } // end constructor 


  // function for the Vport -- can handle this better later
  String[] getSubTable (String tableBegin, String tableEnd) { 
    return getTable ( tableSource, tableBegin, tableEnd);
  }


  String[][] getTableEntities() {
    return tableEntities;
  }


  int getTableEntitiesLength() {
    return tableEntities.length;
  }

  void disableElement (int e ) {
    oldType = tableEntities[e][1];
    tableEntities[e][1] = "disAbled";
  }


  void enableElement (int e ) {
    tableEntities[e][1] = oldType;
  }

  void expandTable (String[][] newEntities) {

    // tableEntities = splice (tableEntities, newEntities, 0); // tableEntities.length-1
  }

  void parseEntities ( String target  ) {
    println("beginning the PARSE ENTITIES ROUTINE in DXFTable"); 
    int numEntities = 0;

    // find all the entity start points
    for (int i = 0; i < tableSource.length; i++) {
      if (tableSource[i].contains( target )) {  // marks the start of an entity
        tableSource[i] = "startEntity";
        numEntities ++;
      } // end if
    } // end for

    String joindxf;
    joindxf = join(tableSource, "~");
    println("joined up dxf:\n\r " + joindxf);

    // split at ~ -- this seems excessive
    tildeEntity = split(joindxf, "startEntity");
    printArray(tildeEntity, "tildeEntity FROM dxfTable.parseEntities"); 
    println();

    tableEntities = new String[numEntities + 1][]; 

    for (int i = 0; i <= numEntities; i++) {

      tableEntities[i] = split(tildeEntity[i], "~");
    } // end for

    // } // end read

    ENTITIES_SET = true;
  } // end parseForEntities

  void printEntities () {

    if ( ENTITIES_SET ) {
      for (int i=0; i < tableEntities.length; i++) {
        println(tableEntities[i]);
      }
    } 
    else {
      println("cannot print ENTITIES not YET created");
    }
  }

  void printSource () {
    printSource ( "");
  }

  void printSource (String id) {

    println("outputting table " + id );
    for (int i = 0; i < tableLength; i ++ ) {
      print ( tableSource[i] + " " );
    }
  } // end print source
  

} // end class 
// ****  

