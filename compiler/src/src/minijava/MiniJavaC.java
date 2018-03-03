package minijava;

import minijava.parser.MiniJavaParser;
import minijava.semantic_check.SemanticChecker;
import minijava.semantic_check.structures.SymbolTable;
import minijava.spiglet_generation.SpigletGenerator;
import minijava.syntaxtree.Goal;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * The main MiniJava compiler class.
 */
public class MiniJavaC {

    public static void main(String []args) {

        if (args.length == 0) {
            System.err.println("Usage: java MiniJavaC [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        for (int i = 0; i != args.length; ++i) {

            FileInputStream inpuStream = null;
            SemanticChecker semanticChecker;
            SpigletGenerator spigletGenerator;

            try {
                // Create the parser
                inpuStream = new FileInputStream(args[i]);
                MiniJavaParser miniJavaParser = new MiniJavaParser(inpuStream);

                // Parse input and create the AST
                Goal goal = miniJavaParser.Goal();

                // Perform semantic check
                semanticChecker = new SemanticChecker(goal);
                SymbolTable symbolTable = semanticChecker.run();

                // Produce intermediate code (Spiglet)
                spigletGenerator = new SpigletGenerator(goal, symbolTable, args[i]);
                spigletGenerator.run();

            } catch (CompilationException e) {
                System.err.println(String.format("MiniJavaC: error in input file %s: ", args[i]));
                System.err.println(e.getMessage());
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (inpuStream != null) inpuStream.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                System.err.println(String.format("%s compiled successfully", args[i]));
            }

        }
        System.exit(0);
    }

}
