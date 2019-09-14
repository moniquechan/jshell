package shellcommands;

import filesystem.Dir;
import test.FileSystemI;

/** The class for the "Man" JShell command. */
public class Man extends Command {
  /** Prints the manual for specified command into the shell.
   * 
   * @param commandString The command that will have its manual printed to the
   *                      shell.
   */
  public void executeCommand(String[] commandString, Dir currDir, FileSystemI root) {
    // Firstly, cast the commandString into an actual command object
    String Filename = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(commandString));
    if(out.getPlaceToPrint() != 0) {
      Filename = commandString[commandString.length-1];
      commandString = get_arguments(commandString);
    }
    Command commandOfWantedManual;
    try {
      commandOfWantedManual = stringToCommandObject(commandString[1]);
    } catch(ArrayIndexOutOfBoundsException e) {
      commandOfWantedManual = null;
    }

    // If the command the user wants the manual for DNE, print an error message
    if (commandOfWantedManual == null) {
      System.err.println("Error: Manual cannot be found; check if valid command is given.");
    }

    // Print the manual to the JShell
    else {
      out.printString(commandOfWantedManual.toString(), currDir, Filename, root);;
    }

  }

  /**Converts a string representation of a command to an actual Command object.
   * Helper for this class
   * 
   * @param commandString The command that will be converted to a Command object
   * @return returns the command to be executed
   */
  private static Command stringToCommandObject(String commandString) {
    // Declare the object to return
    Command commandToReturn = null;
    // For every possible command, assign the correct Command object to be returned
    if (commandString != null) {
      switch (commandString) {
        case "exit":
          commandToReturn = new Exit();
          break;
  
        case "mkdir":
          commandToReturn = new MakeDirectory();
          break;
        case "ls":
          commandToReturn = new Ls();
          break;
  
        case "cd":
          commandToReturn = new ChangeDirectory();
          break;
  
        case "cat":
          commandToReturn = new Cat();
          break;
  
        case "echo":
          commandToReturn = new Echo();
          break;
  
        case "pushd":
          commandToReturn = new Pushd();
          break;
  
        case "popd":
          commandToReturn = new Popd();
          break;
  
        case "pwd":
          commandToReturn = new Pwd();
          break;
  
        case "history":
          commandToReturn = new History();
          break;
  
        case "man":
          commandToReturn = new Man();
          break;
      }
    }

    // Return the command object
    return commandToReturn;
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "man CMD\nPrint documentation for CMD";
  }
}
