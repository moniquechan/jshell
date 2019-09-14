package shellcommands;

import test.FileSystemI;
import filesystem.Dir;

/** A class to represent the Echo Unix command. */
public class Echo extends Command {

  /**
   * returns the contents to append or overwrite to a file or to be displayed to a
   * shell contents is a string of characters surrounded by double quotation
   * marks. This creates a new file if OUTFILE does not exists and erases the old
   * contents if OUTFILE already exists.
   * 
   * @param contents the argument given by user that is to be displayed
   * @param currentDir the current directory
   * @param root the root of the file system
   */

  public void executeCommand(String[] contents, Dir currentDir, FileSystemI root) {
    // get the text the user wants to display/input in a file
    // if not surronded in double quotes, returns an error
    String input = "";
    boolean error = true;
    // Holds the index with the ending quote
    int holder = 0;
    int charcheck = 0;
    

    for (int i = contents.length - 1; i >= 0; i--) {
      if (contents[i].endsWith("\"")) {
        error = false;
        holder = i;
        charcheck = contents[i].lastIndexOf('\"');
        break;
      }
    }

    if (error || contents[0].charAt(0) != '\"' || charcheck == 0) {
      if(contents.length == 0) {
        System.out.println();
        return;
      }
      System.err.println("A valid string was not inputted.");
      return;
    }
    for (int i = 0; i < holder + 1; i++) {
      if (i == 0) {
        input = input + contents[i];
      } else {
        input = input + " " + contents[i];
      }
    }
    // System.out.println(input + " \n holder:" +holder);

    // check text to be sure it contains no extra quotations

    String string = input.substring(1, input.length() - 1);
    // System.out.println(string);
    if (string.indexOf('\"') > -1) {
      // System.out.println(string.indexOf('\"'));
      System.err.println("A valid string was not inputted.");
      return;
    }

    if (contents.length == holder + 1) {
      // print the string
      System.out.println(input.substring(1,input.length()-1));
    }

    else if (contents.length == holder + 3) {
      // check if overwrite
      if (contents[holder + 1].equals(">")) {
        Overwrite.executeCommand(currentDir, contents[holder + 2],
        input.substring(1,input.length()-1), root);
      }
      // append case
      else if (contents[holder + 1].equals(">>")) {
        Append.executeCommand(currentDir, contents[holder + 2],
        input.substring(1,input.length()-1), root);
      }
    }

    // Argument entered has problems
    else {
      System.err.println("Error with arguments inputted.");
    }
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "echo STRING [> OUTFILE] [>> OUTFILE]\nIf OUTFILE is not provided, print STRING on"
        + " the shell. Otherwise, if > is inbetween the STRING and the OUTFILE, overwrite OUTFILE"
        + " with STRING.  Otherwise, if >> is inbetween the STRING and the OUTFILE, append the"
        + " STRING to the OUTFILE";
  }
}
