package spiglet.static_analyzer.iris;

import org.deri.iris.EvaluationException;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.storage.IRelation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 *
 */
public class Evaluator {

    private final KB kb;
    private final List<IQuery> queries;

    public Evaluator(KB kb) throws Exception {
        this.kb = kb;

        // Import queries.
        Parser parser = new Parser();
        File queriesFile = new File("queries/queries.iris");
        Reader queriesReader = new FileReader(queriesFile);

        // Parse queries file and retrieve queries.
        parser.parse(queriesReader);
        this.queries = parser.getQueries();
    }

    public void evaluateQueries(String outputDir) throws Exception {
        FileWriter resultsWriter = new FileWriter(outputDir + "/results.iris");
        int n = 0;
        // Evaluate all queries over the knowledge base.
        for (IQuery query : this.queries) {
            List<IVariable> variableBindings = new ArrayList<>();
            IRelation relation = this.kb.execute(query, variableBindings);

            // Output the variables.
            resultsWriter.write("\n" + query.toString() + "\n" + variableBindings);

            // Output each tuple in the relation, where the term at position i
            // corresponds to the variable at position i in the variable
            // bindings list.
            TreeSet<ITuple> sortedRelations = new TreeSet<>();
            for (int i = 0; i < relation.size(); i++) {
                sortedRelations.add(relation.get(i));
            }
            for (ITuple t: sortedRelations) {
                resultsWriter.write(t.toString() + "\n");
            }
            n += relation.size();
        }
        resultsWriter.close();
    }

}
