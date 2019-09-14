package shellcommands;
import filesystem.Dir;
import test.FileSystemI;

public class Tree extends Command {
  
  public void executeCommand(String[] args, FileSystemI root, Dir currDir) {
    
    // Check if output needs to be redirected
    String Filename = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      Filename = args[args.length-1];
      args = get_arguments(args);
    }
    
 // If the command is not being redirected, AND we are given additional parameters, print error and exit
    if(args.length > 1) {  
      System.err.println("Error: Tree command does not take in any additional parameters");
      return;
    }
    
    // Send command output
    out.printString(root.toString(), currDir, Filename, root);
    
  }
}
