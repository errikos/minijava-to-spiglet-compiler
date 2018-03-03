package minijava;

/**
 * Top level compilation exception class.
 */
public class CompilationException extends Exception {

    public CompilationException(String compilationErrorString) {
        super(compilationErrorString);
    }

}
