package minijava.spiglet_generation.structures;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The Emitter object is initialized with a FileOutputStream and is responsible for writing
 * the produced Spiglet code to that stream.
 */
public class Emitter {

    private final FileOutputStream outputStream;

    public Emitter(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Emits given text with parameters, as is.
     */
    public String emit(String text, Object... params) throws IOException {
        this.outputStream.write(String.format(text, params).getBytes());
        return text;
    }

    /**
     * Emits indented.
     */
    public String emiti(String text, Object... params) throws IOException {
        text = "\t".concat(text);
        return this.emit(text, params);
    }

    /**
     * Emits with new line.
     */
    public String emitln(String text, Object... params) throws IOException {
        text = text.concat("\n");
        return this.emit(text, params);
    }

    /**
     * Emits with new line indented.
     */
    public String emitlni(String text, Object... params) throws IOException {
        text = "\t".concat(text);
        return this.emitln(text, params);
    }

    /**
     * Emits a new line.
     */
    public void nl() throws IOException {
        this.emitln("");
    }

    /**
     * Flushes the output stream associated with the emitter.
     */
    public void flush() throws IOException {
        this.outputStream.flush();
    }

}
