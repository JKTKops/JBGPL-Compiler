import java.util.HashMap;
import java.util.Map;

class SymbolTable {
    private Map<String, Integer> symbolTable;

    SymbolTable() {
        symbolTable = new HashMap<>(39);

        for (int i = 0; i < 16; i++) {
            symbolTable.put("R" + i, i);
        }
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
    }

    void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    int getAddressOf(String symbol) {
        return symbolTable.get(symbol);
    }
}
