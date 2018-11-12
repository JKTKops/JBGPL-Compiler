import java.util.ArrayList;

public class ConcreteSyntaxTree {
    /** Represents the ε production. */
    private final TerminalSymbol EPSILON = new TerminalSymbol("ε");

    private NonterminalSymbol root;


    private abstract class Symbol {
        String symbol;

        Symbol(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private class TerminalSymbol extends Symbol {
        TerminalSymbol(String symbol) {
            super(symbol);
        }
    }

    private class NonterminalSymbol extends Symbol {
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
