package filesystem;

/**
 * A class to represent a file. This type of node can store data, but cannot have any children
 */
public class File {

  private String fname; // The name of this file
  private String contents; // The data stored in this file

  public File(String fname) {
    this.fname = fname;
    this.contents = "";
  }

  /**Overwrite the contents of this file.
   * @param newData The date to override this file with
   */
  public void setContent(String newData) {
    this.contents = newData;
  }

  /**Append the contents of this file with the new data.
   * @param newData The data to append to this file in a new line
   */
  public void appendContent(String newData) {
    if (this.contents.length() == 0) {
      setContent(newData);
    }
    else {
      this.contents = this.contents + "\n" + newData;
    }        
  }

  /**Get name of this file.
   * @return Filename associated with this file
   */
  public String getFileName() {
    return this.fname;
  }

  /**Get the data stored in this file.
   * @return The contents of this file
   */
  public String getContents() {
    return this.contents;
  }
  
  /**Override the toString() method for object.
   * @return A string representation of a file
   * */
  public String toString() {
    return this.fname;
  }
}