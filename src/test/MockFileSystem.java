package test;

import java.util.Stack;
import filesystem.File;
import shellcommands.History;
import filesystem.Dir;

public class MockFileSystem implements FileSystemI{
  
  private Dir root; // The root directory of the file system
  private Stack<String> stackDir; // the stack directory
  
  //Initialize History tracker
  static History tracker = new History();
  
  public MockFileSystem() {
    this.root = new Dir("", "");
    this.stackDir = new Stack<>();
  }
  
  /**
   * Given a path, either directory or file.
   * Delete the file/directory at the given path. 
   * 
   * @param path The path of the directory/file
   * @param startingDir The current directory if needed to get the path
   */
  public void delete(String path, Dir startingDir) {
    if (path == "Invalid") {
      System.err.println("Error: invalid arguments given for method delete(String, Dir)");
    }
    else if (path.contains("relative")){
      if (path.contains("file")) {
        Dir deleteDir = this.getFileParentDirectoryByRelative(startingDir, path);
        deleteDir.removeFile("relativefile");
      }
      else {
        Dir deleteDir = getDirectoryByRelativePath(startingDir, path);
        deleteDir.getParent().removeDir(deleteDir.getDirName());
      }
    }
    
    else {
      if (path.contains("file")) {
        Dir deleteDir = this.getFileParentDirectoryByAbsolutePath(path);
        deleteDir.removeFile("file");
      }
      else {
        Dir deleteDir = this.getDirectoryByAbsolutePath(path);
        deleteDir.getParent().removeDir(deleteDir.getDirName());
      }
      
    }
  }
  
  /**
   * Return a pointer to the root of the file system
   * 
   * @return Pointer to root of file system
   */
  public Dir getRoot() {
    return root;
  }

  /**
   * Return a pointer to the directory that has the given ABSOLUTE path
   * 
   * @param dirPath The absolute path of the directory
   * @return A pointer to the directory that has the specified path. If no such
   *         directory exists, null is returned.
   */
  public Dir getDirectoryByAbsolutePath(String dirPath) {
    if (dirPath == "Invalid") {
      return null;
    }
    else {
      String[] subdirectories = dirPath.substring(1).split("/");
      Dir currentDir = root;
      for(String s : subdirectories){
        currentDir = currentDir.getSubDirectoryByName(s);
      }
      return currentDir;
    }
  }


  /**
   * Return a pointer to the directory that has the given RELATIVE path
   * 
   * @param startingDir     The starting directory for which the path is relative
   *                        to
   * @param relativeDirPath The relative path of the directory
   * @return A pointer to the directory that has the specified path. If no such
   *         directory exists, null is returned.
   */
  public Dir getDirectoryByRelativePath(Dir startingDir, String relativeDirPath) {
    if (relativeDirPath == "Invalid") {
      return null;
    }
    else {
      String[] subdirectories = relativeDirPath.substring(1).split("/");
      Dir currentDir = startingDir;
      for (String s : subdirectories) {
        currentDir = currentDir.getSubDirectoryByName(s);
      }
      return currentDir;
    }
  }

  /**
   * Given an absolute path, returns the directory that contains the file
   * specified by the provided path
   * 
   * @param filePath the string representation of an absolute path to a file
   * @return the directry that contains the file
   */
  public Dir getFileParentDirectoryByAbsolutePath(String absolutePath) {
    if (absolutePath == "Invalid") {
      return null;
    }
    else {
      String[] subdirectories = absolutePath.substring(1).split("/");
      Dir currentDir = root;
      int i = 0;
      while (i < subdirectories.length - 1) {
        currentDir = currentDir.getSubDirectoryByName(subdirectories[i]);
        i++;
      }
      return currentDir;
    }
  }

  /**
   * Given a path, returns the file referenced by an ansolute path. If file does
   * not exist at path, null is returned
   * 
   * @param filePath the string representation of an absolute path to a file
   * @return the file referenced by the absolute path
   */
  public File getFileByAbsolutePath(String absolutePath) {
    File fileToBeReturned = null;
    String fileName = absolutePath.substring(1);
    Dir directoryOfFile = getFileParentDirectoryByAbsolutePath(absolutePath);
    if (fileName.contains("/")) {
      fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
    }
    if (directoryOfFile != null) {
      fileToBeReturned = directoryOfFile.getFileByName(fileName);
    }
    return fileToBeReturned;
  }

  /**
   * Given a path and current directory, returns the parent directory referenced
   * by a relative path.
   * 
   * @param startingDir  the current working directory
   * @param relativePath the string representation of a relative path to a file
   * @return the parent directory of the file.
   */
  public Dir getFileParentDirectoryByRelative(Dir startingDir, String relativePath) {
    if (relativePath == "Invalid") {
      return null;
    }
    else {
      String[] subdirectories = relativePath.substring(1).split("/");
      Dir currentDir = startingDir;
      int i = 0;
      while (i < subdirectories.length - 1) {
        currentDir = currentDir.getSubDirectoryByName(subdirectories[i]);
        i++;
      }
      return currentDir;
    }
  }

  /**
   * Given a path and a directory, returns the file referenced by a relative path.
   * If file does not exist at path, null is returned
   * 
   * @param startingDir  the current working directory
   * @param relativePath the string representation of a relative path to a file
   * @return the file referenced by relative path
   */
  public File getFileByRelativePath(Dir startingDir, String relativePath) {
    if (relativePath == "Invalid") {
      return null;
    }
    else {
      String[] subdirectories = relativePath.substring(1).split("/");
      Dir currentDir = startingDir;
      int i = 0;
      while (i < subdirectories.length - 1) {
        currentDir = currentDir.getSubDirectoryByName(subdirectories[i]);
        i++;
      }
      return currentDir.getFileByName(subdirectories[i + 1]);
    }
  }

  /**
   * Pushes a directory onto the directory stack
   * 
   * @param dir The directory that is to be pushed onto the directory stack
   */
  public void pushDir(String dir) {
    stackDir.push(dir);
  }

  /**
   * Pops a directory from the directory stack
   * @return Most recently placed directory in the stack.
   */
  public String popDir() {
    return stackDir.pop();
  }
  
  /**
   * Get all the directories in current stack
   * @return A pointer to the directory of stacks
   * */
  public Stack<String> getDirectoryStack(){
    return stackDir;
  }
  
  public History getHistoryOfCommands() {
    return this.tracker;
  }
  
  /**
   * Clear ALL data in the Mock File System.
   * */
  public void clearAllData() {
    this.root = new Dir("", "");
    this.tracker = new History();
    this.stackDir = new Stack<>();
  }
  
  /**
   * Helper function for toString() method. Recursively generates what the file
   * system looks like.
   * 
   * @param currDir         A pointer to the current directory
   * @param currIndentation The current indentation level, in order to show
   *                        hierarchy
   * @param result          The string that we will keep appending to
   * @return A string representation of the entire file system
   */
  private String toStringHelper(Dir currDir, int currIndentation, String result) {

    if (currDir != null) {
      // Padding of tab characters in the front
      String frontTabbing = "";

      // Calculate appropriate amount of tabbing
      for (int i = 0; i < currIndentation; i++) {
        frontTabbing += '\t';
      }

      // Get all sub-directories and files in this current directory
      Dir[] subDirs = new Dir[currDir.getSubDirectories().size()];
      File[] files = new File[currDir.getFiles().size()];

      currDir.getSubDirectories().toArray(subDirs);
      currDir.getFiles().toArray(files);

      // Store names of all sub-directories
      for (int i = 0; i < subDirs.length; i++) {
        result += "\n" + frontTabbing + subDirs[i].getDirName();
        result = this.toStringHelper(subDirs[i], currIndentation + 1, result);
      }

      // Store names of all files
      for (int i = 0; i < files.length; i++) {
        result += "\n" + frontTabbing + files[i].getFileName();
      }
    }

    return result;
  }

  /**
   * Override toString() of Object, print every directory and file in the
   * fileSystem
   * 
   * @return String representation of the entire file system
   */
  public String toString() {
    int startingIndentation = 1;
    String result = this.toStringHelper(this.root, startingIndentation, "");
    return "/\n" + result;
  }
}

