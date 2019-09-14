package shellcommands;
import java.util.ArrayList;
import java.util.Arrays;
import driver.Validation;
import filesystem.Dir;
import filesystem.File;
import filesystem.FileSystem;
import test.FileSystemI;

public class Find extends Command {
  
  public void executeCommand(String[] args, FileSystemI root, Dir currDir) {
    //Checker for where to print
    String filename = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(args));
    if(out.getPlaceToPrint() != 0) {
      filename = args[args.length-1];
      args = get_arguments(args);
    }
    if(args.length < 5) {
      System.err.println("Error: Not enough arguments");
      return;
    }
    // Check for flags
    String type = null;
    String expression = null;
    if(!parameterCheck(args)) {
      System.err.println("Error additional arguments after expression or type");
      return;
    }
    ArrayList<String> temp = new ArrayList<String>(Arrays.asList(args));
    type = getFlag(temp, "-type");
    if(type == null || (!type.equals("f") && !type.equals("d")) ) {
      System.err.println("Error: Invalid type to look for.");
      return;
    }
    temp.remove("-type");
    temp.remove(type);
    expression = getFlag(temp, "-name");
    if(expression == null || !Validation.isValidName(expression)) {
      System.err.println("Error: Invalid expression was inputted.");
      return;
    }
    temp.remove("-name");
    temp.remove('\"'+expression+'\"');
    
    String[] newInput = new String[temp.size()];
    temp.toArray(newInput);
    args = newInput;
    
    if(args.length == 1) {
      checksubDirectories(currDir, root, out, type, expression);   
    }
    else {
      checkDirectories(currDir, args, root, out, type, expression);
    }
    out.printStored(currDir, filename, root);   
    
  }
  
  private boolean parameterCheck(String[] args) {
    return (args[args.length-2].equals("-name")) && (args[args.length-4].equals("-type"));
  }
  
  private String getFlag(ArrayList<String> temp, String flag) {
    boolean get = false;
    for(String possibleFlag : temp) {
      if(get) {
        try {
        return possibleFlag.substring(possibleFlag.indexOf('\"')+1, possibleFlag.lastIndexOf('\"'));
        }catch(IndexOutOfBoundsException e) {
          if(possibleFlag.length() == 1 && flag.equals("-type")) {
            return possibleFlag;
          }
          return null;
        }
      }
      if(possibleFlag.equals(flag)) {
        get = true;
      }
    }
    return null;
  }
  
  private void checkDirectories(Dir currentWorkDir, String[] paths, FileSystemI root, 
      Output out, String flag, String expression) {
    
    Dir possibleDir;
    File possibleFile;
    
    for(int i = 1; i < paths.length; i++) {
      possibleDir = null;
      possibleFile = null;
      String currPath = paths[i];

      // If path of current object is absolute
      if(FileSystem.isAbsolutePath(currPath)) {
        possibleDir = root.getDirectoryByAbsolutePath(currPath);
        possibleFile = root.getFileByAbsolutePath(currPath);
      } else {
        possibleDir = root.getDirectoryByRelativePath(currentWorkDir, currPath);
        possibleFile = root.getFileByRelativePath(currentWorkDir, currPath);
      }
      if(possibleDir == null && possibleFile == null) {
        System.err.println("Error: could not find path: "+currPath);
      }
      else {
        if(possibleFile != null) {
          if(possibleFile.getFileName().equals(expression)) {
            out.storeString(paths[i] + "\n");
          }
        }
        else {
          Dir[] subDirectories = new Dir[possibleDir.getSubDirectories().size()];
          possibleDir.getSubDirectories().toArray(subDirectories);
          if(flag.equals("f")) {
            File[] files = new File[possibleDir.getFiles().size()];
            possibleDir.getFiles().toArray(files);
            for(File file : files) {
              if(file.getFileName().equals(expression)) {
                out.storeString(possibleDir.getDirPath() + file.getFileName() + "\n");
              }
            }
          }
          else if(flag.equals("d")) {
            for(Dir currDir : subDirectories) {
              if(currDir.getDirName().equals(expression) && !currDir.getDirName().equals("/")) {
                out.storeString(possibleDir.getDirPath().substring(0, 
                    possibleDir.getDirPath().length()-1) + "\n");
              }              
            }
          }
          for(Dir currDir : subDirectories) {
            checksubDirectories(currDir, root, out, flag, expression);
          }
        }
      }
    }
  }
  
  private void checksubDirectories(Dir enteredDir, FileSystemI root, 
      Output out, String flag, String expression) {
    Dir[] subDirectories = new Dir[enteredDir.getSubDirectories().size()];
    enteredDir.getSubDirectories().toArray(subDirectories);
     if(flag.equals("f")) {
       File[] files = new File[enteredDir.getFiles().size()];
       enteredDir.getFiles().toArray(files);
       for(File file : files) {
         if(file.getFileName().equals(expression)) {
           out.storeString(enteredDir.getDirPath() + file.getFileName() + "\n");
         }
       }
     }
    else if(flag.equals("d")) {
      for(Dir currDir : subDirectories) {
        if(currDir.getDirName().equals(expression) && !currDir.getDirName().equals("/")) {
          out.storeString(currDir.getDirPath().substring(0, currDir.getDirPath().length() - 1) 
              + "\n");
        }              
      }
    }
    for(Dir currDir : subDirectories) {
      checksubDirectories(currDir, root, out, flag, expression);
    }
  }
}
