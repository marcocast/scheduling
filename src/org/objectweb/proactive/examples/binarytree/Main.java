/* 
* ################################################################
* 
* ProActive: The Java(TM) library for Parallel, Distributed, 
*            Concurrent computing with Security and Mobility
* 
* Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
* Contact: proactive-support@inria.fr
* 
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or any later version.
*  
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*  
*  Initial developer(s):               The ProActive Team
*                        http://www.inria.fr/oasis/ProActive/contacts.html
*  Contributor(s): 
* 
* ################################################################
*/ 
package org.objectweb.proactive.examples.binarytree;

public class Main {

  public static void main(String[] args) {
    Main theMainActiveObject = null;

    // Creates an active instance of this class
    try {
      theMainActiveObject = (Main)org.objectweb.proactive.ProActive.newActive(Main.class.getName(), null);

    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }

    // Asks it to perform the test
    theMainActiveObject.doStuff();

    return;
  }


  public void doStuff() {
    BinaryTree myTree = null;
    // This is the code for instanciating a passive version of the binary tree
    //        myTree = new BinaryTree ();

    // This is the code for instanciating an active version of the binary tree
    // If you want to test the pasive version of this test program, simply comment out
    // the next line and comment in the line of code above
    //
    // * The first parameter means that we want to get an active instance of class org.objectweb.proactive.examples.binarytree.ActiveBinaryTree
    // * The second parameter ('null') means we instancate this object through its empty (no-arg) constructor
    //  'null' is actually a convenience for 'new Object [0]'
    // * The last parameter 'null' means we want to instanciate this object in the current virtual machine
    try {
      //          Object o = new org.objectweb.proactive.examples.binarytree.ActiveBinaryTree ();
      myTree = (BinaryTree)org.objectweb.proactive.ProActive.newActive(ActiveBinaryTree.class.getName(), null);
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    }

    // Now we insert 4 elements in the tree
    // Note that this code is the same for the passive or active version of the tree

    myTree.put(1, "one");
    myTree.put(2, "two");
    myTree.put(3, "three");
    myTree.put(4, "four");

    // Now we get all these 4 elements out of the tree
    // method get in class BinaryTree returns a future object if
    // myTree is an active object, but as System.out actually calls toString()
    // on the future, the execution of each of the following 4 calls to System.out
    // blocks until the future object is available.

    System.out.println("Value associated to key 3 is " + myTree.get(3));
    System.out.println("Value associated to key 4 is " + myTree.get(4));
    System.out.println("Value associated to key 2 is " + myTree.get(2));
    System.out.println("Value associated to key 1 is " + myTree.get(1));

    System.out.println("Use CTRL+C to stop the program");
  }
}