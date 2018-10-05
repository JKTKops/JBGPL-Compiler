import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Tokenizer {
    public static void main(String[] args) {
        File f = new File("C:/Users/zergl/IdeaProjects/JBGPL Compiler/src/files/Jack Files/Square/Square.jack");
        String[] tokens = tokenize(f);
        
    }

    public static String[] tokenize(File file) {
        BufferedReader br;
        String fileText;
        StringBuilder sb = new StringBuilder();
        try{
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append("\r\n");
            }
        } catch (Exception e) {
            System.out.println("The file could not be opened.\n" + e);
        }
        fileText = sb.toString();

        String comments = "(//.*$)|(/\\*(.|\\n)*?\\*/)";
        String symbols = "[<>=^~|&+\\-/.,;{}\\[\\]()]";

        /* delete comments from the input string */
        fileText = Pattern.compile(comments, Pattern.MULTILINE).matcher(fileText).replaceAll("");

        /* split the input string into tokens and store in ArrayList */
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(Pattern.compile("(?<="+symbols+")|((?="+symbols+")\\s*)|(\\s+)").split(fileText)));

        // unfortunately without tripling the length of this regex,
        // we can't avoid having a number of pure-whitespace strings in our list.
        for (int i = 0; i < tokens.size();) {
            if (tokens.get(i).trim().length() == 0) {
                tokens.remove(i);
            } else {
                tokens.add(i, tokens.remove(i).trim());
                i++;
            }
        }

        return tokens.toArray(new String[0]);
    }
}
