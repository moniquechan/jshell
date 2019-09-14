package shellcommands;

import java.util.Arrays;
import filesystem.Dir;
import test.FileSystemI;

/** A class to represent the "history" shell command. */
public class History extends Command {
  // Number tracker of commands in history
  private int commandNum = 0;

  // Holds all the commands with an associated number
  private String commands = "";

  /**
   * Prints out the commands the user has inputed to the shell.
   * 
   * @param numbertoParse A string that contains solely a number If the parameter
   *                      is specified, then will print out that number of most
   *                      recent commands If no parameter is specified by user,
   *                      input will be defaulted to "0"
   */
  public void executeCommand(String[] args, Dir currentDirectory, FileSystemI root) {
    int toDisplay = -1;
    String Filename = "";
    String numbertoParse;
    String toPrint = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      Filename = args[args.length-1];
      args = get_arguments(args);
    }
    
    if(args.length == 1) {
      numbertoParse = "0";
    }
    else {
      numbertoParse = args[1];
    }    
    try {
      toDisplay = Integer.parseInt(numbertoParse);
    } catch (NumberFormatException e) {
      System.err.println("Error: invalid additional parameters were given");
      return;
    }

    if (toDisplay < 0) {
      System.err.println("Error: Invalid number was given");
    }

    else if (toDisplay == 0 || toDisplay > commandNum) {
      // Will print out everything until last character, so that we omit the newline
      // at the end
      toPrint = commands.substring(0, commands.length() - 1);
    }

    else {
      String[] list = commands.split("\n");
      int start = list.length - toDisplay;
      String print = String.join("\n", Arrays.copyOfRange(list, start, list.length));
      toPrint = print;
    }
    out.printString(toPrint,currentDirectory, Filename, root);
  }

  /**Updates History to have the string of the user entered argument.
   * 
   * @param argument the argument that the user inputed
   */
  public void addArgument(String argument) {
    this.commandNum++;
    this.commands = commands + commandNum + ". " + argument + "\n";
  }
  
  /**Clears all the data in the stack.
   * */
  public void clearArguments() {
    this.commands = "";
    this.commandNum = 0;
  }
  
  
  /**Returns all the commands the user has ever inputed into the JShell
   * @return String representation of all commands; each is separated by a new line.
   * */
  public String getAllCommands() {
    return this.commands;
  }
  
  public int hasCommands() {
    return this.commandNum;
  }
  
  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "history [number]\nThis command will print out recent commands, one command per line."
        + "The first column is numbered such that the line with the highest number is the most "
        + "recent command. By provoding a number, only the most recent commands, up to the "
        + "amount specifiedby number, will be displayed";
  }
}
