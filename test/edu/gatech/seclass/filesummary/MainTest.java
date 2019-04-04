package edu.gatech.seclass.filesummary;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/*
This test class was developed for Georgia Tech's CS6300 course.
This class and the associated assignment should not be posted in any public repository, per the honor code.
DO NOT ALTER THIS CLASS.  Use it as an example for MyMainTest.java
 */

public class MainTest {

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
    *  TEST UTILITIES
    */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file =  createTmpFile();

        OutputStreamWriter fileWriter =
                     new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        fileWriter.write(input);

        fileWriter.close();
        return file;
    }


    //Read File Utility
    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
	
	/*
	* TEST FILE CONTENT
	*/
	private static final String FILE1 = "1 dog" + System.lineSeparator() + "2 cat";
	private static final String FILE2 = "Log: 123 abc\nError: 123 xyz\nError: 567 abc\nLog: 567 abc";
	private static final String FILE3 = "Up with the white and gold\rDown with the red and black";
    private static final String FILE4 = "";
    private static final String FILE5 = " ";
    private static final String FILE6 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String FILE7 = "Let's try some **special**  %!(characters)!% ###\n" +
            "and line breaks^$@ \r" +
            "of \\different// types; \n" +
            "in 1 file\r"+
            ":-)";
    private static final String FILE8 = "Up with the white and gold\r" +
            "Down with the red and black\r" +
            "Georgia Tech is out for a victory\r" +
            "Well drop a battle axe on georgia's head\r" +
            "When we meet her our team is sure to beat her\r" +
            "Down on the old farm there will be no sound\r" +
            "'Till our bow wows rips through the air\r" +
            "When the battle is over georgia's team will be found\r" +
            "With the Yellow Jacket's swarming 'round! Hey!";
    private static final String FILE9 = ".*";
    private static final String FILE10 = "Howdy Billy," + System.lineSeparator() +
            "I am going to take cs6300 and cs6400 next semester."  + System.lineSeparator() +
            "Did you take cs 6300 last semester? I want to"  + System.lineSeparator() +
            "take 2 courses so that I will graduate Asap!";

    private static final String FILE11 = "\n";
    private static final String FILE12 = "Howdy Billy," + System.lineSeparator() + System.lineSeparator()+
            "I am going to take cs6300 and cs6400 next semester."  + System.lineSeparator() +
            "Did you take cs 6300 last semester? I want to"  + System.lineSeparator() + System.lineSeparator()+
            "take 2 courses so that I will graduate Asap!" + System.lineSeparator();
    private static final String FILE13 = "ABCDEF\nghijkl\rMNOPQR";
    private static final String FILE14 = "abcdef\rGHIJKL\nmnopqr";
    private static final String FILE15 = " \n" + "a\n" + "!\n" + "1\n" + "A";
	

    // test cases

    /*
    *   TEST CASES
    */

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 1 from assignment directions
    @Test
    public void mainTest1() throws Exception {
        File inputFile1 = createInputFile(FILE1);

        String args[] = {inputFile1.getPath()};
        Main.main(args);

        String expected1 = FILE1;

        String actual1 = getFileContent(inputFile1.getPath());

        assertEquals("The files differ!", expected1, actual1);
        assertEquals("4", outStream.toString().trim());
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 2 from assignment directions
    @Test
    public void mainTest2() throws Exception {
        File inputFile1 = createInputFile(FILE1);

        String args[] = {"-a", "2", "-s", "d1atc", inputFile1.getPath()};
        Main.main(args);

        String expected2 = "2 cat" + System.lineSeparator() + "1 dog";

        String actual2 = getFileContent(inputFile1.getPath());

        assertEquals("The files differ!", expected2, actual2);
        assertEquals("cat", outStream.toString().trim());
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 3 from assignment directions
    @Test
    public void mainTest3() throws Exception {
        File inputFile2 = createInputFile(FILE1);

        String args[] = {"-r", "1", inputFile2.getPath()};
        Main.main(args);

        String expected3 = "2 cat";

        String actual3 = getFileContent(inputFile2.getPath());

        assertEquals("The files differ!", expected3, actual3);
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Instructor example 4 from assignment directions
    @Test
    public void mainTest4() throws Exception {
        File inputFile3 = createInputFile(FILE1);

        String args[] = {"-k", "2", "-s", "dog", "-a", inputFile3.getPath()};
        Main.main(args);

        String expected4 = "2 cat";

        String actual4 = getFileContent(inputFile3.getPath());

        assertEquals("The files differ!", expected4, actual4);
        assertEquals("dog", outStream.toString().trim());
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Extra example
    @Test
    public void mainTest5() throws Exception {
        File inputFile5 = createInputFile(FILE2);

        String args[] = {"-a", "-s", "orEO", inputFile5.getPath()};
        Main.main(args);

        String expected5 = "Error: 567 abc\nError: 123 xyz\nLog: 123 abc\nLog: 567 abc";

        String actual5 = getFileContent(inputFile5.getPath());

        assertEquals("The files differ!", expected5, actual5);
        assertEquals("Error", outStream.toString().trim());
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Extra example
    @Test
    public void mainTest6() throws Exception {
        File inputFile6 = createInputFile(FILE2);

        String args[] = {"-k", "abc", inputFile6.getPath()};
        Main.main(args);

        String expected6 = "Log: 123 abc\nError: 567 abc\nLog: 567 abc";

        String actual6 = getFileContent(inputFile6.getPath());

        assertEquals("The files differ!", expected6, actual6);
    }


    // Purpose: To provide an example of a test case format (no arguments passed)
    // Frame #: Instructor error example
    @Test
    public void mainTest7() {
        //no arguments on the command line will pass an array of length 0 to the application, not null.
        String args[]  = new String[0];
        Main.main(args);
        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Extra example
    @Test
    public void mainTest8() throws Exception {
        File inputFile8 = createInputFile(FILE3);

        String args[] = {"-r", "red", inputFile8.getPath()};
        Main.main(args);

        String expected8 = "Up with the white and gold";

        String actual8 = getFileContent(inputFile8.getPath());

        assertEquals("The files differ!", expected8, actual8);
    }

    // Purpose: To provide an example of a test case format
    // Frame #: Extra example
    @Test
    public void mainTest9() throws Exception {
        File inputFile9 = createInputFile(FILE3);

        String args[] = {"-k", "with", "-s", "with e", inputFile9.getPath()};
        Main.main(args);

        String expected9 = FILE3;

        String actual9 = getFileContent(inputFile9.getPath());

        assertEquals("The files differ!", expected9, actual9);
	    assertEquals(" with the white ".trim(), outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest10() {

        String args[] = {"nosuchfile.txt"};
        Main.main(args);
        assertEquals("File Not Found", errStream.toString().trim());

    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest11() throws Exception {
        File inputFile = createInputFile(FILE3);

        String args[] = {"-k", "with", "-r", "with", inputFile.getPath()};
        Main.main(args);

        String expected = FILE3;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest12() throws Exception {
        File inputFile = createInputFile(FILE4);

        String args[] = {"-a", "-s", "abc", inputFile.getPath()};
        Main.main(args);

        String expected = FILE4;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest13() throws Exception {
        File inputFile = createInputFile(FILE6);

        String args[] = {inputFile.getPath()};
        Main.main(args);

        String expected = FILE6;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("1", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest14() throws Exception {
        File inputFile = createInputFile(FILE7);

        String args[] = {"-k", "s" , "2",inputFile.getPath()};
        Main.main(args);

        String expected = "and line breaks^$@ \r" +
                "of \\different// types; ";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest15() throws Exception {
        File inputFile = createInputFile(FILE8);

        String args[] = {"-k", "", "2", "-s", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", inputFile.getPath()};
        Main.main(args);

        String expected = "When the battle is over georgia's team will be found\r" +
                "With the Yellow Jacket's swarming 'round! Hey!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("swarming", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest16() throws Exception {
        File inputFile = createInputFile(FILE9);

        String args[] = {"-a", "-r", "a", "3", "-s", ".", inputFile.getPath()};
        Main.main(args);

        String expected = FILE9;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals(".", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest17() throws Exception {
        File inputFile = createInputFile(FILE10);

        String args[] = {"-n", "-s", "1234", inputFile.getPath()};
        Main.main(args);

        String expected = "1Howdy Billy," + System.lineSeparator() +
                "2I am going to take cs6300 and cs6400 next semester."  + System.lineSeparator() +
                "3Did you take cs 6300 last semester? I want to"  + System.lineSeparator() +
                "4take 2 courses so that I will graduate Asap!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("3".trim(), outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest18() throws Exception {
        File inputFile = createInputFile(FILE11);

        String args[] = {"-s", "abc", inputFile.getPath()};
        Main.main(args);

        String expected = FILE11;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest19() throws Exception {
        File inputFile = createInputFile(FILE12);

        String args[] = {"-a", "2", inputFile.getPath()};
        Main.main(args);

        String expected = System.lineSeparator()+System.lineSeparator()+System.lineSeparator()+
                "I am going to take cs6300 and cs6400 next semester."  + System.lineSeparator() +
                "Did you take cs 6300 last semester? I want to"  + System.lineSeparator() +
                "take 2 courses so that I will graduate Asap!" + System.lineSeparator() +
                "Howdy Billy,";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest20() throws Exception {
        File inputFile = createInputFile(FILE13);

        String args[] = {"-n", "-s", "abcdefghij", inputFile.getPath()};
        Main.main(args);

        String expected = "1ABCDEF\n2ghijkl\r3MNOPQR";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("ghij", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest21() throws Exception {
        File inputFile = createInputFile(FILE13);

        String args[] = {"-a", "3", inputFile.getPath()};
        Main.main(args);

        String expected = FILE13;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest22() throws Exception {
        File inputFile = createInputFile(FILE14);

        String args[] = {"-a", "-r", "z", "2", inputFile.getPath()};
        Main.main(args);

        String expected = FILE14;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest23() throws Exception {
        File inputFile = createInputFile(FILE15);

        String args[] = {"-a", "-s", "aA", inputFile.getPath()};
        Main.main(args);

        String expected =  " \n" + "!\n" + "1\n" + "a\n" + "A";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("a", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest24() throws Exception {
        File inputFile = createInputFile(FILE2);

        String args[] = {"-s", "LogErabcxyz", "-r", "w", inputFile.getPath()};
        Main.main(args);

        String expected = FILE2;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("Error", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest25() throws Exception {
        File inputFile = createInputFile(FILE8);

        String args[] = {"-a", "4", "-n", "-s", "1Up", inputFile.getPath()};
        Main.main(args);

        String expected = "9With the Yellow Jacket's swarming 'round! Hey!" + System.lineSeparator() +
                "4Well drop a battle axe on georgia's head\r" +
                "7'Till our bow wows rips through the air\r" +
                "6Down on the old farm there will be no sound\r" +
                "8When the battle is over georgia's team will be found\r" +
                "5When we meet her our team is sure to beat her\r" +
                "2Down with the red and black\r" +
                "3Georgia Tech is out for a victory\r" +
                "1Up with the white and gold";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("Up", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest26() throws Exception {
        File inputFile = createInputFile(FILE2);

        String args[] = {"-a", "5", inputFile.getPath()};
        Main.main(args);

        String expected = "Log: 123 abc\n" +
                "Error: 567 abc\n" +
                "Log: 567 abc" + System.lineSeparator() +
                "Error: 123 xyz";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest27() throws Exception {
        File inputFile = createInputFile(FILE7);

        String args[] = {"-n", "-r", "*", inputFile.getPath()};
        Main.main(args);

        String expected = "2and line breaks^$@ \r" +
                "3of \\different// types; \n" +
                "4in 1 file\r" +
                "5:-)";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest28() throws Exception {
        File inputFile = createInputFile(FILE3);

        String args[] = {"-k", inputFile.getPath()};
        Main.main(args);

        String expected = FILE3;

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());

    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest29() throws Exception {
        File inputFile = createInputFile(FILE10);

        String args[] = {"-k", "o", "2", "-a", "-n", inputFile.getPath()};
        Main.main(args);

        String expected = "3Did you take cs 6300 last semester? I want to" + System.lineSeparator() +
                "4take 2 courses so that I will graduate Asap!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

    // Purpose: Example of Updated Specifications Test
    @Test
    public void mainTest30() throws Exception {
        File inputFile = createInputFile(FILE8);

        String args[] = {"-n", "-r", "1", "-s", "123456789", inputFile.getPath()};
        Main.main(args);

        String expected = "2Down with the red and black\r" +
                "3Georgia Tech is out for a victory\r" +
                "4Well drop a battle axe on georgia's head\r" +
                "5When we meet her our team is sure to beat her\r" +
                "6Down on the old farm there will be no sound\r" +
                "7'Till our bow wows rips through the air\r" +
                "8When the battle is over georgia's team will be found\r" +
                "9With the Yellow Jacket's swarming 'round! Hey!";

        String actual = getFileContent(inputFile.getPath());

        assertEquals("The files differ!", expected, actual);
        assertEquals("", outStream.toString().trim());
    }

}