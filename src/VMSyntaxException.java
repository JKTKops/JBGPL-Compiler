class VMSyntaxException extends Exception {
    private String lineWithError;

    VMSyntaxException(String setLineWithError) {
        lineWithError = setLineWithError;
    }

    @Override
    public String toString() {
        return "Failed to interpret VM code: " + lineWithError;
    }
}
