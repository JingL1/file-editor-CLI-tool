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

import static org.junit.Assert.assertEquals;

public class MyMainTest {

/*
Place all  of your tests in this class, optionally using MainTest.java as an example.
*/

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
    private static final String FILE0 = "10";
    private static final String FILE1 = "1 dog" + System.lineSeparator() + "2 cat" + System.lineSeparator() + "3 ant";
    private static final String FILE2 = " 1" + System.lineSeparator() + " 2" + System.lineSeparator() + " 3";
    private static final String FILE3 = "!!!" + System.lineSeparator() + "***" + System.lineSeparator() + "###";
    private static final String FILE4 = "!" + System.lineSeparator() + "@" + System.lineSeparator() + "#";
    private static final String FILE5 = "1" + System.lineSeparator() + "d" + System.lineSeparator() + "a";
    private static final String FILE6 = "Log: 123 abc\nError: 123 xyz\nError: 567 abc\nLog: 567 abc";


    // Purpose: To test the case with only one word in the file
    // Frame #: 2
    @Test
    public void filesummaryTest1() throws Exception {
        File inputFile1 = createInputFile(FILE0);

        String args[] = {inputFile1.getPath()};
        Main.main(args);

        String expected1 = FILE0;
        String actual1 = getFileContent(inputFile1.getPath());

        assertEquals("The files differ!", expected1, actual1);
        assertEquals("1", outStream.toString().trim());
    }

    // Purpose: To test the case that method of (-s) specified with a <string> not contained in the file
    // Frame #: 4
    // Updated answer according to test19 in MainTest
    @Test
    public void filesummaryTest2() throws Exception {
        File inputFile2 = createInputFile(FILE1);

        String args[] = {"-s", "zig", inputFile2.getPath()};
        Main.main(args);

        assertEquals("g", outStream.toString().trim());
    }

    // Purpose: To test the case that method of '-a' specified with a <integer> that is larger than any lines' length
    // Frame #: 5
    @Test
    public void filesummaryTest3() throws Exception {
        File inputFile3 = createInputFile(FILE1);

        String args[] = {"-a", "10", inputFile3.getPath()};
        Main.main(args);

        assertEquals("illegal argument exception", errStream.toString().trim());
    }

    // Purpose: To test the case that method of '-a' specified with a <integer> that is not positive
    // Frame #: 6
    @Test
    public void filesummaryTest4() throws Exception {
        File inputFile4 = createInputFile(FILE1);

        String args[] = {"-a", "-3", inputFile4.getPath()};
        Main.main(args);

        assertEquals("illegal argument exception", errStream.toString().trim());
    }

    // Purpose: To test the case of using the method of '-r' and '-k' at the same time
    // Frame #: 7
    @Test
    public void filesummaryTest5() throws Exception {
        File inputFile5 = createInputFile(FILE1);

        String args[] = {"-r", "1","-k","1", inputFile5.getPath()};
        Main.main(args);

        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }

    // Purpose: To test the case that method of '-r'/'-k' specified with a <string> not contained in the file
    // Frame #: 8
    @Test
    public void filesummaryTest6() throws Exception {
        File inputFile6 = createInputFile(FILE1);

        String args[] = {"-r", "8", inputFile6.getPath()};
        Main.main(args);

        String expected6 = "1 dog" + System.lineSeparator() + "2 cat" + System.lineSeparator() + "3 ant";;
        String actual6 = getFileContent(inputFile6.getPath());

        assertEquals("The files differ!", expected6, actual6);
    }

    // Purpose: To test the file with multiple lines of one character (containing alphanumeric letters), processed by (-r|-k)
    // Frame #: 11
    @Test
    public void filesummaryTest7() throws Exception {
        File inputFile7 = createInputFile(FILE5);

        String args[] = {"-r", "1", inputFile7.getPath()};
        Main.main(args);

        String expected7 = "d" + System.lineSeparator() + "a";
        String actual7 = getFileContent(inputFile7.getPath());

        assertEquals("The files differ!", expected7, actual7);
    }

    // Purpose: To test the file with multiple lines of multiple words, and to process by method of (-a) without <integer> and (-r|-k)
    // Frame #: 21
    @Test
    public void filesummaryTest8() throws Exception {
        File inputFile8 = createInputFile(FILE1);

        String args[] = {"-a", "-r","1", inputFile8.getPath()};
        Main.main(args);

        String expected8 = "3 ant" + System.lineSeparator() +  "2 cat";
        String actual8 = getFileContent(inputFile8.getPath());

        assertEquals("The files differ!", expected8, actual8);
    }

    // Purpose: To test the file with multiple lines of multiple words, and to process by method of (-a) with a valid <integer>, and (-s) not at first location
    // Frame #: 32
    @Test
    public void filesummaryTest9() throws Exception {
        File inputFile9 = createInputFile(FILE1);

        String args[] = {"-a", "2","-s", "dog",inputFile9.getPath()};
        Main.main(args);

        String expected9 = "3 ant" + System.lineSeparator() + "2 cat" + System.lineSeparator() + "1 dog";
        String actual9 = getFileContent(inputFile9.getPath());

        assertEquals("The files differ!", expected9, actual9);
        assertEquals("dog", outStream.toString().trim());
    }

    // Purpose: To test the file with multiple lines of multiple words, and to process by method of (-a) without <integer>, and (-s) at first location
    // Frame #: 34
    @Test
    public void filesummaryTest10() throws Exception {
        File inputFile10 = createInputFile(FILE1);

        String args[] = {"-s", "dog", "-a", inputFile10.getPath()};
        Main.main(args);

        String expected10 = "3 ant" + System.lineSeparator() + "2 cat" + System.lineSeparator() +  "1 dog";
        String actual10 = getFileContent(inputFile10.getPath());

        assertEquals("The files differ!", expected10, actual10);
        assertEquals("dog", outStream.toString().trim());
    }

    // Purpose: To test the file with multiple lines of special characters, and no method specified
    // Frame #: 38
    @Test
    public void filesummaryTest11() throws Exception {
        File inputFile11 = createInputFile(FILE3);

        String args[] = {inputFile11.getPath()};
        Main.main(args);

        String expected11 = "!!!" + System.lineSeparator() + "***" + System.lineSeparator() + "###";
        String actual11 = getFileContent(inputFile11.getPath());

        assertEquals("The files differ!", expected11, actual11);
        assertEquals("0", outStream.toString().trim());
    }

    // Purpose: To test the file with multiple lines of special characters, and to process by (-a) without <integer> and (-s) not at first location. The line will not re-order as there is no alpanumeric characters
    // Frame #: 50
    @Test
    public void filesummaryTest12() throws Exception {
        File inputFile12 = createInputFile(FILE3);

        String args[] = {"-a", "-s", "!",inputFile12.getPath()};
        Main.main(args);

        String expected12 = FILE3;
        String actual12 = getFileContent(inputFile12.getPath());

        assertEquals("The files differ!", expected12, actual12);
        assertEquals("!!!", outStream.toString().trim());
    }

    // Purpose: To test the file with multiple lines of single sequence with spaces at the beginning, and to process (-r|-k)
    // Frame #: 59
    @Test
    public void filesummaryTest13() throws Exception {
        File inputFile13 = createInputFile(FILE2);

        String args[] = {"-k", " 1", inputFile13.getPath()};
        Main.main(args);

        String expected13 = " 1";
        String actual13 = getFileContent(inputFile13.getPath());

        assertEquals("The files differ!", expected13, actual13);
    }


    // Purpose: To test the file with multiple lines of a single special character, and to process (-r|-k)
    // Frame #: 15
    @Test
    public void filesummaryTest14() throws Exception {
        File inputFile14 = createInputFile(FILE4);

        String args[] = {"-k", "!",inputFile14.getPath()};
        Main.main(args);

        String expected14 = "!" ;
        String actual14 = getFileContent(inputFile14.getPath());

        assertEquals("The files differ!", expected14, actual14);
    }


    // Purpose: To test the file with multiple lines of a single special character, and to process (-s) and (-r|-k)
    // Frame #: 17
    @Test
    public void filesummaryTest15() throws Exception {
        File inputFile15 = createInputFile(FILE4);

        String args[] = {"-k", "!", "-s", "!!!",inputFile15.getPath()};
        Main.main(args);

        String expected15 = "!" ;
        String actual15 = getFileContent(inputFile15.getPath());

        assertEquals("The files differ!", expected15, actual15);
        assertEquals("!", outStream.toString().trim());
    }


    // Purpose: To test the case with multiple lines in the file, with -s method and a specified <string>
    // Frame #: 12
    @Test
    public void filesummaryTest16() throws Exception {
        File inputFile16 = createInputFile(FILE1);

        String args[] = {"-s", "ant", inputFile16.getPath()};
        Main.main(args);

        String expected16 = FILE1;
        String actual16 = getFileContent(inputFile16.getPath());

        assertEquals("The files differ!", expected16, actual16);
        assertEquals("ant", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines of words in the file, with -k method and -s method
    // Frame #: 13
    @Test
    public void filesummaryTest17() throws Exception {
        File inputFile17 = createInputFile(FILE1);

        String args[] = {"-s", "ant", "-k", "dog", inputFile17.getPath()};
        Main.main(args);

        String expected17 = "1 dog";
        String actual17 = getFileContent(inputFile17.getPath());

        assertEquals("The files differ!", expected17, actual17);
        assertEquals("ant", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines of words in the file, without any method
    // Frame #: 18
    @Test
    public void filesummaryTest18() throws Exception {
        File inputFile18 = createInputFile(FILE1);

        String args[] = {inputFile18.getPath()};
        Main.main(args);

        String expected18 = FILE1;
        String actual18 = getFileContent(inputFile18.getPath());

        assertEquals("The files differ!", expected18, actual18);
        assertEquals("6", outStream.toString().trim());
    }


    // Purpose: To test the case with multiple lines of words in the file, with method of -a
    // Frame #: 20
    @Test
    public void filesummaryTest19() throws Exception {
        File inputFile19 = createInputFile(FILE1);

        String args[] = {"-a", "2", inputFile19.getPath()};
        Main.main(args);

        String expected19 = "3 ant"+ System.lineSeparator() + "2 cat" + System.lineSeparator() + "1 dog" ;
        String actual19 = getFileContent(inputFile19.getPath());

        assertEquals("The files differ!", expected19, actual19);
    }


    // Purpose: To test the case with multiple lines of words in the file, with method of -r and -s
    // Frame #: 25
    @Test
    public void filesummaryTest20() throws Exception {
        File inputFile20 = createInputFile(FILE1);

        String args[] = {"-s", "antt", "-r", "ant", inputFile20.getPath()};
        Main.main(args);

        String expected20 = "1 dog" + System.lineSeparator() + "2 cat";
        String actual20 = getFileContent(inputFile20.getPath());

        assertEquals("The files differ!", expected20, actual20);
        assertEquals("ant", outStream.toString().trim());
    }



    // Purpose: To test the case with multiple lines of words in the file, with method of -a and -s
    // Frame #: 26
    @Test
    public void filesummaryTest21() throws Exception {
        File inputFile21 = createInputFile(FILE1);

        String args[] = {"-a", "2", "-s", "atnt", inputFile21.getPath()};
        Main.main(args);

        String expected21 = "3 ant"+ System.lineSeparator() + "2 cat" + System.lineSeparator() + "1 dog";
        String actual21 = getFileContent(inputFile21.getPath());

        assertEquals("The files differ!", expected21, actual21);
        assertEquals("ant", outStream.toString().trim());
    }


    // Purpose: To test the case with multiple lines of words in the file, with method of -s at the first place and -a
    // Frame #: 34
    @Test
    public void filesummaryTest22() throws Exception {
        File inputFile22 = createInputFile(FILE1);

        String args[] = {"-s", "atnt","-a", "2", inputFile22.getPath()};
        Main.main(args);

        String expected22 = "3 ant"+ System.lineSeparator() + "2 cat" + System.lineSeparator() + "1 dog";
        String actual22 = getFileContent(inputFile22.getPath());

        assertEquals("The files differ!", expected22, actual22);
        assertEquals("ant", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines and includes spaces in the line, without any method
    // Frame #: 58
    @Test
    public void filesummaryTest23() throws Exception {
        File inputFile23 = createInputFile(FILE1);

        String args[] = {inputFile23.getPath()};
        Main.main(args);

        String expected23 = FILE1;
        String actual23 = getFileContent(inputFile23.getPath());

        assertEquals("The files differ!", expected23, actual23);
        assertEquals("6", outStream.toString().trim());
    }


    // Purpose: To test the case with multiple lines and includes spaces in the line, with method of -s at the first place and -k
    // Frame #: 75
    @Test
    public void filesummaryTest24() throws Exception {
        File inputFile24 = createInputFile(FILE1);

        String args[] = {"-s", "atnt","-k", "1",inputFile24.getPath()};
        Main.main(args);

        String expected24 = "1 dog";;
        String actual24 = getFileContent(inputFile24.getPath());

        assertEquals("The files differ!", expected24, actual24);
        assertEquals("ant", outStream.toString().trim());
    }


    // Purpose: To test the case with multiple lines and includes spaces in the line, with method of -s at the first place, -r and -a
    // Frame #: 73
    @Test
    public void filesummaryTest25() throws Exception {
        File inputFile25 = createInputFile(FILE1);

        String args[] = {"-s", "atnt","-r", "1","-a", "2",inputFile25.getPath()};
        Main.main(args);

        String expected25 = "3 ant"+ System.lineSeparator() + "2 cat";
        String actual25 = getFileContent(inputFile25.getPath());

        assertEquals("The files differ!", expected25, actual25);
        assertEquals("ant", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines and includes spaces in the line, with method of -r and -a
    // Frame #: 61
    @Test
    public void filesummaryTest26() throws Exception {
        File inputFile26 = createInputFile(FILE1);

        String args[] = {"-r", "1","-a", "2",inputFile26.getPath()};
        Main.main(args);

        String expected26 = "3 ant"+ System.lineSeparator() + "2 cat";
        String actual26 = getFileContent(inputFile26.getPath());

        assertEquals("The files differ!", expected26, actual26);
    }

    // Purpose: To test the case with multiple lines and includes spaces in the line, with method of -k
    // Frame #: 59
    @Test
    public void filesummaryTest27() throws Exception {
        File inputFile27 = createInputFile(FILE1);

        String args[] = {"-k", "3",inputFile27.getPath()};
        Main.main(args);

        String expected27 = "3 ant";
        String actual27 = getFileContent(inputFile27.getPath());

        assertEquals("The files differ!", expected27, actual27);
    }

    // Purpose: To test the case with multiple lines and includes non-alphanumeric chars, with method of -a, -r and -s at the first position
    // Frame #: 55
    @Test
    public void filesummaryTest28() throws Exception {
        File inputFile28 = createInputFile(FILE6);

        String args[] = {"-s","567", "-a", "-r","Log",inputFile28.getPath()};
        Main.main(args);

        String expected28 = "Error: 567 abc\nError: 123 xyz";
        String actual28 = getFileContent(inputFile28.getPath());

        assertEquals("The files differ!", expected28, actual28);
        assertEquals("567", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines and includes non-alphanumeric chars, with method of -a, -r and -s not at the first position
    // Frame #: 51
    @Test
    public void filesummaryTest29() throws Exception {
        File inputFile29 = createInputFile(FILE6);

        String args[] = { "-a", "-r","Log","-s","567",inputFile29.getPath()};
        Main.main(args);

        String expected29 = "Error: 567 abc\nError: 123 xyz";
        String actual29 = getFileContent(inputFile29.getPath());

        assertEquals("The files differ!", expected29, actual29);
        assertEquals("567", outStream.toString().trim());
    }

    // Purpose: To test the case with multiple lines and includes non-alphanumeric chars, with method of -s
    // Frame #: 51
    @Test
    public void filesummaryTest30() throws Exception {
        File inputFile30 = createInputFile(FILE6);

        String args[] = {"-s","Lgo",inputFile30.getPath()};
        Main.main(args);

        String expected30 = FILE6;
        String actual30 = getFileContent(inputFile30.getPath());

        assertEquals("The files differ!", expected30, actual30);
        assertEquals("Log", outStream.toString().trim());
    }
}
