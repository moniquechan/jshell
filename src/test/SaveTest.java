/**
 * 
 */
package test;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import filesystem.Dir;
import shellcommands.History;
import shellcommands.Save;

/**
 * A class to test the "save" shell command.
 *
 */
public class SaveTest {

  // Initialize new and default output streams
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  
  // Private variables for testing
  private MockFileSystem root;
  private Save command;
  History tracker;

  /**
   * Setup required data before running all the tests.
   */
  @Before
  public void setUp(){
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    
    root = new MockFileSystem();
    command = new Save();
    tracker = root.getHistoryOfCommands();
  }
  
  /**
   * Restore the original system.out and system.err streams
   */
  @After
  public void restoreStreams() {
      System.setOut(originalOut);
      System.setErr(originalErr);
  }
  
  /**
   * Do not specify a save directory for the File System.
   * */
  @Test
  public void saveWithNoArguments() {
    command.executeCommand(new String[] {"save"}, root, tracker);
    assertEquals("Error: Insufficient Arguments."+System.getProperty("line.separator"),
        this.errContent.toString());
  }
  
  /**
   * Save the FileSystem with an invalid path on the user's computer.
   * */
  @Test
  public void saveWithInvalidPath() {
    command.executeCommand(new String[] {"save", "path/does/not/exist/hopefully?"}, root, tracker);
    System.out.println(errContent.toString());
    assertEquals("Error: Unable to write to the file path/does/not/exist/hopefully?"+System.getProperty("line.separator"),
        errContent.toString());
  }
  
  /**
   * Save an empty FileSystem object
   * */
  @Test
  public void saveEmptyFileSystem() {
    command.executeCommand(new String[] {"save", "src/test/sampleFileSystems/emptyFileSystem.txt"}, root, tracker);
    
    // Assert the files at both expected and actual location are identical
    assertTrue(compareFiles("src/test/sampleFileSystems/emptyFileSystemEXPECTED.txt", 
        "src/test/sampleFileSystems/emptyFileSystem.txt"));
  }
  
  /**
   * Save FileSystem with many nested directories and files and history data
   * */
  @Test
  public void saveFileSystemWithManyDirectories() {
    
  }
  
  /**
   * Save FileSystem with data in the directory stacked (created by the "pushd" and "popd" shell commands).
   * */
  @Test
  public void saveFileSystemWithManyItemsInDirectoryStack() {
    
  }
  
  /**
   * Helper function to compare contents of two files
   * @param expected A path to the file containing expected data at each line.
   * @param actual A path to the file containing ACTUAl data at each line.
   * @return True iff both files are identical, False otherwise
   * */
  private boolean compareFiles(String expected, String actual) {
    Path expectedPath = Paths.get(expected);
    Path actualPath = Paths.get(actual);
    
    ArrayList<String> expectedData;
    ArrayList<String> actualData;

    // Try to retrieve the files at given path
    try {
      expectedData = (ArrayList<String>) Files.readAllLines(expectedPath);
      actualData = (ArrayList<String>) Files.readAllLines(actualPath);

    } catch (IOException e) {

      // If error occurs, simply return false
      return false;
      //e.printStackTrace();
    }
    
    // Sort the two arrays, in case the order was changed during processing
    Collections.sort(expectedData);
    Collections.sort(actualData);
    
    // True if and only if both lists have same data in same order
    return expectedData.equals(actualData);
  }

}
