import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VMTranslator {
    private String fileName;
    private String currentFunction;
    private int conditionalCounter;
    private int callCounter;
    private PrintWriter writer;

    VMTranslator(File file) {
        try {
            writer = new PrintWriter(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("If you managed to trigger this, you did something horribly wrong.");
        }
    }

    private void updateFunction(String newFunction) {
        currentFunction = newFunction;
        conditionalCounter = 0;
    }

    void newFile(String file) {
        fileName = file;
    }

    /**
     * Returns all the pre-application code.
     * 1) boostrapper, which calls Sys.init
     * 2) The stacker used in calling functions
     * 3) The returner used by the return statement
     *
     * Adds formatted assembly pre-application code to the PrintWriter.
     */
    private void bootstrap() {
        String ret = "@255\nD=A\n@SP\nM=D\n@Sys.init\n0;JMP\n" + // bootstrapper

                /* RETURNER */
                "(RETURNER)\n@5\nD=A\n@LCL\nA=M-D\nD=M\n@R13\nM=D\n" + // store return address
                "@SP\nA=M\nD=M\n@ARG\nA=M\nM=D\n" + // pop arg 0
                "D=A\n@SP\nM=D\n" + // reset SP to arg 0
                "@LCL\nD=M\n@R14\nAM=D-1\nD=M\n@THAT\nM=D\n" + // restore THAT pointer
                "@R14\nAM=M-1\nD=M\n@THIS\nM=D\n" + // restore THIS pointer
                "@R14\nAM=M-1\nD=M\n@ARG\nM=D\n" + // restore ARG pointer
                "@R14\nAM=M-1\nD=M\n@LCL\nM=D\n" + // restore LCL pointer
                "@R13\nA=M\n0;JMP\n" + // bail

                /* STACKER */
                "(STACKER)\n@SP\nAM=M+1\nM=D\n" + // push return address (stored in D as a precondition)
                "@LCL\nD=M\n@SP\nAM=M+1\nM=D\n" + // push LCL pointer
                "@ARG\nD=M\n@SP\nAM=M+1\nM=D\n" + // push ARG pointer
                "@THIS\nD=M\n@SP\nAM=M+1\nM=D\n" + // push THIS pointer
                "@THAT\nD=M\n@SP\nAM=M+1\nM=D\n" + // push THAT pointer
                "@4\nD=A\n@R13\nD=D+M\n@SP\nD=M-D\n@ARG\nM=D\n" + // store new ARG pointer
                "@SP\nD=M+1\n@LCL\nM=D\n" + // LCL pointer <- ++SP
                "@R14\nA=M\n0;JMP\n"; // bail

        writer.print(ret.replaceAll("\\n", "\r\n"));
    }

    /**
     * Returns formatted Assembly code to push to stack.
     *
     * @param args args[0] holds the source segment name, args[1] holds the segment index.
     * @return formatted Assembly code for the push operation.
     * @throws VMSyntaxException if not two args.
     */
    private String push(String... args) throws VMSyntaxException {
        String ifError = "push";
        for (String s: args) {
            ifError += " " + s;
        }

        if (args.length != 2) {
            throw new VMSyntaxException(ifError);
        }
        String segment = args[0];

        String ret = "";
        if (segment.equals("static")) {
            ret += "@" + fileName + "." + args[1] + "\nD=M";
        } else if (segment.equals("pointer")) {
            switch (args[1]) {
                case "0":
                    ret += "@THIS";
                    break;
                case "1":
                    ret += "@THAT";
                    break;
                default:
                    throw new VMSyntaxException(ifError);
            }
            ret += "\nD=M";
        } else {
            ret += "@" + args[1] + "\nD=A";
            if (!args[0].equals("constant")) {
                if (segment.equals("temp")) {
                    ret += "\n@5\nA=A+D";
                } else {
                    switch (segment) {
                        case "local":
                            ret += "\n@LCL";
                            break;
                        case "argument":
                            ret += "\n@ARG";
                            break;
                        case "this":
                            ret += "\n@THIS";
                            break;
                        case "that":
                            ret += "\n@THAT";
                            break;
                        default:
                            throw new VMSyntaxException(ifError);
                    }
                    ret += "\nA=M+D";
                }
                ret += "\nD=M";
            }
        }
        ret += "\n@SP\nAM=M+1\nM=D\n";
        return ret;
    }

    /**
     * Returns formatted Hack Assembly to pop from stack.
     *
     * @param args args[0] is a memory segment, args[1] is a segment index
     * @return formatted Hack Assembly code.
     * @throws VMSyntaxException if not two args.
     */
    private String pop(String... args) throws VMSyntaxException {
        String ifError = "pop";
        for (String s: args) {
            ifError += " " + s;
        }
        if (args.length != 2) {
            throw new VMSyntaxException(ifError);
        }
        String segment = args[0];

        String ret = "";
        if (segment.equals("static") || segment.equals("pointer")) {
            ret += "@SP\nM=M-1\nA=M+1\nM=D\n";
            switch (segment) {
                case "static":
                    ret += "@" + fileName + "." + args[1];
                    break;
                case "pointer":
                    switch (args[1]) {
                        case "0":
                            ret += "@THIS";
                            break;
                        case "1":
                            ret += "@THAT";
                            break;
                        default:
                            throw new VMSyntaxException(ifError);
                    }
            }
            ret += "\nM=D\n";
        } else {
            if (segment.equals("temp")) {
                ret += "@5\nD=A";
            } else {
                switch (segment) {
                    case "local":
                        ret += "@LCL";
                        break;
                    case "argument":
                        ret += "@ARG";
                        break;
                    case "this":
                        ret += "@THIS";
                        break;
                    case "that":
                        ret += "@THAT";
                        break;
                }
                ret += "\nD=M";
            }
            ret += "\n@" + args[1];
            ret += "\nD=D+A\n@R13\nM=D\n@SP\nM=M-1\nA=M+1\nD=M\n@R13\nA=M\nM=D\n";
        }
        return ret;
    }

    private String math(String operation) throws VMSyntaxException {
        String ret = "";
        switch (operation) {
            case "add":
                ret += "@SP\nA=M\nD=M\n@SP\nAM=M-1\nM=D+M\n";
                break;
            case "sub":
                ret += "@SP\nA=M\nD=-M\n@SP\nAM=M-1\nM=D+M\n";
                break;
            case "neg":
                ret += "@SP\nA=M\nM=-M\n";
                break;
            case "and":
                ret += "@SP\nA=M\nD=M\n@SP\nAM=M-1\nM=D&M\n";
                break;
            case "or":
                ret += "@SP\nA=M\nD=M\n@SP\nAM=M-1\nM=D|M\n";
                break;
            case "not":
                ret += "@SP\nA=M\nM=!M\n";
                break;
            default:
                throw new VMSyntaxException(operation);
        }
        return ret;
    }

    private String conditional(String conditional) throws VMSyntaxException {
        String ret = "";
        ret += "@SP\nA=M\nD=-M\n@SP\nAM=M-1\nD=D+M\n"; // -pop() + peek()
        ret += "@" + currentFunction + "_if_true" + conditionalCounter + "\n";
        switch(conditional) {
            case "lt":
                ret += "D;JLT\n";
                break;
            case "eq":
                ret += "D;JEQ\n";
                break;
            case "gt":
                ret += "D;JGT\n";
                break;
            default:
                throw new VMSyntaxException(conditional);
        }
        ret += "@SP\nA=M\nM=0\n@continue_" + currentFunction + conditionalCounter + "\n";
        ret += "0;JMP\n("+ currentFunction + "_if_true" + conditionalCounter + ")\n@SP\nA=M\nM=-1\n";
        ret += "(continue_" + currentFunction + conditionalCounter + ")\n";
        conditionalCounter++;
        return ret;
    }

    private String label(String label) {
        return "(" + currentFunction + "$" + label + ")\n";
    }

    private String go(String label) {
        return "@" + currentFunction + "$" + label + "\n0;JMP\n";
    }

    private String ifGo(String label) {
        return "@SP\nM=M-1\nA=M+1\nD=M\n@" + currentFunction + "$" + label + "\nD;JNE\n";
    }

    private String functionDec(String... args) throws VMSyntaxException {
        String ifError = "function";
        for (String s: args) {
            ifError += " " + s;
        }
        if (args.length != 2) {
            throw new VMSyntaxException(ifError);
        }
        try {
            int test = Integer.parseInt(args[1]);
            if (test < 0) {
                throw new VMSyntaxException(ifError);
            }
        } catch (NumberFormatException e) {
            throw new VMSyntaxException(ifError);
        }

        updateFunction(args[0]);
        String ret = "(" + currentFunction + ")\n";
        switch (args[1]) {
            case "0":
                break;
            case "1":
                ret += push("constant", "0");
                break;
            default:
                ret += "@" + args[1] + "\nD=A\n(LOOP_" + currentFunction + ")\n";
                ret += "D=D-1\n@SP\nAM=M+1\nM=0\n@LOOP_" + currentFunction + "\nD;JGT\n";
                break;
        }
        return ret;
    }

    private String functionCall(String... args) throws VMSyntaxException {
        String ifError = "call";
        for (String s: args) {
            ifError += " " + s;
        }
        if (args.length != 2) {
            throw new VMSyntaxException(ifError);
        }
        try {
            int test = Integer.parseInt(args[1]);
            if (test < 0) {
                throw new VMSyntaxException(ifError);
            }
        } catch (NumberFormatException e) {
            throw new VMSyntaxException(ifError);
        }

        String ret = "@" + args[1] + "\nD=A\n@R13\nM=D\n@" + args[0];
        ret += "\nD=A\n@R14\nM=D\n@RETURN_ADDRESS_" + callCounter;
        ret += "\nD=A\n@STACKER\n0;JMP\n(RETURN_ADDRESS_" + callCounter + ")\n";
        callCounter++;
        return ret;
    }

    private String functionRet() {
        return "@RETURNER\n0;JMP\n";
    }

    private void translate(String commandType, String... args) throws VMSyntaxException {
        String toWrite = "";
        if (commandType.equals("ARITHMETIC")) {
            switch (args[0]) {
                case "lt":
                case "eq":
                case "gt":
                    toWrite = conditional(args[0]);
                    break;
                default:
                    toWrite = math(args[0]);
                    break;
            }
        } else if (commandType.equals("PUSH")) {
            toWrite = push(args);
        } else if (commandType.equals("POP")) {
            toWrite = pop(args);
        } else if (commandType.equals("LABEL")) {
            toWrite = label(args[0]);
        } else if (commandType.equals("GOTO")) {
            toWrite = go(args[0]);
        } else if (commandType.equals("IF-GO")) {
            toWrite = ifGo(args[0]);
        } else if (commandType.equals("FUNCTION")) {
            toWrite = functionDec(args);
        } else if (commandType.equals("CALL")) {
            toWrite = functionCall(args);
        } else if (commandType.equals("RETURN")) {
            toWrite = functionRet();
        }
        // no else as arithmetic will always be the type of an invalid command, and math will throw the exception.
        writer.print(toWrite.replaceAll("\\n", "\r\n"));
    }

    private void close() {
        writer.close();
    }

    public static void main(String[] args) {
        boolean directory;
        Scanner sc = new Scanner(System.in);
        System.out.print("File or Dir?: ");
        directory = sc.nextLine().equalsIgnoreCase("dir");
        String fileOrDirName = "C:/Users/zergl/IdeaProjects/JBGPL TreeBuilder/src/files/VM Files";
        System.out.print("Name of File or Directory (do not include .vm): ");
        String actualName = sc.nextLine();
        if (actualName.contains("/")) {
            fileOrDirName += actualName.substring(0, actualName.indexOf("/") + 1);
            actualName = actualName.substring(actualName.indexOf("/") + 1);
        }
        fileOrDirName += actualName;

        VMParser parser;
        if (directory) {
            parser = new VMParser(VMParser.finder(fileOrDirName));
        } else {
            File[] file = {new File(fileOrDirName + ".vm")};
            parser = new VMParser(file);
        }

        if (directory) {
            fileOrDirName += "/" + actualName;
        }
        VMTranslator translator = new VMTranslator(new File(fileOrDirName + ".asm"));
        translator.bootstrap();
        try {
            parser.advance(translator);
            while (parser.hasMoreCommands()) {
                translator.translate(parser.commandType(), parser.getArgs());
                parser.advance(translator);
            }
        } catch (VMSyntaxException e) {
            System.out.println(e);
            System.out.println("Error occurred in file: " + parser.getFileName());
        }
        translator.close();
    }
}
