import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Assembler {
    public static void main(String[] args) {
        String fileName = "";
        System.out.print("Filename of a .asm file (without .asm): ");
        Scanner sc = new Scanner(System.in);
        fileName = "C:/Users/zergl/IdeaProjects/JBGPL Compiler/src/files/" +sc.nextLine();
        File file = new File(fileName + ".asm");
        AsmParser parser = new AsmParser(file);
        try {
            PrintWriter writer = new PrintWriter(fileName + ".hack", StandardCharsets.UTF_8);

            SymbolTable symbolTable = new SymbolTable();

            /* START PASS ONE*/
            parser.advance();
            for (int line = 0; parser.hasMoreCommands(); line++, parser.advance()) {
                if (parser.commandType().equals("L_COMMAND")) {
                    symbolTable.addEntry(parser.symbol(), line);
                    line--;
                }
            }

            /* START PASS TWO */
            int nextMemoryAddress = 16;
            parser = new AsmParser(file);
            parser.advance();
            while(parser.hasMoreCommands()) {
                String cType = parser.commandType();
                String toWrite;
                switch (cType) {
                    case "A_COMMAND":
                        if (parser.symbol().matches("[0-9]*")) {
                            int address = Integer.parseInt(parser.symbol());
                            toWrite = Integer.toBinaryString(0x10000 | address).substring(1);
                            break;
                        } else {
                            if (symbolTable.contains(parser.symbol())) {
                                int address = symbolTable.getAddressOf(parser.symbol());
                                toWrite = Integer.toBinaryString(0x10000 | address).substring(1);
                                break;
                            } else {
                                int address = nextMemoryAddress;
                                nextMemoryAddress++;
                                symbolTable.addEntry(parser.symbol(), address);
                                toWrite = Integer.toBinaryString(0x10000 | address).substring(1);
                                break;
                            }
                        }
                    case "C_COMMAND":
                        toWrite = AsmTranslator.assemble(
                                parser.dest(), parser.comp(), parser.jump());
                        break;
                    default:
                        parser.advance();
                        continue;
                }
                System.out.println(toWrite);
                writer.println(toWrite);
                parser.advance();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Something went wrong." + e);
        }
    }
}
