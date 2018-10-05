import java.io.*;

public class VMParser {
    private static BufferedReader br;
    private static String line;
    private File[] files;
    private int currentFile = 0;

    static File[] finder(String dirName){
        File dir = new File(dirName);
        return dir.listFiles((dir1, filename) -> filename.endsWith(".vm"));
    }

    VMParser(File[] files) {
        this.files = files;
        try{
            br = new BufferedReader(new FileReader(files[0]));
            System.out.println("Now translating: " + files[0].getName());
        } catch (Exception e) {
            System.out.println("The file could not be opened.\n" + e);
        }
    }

    String getFileName() {
        return files[currentFile].getName();
    }

    void advance(VMTranslator translator) {
        try {
            line = br.readLine();
            if (hasMoreCommands()) {
                if (line.contains("//")) { // ignore comments
                    line = line.substring(0, line.indexOf("//")).trim();
                }
            } else {
                currentFile++;
                if (currentFile < files.length) {
                    br = new BufferedReader(new FileReader(files[currentFile]));
                    String fileName = files[currentFile].getName();
                    translator.newFile(fileName.substring(0,fileName.indexOf(".vm")));
                    advance(translator); // first advance for the new reader
                    System.out.println("Now translating: " + fileName);
                }
                return;
            }
        } catch (IOException e) {
            System.out.println("Couldn't read line.");
        }
        while (line.isEmpty()) {
            try {
                line = br.readLine();
                if (line.contains("//")) { // ignore comments
                    line = line.substring(0, line.indexOf("//")).trim();
                }
            } catch (IOException e) {
                System.out.println("Couldn't read line");
            }
        }
    }

    boolean hasMoreCommands() {
        return line != null;
    }

    String commandType() {
        switch (line.split(" ")[0]) {
            case "push":
                return "PUSH";
            case "pop":
                return "POP";
            case "label":
                return "LABEL";
            case "goto":
                return "GOTO";
            case "if-goto":
                return "IF-GO";
            case "function":
                return "FUNCTION";
            case "call":
                return "CALL";
            case "return":
                return "RETURN";
            default:
                return "ARITHMETIC";
        }
    }

    String[] getArgs() {
        if (commandType().equals("ARITHMETIC")) {
            String[] ret = new String[1];
            ret[0] = line;
            return ret;
        }
        return line.substring(line.indexOf(" ") + 1).split(" ");
    }
}
