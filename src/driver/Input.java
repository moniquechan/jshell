package driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import filesystem.Dir;
import filesystem.FileSystem;
import shellcommands.Command;
import shellcommands.Cat;
import shellcommands.ChangeDirectory;
import shellcommands.Cp;
import shellcommands.Echo;
import shellcommands.Exit;
import shellcommands.Find;
import shellcommands.Get;
import shellcommands.History;
import shellcommands.Load;
import shellcommands.Ls;
import shellcommands.MakeDirectory;
import shellcommands.Man;
import shellcommands.Mv;
import shellcommands.Popd;
import shellcommands.Pushd;
import shellcommands.Pwd;
import shellcommands.Save;
import shellcommands.Tree;

/**A class to handle user input into the JShell, and send required data to the command that is called.*/
public class Input {

  // Hash table to store all the commands
  private static Hashtable<String, Command> setOfCommands;
  
  public Input() {
    // Instantiate all commands into the hashtable
    setOfCommands = new Hashtable<String, Command>();

    setOfCommands.put("exit", new Exit());
    setOfCommands.put("mkdir", new MakeDirectory());
    setOfCommands.put("ls", new Ls());
    setOfCommands.put("cd", new ChangeDirectory());
    setOfCommands.put("cat", new Cat());
    setOfCommands.put("echo", new Echo());
    setOfCommands.put("tree", new Tree());
    setOfCommands.put("pushd", new Pushd());
    setOfCommands.put("popd", new Popd());
    setOfCommands.put("pwd", new Pwd());
    setOfCommands.put("man", new Man());
    setOfCommands.put("get", new Get());
    setOfCommands.put("save", new Save());
    setOfCommands.put("load", new Load());
    setOfCommands.put("cp", new Cp());
    setOfCommands.put("mv", new Mv());
    setOfCommands.put("find", new Find());
  }
  
  /**Ask the user for an input.
   * @param tracker An instance of History object to store all user input, used by the "history" command.
   * @return String representation of the user input 
   */
  public String getUserInput(History tracker) {
    String userInput = "";
    
    // If scanning fails, log the result
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      userInput = reader.readLine();
      } catch (Exception e) {
        e.printStackTrace();
      }
    
    //Storing User input in history tracker
    if(!userInput.trim().equals("")) {
      tracker.addArgument(userInput);
    }
    return userInput;
  }
  
  /**Convert the user input into a usable array of strings.
   * @param userInput The raw input from console
   * @return The parsed user input
   *
   **/
  public String[] parseUserInput(String userInput) {
    // Convert to array
    userInput = userInput.trim();
    
    ArrayList<String> result = new ArrayList<String>(Arrays.asList(userInput.split(" ")));
    
    for (int i = 0; i < result.size(); i++) {
      try {
        while (result.get(i).equals("")) {
          result.remove(i);
        }
      }catch (Exception e) {
        result.add("");
      }
    }
    
    return result.toArray(new String[result.size()]);
  }
  
  /**Send the user input to appropriate helper class.
   * @param args The arguments given in the shell.
   * @param currentDirectory A pointer to the current working directory the user is in.
   * @param root A pointer to the root of the file system.
   * @param tracker A pointer to the History object, which keeps track of all user inputed commands.
   * @return returns The currentDirectory after the command is executed.
   * */
  public Dir sendUserInput(String[] args, Dir currentDirectory, FileSystem root, History tracker) {
    /*Check args[0] and send parsedUserInput array to appropriate class */
    Dir tempDir;
    switch (args[0]) {
      case "exit":
        Exit.executeCommand(args);
        break;
        
      case "mkdir":
        try {
          MakeDirectory tempMkdir = (MakeDirectory) setOfCommands.get("mkdir");
          tempMkdir.executeCommand(root, currentDirectory, Arrays.copyOfRange(args, 1, 
          args.length));
        }
        catch (Exception e) {
          // If error occurs, it means user gave a bad path
          String temp = e.getMessage();
          if (temp != null) {
            System.out.println(temp);
          }
          System.out.println("Error: Invalid directory specified. "
                + "Not all directories were created.");
        }
        
        break;
      case "ls":
        Ls tempLs = (Ls) setOfCommands.get("ls");
        tempLs.executeCommand(args, currentDirectory, root, false);
        break;
        
      case "cd":
        ChangeDirectory tempCd = (ChangeDirectory) setOfCommands.get("cd");
        tempDir = tempCd.executeCommand(root, currentDirectory, Arrays.copyOfRange(args,
        1, args.length));
        if (tempDir != null) {
          currentDirectory = tempDir;
        } else {
          System.err.println("Error: The specified directory is invalid.");
        }
        break;
      
      case "cat":
        // need to verify if file exists
        Cat tempCat = (Cat) setOfCommands.get("cat");
        tempCat.executeCommand(Arrays.copyOfRange(args, 1, args.length), currentDirectory, root);
        break;
        
      case "echo":
        Echo tempEcho = (Echo) setOfCommands.get("echo");
        tempEcho.executeCommand(Arrays.copyOfRange(args, 1, args.length), currentDirectory, root);
        break;
      
      case "tree":
        Tree tempTree = (Tree) setOfCommands.get("tree");
        tempTree.executeCommand(args, root, currentDirectory);
        break;
      
      case "pushd":
        Pushd tempPushd = (Pushd) setOfCommands.get("pushd");  
        tempDir = tempPushd.executeCommand(args, currentDirectory, root);
        if (tempDir != null) {
          currentDirectory = tempDir;
        } else {
          System.err.println("Error: Invalid directory given");
        }
        break;
      
      case "popd":
        Popd tempPopd = (Popd) setOfCommands.get("popd");
        tempDir = tempPopd.executeCommand(currentDirectory, root, args);
        if (tempDir != null) {
          currentDirectory = tempDir;
        } else {
          System.err.println("Error: Invalid directory given");
        }
        break;

      case "pwd":
        Pwd tempPwd = (Pwd) setOfCommands.get("pwd");
        tempPwd.executeCommand(args, currentDirectory, root);
        break;
        
      case "history":
        tracker.executeCommand(args, currentDirectory, root);
        break;

      case "man":
        Man tempMan = (Man) setOfCommands.get("man");  
        tempMan.executeCommand(args, currentDirectory, root);
        break;

      case "get":
        Get tempGet = (Get) setOfCommands.get("get");
        tempGet.executeCommand(Arrays.copyOfRange(args, 1, args.length), currentDirectory, root);
        break;
      case "save":
        Save tempSave = (Save) setOfCommands.get("save");
        tempSave.executeCommand(args, root, tracker);
        break;
        
      case "load":
        Load tempLoad = (Load) setOfCommands.get("load");
        tempLoad.executeCommand(args, root, tracker);
        break;

      case "cp":
        try{
          Cp tempCp = (Cp) setOfCommands.get("cp");
          tempCp.executeCommand(args[1], args[2], root, currentDirectory);
        }
        catch (Exception e){
          System.err.println("Error: Invalid arguments inputted");
        }
        break;

      case "mv":
        try{
          Mv tempMv = (Mv) setOfCommands.get("mv");
          tempMv.executeCommand(args[1], args[2], root, currentDirectory);
        }
        catch (Exception e) {
          System.err.println("Error: Invalid arguments inputted");
        }
        break;
       
      case "find":
        Find tempFind = (Find) setOfCommands.get("find");
        tempFind.executeCommand(args, root, currentDirectory);
        break;
        
      case "":
        break;

      default:
        System.err.println("Error: Command \"" + args[0] + "\" not found.");
        break;
    }
    return currentDirectory;
    
  }
}
