package shellcommands;

import filesystem.File;
import filesystem.FileSystem;
import test.FileSystemI;
import filesystem.Dir;


/** A class to represent the copy command. */
public class Cp extends Command {

  /**
   * Copies a file or directory to another directory.
   * 
   * @param oldPath the directory path that is to be copied
   * @param newPath the destination of the copied paht
   * @param root the filesystem
   * @param currDir the current working directory
   * @return if the copy was successful
   */
  public boolean executeCommand(String oldPath, String newPath, FileSystemI root, Dir currDir) {
    // get File/Dir of newPath and oldPath
    // if newPath is absolute
    Dir destinationDir;
    File destinationFile;
    Dir startingDir;
    File startingFile;
    
    if (FileSystem.isAbsolutePath(newPath)) {
      destinationDir = root.getDirectoryByAbsolutePath(newPath);
      destinationFile = root.getFileByAbsolutePath(newPath);
    }
    else {
      destinationDir = root.getDirectoryByRelativePath(currDir, newPath);
      destinationFile = root.getFileByRelativePath(currDir, newPath);
    }

    //if oldPath is absolute
    if (FileSystem.isAbsolutePath(oldPath)) {
      startingDir = root.getDirectoryByAbsolutePath(oldPath);
      startingFile = root.getFileByAbsolutePath(oldPath);
    }
    else {
      startingDir = root.getDirectoryByRelativePath(currDir, oldPath);
      startingFile = root.getFileByRelativePath(currDir, oldPath);
    }

    if (!(isValidArgs(destinationDir, destinationFile, startingDir, startingFile,
        oldPath, newPath))) {
      return false;
    }

    // if oldPath is a file
    if (startingFile != null) {
      // if newPath is a directory then copy to new directory
      String absolutePath = currDir.getDirPath() + newPath;
      // for edge case
      if (absolutePath.equals("//")) {
        absolutePath = "/";
      }
      Overwrite.executeCommand(root.getDirectoryByAbsolutePath(absolutePath),
          startingFile.getFileName(), startingFile.getContents(), root);
    }
    // if oldPath is a directory
    else if (startingDir != null) {
      // // check if destination is a child of starting
      Dir parentDir = destinationDir.getParent();
      boolean isSubdir = false;
      boolean replace = false;
      Dir subdirectory = null;
      Dir childDir = destinationDir;
      // check if trying to copy a parent into a child directory
      if (parentDir != null) {
        while (parentDir.getDirName() != "") {
          subdirectory = parentDir.getSubDirectoryByName(childDir.getDirName());
          if (subdirectory == startingDir) {
            isSubdir = true;
          }
          childDir = parentDir;
          parentDir = parentDir.getParent();
        }
        subdirectory = parentDir.getSubDirectoryByName(childDir.getDirName());
        if (subdirectory == startingDir) {
          isSubdir = true;
        }
      }
      
      // check if there is a directory with the same name
      Dir sameName = destinationDir.getSubDirectoryByName(startingDir.getDirName());
      replace = sameName != null;
      // if user is trying to move a parent to a child
      if (isSubdir || oldPath.equals("/")) {
        System.err.println("Error: cannot move a parent directory to its child");
        return false;
      }
      else {
      // create a copy of startingDir
        Dir copyOfStart = Cp.createCopy(startingDir, 
            new Dir(startingDir.getDirName(), destinationDir.getDirPath()));
        // delete directory with the same name if it already exists
        if (replace) {
          root.delete(sameName.getDirPath(), currDir);
        }
        // add starting directory to destination
        destinationDir.addNewDirectory(copyOfStart);
        if (sameName == startingDir) {
          return false;
        }
      }
    }
    return true;
  }
  
  /** Creates a copy of a given Directory.
   * @param original the directory to be copied
   * @param copy the current copied directory
   * @return the completed copied directory
   */
  
  private static Dir createCopy(Dir original, Dir copy) {
    Dir[] subDirectories = new Dir[original.getSubDirectories().size()];
    original.getSubDirectories().toArray(subDirectories);

    File[] files = new File[original.getFiles().size()];
    original.getFiles().toArray(files);

    // add all directories
    for (Dir currDir : subDirectories) {
      //add to copy
      copy.addNewDirectory(createCopy(currDir, new Dir(currDir.getDirName(),copy.getDirPath())));
      
    }

    // add all files
    for (File currFile : files) {
      String fileName = currFile.getFileName();
      copy.addNewFile(fileName);
      copy.getFileByName(fileName).setContent(currFile.getContents());
    }

    return copy;
  }
  
  /**Checks if the arguments sent are valid.
   * Cannot copy a file or directory to a file location
   * @param desDir the destination directory, null if doesn't exist
   * @param desFile the destination file, null if doesn't exist
   * @param startDir the file to be copied
   * @return true if valid arguments are inputted
   */
  private static boolean isValidArgs(Dir desDir, File desFile, Dir startDir, 
       File startFile, String oldPath, String newPath) {
    
    // if newPath does not exist
    if (desFile == null && desDir == null) {
      System.err.println("Error: There is no file/directory with the provided path: " + newPath);
      return false;
    }
    // if oldPath does not exist
    if (startFile == null && startDir == null) {
      System.err.println("Error: There is no file/directory with the provided path: " + oldPath);
      return false;
    }
    // check if destination is a file and source is a directory
    if (desFile != null) {
      System.err.println("Error: Cannot move to a file location");
      return false;
    }
    return true;
  }
  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  
  public String toString() {
    return "Copies item from OLDPATH to NEWPATH. Both OLDPATH  and  NEWPATH  may  be  relative  "
      + "to  the current  directory  or  may  be  full  paths.   If  NEWPATH  is  a directory, "
        + "copy the the directory or file in OLDPATH to NEWPATH .";
  }

}
