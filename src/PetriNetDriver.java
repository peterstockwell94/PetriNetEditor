import java.io.*;

public class PetriNetDriver {

    public static void main(String[] args) throws IOException, PetriNetFormatException {
        BufferedReader         r = new BufferedReader(new FileReader("C:/Users/Peter/Programming/a1647968/15s2-edc-prac2/simple.pn"));
        PrintWriter            w = new PrintWriter("C:/Users/Peter/Programming/a1647968/15s2-edc-prac2/output.pn");
        PetriNetReaderWriter prw = new PetriNetReaderWriter();
        PetriNetImpl          pn = new PetriNetImpl();
        prw.read(r, pn);

        System.out.println(pn);
        // Place p = pn.findPlace("P1");
        pn.addArc(pn.findPlace("P1"), pn.findTransition("put"), 0);

        pn.simInitialise();
        // for (Place p : pn.getPlaces()) System.out.println(((PlaceImpl)p).debug());

        for (int i=0; i < 10; i++) {
            System.out.println(pn.simEnabledTransitions());
            for (Transition t : pn.simEnabledTransitions()) {
                pn.simFire(t);
                for (Place p : pn.getPlaces()) System.out.println(((PlaceImpl) p).debug());
            }
        }

        System.out.println(pn);

        prw.write(w, pn);
        w.close();
    }

}
