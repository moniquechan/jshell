package filesystem;

import java.util.ArrayList;
/**
 * A class to represent a directory. This is a node in a larger tree, which is FileSystem.
 * This type of node can have any number of children (type Dir and File).
 */

public class Dir {

  private String dname; // Name of this directory
  private String dpath; // The absolute path of this directory, starting at the root
  //The sub-directories within this directory
  private ArrayList<Dir> subDirectories = new ArrayList<Dir>(); 
  //All the files contained in this directory
  private ArrayList<File> files = new ArrayList<File>(); 
  private Dir parent; // The parent directory of this child

  /**Constructor for directory, returns Dir object given a name for this directory.
   * @param dirName The name of the directory
   */
  public Dir(String dirName, String parentPath) {
    this.dname = dirName;
    this.dpath = parentPath + dirName + "/"; // Set path of current directory
  }

  /**Add a child directory to current directory.
   * @param newDirectory The name of the new directory
   */
  public void addNewDirectory(Dir newDirectory) {
    newDirectory.setParent(this); // Set parent of this new directory
    this.subDirectories.add(newDirectory);
      
  }

  /**Add a file to current directory.
   * @param newFileName The name of the new file to add
   * @return A pointer to the file that was created
   */
  public File addNewFile(String newFileName) {
    File newFile = new File(newFileName);
    this.files.add(newFile);
    return newFile;
  }
  /**Removes a given dirname from its parent directory 
   * @param DirName
   */
  public void removeDir(String DirName) {
    Dir[] subDir = new Dir[subDirectories.size()];
    subDirectories.toArray(subDir);
    for(Dir dir : subDir) {
      if(dir.getDirName().equals(DirName)) {
        subDirectories.remove(dir);
      }
    }
  }
  
  /** Removes a given filename from its directory
   * @param FileName
   */
  public void removeFile(String FileName) {
    File[] arrayFile = new File[files.size()];
    files.toArray(arrayFile);
    for(File file : arrayFile) {
      if(file.getFileName().equals(FileName)) {
        files.remove(file);
      }
    }
  }

  /**Retrieve the name of this directory.
   * @return Name of the directory
   */
  public String getDirName() {
    return this.dname;
  }
  
  /**Retrieve the path of this directory, relative to the root.
   * @return Absolute path of this directory
   * */
  public String getDirPath() {
    return this.dpath;
  }
  
  /**Return all the sub-directories of this directory.
   * @return All sub-directories in this directory
   * */
  public ArrayList<Dir> getSubDirectories() {
    return this.subDirectories;
  }
  
  /**Return all the files in this directory.
   * @return All files contained in this directory
   * */
  public ArrayList<File> getFiles() {
    return this.files;
  }
  
  /**Get a pointer to a appropriate sub-directory, given its name.
   * @param dirName The name of the directory
   * @return A node with the directory name. If such a directory does not exist, null is returned.
   * */
  public Dir getSubDirectoryByName(String dirName) {
    Dir[] children = new Dir[this.subDirectories.size()];
    this.subDirectories.toArray(children);
    int length = children.length;
    for (int i = 0; i < length; i++) {
      if (dirName.equals(children[i].getDirName())) {
        return children[i];
      }
    }
    return null;
  }
  
  /**Get a pointer to a file, given its name.
   * @param fname The name of the file
   * @return A File node with name fname. If such a file does not exist, null is returned.
   * */
  public File getFileByName(String fname) {
    File[] children = new File[this.files.size()];
    this.files.toArray(children);
    int length = children.length;
    for (int i = 0; i < length; i++) {
      if (fname.equals(children[i].getFileName())) {
        return children[i];
      }
    }
    return null;
  }
  
  /**Set the parent of this directory. To be used when a subdirectory is created
   * @param parent The parent of this directory
   * */
  public void setParent(Dir parent) {
    this.parent = parent;
  }
  
  /**Get the parent node of this node.
   * @return The parent node
   * */
  public Dir getParent() {
    return this.parent;
  }
  
  /**Override the toString() method for object.
   * @return A string representation of the directory
   * */
  public String toString() {
    return this.dpath;
  }
}