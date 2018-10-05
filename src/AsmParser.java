import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class AsmParser {
    private static BufferedReader br;
    private static String line;

    AsmParser(File file) {
        try{
            br = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            System.out.println("The file could not be opened.\n" + e);
        }
    }

    void advance() {
        try {
            line = br.readLine();
            if (hasMoreCommands()) {
                line = line.replaceAll(" ", "");
                if (line.contains("//")) { // ignore comments
                    line = line.substring(0, line.indexOf("//"));
                }
            } else {
                return;
            }
        } catch (IOException e) {
            System.out.println("Couldn't read line.");
        }
        while (line.isEmpty()) {
            try {
                line = br.readLine().replaceAll(" ", "");
                if (line.contains("//")) { // ignore comments
                    line = line.substring(0, line.indexOf("//"));
                }
            } catch (IOException e) {
                System.out.println("Couldn't read line");
            }
        }
        //System.out.println(line);
    }

    boolean hasMoreCommands() {
        return line != null;
    }

    String commandType() {
        if (line.substring(0,1).equals("@")) {
            return "A_COMMAND";
        } else if (line.matches("[ADM]{0,3}((=.+)|(.+;)).{0,3}")) {
            return "C_COMMAND";
        }
        return "L_COMMAND";
    }

    String symbol () {
        switch (commandType()) {
            case "C_COMMAND":  // this should never be called
                return "Something has gone horribly wrong.";
            case "A_COMMAND":
                return line.substring(1);
            default:
                return line.substring(1, line.length() - 1); // (XXXXXXXX) returns the Xs
        }
    }

    String dest() {
        if (!commandType().equals("C_COMMAND")) {
            System.out.println("That's not a C Command.");
        }
        int ind = line.indexOf("=");
        if (ind > 0) {
            return line.substring(0,ind);
        } else {
            return "";
        }
    }

    String comp() {
        int startInd = line.indexOf("=") + 1;
        int endInd = line.indexOf(";");
        if (endInd < 0) {
            return line.substring(startInd);
        } else {
            return line.substring(startInd, endInd);
        }
    }

    String jump() {
        int jumpInd = line.indexOf(";");
        if (jumpInd < 0) {
            return "";
        } else {
            return line.substring(jumpInd + 1);
        }
    }
}
