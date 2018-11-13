import java.util.ArrayList;

public class ConcreteSyntaxTree {
    /** Represents the ε production. */
    public static final TerminalSymbol EPSILON = new TerminalSymbol("ε");

    private NonterminalSymbol root;

    public ConcreteSyntaxTree() {
        root = new NonterminalSymbol("Start");
    }

    public NonterminalSymbol getRoot() {
        return root;
    }

    abstract static class Symbol {
        String symbol;

        Symbol(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    static class TerminalSymbol extends Symbol {
        TerminalSymbol(String symbol) {
            super(symbol);
        }
    }

    static class NonterminalSymbol extends Symbol {
        private ArrayList<Symbol> children;

        NonterminalSymbol(String symbol) {
            super(symbol);
            children = new ArrayList<>();
        }

        public void addChild(Symbol toAdd) {
            if (toAdd == EPSILON) return;
            children.add(toAdd);
        }

        public ArrayList<Symbol> getChildren() {
            return children;
        }
    }
}
