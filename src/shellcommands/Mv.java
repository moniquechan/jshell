package shellcommands;

import test.FileSystemI;
import filesystem.Dir;

/** A class to represent the copy command. */
public class Mv extends Command {

  /**
   * Prints out the commands the user has inputed to the shell.
   * 
   * @param oldPath the directory path that is to be copied
   * @param newPath the destination of the copied paht
   */
  public void executeCommand(String oldPath, String newPath, FileSystemI root, Dir currDir) {
    Cp tempCp = new Cp();
    boolean copied;
    // run copy command
    copied = tempCp.executeCommand(oldPath, newPath, root, currDir);
    // delete oldPath if copy was successful
    if (copied) {
      root.delete(oldPath, currDir);
    }
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "Move item OLDPATH to NEWPATH. Both OLDPATH  and  NEWPATH  may  be  relative  to  the "
        + " current  directory  or  may  be  full  paths.   If  NEWPATH  is  a directory, move the"
        + " the directory or file in OLDPATH to NEWPATH .";
  }

}
