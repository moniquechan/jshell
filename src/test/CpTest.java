package test;

import static org.junit.Assert.*;
import org.junit.*;

import shellcommands.Cp;
import filesystem.Dir;
import filesystem.FileSystem;
import filesystem.File;

public class CpTest {

  Dir currentDirectory;
  Dir parent;
  Dir child;
  FileSystem root;
  Cp command;
  
  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    /* setUp FileSystem to this tree
     * /    parent          child         file
     *      relativeParent  relativeChild relativeFile */
    command = new Cp();
    root = FileSystem.createFileSystem();
    currentDirectory = root.getRoot();
    // create a parent directory and a child in it
    parent = new Dir("parent", "/");
    child = new Dir("child", parent.getDirPath());
    parent.addNewDirectory(child);
    child.addNewFile("file");
    currentDirectory.addNewDirectory(parent);
    
  }

  /**Test copying a child dir to a parent dir */
  @Test
  public void copyChildToParent() {
    boolean result = command.executeCommand("/parent/child/", "/", root, currentDirectory);
    assertEquals(result, true);
    assertTrue(root.getDirectoryByAbsolutePath("/child") != null);
  }
  
  /**Test copying a parent dir to child dir */
  @Test
  public void parentToChild() {
    boolean result = command.executeCommand("/", "/parent/child", root, currentDirectory);
    //copy failed
    assertFalse(result);
  }
  /**Test copying to a file location */
  @Test
  public void copyToFileLocation() {
    boolean result = command.executeCommand("/", "/parent/child/file", root, currentDirectory);
    //copy failed
    assertFalse(result);
  }
  /**Test copying a dir to a location with a dir with the same name */
  @Test
  public void copyToDirWithSameName() {
    Dir sameName = new Dir("child", "/");
    currentDirectory.addNewDirectory(sameName);
    command.executeCommand("/parent/child/", "/", root, currentDirectory);
    currentDirectory = currentDirectory.getSubDirectoryByName("child");
    // check child directory with file is in root
    assertTrue(currentDirectory.getFileByName("file") != null);
  }
  /**Test copying a file to a location with a file with the same name */
  @Test
  public void copyToFileWithSameName() {
    currentDirectory.addNewFile("file");
    File fileWithContents = child.getFileByName("file");
    fileWithContents.setContent("content");
    command.executeCommand("parent/child/file", "/", root, currentDirectory);
    File fileInRoot = currentDirectory.getFileByName("file");
    assertTrue(fileInRoot.getContents().equals("content"));
  }
  /**Test with invalid input */
  @Test
  public void invalidArguments() {
    boolean result = command.executeCommand("invalid/path", "/", root, currentDirectory);
    assertFalse(result);
  }
}
