package shellcommands;

import filesystem.Dir;
import filesystem.FileSystem;
import test.FileSystemI;
import driver.Validation;
import java.util.Arrays;

/** A class to represent the "mkdir" shell command. */
public class MakeDirectory extends Command {

  /**Make a directory at the given absolute/relative path.
   * 
   * @param root     A pointer to the root of the file system
   * @param currDir  The current working directory of the user
   * @param dirPaths The paths of the new directories that will be created
   */
  public void executeCommand(FileSystemI root, Dir currDir, String[] dirPaths) {
    Output out = new Output();
    out.setPlaceToPrint(checker(dirPaths));
    if(out.getPlaceToPrint() != 0) {
      dirPaths = get_arguments(dirPaths);
    }

    // If we aren't given any directories, output error message and exit
    if (dirPaths.length == 0) {
      System.err.println("Error: No arguments given. Try \"man mkdir\" for help.");
      return;
    }

    // Go through every directory in dirPaths
    for (String path : dirPaths) {

      // Default case if path points to current directory
      // Store the parent node of the directory we want to create
      String parentPath = currDir.getDirPath();
      String directoryToCreate = path;

      Dir parentDirectory = null; // The parent node to which this directory will be added

      // If path contains any "/" characters, we do a special case
      if (path.contains("/")) {
        // Extract name of new directory to create, as well as its parent directory
        String[] temp = path.split("/");
        directoryToCreate = temp[temp.length - 1];
        parentPath = String.join("/", Arrays.copyOfRange(temp, 0, temp.length - 1));

        // Special case for when we are creating a directory at the root via absolute
        // path
        if (parentPath.equals("")) {
          // If we get empty string, it means we want to make a directory at the root
          parentPath = "/"; 
        }
      }

      // Before continuing, check if directoryToCreate is a valid directory name
      if (!Validation.isValidName(directoryToCreate)) {
        System.err.println("Error: The directory with name \"" + directoryToCreate
            + "\" is not valid.");
      } else {
        // If the path we're given is relative, call appropriate helper
        if (FileSystem.isAbsolutePath(parentPath)) {
          parentDirectory = root.getDirectoryByAbsolutePath(parentPath);
        } else {
          parentDirectory = root.getDirectoryByRelativePath(currDir, parentPath);
        }

        // If parent directory is null, it mean the path given is invalid
        if (parentDirectory == null) {
          System.err.println("Error: Invalid parent path given for \"" + parentPath + "\".");
        } else {
          // If the new directory we're going to create already exists, throw error
          if (parentDirectory.getSubDirectoryByName(directoryToCreate) != null) {
            // If this directory already exists, don't create it
            System.err.println("Error: Directory with name \"" + directoryToCreate
                + "\" already" + " exists.");
          }
          // If a file with the same name as the directory to create exists, don't create
          // it
          else if (parentDirectory.getFileByName(directoryToCreate) != null) {
            System.err.println("Error: A File with name \"" + directoryToCreate + "\" already "
                + "exists. Cannot create a directory here with this name.");
          }

          else {
            parentDirectory.addNewDirectory(new Dir(directoryToCreate,
                parentDirectory.getDirPath()));
          }
        }
      }
    }
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "mkdir DIR\nCreate directories, each of which may be relative to the current"
        + "directory or may be a full path.";
  }
}
