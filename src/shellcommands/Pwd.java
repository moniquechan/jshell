package shellcommands;

import filesystem.Dir;
import test.FileSystemI;

public class Pwd extends Command {

  /**Prints current working directory.
   * 
   * @param workingDir A pointer to the current working directory.
   */
  public void executeCommand(String[] args, Dir workingDir, FileSystemI root) {
    String Filename = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      Filename = args[args.length-1];
      args = get_arguments(args);
    }  
    out.printString(workingDir.getDirPath(), workingDir, Filename, root);
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "pwd\nPrint the current working directory (including the whole path).";
  }
}