import java.util.HashMap;

class VarStack {
    private Node topNode;

    VarStack() {
        topNode = new Node();
    }

    void downScope() {
        topNode = topNode.addNode();
    }

    void upScope() {
        topNode = topNode.deleteNode();
    }

    void addVar(String identifier, String type) {
        topNode.localVars.put(identifier, type);
    }

    /** returns false if the cast fails */
    boolean castVar(String identifier, String type) {
        if (!topNode.localVars.containsKey(identifier)) {
            return false;
        }
        topNode.localVars.replace(identifier, type);
        return true;
    }

    boolean scopeContains(String identifier) {
        return topNode.localVars.containsKey(identifier);
    }

    boolean scopeContains(String identifier, String type) {
        if (!topNode.localVars.containsKey(identifier)) {
            return false;
        }
        return !topNode.localVars.get(identifier).equals(type);
    }

    private class Node {
        private HashMap<String, String> localVars;
        private Node nextNode;

        Node() {
            localVars = new HashMap<>();
            nextNode = null;
        }

        private Node(Node next) {
            nextNode = next;
        }

        Node addNode() {
            return new Node(this);
        }

        Node deleteNode() {
            return nextNode;
        }
    }
}
