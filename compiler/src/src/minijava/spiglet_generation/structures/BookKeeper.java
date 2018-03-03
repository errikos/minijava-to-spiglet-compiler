package minijava.spiglet_generation.structures;

/**
 * The Spiglet producer book-keeper.
 * Keeps track of the labels and temps, hands them and resets them when necessary.
 */
public class BookKeeper {

    private int nextLabel;
    private int nextTemp;

    public int getLabel() {
        return this.nextLabel++;
    }

    public int getTemp() {
        return this.nextTemp++;
    }

    public void resetLabel() {
        this.nextLabel = 1;
    }

    public void resetLabel(int value) {
        this.nextLabel = value;
    }

    public void resetTemp() {
        this.nextTemp = 1;
    }

    public void resetTemp(int value) {
        this.nextTemp = value;
    }

}
