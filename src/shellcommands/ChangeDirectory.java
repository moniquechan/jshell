package shellcommands;

import filesystem.Dir;
import filesystem.FileSystem;
import test.FileSystemI;

/** A class to represent the "cd" shell command. */
public class ChangeDirectory extends Command {

  /**Go to a new directory, given its path.
   * 
   * @param root          A pointer to the root of the file system
   * @param currDirectory A node of the current directory
   * @param args          The path of the new directory to navigate to
   * @return A Dir node with the path of the directory specified. If no such
   *         directory exists, null is returned.
   */
  public Dir executeCommand(FileSystemI root, Dir currDirectory, String[] args) {

    // If we aren't given any directories, output error message and exit
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      args = get_arguments(args);
    }
    if (args.length == 0) {
      System.err.println("Error: No arguments given. Try \"man cd\" for help.");
      return null;
    }
    if(args.length > 1) {
      System.err.println("Error: Too many arguments.");
      return null;
    }

    String path = args[0];
    
    // Special cases if path is ".." or "." characters on its own
    if(path.equals("..")) {
      return currDirectory.getParent();
    } else if(path.equals(".")) {
      return currDirectory;
    }
    
    // If path does not contain any "/" characters, then we only need to look at
    // children of current directory
    if (!(path.contains("/"))) {
      return currDirectory.getSubDirectoryByName(path);
    }

    // General case: we have an absolute/relative path
    if (FileSystem.isAbsolutePath(path)) {
      return root.getDirectoryByAbsolutePath(path);
    } else {
      return root.getDirectoryByRelativePath(currDirectory, path);
    }

  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "cd DIR\nChange directory to DIR, which may be relative to the current directory or"
        + "may be a full path. As with Unix, .. means a parent directory and a . means the current"
        + "directory. The directory must be /, the forward slash. The foot of the file system is a"
        + "single slash: /. ";
  }
}
