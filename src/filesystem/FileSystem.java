package filesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import test.FileSystemI;

/**
 * A class to represent a File System. This file system makes use of Dir and
 * File objects.
 */
public class FileSystem implements FileSystemI {

  private Dir root; // The root directory of the file system
  private Stack<String> stackDir; // the stack directory
  private static FileSystem fileSystemReference = null;

  private FileSystem() {
    this.root = new Dir("", "");
    this.stackDir = new Stack<>();
  }

  /**
   * This class makes FileSystem a singleton. If 2 shells are open at the same time,
   * they use the same file system
   * @return The reference to the filesystem shared between shells
   */
  public static FileSystem createFileSystem(){
    if(fileSystemReference == null) {
      fileSystemReference = new FileSystem();
    }
    
    return fileSystemReference;
  }
  
  /**
   * Given a path, either directory or file.
   * Delete the file/directory at the given path. 
   * 
   * @param path The path of the directory/file
   * @param startingDir The current directory if needed to get the path
   */
  public void delete(String path, Dir startingDir) {
    Dir desiredDir = null;
    File desiredFile = null;
    if(isAbsolutePath(path)) {
      desiredDir = getDirectoryByAbsolutePath(path);
      if(desiredDir == null) {
        desiredDir = getFileParentDirectoryByAbsolutePath(path);
        desiredFile = getFileByAbsolutePath(path);
      }
    }
    else {
      desiredDir = getDirectoryByRelativePath(startingDir, path);
      if(desiredDir == null) {
        desiredDir = getFileParentDirectoryByRelative(startingDir, path);
        desiredFile = getFileByRelativePath(startingDir, path);
      }
      
    }
    remove(desiredDir, desiredFile);
  }
  
  /**
   * A helper function for delete.
   * Given a directory or file. 
   * Will remove them from the Filesystem.
   * 
   * @param desiredDir The directory to be removed.
   * @param desiredFile The File to be removed.
   */

  private void remove(Dir desiredDir, File desiredFile) {
    Dir parent = root;
    if(desiredFile != null) {
      desiredDir.removeFile(desiredFile.getFileName());
    }
    else if (desiredFile == null && desiredDir != null) {
      ArrayList<Dir> names = desiredDir.getSubDirectories();
      Dir[] dirnames = new Dir[names.size()];
      names.toArray(dirnames);
      ArrayList<File> fnames = desiredDir.getFiles();
      File[] filenames = new File[fnames.size()];
      fnames.toArray(filenames);
      if(desiredDir != root) {
        parent = desiredDir.getParent();
      }
      parent.removeDir(desiredDir.getDirName());
      for(Dir temp : dirnames) {
        remove(temp,null);
      }
      for(File temp : filenames) {
        desiredDir.removeFile(temp.getFileName());
      }
    }
  }

  /**
   * Return a pointer to the root of the file system
   * 
   * @return Pointer to root of file system
   */
  public Dir getRoot() {
    return this.root;
  }

  /**
   * Return a pointer to the directory that has the given ABSOLUTE path
   * 
   * @param dirPath The absolute path of the directory
   * @return A pointer to the directory that has the specified path. If no such
   *         directory exists, null is returned.
   */
  public Dir getDirectoryByAbsolutePath(String dirPath) {
    String[] possible_paths = dirPath.substring(1).split("/");

    // Special case if possible_paths has length 0 ==> we we're given invalid path
    if (possible_paths.length == 0) {
      return null;
    }

    // Special case if possible_paths[0] is "", then we need to return the root
    if (possible_paths[0].equals("")) {
      return root;
    }

    return getchildDirectory(possible_paths, 0, root);
  }

  /**
   * A helper function for getting a directory by absolute path
   * 
   * @param pathcheck   The path of the sub directory
   * @param num         A counter to keep track of where we are in the pathcheck
   *                    array
   * @param startingdir A pointer to the starting directory (usually the root)
   */
    private Dir getchildDirectory(String[] pathcheck, int num, Dir startingdir) {
      if(pathcheck.length == num) {
        return startingdir;
      }
      else {
        Dir desiredDirectory;
        if(!pathcheck[num].contains(".")) {
          try {
            desiredDirectory = getchildDirectory(pathcheck, num+1, 
                startingdir.getSubDirectoryByName(pathcheck[num]));
          }catch(Exception e) {
            desiredDirectory = null;
          }
        }
        else if(pathcheck[num].equals("..")) {
          try {
            desiredDirectory = getchildDirectory(pathcheck, num+1, 
                startingdir.getParent());
          }catch(Exception e) {
            desiredDirectory = null;
          }
        }
        else if(pathcheck[num].equals(".")) {
          desiredDirectory = getchildDirectory(pathcheck, num+1, 
              startingdir);
        }
        else {
          desiredDirectory = null;
        }
        return desiredDirectory;
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

      // Split the relative directory by "/"
      String[] splitPath = relativeDirPath.split("/");

      // Call helper function to recursively find the directory
      Dir result = this.directoryByRelativePathHelper(startingDir, splitPath);

      return result;
    }

    private Dir directoryByRelativePathHelper(Dir currDir, String[] path) {

      // Continue if we don't have null pointer AND path.length > 1
      if (currDir != null) {

        /*
         * Special cases are included for when we've reached the last element in array
         * "path"
         */
        switch (path[0]) {
        case "..": // .. means go up by one directory
          if (path.length == 1) {
            currDir = currDir.getParent(); // get parent of current directory
          } else {
            // Go up one level in the file system
            currDir = this.directoryByRelativePathHelper(currDir.getParent(),
            Arrays.copyOfRange(path, 1, path.length));
          }
          break;

        case ".": // . means current directory
          if (path.length > 1) {
            // Don't change the directory
            currDir = this.directoryByRelativePathHelper(currDir,
            Arrays.copyOfRange(path, 1, path.length));
          }
          break;

        default: // If above two aren't specified, it means we're given a directory name
          if (path.length == 1) {
            currDir = currDir.getSubDirectoryByName(path[0]);
          } else {
            currDir = this.directoryByRelativePathHelper(currDir.getSubDirectoryByName(path[0]),
                Arrays.copyOfRange(path, 1, path.length));
          }
          break;
        }
      }

      return currDir;
    }

  /**
   * Given an absolute path, returns the directory that contains the file
   * specified by the provided path
   * 
   * @param filePath the string representation of an absolute path to a file
   * @return the directry that contains the file
   */
  public Dir getFileParentDirectoryByAbsolutePath(String absolutePath) {
    String fileName = absolutePath.substring(1);
    Dir directoryOfFile = root;
    if (fileName.contains("/")) {
      String absolutePathWithoutFile = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
      fileName = absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
      directoryOfFile = getDirectoryByAbsolutePath(absolutePathWithoutFile);
    }
    return directoryOfFile;
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
    Dir directoryOfFile = startingDir;
    if (relativePath.contains("/")) {
      String relativePathWithoutFile = relativePath.substring(0, relativePath.lastIndexOf("/"));
      directoryOfFile = getDirectoryByRelativePath(startingDir, relativePathWithoutFile);
    }

    return directoryOfFile;
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
    File fileToBeReturned = null;
    String fileName = relativePath;
    Dir directoryOfFile = getFileParentDirectoryByRelative(startingDir, relativePath);
    if (relativePath.contains("/")) {
      fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1, relativePath.length());
    }

    if (directoryOfFile != null) {
      fileToBeReturned = directoryOfFile.getFileByName(fileName);
    }
    return fileToBeReturned;
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
   * Determine if a path to a file/directory is absolute or relative
   * 
   * @param path The path of the file/directory. Path must include <b><i>AT
   *             LEAST</i></b> one character
   * @return true/false if the path is absolute or relative, respectively
   */
  public static boolean isAbsolutePath(String path) {
    return (path.substring(0, 1).equals("/") ? true : false);
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
  public Stack<String> getDirectoryStack() {
    return this.stackDir;
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