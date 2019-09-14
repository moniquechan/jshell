package shellcommands;

/**
 * A class to represent a Command object. All commands can be executed and can
 * have string representations
 */
public abstract class Command {
  /**Execute a JShell command.
   */
  public static void executeCommand() {
    System.out.println("This is a generic execute function. Please override.");
  }
  
  public static int checker(String[] input) {
    if(input == null) {
      return 0;
    }
    int num = input.length;
    int file = 0;
    if(num > 2) {
      if(input[num - 2].equals(">")) {
        file = -1;
      }
      if(input[num - 2].equals(">>")) {
        file = 1;
      }
    }
    return file;
  }
  public static String[] get_arguments(String[] input) {
    int num = input.length;
    String[] args = new String[num - 2];
    for(int i = 0; i < num - 2; i++) {
      args[i] = input[i];
    }
    return args;
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "This is a generic command class. Please override.";
  }
}
