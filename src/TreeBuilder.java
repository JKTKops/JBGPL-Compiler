public class TreeBuilder {
    private Compiler parent;
    private String className = "";
    private String[] tokens;
    private int currentToken;
    private final String NORM_IDEN = "[a-z][_A-Za-z0-9]*";
    private final String CLASS_IDEN = "[A-Z][_A-Za-z0-9]*";
    private final String[] keywords = {"class", "static", "int", "char", "bool", "void", "true",
                               "false", "null", "this", "if", "else", "while", "for", "return", "new"};
    private VarStack scopeVars;
    private String currentMethod = "";

    TreeBuilder(String[] t, Compiler c) {
        parent = c;
        tokens = t;
        scopeVars = new VarStack();
        currentToken = 0;
        while (!tokens[currentToken].equals("class")) {
            currentToken++;
        }
    }

    String classTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<class>\n");
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
        className = tokens[currentToken];
        ret.append("<identifier> ").append(tokens[currentToken]).append(" </identifier>\n");
        if (!tokens[++currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "'{' expected");
        }
        ret.append("<symbol> { </symbol>\n");

        currentToken++;
        while (!tokens[currentToken].equals("}")) {
            String token = tokens[currentToken];
            if (!(token.equals("int") || token.equals("bool") || token.equals("char") || token.equals("void")
                  || token.equals("static") || validClass(token))) {
                System.out.println(ret);
                throw new SyntaxException(tokens, currentToken, "Class variable or method declaration expected.");
            }
            boolean isStatic = token.equals("static");
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

        String type = tokens[currentToken];
        if (tokens[currentToken+1].equals("[")) {
            if (!tokens[currentToken+2].equals("]")) {
                throw new SyntaxException(tokens, currentToken, "] expected.");
            }
            type += "[]";
        }
        ret.append(varTypeTree(false));
        if (scopeVars.scopeContains(tokens[currentToken])) {
            throw new SyntaxException(tokens, currentToken, "Identifier already defined in scope.");
        }
        scopeVars.addVar(tokens[currentToken], type);
        ret.append(normIdenTree());

        if (tokens[currentToken].equals("=")) {
            ret.append("<symbol> = </symbol>\n");
            currentToken++;
            //ret.append(expressionTree(type));
            currentToken++; // expressionTree will do this part for us
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

                if (scopeVars.scopeContains(tokens[currentToken])) {
                    throw new SyntaxException(tokens, currentToken, "Identifier already defined in scope.");
                }
                scopeVars.addVar(tokens[currentToken], type);
                ret.append(normIdenTree());
            }
        }
        if (!tokens[currentToken].equals(";")) {
            throw new SyntaxException(tokens, currentToken, "; expected.");
        }
        ret.append("<symbol> ; </symbol>\n");
        ret.append("</classVarDec>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String subroutineDecTree(boolean subStatic) throws SyntaxException {
        StringBuilder ret = new StringBuilder("<subroutineDec>\n");
        if (subStatic) {
            ret.append("<keyword> static </keyword>\n");
        }
        ret.append(varTypeTree(true));
        currentMethod = tokens[currentToken];
        ret.append(normIdenTree());

        scopeVars.downScope();
        ret.append(parameterListTree()); // advances past final )

        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "{ expected.");
        }
        ret.append("<symbol> { </symbol>\n");
        ret.append("<subroutineBody>\n");
        currentToken++;

        boolean hasReturn = false;
        while (!tokens[currentToken].equals("}") && !hasReturn) {
            if (tokens[currentToken].equals("return")) {
                hasReturn = true;
            }
            ret.append(statementTree());
        }
        if (hasReturn && !tokens[currentToken].equals("}")) {
            throw new SyntaxException(tokens, currentToken, "Unreachable statement.");
        }
        if (!(hasReturn || parent.getMethods().get(currentMethod).equals("void"))) {
            throw new SyntaxException(tokens, currentToken, "Return statement required.");
        }

        scopeVars.upScope();
        ret.append("</subroutineBody>\n");
        ret.append("<symbol> } </symbol>\n");
        ret.append("</subroutineDec>\n");
        currentToken++;
        currentMethod = "";
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
            if (scopeVars.scopeContains(tokens[currentToken])) {
                throw new SyntaxException(tokens, currentToken, "Identifier already defined in scope.");
            }
            scopeVars.addVar(tokens[currentToken], tokens[currentToken - 1]);
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
            return returnTree();
        } else if (tokens[currentToken].equals("int") || tokens[currentToken].equals("bool")
                || tokens[currentToken].equals("char") || (validClass(tokens[currentToken]) && !tokens[currentToken + 1].equals("."))) {
            return localVarDecTree();
        } else {
            if (tokens[currentToken + 1].matches("[=\\[]")) {
                if (tokens[currentToken+1].equals("=")) {
                    return assignmentTree();
                }
                int i = currentToken;
                while (!tokens[i].equals("]")) {
                    i++;
                }
                i++;
                if (!tokens[i].matches("[=.]")) {
                    throw new SyntaxException(tokens, i, "Not a statement.");
                } else if (tokens[i].equals("=")) {
                    return assignmentTree();
                } else {
                    //return callTree();
                }
            } else if (tokens[currentToken].equals("else")) {
                throw new SyntaxException(tokens, currentToken, "Else without if.");
            }
            //return callTree();
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
        currentToken++;
        //ret.append(expressionTree("boolean"));
        if (!tokens[currentToken].equals(")")) {
            throw new SyntaxException(tokens, currentToken, ") expected.");
        }
        ret.append("<symbol> ) </symbol>\n");
        currentToken++;

        ret.append(blockBodyTree());
        ret.append("</whileStatement>\n");
        return ret.toString();
    }

    /** token initially expects 'if' */
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
        currentToken++;
        //ret.append(expressionTree("boolean"));
        if (!tokens[currentToken].equals(")")) {
            throw new SyntaxException(tokens, currentToken, ") expected.");
        }
        ret.append("<symbol> ) </symbol>\n");
        currentToken++;
        ret.append(blockBodyTree());
        return ret.toString();
    }

    /** currentToken initially expects { */
    private String blockBodyTree() throws SyntaxException {
        scopeVars.downScope();
        StringBuilder ret = new StringBuilder();
        if (!tokens[currentToken].equals("{")) {
            throw new SyntaxException(tokens, currentToken, "{ expected.");
        }
        ret.append("<symbol> { </symbol>\n");
        currentToken++;
        while (!tokens[currentToken].equals("}")) {
            ret.append(statementTree());
        }
        ret.append("<symbol> } </symbol>\n");
        currentToken++;
        scopeVars.upScope();
        return ret.toString();
    }

    /** currentToken initially expects "return" */
    private String returnTree() throws SyntaxException {
        if (currentMethod.equals("")) { // this should never trigger, but here anyway for safety.
            throw new SyntaxException(tokens, currentToken, "Return statement outside method.");
        }
        StringBuilder ret = new StringBuilder("<returnStatement>\n<keyword> return </keyword>\n");
        currentToken++;
        //ret.append(expressionTree(parent.getMethods().get(currentMethod)));
        currentToken++;
        if (!tokens[currentToken].equals(";")) {
            throw new SyntaxException(tokens, currentToken, "; expected.");
        }
        ret.append("<symbol> ; </symbol>\n");
        ret.append("</returnStatement>\n");
        currentToken++;
        return ret.toString();
    }

    /** currentToken initially expects type */
    private String localVarDecTree() throws SyntaxException {
        StringBuilder ret = new StringBuilder("<localVarDec>\n");

        String type = tokens[currentToken];
        if (tokens[currentToken+1].equals("[")) {
            if (!tokens[currentToken+2].equals("]")) {
                throw new SyntaxException(tokens, currentToken, "] expected.");
            }
            type += "[]";
        }
        ret.append(varTypeTree(false));
        if (scopeVars.scopeContains(tokens[currentToken])) {
            throw new SyntaxException(tokens, currentToken, "Identifier already defined in scope.");
        }
        scopeVars.addVar(tokens[currentToken], type);
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

                if (scopeVars.scopeContains(tokens[currentToken])) {
                    throw new SyntaxException(tokens, currentToken, "Identifier already defined in scope.");
                }
                scopeVars.addVar(tokens[currentToken], type);
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
        String type = scopeVars.getType(tokens[currentToken]);
        if (type.equals("Identifier not found.")) {
            throw new SyntaxException(tokens, currentToken, type);
        }
        ret.append(normIdenTree());

        if (tokens[currentToken].equals("[")) {
            ret.append("<symbol> [ </symbol>\n");
            currentToken++;
            //ret.append(expressionTree("int"));
            if (!tokens[currentToken].equals("]")) {
                throw new SyntaxException(tokens, currentToken, "] expected.");
            }
            ret.append("<symbol> ] </symbol>\n");
            currentToken++;
        }

        if (!tokens[currentToken].equals("=")) {
            throw new SyntaxException(tokens, currentToken, "= expected."); // I don't think it's possible to hit this
        }
        ret.append("<symbol> = </symbol>\n");
        currentToken++;
        //ret.append(expressionTree(type));
        currentToken++; // expressionTree will advance past this when we write it
        if (!tokens[currentToken].equals(";")) {
            throw new SyntaxException(tokens, currentToken, "; expected.");
        }
        ret.append("<symbol> ; </symbol>\n");

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
        if (tokens[currentToken].equals("[")) {
            currentToken++;
            if (!tokens[currentToken].equals("]")) {
                throw new SyntaxException(tokens, currentToken, "] expected.");
            }
            ret.append("<symbol> [] </symbol>\n");
            currentToken++;
        }
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
        for (String s: parent.getClasses()) {
            if (s.equals(potentialClass)) {
                return true;
            }
        }
        return false;
    }
}
