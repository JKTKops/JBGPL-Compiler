import java.io.File;

public class CompilationEngine {
    public static void main(String[] args) {
        File f = new File("C:/Users/zergl/IdeaProjects/JBGPL Compiler/src/files/Jack Files/Test/Test.jbgpl");
        String[] tokens = Tokenizer.tokenize(f);

        Compiler comp = new Compiler(tokens);
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
