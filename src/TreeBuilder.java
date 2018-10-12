import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TreeBuilder {
    private String[] tokens;
    private int currentToken = 0;
    private final String NORM_IDEN = "[a-z][_A-Za-z0-9]*";
    private final String CLASS_IDEN = "[A-Z][_A-Za-z0-9]*";
    private final String[] keywords = {"class", "static", "int", "char", "bool", "void", "true",
                               "false", "null", "this", "if", "else", "while", "for", "return", "new"};
    private ArrayList<String> classes;
    private VarStack scopeVars = new VarStack();

    TreeBuilder(String[] t, String[] c) {
        tokens = t;
        classes = new ArrayList<>(Arrays.asList(c));
    }

    String classTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<class>\n");
        /* this while loop needs to be fixed for later, if we want jbgpl.String rather than just String */
        while (tokens[currentToken].equals("import")) {
            ret.append("<importStatement>\n<identifier> ").append(tokens[++currentToken]).append(" </identifier>\n");
            classes.add(tokens[currentToken]);
            currentToken++;
            if (!tokens[currentToken].equals(";")) {
                throw new SyntaxException(tokens, currentToken, "; expected.");
            }
            ret.append("<symbol> ; </symbol>\n</importStatement>\n");
            currentToken++;
        }

        if (!tokens[currentToken].equals("class")) {
            throw new SyntaxException(tokens, currentToken, "'class' expected");
        }
        ret.append("<keyword> class </keyword>\n");
        if (!tokens[++currentToken].matches(CLASS_IDEN)) {
            throw new SyntaxException(tokens, currentToken, "Class names must match " + CLASS_IDEN);
        } else if (!validClass(tokens[currentToken])) {
            String temp = "Class " + tokens[currentToken] + " must be in a file named " + tokens[currentToken] + ".jbgpl";
            throw new SyntaxException(tokens, currentToken, temp);
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
                ret.append(classVarDecTree(isStatic));
            } else {
                ret.append(subroutineDecTree(isStatic));
            }
        }
        ret.append("<symbol> } </symbol>\n");
        ret.append("</class>");
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String classVarDecTree(boolean varStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<classVarDec>\n");
        if (varStatic) {
            ret.append("<keyword> static </keyword>\n");
        }

        ret.append(varTypeTree(false));
        ret.append(normIdenTree());

        if (tokens[currentToken].equals("=")) {
            ret.append("<symbol> ; </symbol>\n</classVarDec>\n");
            currentToken--;
            ret.append(assignmentTree());
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

                ret.append(normIdenTree());
            }
            ret.append("<symbol> ; </symbol>\n");
            ret.append("</classVarDec>\n");
            currentToken++;
        }
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String subroutineDecTree(boolean subStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<subroutineDec>\n");
        if (subStatic) {
            ret.append("<keyword> static </keyword>\n");
        }
        ret.append(varTypeTree(true));
        ret.append(normIdenTree());
        Compiler.definedMethods.add(tokens[currentToken - 1]);
        ret.append(parameterListTree()); // advances past final )

        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "{ expected.");
        }
        ret.append("<symbol> { </symbol>\n");
        ret.append("<subroutineBody>\n");
        currentToken++;

        while (!tokens[currentToken].equals("}")) {
            ret.append(statementTree());
        }
        ret.append("</subroutineBody>\n");
        ret.append("<symbol> } </symbol>\n");
        ret.append("</subroutineDec>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken initially expects ( */
    private String parameterListTree()  throws SyntaxException {
        if (!tokens[currentToken].equals("(")) {
            throw new SyntaxException(tokens, currentToken, "( expected.");
        }
        StringBuilder ret = new StringBuilder("<symbol> ( </symbol>\n");
        ret.append("<parameterList>\n");
        currentToken++;

        while (!tokens[currentToken].equals(")")) {
            ret.append(varTypeTree(false));
            ret.append(normIdenTree());
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

    /** expects first token of statement */
    private String statementTree() throws SyntaxException {
        if (tokens[currentToken].equals("while")) {
            return whileTree();
        } else if (tokens[currentToken].equals("if")) {
            return ifElseBlockTree();
        } else if (tokens[currentToken].equals("for")) {
            //return compileFor();
        } else if (tokens[currentToken].equals("return")) {
            //return compileReturn();
        } else if (tokens[currentToken].equals("int") || tokens[currentToken].equals("bool")
                || tokens[currentToken].equals("char") || (validClass(tokens[currentToken]) && !tokens[currentToken + 1].equals("."))) {
            return localVarDecTree();
        } else {
            if (tokens[currentToken + 1].equals("=")) {
                return assignmentTree();
            } else if (tokens[currentToken].equals("else")) {
                throw new SyntaxException(tokens, currentToken, "Else without if.");
            }
            //return compileCall();
        }
        throw new SyntaxException(tokens, currentToken, "Not a Statement.");
    }

    /** token initially expects 'while'
     * Note there is indirect recursion between this method and statementTree
     */
    private String whileTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<whileStatement>\n<keyword> while </keyword>\n");
        currentToken++;
        if (!tokens[currentToken].equals("(")) {
            throw new SyntaxException(tokens, currentToken, "( expected.");
        }
        ret.append("<symbol> ( </symbol>\n");
        //ret.append(compileExpression());
        currentToken += 2; // compileExpression() will advance past this
        ret.append("<symbol> ) </symbol>\n");

        ret.append(blockBodyTree());
        ret.append("</whileStatement>\n");
        return ret.toString();
    }

    private String ifElseBlockTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<ifStatement>\n<keyword> if </keyword>");
        currentToken++;
        ret.append(ifTree());

        boolean finalElse = false; // prevents accidentally interpreting something like if () {} else {} else {}
        while (tokens[currentToken].equals("else") && !finalElse) {
            currentToken++;
            if (tokens[currentToken].equals("if")) {
                ret.append("<keyword> else if </keyword>\n");
                currentToken++;
                ret.append(ifTree());
            } else {
                ret.append("<keyword> else </keyword>\n");
                ret.append(blockBodyTree());
                finalElse = true;
            }
        }

        ret.append("</ifStatement>\n");
        return ret.toString();
    }

    /** currentToken initially expects ( */
    private String ifTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        if (!tokens[currentToken].equals("(")) {
            throw new SyntaxException(tokens, currentToken, "( expected.");
        }
        ret.append("<symbol> ( </symbol>\n");
        //ret.append(compileExpression());
        currentToken += 2; // compileExpression() will advance past this
        ret.append("<symbol> ) </symbol>\n");
        ret.append(blockBodyTree());
        return ret.toString();
    }

    /** currentToken initially expects { */
    private String blockBodyTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "( expected.");
        }
        ret.append("<symbol> { </symbol>\n");
        currentToken++;
        while (!tokens[currentToken].equals("}")) {
            ret.append(statementTree());
        }
        ret.append("<symbol> } </symbol>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String localVarDecTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<localVarDec>\n");
        ret.append(varTypeTree(false));
        ret.append(normIdenTree());

        if (tokens[currentToken].equals("=")) {
            ret.append("<symbol> ; </symbol>\n</localVarDec>\n");
            currentToken--;
            ret.append(assignmentTree());
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

                ret.append(normIdenTree());
            }
            ret.append("<symbol> ; </symbol>\n");
            ret.append("</localVarDec>\n");
            currentToken++;
        }
        return ret.toString();
    }

    /** currentToken initially expects identifier */
    private String assignmentTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<assignment>\n");
        ret.append(normIdenTree());
        ret.append("<symbol> = </symbol>\n"); // only ever called if there's an = here so
        currentToken++;
        //ret.append(compileExpression());
        currentToken+=2; // compileExpression will advance past this when we write it

        ret.append("</assignment>\n");
        return ret.toString();
    }

    /** currentToken expects type */
    private String varTypeTree(boolean subroutine) throws SyntaxException {
        StringBuilder ret = new StringBuilder();
        String type = tokens[currentToken];
        if (!(type.equals("int") || type.equals("char") || type.equals("bool") || validClass(type))) {
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
    private String normIdenTree() throws SyntaxException {
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
    /** for searching the list of classes */
    private boolean validClass(String potentialClass) {
        for (String s: classes) {
            if (s.equals(potentialClass)) {
                return true;
            }
        }
        return false;
    }
}
