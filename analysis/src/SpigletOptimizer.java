import spiglet.static_analyzer.StaticAnalyzer;
import utils.FileUtils;
import utils.FilenameUtils;

import java.io.File;

/**
 *
 */
public class SpigletOptimizer {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Usage: java SpigletOptimizer [file1] [file2] ... [fileN]");
            System.exit(1);
        }

        for (String arg: args) {
            System.out.println(String.format(":: Processing file: %s", arg));
            try {
                // Get input file basename. The output directory will be named that name.
                String inputBaseName = FilenameUtils.getBaseName(arg);
                // Create the output directory (delete first if exists).
                File outputDir = new File(inputBaseName);
                FileUtils.deleteDir(outputDir);
                outputDir.mkdir();
                // Perform static analysis.
                StaticAnalyzer staticAnalyzer = new StaticAnalyzer(arg, inputBaseName);
                staticAnalyzer.run();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            } finally {
                System.out.println(":: Done");
                System.out.println();
            }
        }

    }

}
