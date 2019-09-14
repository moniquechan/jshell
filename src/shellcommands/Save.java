package shellcommands;
import java.io.FileOutputStream;
import java.util.Stack;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import filesystem.Dir;
import filesystem.File;
import filesystem.FileSystem; 
import test.FileSystemI;


/**A class to represent he Save command. Given a save location, this command will write all the contents of the 
 * File System to an actual file on the user's computer.*/
public class Save extends Command{
  
  /**Save the current state of File System into an XML file.
   * @param args The arguments given by the user
   * @param root A pointer to the File System object
   * @param tracker A pointer to the History stack that stores all user inputed data
   * */
  public void executeCommand(String args[], FileSystemI root, History tracker) {
    
    // If only one argument is given ("save" on its own), then display error and exit
    if(checker(args) != 0) {
      args = get_arguments(args);
    }
    if(args.length < 2) {
      System.err.println("Error: Insufficient Arguments.");
      return;
    }
    
    // Try to open the file specified by the user
    try {
      FileOutputStream file = new FileOutputStream(args[1]);
      
      // Instantiate XML writing API
      XMLOutputFactory xmlOut = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlWriter = xmlOut.createXMLStreamWriter(file, "UTF-8");
      
      // Headers for XML document
      xmlWriter.writeStartDocument("UTF-8", "1.0");
      xmlWriter.writeStartElement("FileSystem");
      
      // Helper function to write all files/directories in the file system
      writeFileSystemToFile(xmlWriter, root.getRoot());
      
      // Helper function to write all History data to XML
      addHistoryToXML(xmlWriter, tracker);
      
      // Helper function to write all Stack directory data to XML
      addStackDirToXML(xmlWriter, root);
      
      // End the <FileSystem> tag
      xmlWriter.writeEndElement();

      // End document header and flush the writer
      xmlWriter.writeEndDocument();
      xmlWriter.flush();
      
      // Close all writers when we are done
      xmlWriter.close();
      file.close();
      
    } catch(Exception e) {
      System.err.println("Error: Unable to write to the file " + args[1]);
      //System.err.println(e.getMessage());
      return;
    }
  }
  
  /**Add all the directories and files in the File System to a file on the computer.
   * @param writer The XML output stream used to write to a file
   * @param currDir The starting directory, must be the root of the File System
   * */
  private void writeFileSystemToFile(XMLStreamWriter writer, Dir currDir) {
    
    Dir[] subDirs = new Dir[currDir.getSubDirectories().size()];
    File[] files = new File[currDir.getFiles().size()];
    
    currDir.getSubDirectories().toArray(subDirs);
    currDir.getFiles().toArray(files);
    
    // Store names of all sub-directories
    for (int i = 0; i < subDirs.length; i++) {
      try {
        
        // Create starting <Directory> element for the current directory
        writer.writeStartElement("Directory");
        writer.writeAttribute("name", subDirs[i].getDirName());

        // Recursive call: Call this function again with its children
        writeFileSystemToFile(writer, subDirs[i]);
        
        // End the <Directory> tag
        writer.writeEndElement();
        
      } catch (XMLStreamException e) {
        System.err.println("Error: Unable to completely write the File System to the specified file. Please try again.");
        return;
      }
    }
    
    /*Repeat same process as above, but for all the files*/
    for (int i = 0; i < files.length; i++) {
      try {

        // Write all file information
        writer.writeStartElement("File");   // <File> element
        writer.writeAttribute("name", files[i].getFileName());  // Write name of file
        writer.writeAttribute("contents", files[i].getContents()); // Write all contents of file
        writer.writeEndElement();   // End File element </File>
        
      } catch (XMLStreamException e) {
        System.err.println("Error: Unable to completely write the File System to the specified file. Please try again.");
        return;
      }
    }
    
  }
  
  /**Add the history of user input into the XML file.
   * @param writer The XML output stream used to write to a file
   * @param tracker The object containing the entire history of what the user has inputed
   * */
  private void addHistoryToXML(XMLStreamWriter writer, History tracker) {
    String[] history = tracker.getAllCommands().split("\n");    // Store all the user inputed data
    
    
    try {

      // <History> staring element
      writer.writeStartElement("History");
      
      for(int i = 0; i < history.length - 1; i++) {
        writer.writeStartElement("UserInput");      // <UserInput> start tag
        writer.writeAttribute("value", history[i]); // <UserInput value="..."> tag
        writer.writeEndElement();                   // </UserInput> end tag
      }
      
      writer.writeEndElement();
      
    } catch (XMLStreamException e) {
      System.err.println("Error: Unable to completely write the File System to the specified file. Please try again.");
      return;
    }
  }
  
  /**Write all of the stack data generated by the "pushd" command to XML.
   * @param writer The XML output stream used to write to a file
   * @param root Pointer to the FileSystem object that contains the stack in question
   * */
  private void addStackDirToXML(XMLStreamWriter writer, FileSystemI root) {
    
    // The stack of directories contained in FileSystem
    Stack<String> tempStack = root.getDirectoryStack();

    try {

      // <History> staring element
      writer.writeStartElement("DirectoryStack");
      
      // For every element in the stack
      for(String currDir : tempStack) {
        writer.writeStartElement("DirectoryPath"); // <DirectoryPath> start tag
        writer.writeAttribute("value", currDir);    // Attribute being written is the directory path
        writer.writeEndElement();                   // </DirectoryPath> end tag
      }
      
      writer.writeEndElement();
      
    } catch (XMLStreamException e) {
      System.err.println("Error: Unable to completely write the File System to the specified file. Please try again.");
      return;
    }
  }
}
