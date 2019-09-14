package shellcommands;

import test.FileSystemI;
import filesystem.Dir;

/** A class to represent the Popd shell command. */
public class Popd extends Command {

  /**pops directory from the top of the directory stack and changes to that.
   * directory
   * 
   * @param currentDir A pointer to the current working directory of the user
   * @param root       The root of the file system
   * @return returns the new current directory after the pop command is executed
   */
  public Dir executeCommand(Dir currentDir, FileSystemI root, String[] args) {
    ChangeDirectory tempCd = new ChangeDirectory();
    String newDirectory;
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      args = get_arguments(args);
    }
    // pop dir from stack
    try {
      newDirectory = root.popDir();
      return tempCd.executeCommand(root, currentDir, new String[] { newDirectory });

    } catch (Exception e) {
      System.err.println("Error: No directories remaining in Stack. "
          + "Use \"pushd [DIR]\" to add more directories.");
    }

    return null;
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "popd\nThe popd command removes the top most directory from the directory stack and"
        + "makes it the current working directory.";
  }

}
