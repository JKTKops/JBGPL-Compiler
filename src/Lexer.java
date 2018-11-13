import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Lexer {
    private List<String> tokens;
    private BufferedReader br = null;
    private String line;
    private File[] files;
    private int currentToken;
    private int currentFile;
    private int lineNumber;
    private int charNumber;

    private static File[] finder(String dirName){
        File dir = new File(dirName);
        return dir.listFiles((dir1, filename) -> filename.endsWith(".jbgpl"));
    }

    Lexer(String dir) {
        tokens = new ArrayList<>();
        files = finder(dir);
        try{
            br = new BufferedReader(new FileReader(files[0]));
            System.out.println("Now lexing: " + files[0].getName());
            tokens.add("new file: " + files[0].getName());
        } catch (Exception e) {
            System.out.println("The file could not be opened.\n" + e);
        }
        currentFile = 0;
        advance();
        while (currentFile < files.length) {
            tokenizeLine();
            advance();
        }

        currentToken = -1; // will be incremented before access ever happens
        currentFile = 0;
        lineNumber = 1;
        charNumber = 1;
    }

    private void advance() {
        try {
            line = br.readLine();
            if (line != null) {
                if (line.contains("//")) { // ignore comments
                    line = line.substring(0, line.indexOf("//")).trim();
                }
            } else {
                currentFile++;
                if (currentFile < files.length) {
                    br = new BufferedReader(new FileReader(files[currentFile]));
                    String fileName = files[currentFile].getName();
                    advance(); // first advance for the new reader
                    System.out.println("Now lexing: " + fileName);
                    tokens.add("new file: " + fileName);
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
    private void tokenizeLine() {
        char[] chars = line.toCharArray();
        StringBuilder token = new StringBuilder();
        for (Character aChar : chars) {
            if (aChar.toString().matches("[_A-Za-z0-9]+")) {
                token.append(aChar);
            } else if (aChar.toString().matches("\\s")) {
                if (token.toString().length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                tokens.add(aChar.toString());
            } else if (aChar == '&' && token.toString().equals("&")) {
                token.append(aChar);
            } else if (aChar == '|' && token.toString().equals("|")) {
                token.append(aChar);
            // add special case for String literals
            } else {
                if (token.toString().length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                tokens.add(aChar.toString());
            }
        }
        tokens.add("new line");
    }

    public String getFileName() {
        return files[currentFile].getName();
    }

    public String next() {
        currentToken++;
        String token = tokens.get(currentToken);
        if (token.matches("\\s")) {
            charNumber++;
            return next();
        } else if (token.equals("new line")) {
            charNumber = 0;
            lineNumber++;
            return next();
        } else if (token.matches("new file:.*\\.jbgpl")) {
            charNumber = 0;
            lineNumber = 0;
            currentFile++;
            return next();
        } else {
            charNumber += token.length();
            return token;
        }
    }

    public String peek(int distance) {
        int i = currentToken;
        for (int tokensSeen = 0; tokensSeen < distance; i++) {
            if (!tokens.get(i).matches("\\s")) {
                tokensSeen++;
            }
        }
        if (i >= tokens.size()) {
            return null;
        }
        return tokens.get(i);
    }
}
