package test;

import test.MockFileSystem;
import filesystem.Dir;
import shellcommands.Find;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FindTest {
  
  Dir currentDirectory;
  Dir parent;
  Dir child;
  Dir child2;
  MockFileSystem root;
  Find command;
  
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  private static String errl = System.getProperty("line.separator");
  private static String outl = "\n";
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

  }

  @Before
  public void setUp() throws Exception {
    root = new MockFileSystem();
    command = new Find();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
    currentDirectory = root.getRoot();
    parent = new Dir("parent", "/");
    currentDirectory.addNewDirectory(parent);
    child = new Dir("d1", parent.getDirPath());
    parent.addNewDirectory(child);
    parent.addNewFile("f1");
    parent.addNewFile("f2");
    child2 = new Dir("d2",child.getDirPath());
    child.addNewDirectory(child2);
    child.addNewFile("f3");
    child.addNewFile("f4");
    child2.addNewFile("f5");
  }

  @After
  public void tearDown() throws Exception {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }
  @Test //Note cannot find a directory you are already in
  public void rootCheck() {
    command.executeCommand(new String[] {"find","-type","d","-name","\"\\\""}, root, currentDirectory);
    assertEquals("",outContent.toString());
  }
  
  @Test
  public void nothing() {
    command.executeCommand(new String[] {"find","-type","f","-name","\"f\""}, root, currentDirectory);
    assertEquals("",outContent.toString());
    command.executeCommand(new String[] {"find","-type","d","-name","\"f3\""}, root, currentDirectory);
    assertEquals("",outContent.toString());
    command.executeCommand(new String[] {"find","-type","d","-name","\"f1\""}, root, currentDirectory);
    assertEquals("",outContent.toString());
  }
  @Test
  public void errorTest() {
    command.executeCommand(new String[] {"find","-type","f","-name","f1"}, root, currentDirectory);
    assertEquals("Error: Invalid expression was inputted."+errl,
        errContent.toString());
    errContent.reset();
    command.executeCommand(new String[] {"find","-type","e","-name","nothing"}, root, currentDirectory);
    assertEquals("Error: Invalid type to look for." + errl,
        errContent.toString());
    errContent.reset();
    command.executeCommand(new String[] {"find","-type","d","-name"}, root, currentDirectory);
    assertEquals("Error: Not enough arguments"
    + errl,errContent.toString());
    errContent.reset();
    command.executeCommand(new String[] {"find","-type","d","-name","d1","\\"},
        root, currentDirectory);
    assertEquals("Error additional arguments after expression or type"
    + errl,errContent.toString());
  }
  
  @Test
  public void SingleOutputNoPaths() {
    command.executeCommand(new String[] {"find","-type","f","-name","\"f1\""}, root, currentDirectory);
    assertEquals("/parent/f1"+outl,
        outContent.toString());
    outContent.reset();
    command.executeCommand(new String[] {"find","-type","d","-name","\"d2\""}, root, currentDirectory);
    assertEquals("/parent/d1/d2"+outl,
        outContent.toString());
    outContent.reset();
    command.executeCommand(new String[] {"find","-type","f","-name","\"f2\""},
        root, currentDirectory);
    assertEquals("/parent/f2"+outl,outContent.toString());
  }
  
  @Test
  public void MultipleOutputNoPaths() {
    child.addNewFile("f2");
    child2.addNewFile("f2");
    command.executeCommand(new String[] {"find","-type","f","-name","\"f2\""},
        root, currentDirectory);
    assertEquals("/parent/f2"+outl+"/parent/d1/f2"+outl+"/parent/d1/d2/f2"+outl,outContent.toString());
    outContent.reset();
    Dir add = new Dir("d2", root.getRoot().getDirPath());
    currentDirectory.addNewDirectory(add);
    command.executeCommand(new String[] {"find","-type","d","-name","\"d2\""}, root, currentDirectory);
    assertEquals("/d2"+outl+"/parent/d1/d2"+outl,outContent.toString());
  }
  
  @Test //Includes cases of no output
  public void SingleOutputPathsGiven() {
    command.executeCommand(new String[] {"find","/parent/","-type","f","-name","\"f5\""}, root, currentDirectory);
    assertEquals("/parent/d1/d2/f5"+outl,
        outContent.toString());
    outContent.reset();
    command.executeCommand(new String[] {"find","/parent/d1/","-type","f","-name","\"f1\""}, root, currentDirectory);
    assertEquals("",outContent.toString());
    outContent.reset();
    child2.addNewDirectory(new Dir("d3",child2.getDirPath()));
    command.executeCommand(new String[] {"find","/parent/","-type","d","-name","\"d3\""},
        root, currentDirectory);
    assertEquals("/parent/d1/d2/d3"+outl,outContent.toString());
  }
  
  @Test //Includes Errors
  public void MultipleOutputPathsGiven() {
    
  }

}
