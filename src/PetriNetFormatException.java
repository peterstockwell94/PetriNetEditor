public class PetriNetFormatException extends Exception {
    private int lineNumber;

    public PetriNetFormatException(int lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public String toString() {
        return lineNumber + ":0:" + super.toString();
    }
}
