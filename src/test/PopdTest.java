package test;

import static org.junit.Assert.*;
import org.junit.*;

import test.MockFileSystem;
import shellcommands.Popd;
import filesystem.Dir;

public class PopdTest {

  Dir currentDirectory;
  Dir parent;
  Dir child;
  MockFileSystem root;
  String[] args = {"popd"};
  Popd command;
  
  @Before
  public void setUp() {
    command = new Popd();
    root = new MockFileSystem();
    currentDirectory = root.getRoot();
    // create a parent directory and a child in it
    parent = new Dir("parent", "/");
    child = new Dir("child", parent.getDirPath());
    currentDirectory.addNewDirectory(parent);
    parent.addNewDirectory(child);
  }
  /**Test popping an empty stack */
  @Test
  public void popEmptyStack() {
    Dir result = command.executeCommand(currentDirectory, root, args);
    // check nothing changed
    assertEquals(result, null);
  }
  /**Test popping stack with one item */
  @Test
  public void popOneItem() {
    root.pushDir("/parent/child/");
    Dir result = command.executeCommand(root.getRoot(), root, args);
    assertEquals(result, child);
  }
  /**Test popping stack with multiple items */
  @Test
  public void popFromMultiple() {
    root.pushDir("/parent/child/");
    root.pushDir("/parent/");
    // check first pop
    Dir result = command.executeCommand(root.getRoot(), root, args);
    assertEquals(result, parent);
    // check second pop
    result = command.executeCommand(root.getRoot(), root, args);
    assertEquals(result, child);
  }
}
