package minijava.spiglet_generation;

import minijava.semantic_check.structures.SymbolTable;
import minijava.spiglet_generation.visitors.SpigletProducer;
import minijava.syntaxtree.Goal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Wrapper object for the Spiglet generation process.
 */
public class SpigletGenerator {

    private final Goal syntaxTree;
    private final SymbolTable symbolTable;
    private final FileOutputStream outputStream;
    private final SpigletProducer spigletProducer;

    public SpigletGenerator(Goal syntaxTree, SymbolTable symbolTable, String fileName) throws FileNotFoundException {
        this.syntaxTree = syntaxTree;
        this.symbolTable = symbolTable;
        this.outputStream = new FileOutputStream(String.format("%s.spg", fileName.split("\\.java")[0]));
        this.spigletProducer = new SpigletProducer(this.outputStream);
    }

    public void run() throws Exception {
        this.syntaxTree.accept(this.spigletProducer, this.symbolTable);
    }

}
