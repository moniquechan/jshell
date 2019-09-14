package shellcommands;

import test.FileSystemI;
import filesystem.Dir;
import filesystem.File;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;

/** A class to represent the Get command. */
public class Get extends Command {

  /**
   * Gets a file from given URL and add its to the current working directory
   * 
   * @param url The location of the file represented by a url
   * @param currentDirectory The current working directory of the user
   * @param root             A pointer to the root of the file system
   */
  public void executeCommand(String[] args, Dir currentDirectory, FileSystemI root) {
    String returnString = "";
    if(checker(args) != 0) {
      args = get_arguments(args);
    }
    if(args.length != 1) {
      return;
    }
    String url = args[0];
    try {
        URL theURL = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(theURL.openStream()));
        String line;
        while ((line = in.readLine()) != null) {
            returnString = returnString + line + "\n";
        }
        in.close();
         
    }
    catch (MalformedURLException e) {
        System.out.println("Malformed URL: " + e.getMessage());
    }
    catch (IOException e) {
        System.out.println("I/O Error: " + e.getMessage());
    }

    String [] fileNameArray = url.split("/");

    String fileName = fileNameArray[fileNameArray.length-1];
    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
    System.out.println(fileName);
    File newFile = currentDirectory.addNewFile(fileName);
    newFile.setContent(returnString.trim());
  }

  /**Override the toString() method for object.
   * 
   * @return A string that describes the class
   */
  public String toString() {
    return "get URL\nRetrieve the file at that URL and add it to the current working directory.";
  }

}
