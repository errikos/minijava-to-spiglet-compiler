package minijava.semantic_check;

import minijava.CompilationException;

/**
 * Semantic check exception class.
 */
public class SemanticCheckException extends CompilationException {

    public SemanticCheckException(String semanticCheckErrorString) {
        super(semanticCheckErrorString);
    }

}
