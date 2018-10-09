import java.io.File;
import java.util.ArrayList;
public class CompilationEngine {
    static ArrayList<String> definedMethods = new ArrayList<>();
    static ArrayList<String> calledMethods = new ArrayList<>();

    private static String[] classFinder(String dirName){
        File dir = new File(dirName);
        File[] files = dir.listFiles((dir1, filename) -> filename.endsWith(".jbgpl"));
        if (files == null) {
            return new String[0];
        }
        String[] ret = new String[files.length];
        for (int i = 0; i < ret.length; i++) {
            String temp = files[i].getName();
            ret[i] = temp.substring(0, temp.indexOf("."));
        }
        return ret;
    }

    public static void main(String[] args) {
        String dirPath = "C:/Users/zergl/IdeaProjects/JBGPL Compiler/src/files/Jack Files/Test";
        File f = new File(dirPath + "/Test.jbgpl");
        String[] tokens = Tokenizer.tokenize(f);
        String[] classes = CompilationEngine.classFinder(dirPath);

        Compiler comp = new Compiler(tokens, classes);
        String out = "";
        try {
            out = comp.compileClass();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Reached end of file while parsing.");
        } catch (SyntaxException e) {
            System.out.println(e);
        }

        System.out.println(out);
    }
}
