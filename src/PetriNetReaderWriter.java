import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Pattern;

public class PetriNetReaderWriter implements PetriNetIO {

    private final String      PLACE = "plc";
    private final String TRANSITION = "trn";
    private final String     ARC_TP = "atp";
    private final String     ARC_PT = "apt";
    private final String INITIALISE = "ini";

    @Override
    public void read(Reader r, PetriNet pn) throws IOException, PetriNetFormatException {
        if (pn == null) throw new IllegalArgumentException("Error reading Petri Net: Null Petri Net");
        BufferedReader br = new BufferedReader(r);
        String line = br.readLine();
        int lineNumber = 1;
        try {
            while (line != null) {
                line = Pattern.compile("\\s+").matcher(line.trim()).replaceAll(" ");
                String tokens[] = line.split(" ");
                switch (tokens[0]) {
                    case PLACE:
                        {
                            if (tokens.length < 4 || (tokens.length > 4 && tokens[4].charAt(0) != '#')) throw new PetriNetFormatException(lineNumber, line);
                            String name = tokens[1];
                            int x = Integer.parseInt(tokens[2]);
                            int y = Integer.parseInt(tokens[3]);
                            pn.newPlace(name, x, y);
                        }
                        break;
                    case TRANSITION:
                        {
                            if (tokens.length < 4 || (tokens.length > 4 && tokens[4].charAt(0) != '#')) throw new PetriNetFormatException(lineNumber, line);
                            String name = tokens[1];
                            int x = Integer.parseInt(tokens[2]);
                            int y = Integer.parseInt(tokens[3]);
                            pn.newTransition(name, x, y);
                        }
                        break;
                    case ARC_PT:
                        {
                            if (tokens.length < 4 || (tokens.length > 4 && tokens[4].charAt(0) != '#')) throw new PetriNetFormatException(lineNumber, line);
                            Place      p = pn.findPlace(tokens[1]);
                            Transition t = pn.findTransition(tokens[2]);
                            int        w = Integer.parseInt(tokens[3]);
                            pn.addArc(p, t, w);
                        }
                        break;
                    case ARC_TP:
                        {
                            if (tokens.length < 4 || (tokens.length > 4 && tokens[4].charAt(0) != '#')) throw new PetriNetFormatException(lineNumber, line);
                            Transition t = pn.findTransition(tokens[1]);
                            Place      p = pn.findPlace(tokens[2]);
                            int        w = Integer.parseInt(tokens[3]);
                            pn.addArc(t, p, w);
                        }
                        break;
                    case INITIALISE:
                        {
                            if (tokens.length < 3 || (tokens.length > 3 && tokens[3].charAt(0) != '#')) throw new PetriNetFormatException(lineNumber, line);
                            Place p = pn.findPlace(tokens[1]);
                            int   w = Integer.parseInt(tokens[2]);
                            pn.addInitialArc(p, w);
                        }
                        break;
                    default:
                        if (tokens[0].length() > 0 && tokens[0].charAt(0) != '#') throw new PetriNetFormatException(lineNumber, line);
                }
                line = br.readLine();
                lineNumber++;
            }
        } catch (PetriNetFormatException e) {
            throw new PetriNetFormatException(lineNumber, "Error reading Petri Net: Line " + lineNumber + " isn't formatted properly:\n" + line + "\n");
        }
    }

    @Override
    public void write(Writer w, PetriNet p) throws IOException {
        try {
            String str = p.toString();
            str = Pattern.compile("PLACE").matcher(str).replaceAll(PLACE);
            str = Pattern.compile("TRANSITION").matcher(str).replaceAll(TRANSITION);
            str = Pattern.compile("ARCPT").matcher(str).replaceAll(ARC_PT);
            str = Pattern.compile("ARCTP").matcher(str).replaceAll(ARC_TP);
            str = Pattern.compile("INITIAL").matcher(str).replaceAll(INITIALISE);
            str = Pattern.compile(",|\\(|\\)").matcher(str).replaceAll(" ");
            str = Pattern.compile("\\s\n").matcher(str).replaceAll("\n");
            str = Pattern.compile("(\\s\\d+)(\\s[a-zA-Z]\\w*)(\n)").matcher(str).replaceAll("$2$1$3");
            w.write(str);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException("Error writing Petri Net: Null Petri Net");
        }
    }

}
