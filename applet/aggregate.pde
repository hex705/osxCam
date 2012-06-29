/*


based on concordance shiffman :

http://www.shiffman.net/2006/01/31/mmm-binary-trees/


see this for great explanation of trees

http://math.hws.edu/javanotes/c9/s4.html

*/


class Aggregate {
  
	 Node root;
	
	 Aggregate() {
		root = null;
	}
	
// methods

	int insert (Entity e)
	{	
      	          int c = 0;
		  Node newNode = new Node(e);
		  if (root == null) 
		  { 
                       root = newNode;
	               c = 2;
		  } else {
	               c = root.insertNode( newNode );
	          }
            return c;
	}

        // search tree for a string -- in this case an entity hash-key
//        boolean contains ( Entity entityToFind ) 
//        {
//          if (root == null) 
//          {
//            return false;
//          } else {
//            return root.branchesContains ( root , entityToFind ) ;
//          }  
//          
//        } // end contains
        
         // search tree for a string -- in this case an entity hash-key
        int traverseInsert ( Entity parentToFind, Entity childToInsert ) 
        {
          if (root == null) 
          {
            return 99;
          } else {
            return root.traverseInsert ( root , parentToFind, childToInsert ) ;
          }  
          
        } // end contains

	// Start the recursive traversing of the tree
	public void print(){
	   if (root != null)
	         root.printNodes();
	   }

       // Start the recursive traversing of the tree
	public ArrayList getNodes(){
           ArrayList a = new ArrayList();
	   if (root != null) {
                //a.add(root.nodeEntity);
	        a = root.getNodes( a );
	   }  
          return a;
        }

	
} // end aggregate
	
	
      class Node {
		
	    Entity nodeEntity;
            String entityKey;
	    Node left;
            Node right;
            int count;
		
	    Node ( Entity _e ) {
		   nodeEntity = _e;
                   entityKey = _e.getMyKey();
		   left  = null;
		   right = null;
	    } // end node constructor 
		
		
          // node methods 
          		
		// Inserts a new node as a descendant of this node
		// If both spots are full, keep on going. . .
	int insertNode(Node newNode)  {
  
	            // we are in a node (PARENT) it has an entity
                    // println("inside NODE");
		     //println(nodeEntity.getMyKey()); // prints the parent key 
		    // println(newNode.nodeEntity.getMyKey()); // prints the new node key

	         int compareValue = nodesConnected (nodeEntity, newNode.nodeEntity );  // parent, child 

		   // if new element END is touching the start of PARENT
		   if ( compareValue  < 0 )
		   {
		      // if the spot is empty (null) insert here
		      // otherwise keep going!
		      if (left == null) left = newNode;
		      else left.insertNode(newNode);
                     
		   }
		   // if new word is alphabeticall after
		   else if (compareValue > 0)
		   {
		      // if the spot is empty (null) insert here
		      // otherwise keep going!
		      if (right == null) right = newNode;
		      else right.insertNode(newNode);

		   // if new word is the same
		   } else {
		      // We'd do something here if we wanted to when the Strings are equal
		      // For example, increment a counter
                      
		   }

                    return compareValue;
                    
		}  // end insert node 


          // this is node insert -- NOT tree insert !!!
          
         int traverseInsert (Node thisNode, Entity entityToFind, Entity childToInsert ) 
         {
           
           int returnCode= -99 ;

           if ( thisNode == null ) {
             return returnCode;
           }
           
           // check to see if disabled -- break here if it is
           if ( ! thisNode.nodeEntity.isEnabled() ) {
             println("Entity disabled");
             return -999;
           }
            
           
           if ( thisNode.nodeEntity == entityToFind ) 
           {
             // found the node that will act as parent
               Node newNode = new Node(childToInsert);
               returnCode = thisNode.insertNode( newNode );
       
             return returnCode;  // insert outcome
             
           }   // end if
           
           // see if it is left -- if this is true
           // if it is not -- tehn check right side -- if it is then quit
          
           int isLeft = traverseInsert(thisNode.left, entityToFind, childToInsert );
         
           // ugly but still only search half the tree - some of the time
           if ( isLeft == 0 || isLeft == -1 || isLeft == 1 ) {
             // not on left -- try right
              
               return isLeft;
            } else {
                // println("searching right...");
              int isRight = traverseInsert(thisNode.right, entityToFind, childToInsert );
              if (isRight == 0 || isRight == -1 || isRight == 1 ) 
              {
                  return isRight;
              } else {
                  println("PARENT ENTITY NOT FOUND");
                  return isRight;
              }
               
            }//end else 
        
         }
         
         
//         boolean branchesContains (Node thisNode, Entity entityToFind ) 
//         {
//           
//           if ( thisNode == null ) {
//             return false;
//           }
//           
//           if ( thisNode.nodeEntity == entityToFind ) 
//           {
//             // found the node that will act as parent
//             println ("branches parent found  ");
//             println ("Attempting to insert CHILD");
//             return true;
//           }   // end if
//           
//           // see if it is left -- if this is true
//           // if it is not -- tehn check right side -- if it is then quit
//           println("searching left ...");
//           boolean isLeft = branchesContains (thisNode.left, entityToFind );
//           
//           // ugly but still only searc half the tree
//           
//           if ( isLeft ) {
//             // not on left -- try right
//               println("...................... ** PARENT found on left ** ");
//               return isLeft;
//            } else {
//              println("searching right...");
//              boolean isRight = branchesContains (thisNode.right, entityToFind );
//              if (isRight) 
//              {
//                  println("...................... ** PARENT found on right **");
//                  return isRight;
//              } else {
//                  println("PARENT ENTITY NOT FOUND");
//                  return isRight;
//              }
//               
//            }//end else 
//        
//         }
         

        void printNodes()  {
          
                   if (left != null) left.printNodes();
                   
                   println( "node key " + nodeEntity.getMyKey() ); //+ " left " + left.nodeEntity.getMyKey() + "right " + right.nodeEntity.getMyKey());
                   
                   if (right != null) right.printNodes();
                }
                
         public ArrayList getNodes(ArrayList a){

                   if (left != null) left.getNodes( a );
                   
                   println( "node key " + nodeEntity.getMyKey() ); //+ " left " + left.nodeEntity.getMyKey() + "right " + right.nodeEntity.getMyKey());
                   a.add(nodeEntity);
                   
              
                   if (right != null) right.getNodes( a );
                   return (a);
                }
      
      
      int nodesConnected (Entity parent, Entity child) {
            
                int c = 0; // not connected -- do nothing
                float tolerance = 0.0100;

                
                PVector childStart  = child.getStartLoc();
                PVector childEnd    = child.getEndLoc();
                PVector parentStart = parent.getStartLoc();
                PVector parentEnd   = parent.getEndLoc();
                
              println( "parent " + parent.getMyKey() + " child " + child.getMyKey());
              
                
                // parent OK -- child is reversed
                if ( 
                      (   ( abs( parentStart.x - childStart.x) < tolerance )   &&  ( abs( parentStart.y - childStart.y ) < tolerance )   ) 
                
                    || 
                    
                      (  (  abs( parentEnd.x - childEnd.x   ) < tolerance  )   &&   ( abs( parentEnd.y - childEnd.y   ) < tolerance   )   ) 
                      
                    )  
                    
                   {
                     // child is "facing wrong way"  flip the child
                     println("flipping child");
                     child.flip();
                     // have to write flip for  line, arc, circle
                     // point does nothing -- it will never be here.
                     
                     childStart  = child.getStartLoc();
                     childEnd    = child.getEndLoc();
                     parentStart = parent.getStartLoc();
                     parentEnd   = parent.getEndLoc();
                       
                   }
       
                
                  if (  ( abs( parentStart.x - childEnd.x  ) < tolerance )   &&  ( abs( parentStart.y - childEnd.y   ) < tolerance )   ) 
                       {
                           c=-1;       
                       }
                  
                        
                
                  if (  ( abs( parentEnd.x - childStart.x ) < tolerance )   &&  ( abs( parentEnd.y - childStart.y ) < tolerance )     ) 
            
                        {
                           c=1;       
                        }
                
                return c;
                
          } // end connected ?
		
		
    } // end NODE class


	

