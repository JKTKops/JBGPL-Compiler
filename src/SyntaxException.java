public class SyntaxException extends Exception {
    private String error;

    SyntaxException(String[] tokens, int currentToken, String error) {
        this.error = "Token " + (currentToken+1) + ":\nfound: " + tokens[currentToken] + "\n" + error;
    }

    @Override
    public String toString() {
        return error;
    }
}
