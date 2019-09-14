package shellcommands;

import test.FileSystemI;
import filesystem.Dir;

/** A class to represent the Pushd shell command. */
public class Pushd extends Command {

  /**pushes current directory onto directory stack and changes new current.
   * directory to the given Dir
   * 
   * @param args       The user inputed arguments 
   * @param currentDir The current working directory
   * @param root       The root of the file system
   * @return A pointer to the new directory
   */
  public Dir executeCommand(String[] args, Dir currentDir, FileSystemI root) {
    ChangeDirectory tempCd = new ChangeDirectory();
    if (args.length <= 1) {
      System.err.println("Error: No arguments given.");
      return null;
    }
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      args = get_arguments(args);
    }

    Dir result = tempCd.executeCommand(root, currentDir, new String[] { args[1] });

    if (result != null) {
      // push dir into stack
      root.pushDir(currentDir.getDirPath());
    }
    return result;
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "pushd\nSaves the current working directory by pushing onto directory stack and then"
        + "changes the new current working directory to DIR.";
  }
}
