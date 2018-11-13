public class Parser {
    private Lexer lexer;
    private ConcreteSyntaxTree CST;

    public Parser(Lexer setLexer, ConcreteSyntaxTree setCST) {
        lexer = setLexer;
        CST = setCST;
    }

    public ConcreteSyntaxTree.NonterminalSymbol parse() {
        ConcreteSyntaxTree.NonterminalSymbol root = CST.getRoot();
        root.addChild(parseProgram());
        return root;
    }
    public ConcreteSyntaxTree.NonterminalSymbol parseProgram() {
        ConcreteSyntaxTree.NonterminalSymbol program = new ConcreteSyntaxTree.NonterminalSymbol("Program");

        if (lexer.peek(1) == null) {
            program.addChild(ConcreteSyntaxTree.EPSILON);
            return program;
        }
        program.addChild(parseFile());
        program.addChild(parseProgram());

        return program;
    }
}
