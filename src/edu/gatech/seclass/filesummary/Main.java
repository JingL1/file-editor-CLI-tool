package edu.gatech.seclass.filesummary;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    private static String content;
    static Charset charset = StandardCharsets.UTF_8;

    public static void main(String[] args) {

        //take-in commands using commons CLI
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        OptionGroup keepOrRemove = new OptionGroup();
        declareOptions(options, keepOrRemove);

        //process command line
        processCommands(args, parser, options);
    }


    //process all commands
    private static void processCommands(String[] args, CommandLineParser parser, Options options) {

        //if there is command, split options and file name
        if (args.length == 0) {
            usage();
        } else {
            String[] opt = splitOption(args);

            if(content != null) {

                char[] charContent = content.toCharArray();

                try {
                    CommandLine cmd = parser.parse(options, opt);///remove txt file

                    //process without an option
                    if (opt.length == 0) {
                        noCommandParam();
                    }

                    //process -s
                    if (cmd.hasOption("s")) {
                        commandFuncS(cmd);
                    }

                    //process -n
                    if (cmd.hasOption("n")) {
                        commandFuncN(args[args.length - 1], charContent);
                    }

                    //process -k
                    if (cmd.hasOption("k")) {
                        commandFuncK(args, cmd);

                    //process -r
                    } else if (cmd.hasOption("r")) {
                        commandFuncR(args[args.length - 1], cmd);
                    } else if (cmd.hasOption("r") && cmd.hasOption("k")) {
                        usage();
                    }

                    //process -a
                    if (cmd.hasOption("a")) {
                        CommandFuncA(args, cmd);
                    }

                } catch (ParseException e) {
                    usage();
                }
            }
        }
    }

    //Base Function
    private static void noCommandParam() {
        String[] str;
        String alphNum = content.replaceAll("[^(a-zA-Z0-9)\\s|\n|\r]", "");
        str = alphNum.split("\\s|\r|\n");
        System.out.print(str.length);
    }

    //Function S
    private static void commandFuncS(CommandLine cmd) {
        String[] str;//split with space and \r
        str = content.split("\\s|\r|\n");

        String res = "";
        String currentRes = "";
        String value = cmd.getOptionValue("s");

        //Check str[i] that contains all sequence
        for (int i = 0; i < str.length; i++) {
            String seq = str[i];

            //if no spaces in param
            if (value.indexOf(" ") == -1) {
                int[] valid = new int[2];//0-startIndex；1-length
                valid = checkStrSeq(seq, value, valid);
                currentRes = seq.substring(valid[0],valid[0] + valid[1]);
                res = (res.length() < currentRes.length()) ? currentRes : res;
                //if with spaces
            } else {
                if (checkStr(seq, value)) {
                    currentRes += " " + seq;
                    res = (res.length() > currentRes.length()) ? res : currentRes;
                } else {
                    currentRes = "";
                }
            }
        }
        System.out.println(res.trim());
    }

    //Function N
    private static void commandFuncN(String arg, char[] charContent) {
        //break each line into an ArrayList
        ArrayList<String> resN = new ArrayList<>();
        int offset = 0;
        int count = 0;
        int lineNO = 1;
        for (int i = 0; i < charContent.length; i++) {
            count++;
            if (charContent[i] == '\n' || charContent[i] == '\r' || i == charContent.length - 1) {
                String str1 = String.valueOf(charContent, offset, count);
                str1 = lineNO + str1;
                lineNO++;
                resN.add(str1);
                offset = i + 1;
                count = 0;
            }
        }
        StringBuilder contentN = new StringBuilder();
        for (int i = 0; i < resN.size(); i++) {
            contentN.append(resN.get(i));
        }
        content = contentN.toString();
        try {
            writeInputFile(content, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function K
    private static void commandFuncK(String[] args, CommandLine cmd) {
        char[] charContent;
        String[] params = cmd.getOptionValues("k");
        if(params == null || params.length == 0){
            usage();
        }else {

            charContent = content.toCharArray();
            ArrayList<String> resK = new ArrayList<>();
            int offset = 0;
            int count = 0;
            for (int i = 0; i < charContent.length; i++) {
                count++;
                if (charContent[i] == '\n' || charContent[i] == '\r' || i == charContent.length - 1) {
                    String str1 = String.valueOf(charContent, offset, count);
                    String value = cmd.getOptionValue("k");
                    if (str1.contains(value)) {
                        resK.add(str1);
                    }
                    offset = i + 1;
                    count = 0;
                }
            }

            //resK: ArrayList that contains all lines with {string}
            StringBuilder contentK = new StringBuilder();

            //set default integer value to 0
            int start = 0;
            if (params.length > 1) {
                start = resK.size() - Integer.parseInt(params[1]);
            }

            //write into file
            writeFileAfterCommand(args[args.length - 1], resK, contentK, start);
        }
    }

    //Function R
    private static void commandFuncR(String arg, CommandLine cmd) {
        char[] charContent;
        charContent = content.toCharArray();
        ArrayList<String> strR = new ArrayList<>();
        int offset = 0;
        int count = 0;
        for (int i = 0; i < charContent.length; i++) {
            count++;
            if (charContent[i] == '\n' || charContent[i] == '\r' || i == charContent.length - 1) {
                String str1 = String.valueOf(charContent, offset, count);
                String value = cmd.getOptionValue("r");
                if (!str1.contains(value)) {//not contain param
                    strR.add(str1);
                }
                offset = i + 1;
                count = 0;
            }
        }
        StringBuilder contentR = new StringBuilder();

        //write into file
        writeFileAfterCommand(arg, strR, contentR, 0);

    }

    //Function A
    private static void CommandFuncA(String[] args, CommandLine cmd) {
        char[] charContent;
        String[] str;
        charContent = content.toCharArray();
        if (content.matches("^[^(a-zA-Z0-9)\\s|\n|\r] + $")) {// //no comparing if no alphanumeric letters
            try {
                writeInputFile(content, args[args.length - 1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int len = 0;
            char n = ' ';
            for (char c : charContent) {
                if (c == '\n' || c == '\r') {
                    len++;
                    n = '\n';//replace r with n
                }
            }

            str = SplitFileIntoStringArray(charContent, len, n);

            int value = Integer.parseInt(cmd.getOptionValue("a", "0"));
            //negative or out of bound parameter
            if (value >= str[0].length() || value < 0) {
                illegalExecption();
            } else {

                sortWithParam(str, value);

                //After sort: build string[] into string
                String fileContent = "";
                for (String s : str) {
                    fileContent += s;
                }

                //delete the last \n
                String res = deleteLastChar(fileContent);

                try {
                    writeInputFile(res, args[args.length - 1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




    //Declare options
    private static void declareOptions(Options options, OptionGroup keepOrRemove) {
        options.addOption("s", true, "output longest sequence made up of characters in its specified string");
        Option r = new Option("r", true, "remove lines containing <string> specified");
        //Option k = new Option("k", true, "keep lines only containing <string> specified");
        Option a = Option.builder("a").type(Integer.class).required(false).optionalArg(true).numberOfArgs(1).build();
        Option k = Option.builder("k").hasArgs().optionalArg(false).optionalArg(true).required(false).numberOfArgs(2).valueSeparator(',').build();
        Option n = new Option("n", false, "prepend line number");

        keepOrRemove.addOption(r);
        keepOrRemove.addOption(k);
        options.addOptionGroup(keepOrRemove);
        options.addOption(a);
        options.addOption(k);
        options.addOption(n);
    }

    //Read File
    public static String getFileContent(String filename) throws Exception {
        content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        if (content == null)
            fileNotFoundExecption();
        return content;
    }

    //Write File
    public static File writeInputFile(String input, String filename) throws Exception {
        File file = new File(filename);
        OutputStreamWriter fileWriter =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        fileWriter.write(input);
        fileWriter.close();
        return file;
    }

    //Process file
    private static String[] SplitFileIntoStringArray(char[] charContent, int len, char n) {
        String[] str;//add \n or \r to the end of array
        char[] charContent1 = new char[charContent.length + 1];
        int l = charContent.length;
        System.arraycopy(charContent, 0, charContent1, 0, l);
        charContent1[charContent.length] = n;

        //put each line into a string[]
        str = new String[len + 1];
        //convert char into string array
        int offset = 0;
        int j = 0;
        int count = 0;
        for (int i = 0; i < charContent1.length; i++) {
            count++;
            if (charContent1[i] == '\n' || charContent1[i] == '\r' || i == charContent1.length - 1) {
                String str1 = String.valueOf(charContent1, offset, count);
                offset = i + 1;
                str[j] = str1;
                j++;
                count = 0;
            }
        }
        return str;
    }

    //Process file
    private static String deleteLastChar(String fileContent) {
        String res;
        if (fileContent.charAt(fileContent.length() - 1) == '\n' || fileContent.charAt(fileContent.length() - 1) == '\r') {
            res = fileContent.substring(0, fileContent.length() - 1);
        } else {
            res = fileContent.substring(0, fileContent.length() - 1);
        }
        return res;
    }

    //Wirte file after command
    private static void writeFileAfterCommand(String arg, ArrayList<String> resK, StringBuilder contentK, int start) {
        for (int i = start; i < resK.size(); i++) {
            contentK.append(resK.get(i));
        }
        content = contentK.toString();

        //remove last r | n
        if (content.charAt(content.length() - 1) == '\n' || content.charAt(content.length() - 1) == '\r') {
            content = content.substring(0, content.length() - 1);
        }
        try {
            writeInputFile(content, arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //split file name and options
    private static String[] splitOption(String[] args) {
        int optLen = args.length - 1;
        String[] opt = new String[optLen];
        System.arraycopy(args, 0, opt, 0, optLen);
        try {
            content = getFileContent(args[args.length - 1]);
        } catch (Exception e) {
            fileNotFoundExecption();
        }
        return opt;
    }

    //check single sequence and return longest valid seq
    private static int[] checkStrSeq(String seq, String param, int[] valid) {
        //check param for special characters
        int start = 0;
        int end = 0;
        int len = 0;
        for (int j = 0; j < seq.length(); j++) {
            char c = seq.charAt(j);
            if (param.indexOf(c) != -1) {//如果该char存在
                end++;
            } else {
                if(end > 0 && len < end ){
                    valid[0] = start;
                    valid[1] = end;
                }
                end = 0;
                start = j + 1; //start
                continue;
            }
        }
        if(len < end) {
            valid[0] = start;
            valid[1] = end;
        }
        return valid;
    }

    //check multiple seq
    private static boolean checkStr(String seq, String param) {

        if(!checkSpecialChar(param)){//没有特殊符号就把String全部替换成没有特殊符号的样子
            seq = seq.replaceAll("[^(a-zA-Z0-9)\\s|\n|\r]", "");
        }

        for (int j = 0; j < seq.length(); j++) {
            char c = seq.charAt(j);
            if (param.indexOf(c) == -1) {//not exist
                return false;
            }
        }
        return true;
    }

    //check special chars
    private static boolean checkSpecialChar(String param){
        for(int j = 0; j < param.length(); j++){
            if(!Character.isLetterOrDigit(param.charAt(j))){
                return true;//has special char
            }
        }
        return false;//no special char
    }

    //skip non-letters for sorting
    private static int findfirstLetter(String o1, int value){
        while(!Character.isLetter(o1.charAt(value))){
            value++;
        }
        return value;
    }

    //Comparator for option a
    private static void sortWithParam(String[] str, int i) {

        Arrays.sort(str, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                String str1 = o1.toLowerCase().replaceAll("\\d+","");
                String str2 = o2.toLowerCase().replaceAll("\\d+","");
                int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);

                //sort with value = 0
                if(i == 0){

//                    if(checkLetter(str1) && checkLetter(str2)){
//                        return 0;
//                    }

                    //space first
                    if((o1.charAt(0) == ' '|| o1.charAt(0) == ' ') && (o2.charAt(0) != ' '|| o2.charAt(0) != ' '))
                        return -1;
                    if((o1.charAt(0) != ' '|| o1.charAt(0) != ' ') && (o2.charAt(0) == ' '|| o2.charAt(0) == ' '))
                        return 1;
                    //special char first
                    if((o1.charAt(0) >= 33 && o1.charAt(0) <= 47)  && (o2.charAt(0) < 33 || o2.charAt(0) > 47))
                        return -1;
                    if((o2.charAt(0) >= 33 && o2.charAt(0) <= 47)  && (o1.charAt(0) < 33 || o1.charAt(0) > 47))
                        return 1;
                    if((o2.charAt(0) >= 33 && o2.charAt(0) <= 47)  && (o1.charAt(0) >= 33 || o1.charAt(0) <= 47))
                        return 0;
                    //digit first
                    if(Character.isDigit(o1.charAt(0)) && !Character.isDigit(o1.charAt(0)))
                        return -1;
                    if(!Character.isDigit(o1.charAt(0)) && Character.isDigit(o1.charAt(0)))
                        return -1;
                    if (res != 0)
                        return String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
                    else
                        return str1.compareTo(str2);

                } else {
                    //sort r | n first
                    if((o1.charAt(0) == '\n'|| o1.charAt(0) == '\r') && (o2.charAt(0) != '\n'|| o2.charAt(0) != '\r'))
                        return -1;
                    if((o1.charAt(0) != '\n'|| o1.charAt(0) != '\r') && (o2.charAt(0) == '\n'|| o2.charAt(0) == '\r'))
                        return 1;

                    //find the subString after skiping non-letter, compare subString
                    int value1 = findfirstLetter(o1,i);
                    int value2 = findfirstLetter(o2,i);
                    String strOne = o1.substring(value1).toLowerCase().replaceAll("\\d+","");
                    String strTwo = o2.substring(value2).toLowerCase().replaceAll("\\d+","");

                    if (res != 0)
                        return String.CASE_INSENSITIVE_ORDER.compare(strOne, strTwo);
                    else
                        return strOne.compareTo(strTwo);
            }
        }
        });
    }



    //base exception
    private static void usage() {
        System.err.println("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>");
    }

    //illegal param exception
    private static void illegalExecption() {
        System.err.println("illegal argument exception");
    }

    //file not found exception
    private static void fileNotFoundExecption() {
        System.err.println("File Not Found");
    }

}

