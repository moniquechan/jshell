// **********************************************************
// Assignment2:
// Student1: Bilal Ahmed
// UTORID user_name: ahmedbi9
// UT Student #: 1004254843
// Author: Bilal Ahmed
//
// Student2: Henry Lu
// UTORID user_name: lukuante
// UT Student #: 1004144455
// Author: Lu Kuan-Te
//
// Student3: Monique Chan
// UTORID user_name: chanmon3
// UT Student #: 1004352906
// Author: Monique Chan
//
// Student4: John Derick Amalraj
// UTORID user_name: amalraj4  
// UT Student #: 1005034516
// Author: John Derick Amalraj
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// I have also read the plagiarism section in the course info
// sheet of CSC B07 and understand the consequences.
// *********************************************************

package driver;

import filesystem.Dir;
import filesystem.FileSystem;
import shellcommands.History;
import java.io.IOException;

/**
 * A class to represent a Unix shell. 
 * */
public class JShell {

  //Initialize History tracker
  static History tracker = new History();

  public static void main(String[] args) throws IOException {

    // Instantiate our File System
    FileSystem root = FileSystem.createFileSystem();
    // The directory that the user is in starts at the root
    Dir currentDirectory = root.getRoot();
    
    // Object to handle all user input
    Input jShellIn = new Input();
    
    // Continuously ask the user for input in the console
    while (true) {
      
      // (1) Ask user for input
      System.out.print(currentDirectory.getDirPath() + "> ");
      
      // Parse user input
      String [] parsedUserInput = jShellIn.parseUserInput(jShellIn.getUserInput(tracker));
      
      // Send user input to appropriate helper class, and retrieve current directory
      currentDirectory = jShellIn.sendUserInput(parsedUserInput, currentDirectory, root, tracker);
      
    }
  }

  /**Override the toString() method for object
   * @return A string that describes the class
   */
  public String toString(){
    return "This is the JShell class responsible for taking user's inputs";
  }
}
