package driver;

/**
 * A class for validating user inputs.
 * Determines if user inputs are valid or not before proceeding further
 */
public class Validation {
  
  /** A function to determine if a given directory/file name is valid or not.
   * @param name The name of the directory/file in question.
   * */
  public static boolean isValidName(String name) {
    // All possible characters that CANNOT be included in a file/directory name
    String[] namechecker = {"/", ".", " ", "!", "@", "#", "$", "%", "^", "&", "(", ")", "{", "}",
        "~", "|", "<", ">", "?", "\""};
    
    // Check if any of the above characters are in the given file/directory name
    for (String currChar : namechecker) {
      if (name.contains(currChar)) {
        return false;
      }
    }
    return true;
  }

  /**Override the toString() method for object
   * @return A string that describes the class
   */
  public String toString(){
    return "This is the Validation class responsible for validating user inputs";
  }
}