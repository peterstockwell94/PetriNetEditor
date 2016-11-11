import java.io.Reader;
import java.io.IOException;
import java.io.Writer;

/**
 * This class handles reading and writing of Petri Net representations
 */
public interface PetriNetIO
{
    /**
     * Read the description of a Petri Net from a Reader and convert it to a Petri Net
     * @param r The Reader to read from
     * @param p The PetriNet to write into
     * @throws IOException If an I/O error occurs
     * @throws PetriNetFormatException If the description of a Petri Net isn't formatted correctly
     */
    public void read (Reader r, PetriNet p) throws IOException, PetriNetFormatException;

    /**
     * Write a representation of the Petri Net to the Writer
     * @param w The Writer to write to
     * @param p The Petri Net to write
     * @throws IOException If an I/O error occurs
     */
    public void write(Writer w, PetriNet p) throws IOException;
}
