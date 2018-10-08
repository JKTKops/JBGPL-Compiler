import java.util.ArrayList;

public class Compiler {
    private String[] tokens;
    private int currentToken = 0;
    private final String NORM_IDEN = "[a-z][_A-Za-z0-9]*";
    final String CLASS_IDEN = "[A-Z][_A-Za-z0-9]*";
    final String[] keywords = {"class", "static", "int", "char", "bool", "void", "true",
                               "false", "null", "this", "if", "else", "while", "for", "return", "new"};
    ArrayList<String> scopeVars = new ArrayList<>();

    Compiler(String[] t) {
        tokens = t;
    }

    String compileClass() throws SyntaxException {
        if (!tokens[currentToken].equals("class")) {
            throw new SyntaxException(tokens, currentToken, "'class' expected");
        } else if (!tokens[++currentToken].matches(CLASS_IDEN)) {
            throw new SyntaxException(tokens, currentToken, "Class names must match " + CLASS_IDEN);
        } else if (!tokens[++currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "'{' expected");
        }

        StringBuilder ret = new StringBuilder("<class>\n");
        currentToken++;
        while (!tokens[currentToken].equals("}")) {
            boolean isStatic = tokens[currentToken].equals("static");
            if (isStatic) {
                currentToken++;
            }
            if (!tokens[currentToken + 2].equals("(")) {
                ret.append(compileClassVarDec(isStatic));
            } else {
                //ret.append(compileSubroutineDec(isStatic));
            }
        }
        ret.append("</class>");
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String compileClassVarDec(boolean varStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<classVarDec>\n");
        if (varStatic) {
            ret.append("<keyword> static </keyword>\n");
        }

        String type = tokens[currentToken];
        if (!(type.equals("int") || type.equals("char") || type.equals("bool") || type.matches(CLASS_IDEN))) {
            throw new SyntaxException(tokens, currentToken, "Type expected.");
        } else if (type.equals("int") || type.equals("char") || type.equals("bool")) {
            ret.append("<keyword> ").append(type).append(" </keyword>\n");
        } else {
            ret.append("<identifier> ").append(type).append(" </identifier>\n");
        }
        currentToken++;

        if (!(tokens[currentToken].matches(NORM_IDEN) && !keyword(tokens[currentToken]))) {
            throw new SyntaxException(tokens, currentToken, "Identifier expected.");
        }
        ret.append("<identifier> ").append(tokens[currentToken]).append(" </identifier>\n");
        currentToken++;

        if (tokens[currentToken].equals("=")) {
            ret.append("<symbol> ; </symbol>\n</classVarDec>\n");
            currentToken--;
            ret.append(compileAssignment());
        } else {
            while (!tokens[currentToken].equals(";")) {
                if (!tokens[currentToken].equals(",")) {
                    if (tokens[currentToken].equals("=")) {
                        throw new SyntaxException(tokens, currentToken, "Assignment not allowed in multiple declaration.");
                    }
                    throw new SyntaxException(tokens, currentToken, "',' expected.");
                }
                ret.append("<symbol> , </symbol>\n");
                currentToken++;

                if (!(tokens[currentToken].matches(NORM_IDEN) && !keyword(tokens[currentToken]))) {
                    throw new SyntaxException(tokens, currentToken, "Identifier expected.");
                }
                ret.append("<identifier> ").append(tokens[currentToken]).append(" </identifier>\n");
                currentToken++;
            }
            ret.append("<symbol> ; </symbol>\n");
            ret.append("</classVarDec>\n");
            currentToken++;
        }
        return ret.toString();
    }

    /** currentToken initially expects identifier */
    private String compileAssignment() throws SyntaxException {
        if (!tokens[currentToken].matches(NORM_IDEN) || keyword(tokens[currentToken])) {
            throw new SyntaxException(tokens, currentToken, "Identifier expected.");
        }
        StringBuilder ret = new StringBuilder("<assignment>\n");
        ret.append("<identifier> ").append(tokens[currentToken]).append(" </identifier>\n");
        ret.append("<symbol> = </symbol>\n"); // only ever called if there's an = here so
        currentToken += 2;
        //ret.append(compileExpression());
        currentToken+=2; // compileExpression will advance past this when we write it
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String compileSubroutineDec(boolean subStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        if (subStatic) {
            ret.append("<keyword> static </keyword>\n");
        }
        String type = tokens[currentToken];
        if (!(type.equals("int") || type.equals("char") || type.equals("bool") || type.equals("void") || type.matches(CLASS_IDEN))) {
            throw new SyntaxException(tokens, currentToken, "Type expected.");
        } else if (type.equals("int") || type.equals("char") || type.equals("bool") || type.equals("void")) {
            ret.append("<keyword> ").append(type).append(" </keyword>\n");
        } else {
            ret.append("<identifier> ").append(type).append(" </identifier>\n");
        }
        currentToken++;
        ret.append(compileParameterList()); // advances past final )

        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "{ expected.");
        }
        ret.append("<symbol> { </symbol>");
        currentToken++;

        while (!tokens[currentToken].equals("}")) {
            compileStatement();
        }
        ret.append("<symbol> } </symbol>");
        return ret.toString();
    }

    /** for searching keywords */
    private boolean keyword(String token) {
        for (String s: keywords) {
            if (token.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
