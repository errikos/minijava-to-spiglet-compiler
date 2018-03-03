package spiglet.static_analyzer;

import spiglet.parser.SpigletParser;
import spiglet.static_analyzer.fact_producer.visitors.FactGenerator;
import spiglet.static_analyzer.iris.Evaluator;
import spiglet.static_analyzer.iris.KB;
import spiglet.syntaxtree.Goal;

import java.io.FileReader;

/**
 *
 */
public class StaticAnalyzer {

    private final String spigletFileName;
    private final String outputDir;

    public StaticAnalyzer(String spigletFileName, String outputDir) {
        this.spigletFileName = spigletFileName;
        this.outputDir = outputDir;
    }

    public void run() throws Exception {
        this.performStaticAnalysis();
//        this.optimizeCode();
        System.out.println(String.format(":: (!) %s analyzed successfully", this.spigletFileName));
    }

    /**
     * Performs the static analysis.
     */
    private void performStaticAnalysis() throws Exception {
        this.generateFacts();
        KB kb = new KB(this.outputDir);
        Evaluator evaluator = new Evaluator(kb);
        evaluator.evaluateQueries(this.outputDir);
    }

    /**
     * Builds the Datalog knowledge base, by visiting the input Spiglet syntax tree
     * and producing the facts required for the analysis.
     */
    private void generateFacts() throws Exception {
        FileReader reader = new FileReader(this.spigletFileName);
        SpigletParser parser = new SpigletParser(reader);
        Goal syntaxTree = parser.Goal();
        FactGenerator factGenerator = new FactGenerator(this.outputDir);
        syntaxTree.accept(factGenerator);
        reader.close();
    }

//    private void optimizeCode() {
//
//    }

}
