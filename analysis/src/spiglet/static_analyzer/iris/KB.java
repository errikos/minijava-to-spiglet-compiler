package spiglet.static_analyzer.iris;

import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.optimisations.magicsets.MagicSets;
import org.deri.iris.storage.IRelation;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class KB {

    private final IKnowledgeBase knowledgeBase;

    public KB(String factsDir) throws Exception {
        Parser parser = new Parser();

        // Import facts into the KB.
        final Map<IPredicate, IRelation> factsMap = new HashMap<>();
        final File factsDirectory = new File(factsDir);
        if (factsDirectory.isDirectory()) {
            for (final File fileEntry : factsDirectory.listFiles()) {
                Reader factsReader = new FileReader(fileEntry);
                parser.parse(factsReader);
                // Retrieve the facts and put them in the KB facts map.
                factsMap.putAll(parser.getFacts());
            }
        }

        // Create a list to hold the rules.
        final List<IRule> rules = new ArrayList<>();

        // Import rules into the KB.
        File rulesDir = new File("analysis_logic");
        if (rulesDir.isDirectory()) {
            for (final File fileEntry: rulesDir.listFiles()) {
                Reader rulesReader = new FileReader(fileEntry);
                parser.parse(rulesReader);
                // Add all found rules into the rules list.
                rules.addAll(parser.getRules());
            }
        }

        // Create a default configuration.
        Configuration configuration = new Configuration();
        // Enable Magic Sets together with rule filtering.
        configuration.programOptmimisers.add(new MagicSets());

        this.knowledgeBase = new KnowledgeBase(factsMap, rules, configuration);
    }

    public IRelation execute(IQuery query, List<IVariable> variableBindings) throws EvaluationException {
        return this.knowledgeBase.execute(query, variableBindings);
    }

}
