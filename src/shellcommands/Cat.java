package shellcommands;

import filesystem.File;
import filesystem.FileSystem;
import test.FileSystemI;
import filesystem.Dir;
import java.util.ArrayList;

/** A class to represent the Cat command. */
public class Cat extends Command {

  /**
   * Display the contents of FILE1 and other files (i.e. File2 ....) concatenated
   * in the shell.
   * 
   * @param fnames           The files that are to be displayed on the shell
   * @param currentDirectory The current working directory of the user
   * @param root             A pointer to the root of the file system
   */
  public void executeCommand(String[] fnames, Dir currentDirectory, FileSystemI root) {
    ArrayList<File> files = new ArrayList<File>();
    Output out = new Output();
    String outputfile = "";
    out.setPlaceToPrint(checker(fnames));
    if(out.getPlaceToPrint() != 0) {
      outputfile = fnames[fnames.length-1];
      fnames = get_arguments(fnames);
    }
    for (int i = 0; i < fnames.length; i++) {
      // try catch here to see if file is valid
      String test = fnames[i];
      File toBeAdded = null;
      if (FileSystem.isAbsolutePath(test)) {
        toBeAdded = root.getFileByAbsolutePath(test);
      } else {
        toBeAdded = root.getFileByRelativePath(currentDirectory, test);
      }
      files.add(toBeAdded);
    }

    for (int i = 0; i < files.size(); i++) {
      if (files.get(i) != null){
        //System.out.println(files.get(i).getContents() + "\n");
        if(!(out.getPlaceToPrint() != 0 && i + 1 == files.size())) {
          out.storeString(files.get(i).getContents() + "\n");
        }
        else {
          out.storeString(files.get(i).getContents());
        }
      }
      else{
        System.err.println("ERROR; \"" + fnames[i] + "\" is not a valid file");
      }
      
      //if (i + 1 < files.size()) {
        //System.out.println("\n");
        //out.storeString("\n");
      //}
    }
    
    out.printStored(currentDirectory, outputfile, root);
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "cat FILE1 [FILE2 ...]\nDisplay the contents of FILE1 and other files "
        + " (i.e. File2 ....) concatenated in the shell.";
  }

}
