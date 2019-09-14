package shellcommands;

import filesystem.File;
import test.FileSystemI;
import filesystem.FileSystem;
import driver.Validation;
import filesystem.Dir;

/** This class implements the append >> functionality for echo class. */
public class Append extends Command {

  /**
   * Append the contents of an existing file with a string or, create a new file
   * containing the string if the file does not already exist.
   * 
   * @param theDir   The directory that contains the file of interest
   * @param fileName  Name of the file that needs to be appended to/created
   * @param input String to append into the file
   * @param root           Root of the FileSystem for traversal by paths
   */
  public static void executeCommand(Dir theDir, String fileName, String input, FileSystemI root) {

    // Firstly, determine the file to be added and the directory the file is in

    // For if the file given was an absolute path
    File toBeAdded = null;
    if (FileSystem.isAbsolutePath(fileName)) {
      toBeAdded = root.getFileByAbsolutePath(fileName);
      theDir = root.getFileParentDirectoryByAbsolutePath(fileName);
    }
    // For if the file given was a relative path
    else {
      toBeAdded = root.getFileByRelativePath(theDir, fileName);
      theDir = root.getFileParentDirectoryByRelative(theDir, fileName);
    }

    // If the file does not already exist, add the file to the directory
    if (toBeAdded == null) {
      String[] names = fileName.split("/");
      if (fileName.endsWith("/")) {
        System.err.println("Error: The path \"" + fileName + "\" is not valid.");
        return;
      }
      if (!Validation.isValidName(names[names.length - 1])) {
        System.err.println("Error: The file with name \"" + names[names.length - 1] +
        "\" is not " + "valid.");
        return;
      }
      if (theDir == null) {
        System.err.println("Error: Attempting to create file in non-existant directory.");
        return;
      }
      if (theDir.getSubDirectoryByName(names[names.length - 1]) != null) {
        System.err
            .println("Error: Attempting to create file with the same name of an already" +
            " existing directory.");
        return;
      }
      toBeAdded = theDir.addNewFile(names[names.length - 1]);
    }

    // Add the string to the file
    toBeAdded.appendContent(input);
  }

  /**
   * Override the toString() method for object
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "This class is responsible for implementing Echo's append functionality";
  }
}
