package test;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.*;

import filesystem.Dir;
import shellcommands.Pwd;

public class PwdTest {
  
  private MockFileSystem root;
  private Dir currentDirectory;
  private Dir childDir;
  private Dir parent;
  private Pwd command;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @Before
  public void setUp() {
      System.setOut(new PrintStream(outContent));
      System.setErr(new PrintStream(errContent));
      // setup
      root = new MockFileSystem();
      command = new Pwd();
      currentDirectory = root.getRoot();
      // create a parent directory and a child in it
      parent = new Dir("parent", "/");
      childDir = new Dir("child", parent.getDirPath());
      parent.addNewDirectory(childDir);
      currentDirectory.addNewDirectory(parent);
  }

  @After
  public void tearDown() {
      System.setOut(originalOut);
      System.setErr(originalErr);
  }
  
  /**Print working directory if it's the root*/
  @Test
  public void pwdRoot() {
    String[] args = {"pwd"};
    command.executeCommand(args, currentDirectory, root);
    assertEquals("/" + System.getProperty("line.separator"), outContent.toString());
  }
  
  /**change directory then pwd */
  @Test
  public void moveToNewDirectory() {
    String[] args = {"pwd"};
    currentDirectory = parent;
    command.executeCommand(args, currentDirectory, root);
    assertEquals("/parent/" + System.getProperty("line.separator"), outContent.toString());
  }
  
  /**Test with invalid input */
  @Test
  public void testExtraArgs() {
    String[] args = {"pwd", "child"};
    currentDirectory = parent;
    command.executeCommand(args, currentDirectory, root);
    assertEquals("/parent/" + System.getProperty("line.separator"), outContent.toString());
  }
}