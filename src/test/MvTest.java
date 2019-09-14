package test;

import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.*;

import shellcommands.Mv;
import filesystem.Dir;
import filesystem.FileSystem;
import filesystem.File;

public class MvTest {

  Dir currentDirectory;
  Dir parent;
  Dir child;
  FileSystem root;
  Mv command;
  
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  
  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    /* setUp FileSystem to this tree
     * /    parent          child         file
     *      relativeParent  relativeChild relativeFile */
    command = new Mv();
    root = FileSystem.createFileSystem();
    currentDirectory = root.getRoot();
    // create a parent directory and a child in it
    parent = new Dir("parent", "/");
    child = new Dir("child", parent.getDirPath());
    parent.addNewDirectory(child);
    child.addNewFile("file");
    currentDirectory.addNewDirectory(parent);
    
  }
  
  @After
  public void tearDown() {
      System.setOut(originalOut);
      System.setErr(originalErr);
  }
  /**Test moving a child to a parent dir */
  @Test
  public void moveChildToParent() {
    command.executeCommand("/parent/child/", "/", root, currentDirectory);
    assertTrue(root.getDirectoryByAbsolutePath("/child") != null);
  }
  
  /**Test moving a dir to a location with a dir with the same name */
  @Test
  public void moveToDirWithSameName() {
    Dir sameName = new Dir("child", "/");
    currentDirectory.addNewDirectory(sameName);
    command.executeCommand("/parent/child/", "/", root, currentDirectory);
    currentDirectory = currentDirectory.getSubDirectoryByName("child");
    // check child directory with file is in root
    assertTrue(currentDirectory.getFileByName("file") != null);
  }
  /**Test moving a file to a location with a file with the same name */
  @Test
  public void moveToFileWithSameName() {
    currentDirectory.addNewFile("file");
    Dir dirWithFile = currentDirectory.getSubDirectoryByName("child");
    File fileWithContents = dirWithFile.getFileByName("file");
    fileWithContents.setContent("content");
    command.executeCommand("file", "child/file", root, currentDirectory);
    File fileInRoot = currentDirectory.getFileByName("file");
    // content in file in root is removed
    assertTrue(fileInRoot.getContents().equals(""));
  }
}
