import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Compiler {
    private HashMap<String, String> methodTypes = new HashMap<>();
    private ArrayList<String> classes;
    private String[][] tokens;

    Compiler(String dirName) throws SyntaxException {
        String[] fileNames = classFinder(dirName);
        tokens = new String[fileNames.length][];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = Tokenizer.tokenize(new File(dirName + "/" + fileNames[i]));
        }
        for (int i = 0; i < fileNames.length; i++) {
            String temp = fileNames[i];
            fileNames[i] = temp.substring(0, temp.indexOf("."));
        }
        classes = new ArrayList<>(Arrays.asList(fileNames));

        for (String[] token : tokens) {
            int j = 0;
            while (token[j].equals("import")) {
                j++;
                if (!token[j].matches("[A-Z][_A-Za-z0-9]*")) {
                    throw new SyntaxException(token, j, "Built-in class name expected.");
                }
                classes.add(token[j]);
                j++;
                // also get that class from the OS files and copy it into this dir
            }
            findMethods(token);
        }
    }

    String compile() throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        for (String[] curClass : tokens) {
            TreeBuilder tb = new TreeBuilder(curClass, this);
            ret.append(tb.classTree());
        }
        return ret.toString();
    }

    String[] getClasses() {
        return classes.toArray(new String[0]);
    }

    HashMap<String, String> getMethods() {
        return methodTypes;
    }

    private String[] classFinder(String dirName) {
        File dir = new File(dirName);
        File[] files = dir.listFiles((dir1, filename) -> filename.endsWith(".jbgpl"));
        if (files == null) {
            return new String[0];
        }
        String[] ret = new String[files.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = files[i].getName();
        }
        return ret;
    }

    private void findMethods(String[] tokens) throws SyntaxException {
        for (int i = 0; i < tokens.length-2; i++) {
            if (tokens[i + 2].equals("(")) {
                if (tokens[i + 1].matches("[a-z][_A-Za-z0-9]*")) {
                    if (methodTypes.containsKey(tokens[i+1])) {
                        throw new SyntaxException(tokens, i + 1, "Methods cannot be overloaded.");
                    }
                    String pType = tokens[i];
                    if (classes.contains(pType)) {
                        methodTypes.put(tokens[i+1], tokens[i]);
                        i += 3;
                    } else {
                        switch (pType) {
                            case "int":
                            case "bool":
                            case "char":
                            case "void":
                                methodTypes.put(tokens[i+1], pType);
                                i += 3;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String dirPath = "C:/Users/zergl/IdeaProjects/JBGPL Compiler/src/files/Jack Files/Test";
        String out = "";
        try {
            Compiler compiler = new Compiler(dirPath);
            out = compiler.compile();
            System.out.println("done");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Reached end of file while parsing.");
        } catch (SyntaxException e) {
            System.out.println(e);
        }

        System.out.println(out);
    }
}
