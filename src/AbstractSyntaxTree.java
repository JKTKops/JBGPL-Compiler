import java.util.ArrayList;

public class AbstractSyntaxTree {
    private Node root;

    public AbstractSyntaxTree() {
        root = new Node("ProjectRoot");
    }
    public AbstractSyntaxTree(String projectName) {
        root = new Node(projectName);
    }



    private class Node {
        /** If this Node is terminal, represents this symbol's syntactic type.
         * For example, the symbol "while" has type "keyword" and the symbol ";" has type "symbol."
         *
         * If this Node is <em>not</em> terminal, represents the grammatic type.
         * For example, if this Node holds a while statement, its type is "whileStatement". If this Node
         * holds a class, its type is "class".
         */
        String type;
        /** The symbol held by this Node. null if this Node is nonterminal. */
        String symbol;
        /** This Node's grammatic components. If this Node is terminal, then this list is empty. */
        ArrayList<Node> children;

        /** Information about the location of this Node in the source. -1 if this Node is nonterminal. */
        int lineNumber;
        int charNumber;

        Node(String type) {
            this.type = type;
            this.symbol = null;
            this.lineNumber = -1;
            this.charNumber = -1;
        }
        Node(String type, String symbol, int lineNumber, int charNumber) {
            this.type = type;
            this.symbol = symbol;
            this.lineNumber = lineNumber;
            this.charNumber = charNumber;
            children = new ArrayList<>();
        }

        Node(String type, String symbol, int lineNumber, int charNumber, ArrayList<Node> childNodes) {
            this(type, symbol, lineNumber, charNumber);
            children = childNodes;
        }
    }
}
