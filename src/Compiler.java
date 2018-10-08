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
        StringBuilder ret = new StringBuilder("<class>\n");
        if (!tokens[currentToken].equals("class")) {
            throw new SyntaxException(tokens, currentToken, "'class' expected");
        }
        ret.append("<keyword> class </keyword>\n");
        if (!tokens[++currentToken].matches(CLASS_IDEN)) {
            throw new SyntaxException(tokens, currentToken, "Class names must match " + CLASS_IDEN);
        }
        ret.append("<identifier> ").append(tokens[currentToken]).append(" </identifier>\n");
        if (!tokens[++currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "'{' expected");
        }
        ret.append("<symbol> { </symbol>\n");

        currentToken++;
        while (!tokens[currentToken].equals("}")) {
            boolean isStatic = tokens[currentToken].equals("static");
            if (isStatic) {
                currentToken++;
            }
            if (!tokens[currentToken + 2].equals("(")) {
                ret.append(compileClassVarDec(isStatic));
            } else {
                ret.append(compileSubroutineDec(isStatic));
            }
        }
        ret.append("<symbol> } </symbol>\n");
        ret.append("</class>");
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String compileClassVarDec(boolean varStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<classVarDec>\n");
        if (varStatic) {
            ret.append("<keyword> static </keyword>\n");
        }

        ret.append(compileType(false));
        ret.append(compileNormIden());

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

                ret.append(compileNormIden());
            }
            ret.append("<symbol> ; </symbol>\n");
            ret.append("</classVarDec>\n");
            currentToken++;
        }
        return ret.toString();
    }

    /** currentToken initially expects identifier */
    private String compileAssignment() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<assignment>\n");
        ret.append(compileNormIden());
        ret.append("<symbol> = </symbol>\n"); // only ever called if there's an = here so
        currentToken++;
        //ret.append(compileExpression());
        currentToken+=2; // compileExpression will advance past this when we write it

        ret.append("</assignment>\n");
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String compileSubroutineDec(boolean subStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<subroutineDec>\n");
        if (subStatic) {
            ret.append("<keyword> static </keyword>\n");
        }
        ret.append(compileType(true));
        ret.append(compileNormIden());
        ret.append(compileParameterList()); // advances past final )

        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "{ expected.");
        }
        ret.append("<symbol> { </symbol>\n");
        currentToken++;

        while (!tokens[currentToken].equals("}")) {
            //compileStatement();
            currentToken++;
        }
        ret.append("<symbol> } </symbol>\n");
        ret.append("</subroutineDec>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken initially expects ( */
    private String compileParameterList()  throws SyntaxException {
        if (!tokens[currentToken].equals("(")) {
            throw new SyntaxException(tokens, currentToken, "( expected.");
        }
        StringBuilder ret = new StringBuilder("<symbol> ( </symbol>\n");
        ret.append("<parameterList>\n");
        currentToken++;

        while (!tokens[currentToken].equals(")")) {
            ret.append(compileType(false));
            ret.append(compileNormIden());
            if (tokens[currentToken].equals(",")) {
                ret.append("<symbol> , </symbol>\n");
                currentToken++;
                if (tokens[currentToken].equals(")")) {
                    throw new SyntaxException(tokens, currentToken, "Variable type expected.");
                }
            } else if (!tokens[currentToken].equals(")")) {
                throw new SyntaxException(tokens, currentToken, "Cannot resolve symbol, ')' expected.");
            }
        }

        ret.append("</parameterList>\n");
        ret.append("<symbol> ) </symbol>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken expects type */
    private String compileType(boolean subroutine) throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        String type = tokens[currentToken];
        if (!(type.equals("int") || type.equals("char") || type.equals("bool") || type.matches(CLASS_IDEN))) {
            if (!subroutine) {
                throw new SyntaxException(tokens, currentToken, "Variable type expected.");
            } else  if (!type.equals("void")) {
                throw new SyntaxException(tokens, currentToken, "Type expected.");
            }
        } else if (type.equals("int") || type.equals("char") || type.equals("bool") || type.equals("void")) {
            ret.append("<keyword> ").append(type).append(" </keyword>\n");
        } else {
            ret.append("<identifier> ").append(type).append(" </identifier>\n");
        }
        currentToken++;
        return ret.toString();
    }
    /** currentToken expects identifier */
    private String compileNormIden() throws SyntaxException {
        if (!tokens[currentToken].matches(NORM_IDEN)) {
            throw new SyntaxException(tokens, currentToken, "Identifier expected.");
        }
        if (keyword(tokens[currentToken])) {
            throw new SyntaxException(tokens, currentToken, "Keyword not allowed here.");
        }
        return "<identifier> " + tokens[currentToken++] + " </identifier>\n";
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
