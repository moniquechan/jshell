package test;

import static org.junit.Assert.*;
import org.junit.*;

import shellcommands.Pushd;
import filesystem.Dir;

public class PushdTest {

  Dir currentDirectory;
  Dir parent;
  Dir childDir;
  Dir diffDir;
  MockFileSystem root;
  String[] args = {"popd"};
  Pushd command;
  
  @Before
  public void setUp() {
    root = new MockFileSystem();
    command = new Pushd();
    currentDirectory = root.getRoot();
    // create a parent directory and a child in it
    parent = new Dir("parent", "/");
    childDir = new Dir("child", parent.getDirPath());
    diffDir = new Dir("different", "");
    parent.addNewDirectory(childDir);
    currentDirectory.addNewDirectory(parent);
    currentDirectory.addNewDirectory(diffDir);
  }
  
  @Test
  public void pushInvalid() {
    String[] args = {"pushd", "INVALIDPATH"};
    Dir result = command.executeCommand(args, currentDirectory, root);
    assertEquals(result, null);
  }
  
  @Test
  public void pushValidSameDir() {
    String[] args = {"pushd", "/parent/child"};
    Dir result = command.executeCommand(args, currentDirectory, root);
    assertEquals("/parent/child/", result.getDirPath());
  }
  
  @Test
  public void pushValidDiffDir() {
    String[] args = {"pushd", "different"};
    Dir result = command.executeCommand(args, currentDirectory, root);
    assertEquals(diffDir, result);
  }
  
}
