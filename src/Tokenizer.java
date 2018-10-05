import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

public class Tokenizer {
    public static void main(String[] args) {
        BufferedReader br;
        String fileText;
        StringBuilder sb = new StringBuilder();
        try{
            br = new BufferedReader(new FileReader("C:/Users/zergl/IdeaProjects/Mack to Hack/src/files/Square/Square.jack"));
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
        fileText = Pattern.compile(comments, Pattern.MULTILINE).matcher(fileText).replaceAll("");
        fileText = fileText.replaceAll("\\r\\n", "");
        String[] tokens = Pattern.compile("(?<="+symbols+")|((?="+symbols+")\\s*)|(\\s+)").split(fileText);
        for (String s: tokens) {
            s = s.trim();
            if (s.length() > 0) {
                System.out.println(s);
            }
        }
    }
}
