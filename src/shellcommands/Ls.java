package shellcommands;

import java.util.Arrays;
import java.util.ArrayList;
import filesystem.Dir;
import filesystem.File;
import filesystem.FileSystem;
import test.FileSystemI;

/** A class to represent the "ls" Unix shell command. */
public class Ls extends Command {

  /**
   * Prints the current list of files and directories in specified path to the
   * shell If path specified is a file, will print out the contents of file.
   * 
   * @param input          Arguments provided
   * @param currentWorkDir A pointer to the current working directory
   * @param root           A pointer to the root of the file system
   */
  public void executeCommand(String[] input, Dir currentWorkDir, FileSystemI root, boolean R_flag) {
    
    // Checker for where to print to
    String filename = "";
    Output out = new Output();
    out.setPlaceToPrint(checker(input));
    if(out.getPlaceToPrint() != 0) {
      filename = input[input.length-1];
      input = get_arguments(input);
    }
    
    // Look for -R flag, and remove any unnecessary flags along the way
    ArrayList<String> tempInput = new ArrayList<String>(Arrays.asList(input));
    
    for(String currPossibleFlag : input) {
      if(currPossibleFlag.substring(0, 1).equals("-")) {
        
        // If we've found the -R flag, update R_flag 
        if(currPossibleFlag.equals("-R")) {
          R_flag = true;
        }
        tempInput.remove(currPossibleFlag); // Remove all flags
      }
    }
    
    // Copy back tempInput to input via intermediary newInput
    String[] newInput = new String[tempInput.size()];
    tempInput.toArray(newInput);
    
    input = newInput;

    // Special case, if no additional arguments are given, print contents of current directory
    if(input.length == 1) {
      
      Dir[] subDirectories = new Dir[currentWorkDir.getSubDirectories().size()];
      currentWorkDir.getSubDirectories().toArray(subDirectories);

      File[] files = new File[currentWorkDir.getFiles().size()];
      currentWorkDir.getFiles().toArray(files);
      
      // Print names of all directories in current directory
      for(Dir currDir : subDirectories) {
        //System.out.println(currDir.getDirName());
        out.storeString(currDir.getDirName() + "\n");
      }
      
      // Print names of all files in current directory
      for(File currFile : files) {
        //System.out.println(currFile.getFileName());
        out.storeString(currFile.getFileName() + "\n");
      }
      
      // If -R flag is present in ls command
      if(R_flag) {
        String currPath = currentWorkDir.getDirName();

        // Call this function again, with all children and updated paths
        for(Dir currDir: subDirectories) {
          printContentsOfDirectory(currDir, currPath + "/" + currDir.getDirName(), R_flag, out);
        }
      }
    }
    
    // Once we reach here, we know that we are given paths to directories/files
    String currPath = "";
    
    // The object at each path could be a file or a directory
    Dir possibleDir;
    File possibleFile;
    
    for(int i = 1; i < input.length; i++) {

      currPath = input[i];

      // If path of current object is absolute
      if(FileSystem.isAbsolutePath(currPath)) {
        possibleDir = root.getDirectoryByAbsolutePath(currPath);
        possibleFile = root.getFileByAbsolutePath(currPath);
        
      } else {
        possibleDir = root.getDirectoryByRelativePath(currentWorkDir, currPath);
        possibleFile = root.getFileByRelativePath(currentWorkDir, currPath);
      }
      
      // If the directory with the provided path exists, print all of its contents
      if(possibleDir != null) {
        if(currPath.substring(currPath.length()-1, currPath.length()).equals("/")) {    // Make the output more preeeeettyyyy
          currPath = currPath.substring(0, currPath.length()-1);    // Remove the "/" at the end
        }
        printContentsOfDirectory(possibleDir, currPath, R_flag, out);
      }
      
      // If file with given path exists and no "-R" flag is given, print its path
      if((possibleFile != null) && (!R_flag)) {
        //System.out.println(possibleFile.getFileName());
        out.storeString(currPath + "\n");
      }
      
      // If no file and no directory with given path exists, provide user with appropriate error message
      if(possibleFile == null && possibleDir == null) {
        System.err.println("Error: There is no file/directory with the provided path: " + currPath);
      }
      
      
    }
    //System.out.println("OUTPUT: ");
    out.printStored(currentWorkDir, filename, root);
    
  }
  
  private void printContentsOfDirectory(Dir currentWorkDir, String currPath, boolean R_flag,
      Output out) {
    Dir[] subDirectories = new Dir[currentWorkDir.getSubDirectories().size()];
    currentWorkDir.getSubDirectories().toArray(subDirectories);

    File[] files = new File[currentWorkDir.getFiles().size()];
    currentWorkDir.getFiles().toArray(files);

    // If name of current working dir is empty string, it means we're at the root
    //System.out.println((currentWorkDir.getDirName() == "" ? "/" : currPath) + ":"); 
    out.storeString("\n" + (currentWorkDir.getDirName() == "" ? "/" : currPath) + ":" + "\n");

    // Print names of all directories in current directory
    for(Dir currDir : subDirectories) {
      //System.out.print(currDir.getDirName() + " ");
      out.storeString(currDir.getDirName() + " ");
    }

    // Print names of all files in current directory
    for(File currFile : files) {
      //System.out.print(currFile.getFileName() + " ");
      out.storeString(currFile.getFileName() + " ");
    }
    //System.out.println(); // Print new empty line
    
    // If -R flag is present in ls command
    if(R_flag) {
      // Call this function again, with all children and updated paths
      for(Dir currDir: subDirectories) {
        out.storeString("\n");
        printContentsOfDirectory(currDir, currPath + "/" + currDir.getDirName(), R_flag, out);
      }
    }
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "ls [PATH ...]\nDisplays the content of the directory specified by PATH."
        + "if not PATH is given, displays the content of the current working directory";
  }
}
