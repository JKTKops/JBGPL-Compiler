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
        boolean found = false;
        Node traverser = topNode;
        while (traverser != null) {
            if (traverser.localVars.containsKey(identifier)) {
                found = true;
                break;
            }
            traverser = traverser.nextNode;
        }
        return found;
    }

    boolean scopeContains(String identifier, String type) {
        boolean found = false;
        Node traverser = topNode;
        while (traverser != null) {
            if (traverser.localVars.containsKey(identifier)) {
                if (traverser.localVars.get(identifier).equals(type)) {
                    found = true;
                    break;
                }
            }
            traverser = traverser.nextNode;
        }
        return found;
    }

    String getType(String identifier) {
        Node traverser = topNode;
        while (traverser != null) {
            if (traverser.localVars.containsKey(identifier)) {
                return traverser.localVars.get(identifier);
            }
            traverser = traverser.nextNode;
        }
        return "Identifier not found";
    }

    private class Node {
        private HashMap<String, String> localVars;
        private Node nextNode;

        Node() {
            localVars = new HashMap<>();
            nextNode = null;
        }

        private Node(Node next) {
            localVars = new HashMap<>();
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
