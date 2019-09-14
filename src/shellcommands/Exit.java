package shellcommands;

/** A class to represent the "exit" shell command. */
public class Exit extends Command {

  /**
   * Exit the shell with code 0.
   * 
   * @param args The arguments given. This must be an empty array of Strings.
   */
  public static void executeCommand(String[] args) {
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      args = get_arguments(args);
    }
    if (args.length > 1) {
      System.err.println("Error: Exit command does not take any additional parameters");
      return;
    }
    System.exit(0);
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "exit\nQuit the shell";
  }
}
