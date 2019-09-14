package test;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shellcommands.History;
import shellcommands.Load;

/**
 * A class to test the "load" shell command
 * */
public class LoadTest {

  // Initialize new and default output streams
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  
  // Private variables for testing
  private MockFileSystem root;
  private Load command;
  History tracker;

  /**
   * Setup required data before running all the tests.
   */
  @Before
  public void setUp(){
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    
    root = new MockFileSystem();
    command = new Load();
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
   * Do not specify a load directory for the File System.
   * */
  @Test
  public void loadWithNoArguments() {
    root.clearAllData();

    command.executeCommand(new String[] {"load"}, root, tracker);
    assertEquals("Error: Insufficient arguments given."+System.getProperty("line.separator"),
        errContent.toString());
  }
  
  /**
   * Load the FileSystem with an invalid path on the user's computer.
   * */
  @Test
  public void loadWithInvalidPath() {
    root.clearAllData();
    
    command.executeCommand(new String[] {"load", "path/does/not/exist/hopefully?"}, root, tracker);
    System.out.println(errContent.toString());
    assertEquals("Error: Unable to read from file path/does/not/exist/hopefully?; "
        + "it may be corrupt or may "
        + "not exist."+System.getProperty("line.separator"),
        errContent.toString());
  }
  
  /**
   * Load an empty FileSystem object.
   * */
  @Test
  public void loadEmptyFileSystem() {
    root.clearAllData();
    
    command.executeCommand(new String[] {"load", "src/test/sampleFileSystems/"
        + "emptyFileSystem.txt"}, 
        root, tracker);
    
    // Print all the contents of the Mock file system
    System.out.println(root.toString());
    
    assertEquals("/\n"+System.getProperty("line.separator"), outContent.toString());
  }
  
  /**
   * Load FileSystem with many nested directories and files and history data
   * */
  @Test
  public void loadFileSystemWithManyDirectories() {
    root.clearAllData();

    command.executeCommand(new String[] {"load", "src/test/sampleFileSystems/"
        + "sampleFileSystem.txt"}, 
        root, tracker);
    String expected = "/\n" + 
        "\n" + 
        "\tf\n" + 
        "\t\tf1\n" + 
        "\t\tf2\n" + 
        "\t\t\tf3\n" + 
        "\t\t\tfile2\n" + 
        "\t\tfile1\n" + 
        "\tt\n" + 
        "\t\tt1\n" + 
        "\t\t\tt2\n" + 
        "\t\t\t\tt3\n" + 
        "\t\t\t\t\tfile3\n" + 
        "\t\t\t\tfile4\n" + 
        "\tfileAtRoot" + System.getProperty("line.separator");
    
    System.out.println(root.toString());
    assertEquals(expected, outContent.toString());
  }
  
  /**
   * Load FileSystem with data in the directory stacked (created by the "pushd" and "popd" shell commands).
   * */
  @Test
  public void loadFileSystemWithManyItemsInDirectoryStack() {
    root.clearAllData();
    command.executeCommand(new String[] {"load", "src/test/sampleFileSystems/"
        + "FileSystemWithDirectoryStack.txt"}, 
        root, tracker);
    
    // Previous test confirmed that the files/directories already exist, so we now compare stack data
    
  }

}
