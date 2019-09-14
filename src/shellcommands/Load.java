package shellcommands;

import java.io.FileInputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import filesystem.Dir;
import filesystem.File;
import filesystem.FileSystem;
import test.FileSystemI;
import java.util.Stack;

/**A class to represent the Load shell command. This command will load a file system and all other
 * important data from a specified file on the user's computer.*/
public class Load extends Command{
  
  /**Load the state of File System from an XML file.
   * @param args The arguments given by the user
   * @param root A pointer to the File System object
   * @param tracker A pointer to the History stack that stores all user inputed data
   * */
  public void executeCommand(String[] args, FileSystemI root, History tracker) {
    
    // If insufficient arguments, display error and exit
    if(checker(args) != 0) {
      args = get_arguments(args);
    }
    if(args.length < 2) {
      System.err.println("Error: Insufficient arguments given.");
      return;
    } else if((tracker.hasCommands() > 1)) {
      System.err.println("Error: You cannot use \"load\" command after running any other commands.");
      return;
    }
    
    // Try to open the file specified by the user
    try {
      FileInputStream file = new FileInputStream(args[1]);
      
      // Instantiate the readers for XML file
      XMLInputFactory xmlFact = XMLInputFactory.newInstance();
      XMLStreamReader xmlReader = xmlFact.createXMLStreamReader(file);
      
      // Clear all data in the history stack
      tracker.clearArguments();
      
      // Helper function to parse XML file
      createFileSystemFromXML(xmlReader, root.getRoot());
      
      // Helper function to load the History data in our XML
      parseHistory(xmlReader, tracker);
      
      // Helper function to load the directory stack from XML file
      parseDirectoryStack(xmlReader, root);
      
      // Close all writing streams
      xmlReader.close();
      file.close();
      
    } catch(Exception e) {
      System.err.println("Error: Unable to read from file " + args[1] + "; it may be corrupt or may not exist.");
      return;
    }
  }
  
  /**Given a stream to an XML file, re-build the entire File System.
   * @param reader A stream to the XML reader.
   * @param currDir The starting directory; <b><i>must be the root</i></b>
   *  */
  private void createFileSystemFromXML(XMLStreamReader reader, Dir currDir) {
    // Stack to hold what level in the File System we're at
    
    try {
      while(reader.hasNext()) {
        // If we are beginning a new element
        if(reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
          QName name = reader.getName();

          // If this element is of type Directory
          if(name.toString().equals("Directory")) {
            Dir tempDir = new Dir(reader.getAttributeValue(null, "name"), currDir.getDirPath());
            currDir.addNewDirectory(tempDir);
            
            // Go inside this newly created directory and continue
            currDir = tempDir;

          } else if(name.toString().equals("File")) {
            File tempFile = currDir.addNewFile(reader.getAttributeValue(null, "name"));
            tempFile.setContent(reader.getAttributeValue(null, "contents"));

          }else if(name.toString().equals("History")) {  // If we've reached the end of all files/directories, exit
            return;
          }

        } else if(reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
          QName name = reader.getName();
          if(name.toString().equals("Directory")){
            // We've reached the end, go up a directory
            currDir = currDir.getParent();
          }
        }

        reader.next();
      }

    } catch (XMLStreamException e) {
      System.err.println("Error: Unable to fully re-build File System. The specified file may be corrupt.");
    }
  }
  
  /**A function to populate the History stack of File System. 
   * @param reader A stream to the XML reader.
   * @param tracker A pointer to the History object that File System uses.
   * */
  private void parseHistory(XMLStreamReader reader, History tracker) {
    try {
      while(reader.hasNext()) {
        // If we are beginning a new element
        if(reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
          QName name = reader.getName();

          // If current tag is the <UserInput...> tag, then continue
          if(name.toString().equals("UserInput")) {
            String tempHistoryEntry = reader.getAttributeValue(null, "value");  // Store current history entry
            
            // Trim all the numbers and other formatting from the front
            int startingIndex = tempHistoryEntry.indexOf(" ");
            tracker.addArgument(tempHistoryEntry.substring(startingIndex+1, tempHistoryEntry.length()));
          }
        }
        // Otherwise, if we've reached and end element and it is the </History> tag, then exit the function
        else if(reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
          QName name = reader.getName();
          if(name.toString().equals("History")) {
            return;
          }
        }
        reader.next();
      }

    } catch (XMLStreamException e) {
      System.err.println("Error: Unable to fully re-build File System. The specified file may be corrupt.");
    }
  }
  
  public void parseDirectoryStack(XMLStreamReader reader, FileSystemI root) {
    
    // The stack of directories contained in FileSystem
    Stack<String> tempStack = root.getDirectoryStack();

    try {
      while(reader.hasNext()) {
        // If we are beginning a new element
        if(reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
          QName name = reader.getName();

          // If current tag is the <DirectoryPath...> tag, then continue
          if(name.toString().equals("DirectoryPath")) {
            String currItem = reader.getAttributeValue(null, "value");
            tempStack.push(currItem);   // Push the item to the stack of directories
          }
        }
        reader.next();
      }

    } catch (XMLStreamException e) {
      System.err.println("Error: Unable to fully re-build File System. The specified file may be corrupt.");
    }
  }
}
