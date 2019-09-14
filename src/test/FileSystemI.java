package test;

import java.util.Stack;
import filesystem.File;
import filesystem.Dir;

public interface FileSystemI{
  /**
   * Given a path, either directory or file.
   * Delete the file/directory at the given path. 
   * 
   * @param path The path of the directory/file
   * @param startingDir The current directory if needed to get the path
   */
  public void delete(String path, Dir startingDir);
  
  /**
   * Return a pointer to the root of the file system
   * 
   * @return Pointer to root of file system
   */
  public Dir getRoot();

  /**
   * Return a pointer to the directory that has the given ABSOLUTE path
   * 
   * @param dirPath The absolute path of the directory
   * @return A pointer to the directory that has the specified path. If no such
   *         directory exists, null is returned.
   */
  public Dir getDirectoryByAbsolutePath(String dirPath);


  /**
   * Return a pointer to the directory that has the given RELATIVE path
   * 
   * @param startingDir     The starting directory for which the path is relative
   *                        to
   * @param relativeDirPath The relative path of the directory
   * @return A pointer to the directory that has the specified path. If no such
   *         directory exists, null is returned.
   */
  public Dir getDirectoryByRelativePath(Dir startingDir, String relativeDirPath);

  /**
   * Given an absolute path, returns the directory that contains the file
   * specified by the provided path
   * 
   * @param filePath the string representation of an absolute path to a file
   * @return the directry that contains the file
   */
  public Dir getFileParentDirectoryByAbsolutePath(String absolutePath);

  /**
   * Given a path, returns the file referenced by an ansolute path. If file does
   * not exist at path, null is returned
   * 
   * @param filePath the string representation of an absolute path to a file
   * @return the file referenced by the absolute path
   */
  public File getFileByAbsolutePath(String absolutePath);

  /**
   * Given a path and current directory, returns the parent directory referenced
   * by a relative path.
   * 
   * @param startingDir  the current working directory
   * @param relativePath the string representation of a relative path to a file
   * @return the parent directory of the file.
   */
  public Dir getFileParentDirectoryByRelative(Dir startingDir, String relativePath);

  /**
   * Given a path and a directory, returns the file referenced by a relative path.
   * If file does not exist at path, null is returned
   * 
   * @param startingDir  the current working directory
   * @param relativePath the string representation of a relative path to a file
   * @return the file referenced by relative path
   */
  public File getFileByRelativePath(Dir startingDir, String relativePath);

  /**
   * Pushes a directory onto the directory stack
   * 
   * @param dir The directory that is to be pushed onto the directory stack
   */
  public void pushDir(String dir);

  /**
   * Pops a directory from the directory stack
   * @return Most recently placed directory in the stack.
   */
  public String popDir();
  
  /**
   * Get all the directories in current stack
   * @return A pointer to the directory of stacks
   * */
  public Stack<String> getDirectoryStack();

  /**
   * Override toString() of Object, print every directory and file in the
   * fileSystem
   * 
   * @return String representation of the entire file system
   */
  public String toString();
}

