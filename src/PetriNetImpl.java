import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

public class PetriNetImpl implements PetriNet {
    private Set<PlaceImpl>        places;
    private Set<TransitionImpl>   transitions;
    private Set<Arc>              ptArcs;
    private Set<Arc>              tpArcs;
    private Set<Initialisation>   initialisations;
    private Set<PetriNetListener> listeners;

    public static final String  PLACE_REGEX = "\\A[A-Z]\\w*\\z";
    public static final String  TRANSITION_REGEX = "\\A[a-z]\\w*\\z";

    public PetriNetImpl() {
        places          = new LinkedHashSet<PlaceImpl>();
        transitions     = new LinkedHashSet<TransitionImpl>();
        ptArcs          = new LinkedHashSet<Arc>();
        tpArcs          = new LinkedHashSet<Arc>();
        initialisations = new LinkedHashSet<Initialisation>();
        listeners       = new LinkedHashSet<PetriNetListener>();
    }

    public Place newPlace(String name, int x, int y) throws IllegalArgumentException {
        Pattern p = Pattern.compile(PLACE_REGEX);
        if (name == null)            throw new IllegalArgumentException("Error adding Place: Null Place name");
        if (!p.matcher(name).find()) throw new IllegalArgumentException("Error adding Place: \"" + name + "\" is not a valid Place name");
        if (findPlace(name) != null) throw new IllegalArgumentException("Error adding Place: A Place with the name \"" + name + "\" already exists");

        PlaceImpl np = new PlaceImpl(name, x, y);
        places.add(np);
        changed();
        return np;
    }

    public void removePlace(Place p) {
        try {
            // Remove place from pre/post sets of all Transitions
            for (Transition t : transitions) {
                TransitionImpl ti = (TransitionImpl) t;

                ti.removeIncoming(p);
                ti.removeOutgoing(p);
            }
            {
                // Remove associated Place->Transition Arcs, since they will become invalid
                Iterator<Arc> iter = ptArcs.iterator();
                while (iter.hasNext()) {
                    Arc a = iter.next();
                    if (a.getPlace().equals(p)) iter.remove();
                }
            }
            {
                // Remove associated Transition->Place Arcs, since they will become invalid
                Iterator<Arc> iter = tpArcs.iterator();
                while (iter.hasNext()) {
                    Arc a = iter.next();
                    if (a.getPlace().equals(p)) iter.remove();
                }
            }
            {
                // Remove associated Initialisations, since they will become invalid
                Iterator<Initialisation> iter = initialisations.iterator();
                while (iter.hasNext()) {
                    Initialisation i = iter.next();
                    if (i.getPlace().equals(p)) iter.remove();
                }
            }

            // Remove the Place
            places.remove(p);
            changed();
        } catch (Exception e) {
            // System.err.println(e);
            // e.printStackTrace();
        }
    }

    public Place findPlace(String name) {
        for (Place p : places) if (p.getName().equals(name)) return p;
        return null;
    }

    public Set<Place> getPlaces() {
        return new LinkedHashSet<Place>(places);
    }

    public Transition newTransition(String name, int x, int y) throws IllegalArgumentException {
        Pattern p = Pattern.compile(TRANSITION_REGEX);
        if (name == null)                 throw new IllegalArgumentException("Error adding Transition: Null Transition name");
        if (!p.matcher(name).find())      throw new IllegalArgumentException("Error adding Transition: \"" + name + "\" is not a valid Transition name");
        if (findTransition(name) != null) throw new IllegalArgumentException("Error adding Transition: A Transition with the name \"" + name + "\" already exists");

        TransitionImpl nt = new TransitionImpl(name, x, y);
        transitions.add(nt);
        changed();
        return nt;
    }

    public void removeTransition(Transition t) {
        // Remove Transition from pre/post sets of all Places
        for (Place p : places) {
            PlaceImpl pi = (PlaceImpl) p;
            pi.removeIncoming(t);
            pi.removeOutgoing(t);
        }
        {
            // Remove associated Place->Transition Arcs, since they will become invalid
            Iterator<Arc> iter = ptArcs.iterator();
            while (iter.hasNext()) {
                Arc a = iter.next();
                if (a.getTransition().equals(t)) iter.remove();
            }
        }
        {
            // Remove associated Transition->Place Arcs, since they will become invalid
            Iterator<Arc> iter = tpArcs.iterator();
            while (iter.hasNext()) {
                Arc a = iter.next();
                if (a.getTransition().equals(t)) iter.remove();
            }
        }
        // Remove the transition
        transitions.remove(t);
        changed();
    }

    public Transition findTransition(String name) {
        for (Transition t : transitions) if (t.getName().equals(name)) return t;
        return null;
    }

    public Set<Transition> getTransitions() {
        return new LinkedHashSet<Transition>(transitions);
    }

    public Arc findArc(Place p, Transition t) {
        for (Arc a : ptArcs) if (a.getPlace().equals(p) && a.getTransition().equals(t)) return a;
        return null;
    }

    public Arc findArc(Transition t, Place p) {
        for (Arc a : tpArcs) if (a.getTransition().equals(t) && a.getPlace().equals(p)) return a;
        return null;
    }

    public void addArc(Place p, Transition t, int w) throws IllegalArgumentException {
        if (p == null)                throw new IllegalArgumentException("Error adding Arc: Null Place");
        if (t == null)                throw new IllegalArgumentException("Error adding Arc: Null Transition");
        if (w < 0)                    throw new IllegalArgumentException("Error adding Arc: \"" + w + "\" is not a valid Arc weight");
        if (!places.contains(p))      throw new IllegalArgumentException("Error adding Arc: Place \"" + p + "\" does not exist");
        if (!transitions.contains(t)) throw new IllegalArgumentException("Error adding Arc: Transition \"" + t + "\" does not exist");

        PlaceImpl pi = (PlaceImpl) p;
        TransitionImpl ti = (TransitionImpl) t;

        Arc a = new Arc(p,t,w);
        if (ptArcs.add(a)) {
            ti.addIncoming(p);
            pi.addOutgoing(t);
        } else {
            findArc(p,t).addWeight(w);
        }
        changed();
    }

    public void addArc(Transition t, Place p, int w) throws IllegalArgumentException {
        if (t == null || p == null)   throw new IllegalArgumentException("Error adding Arc: Null Transition or Place");
        if (w <= 0)                   throw new IllegalArgumentException("Error adding Arc: \"" + w + "\" is not a valid Arc weight");
        if (!places.contains(p))      throw new IllegalArgumentException("Error adding Arc: Place \"" + p + "\" does not exist");
        if (!transitions.contains(t)) throw new IllegalArgumentException("Error adding Arc: Transition \"" + t + "\" does not exist");

        PlaceImpl pi = (PlaceImpl) p;
        TransitionImpl ti = (TransitionImpl) t;

        Arc a = new Arc(t,p,w);
        if (tpArcs.add(a)) {
            pi.addIncoming(t);
            ti.addOutgoing(p);
        } else {
            findArc(t,p).addWeight(w);
        }
        changed();
    }

    public int getArcWeight(Place p, Transition t) {
        for (Arc a : ptArcs) if (a.equals(new Arc(p,t))) return a.getWeight();
        return 0;
    }

    public int getArcWeight(Transition t, Place p) {
        for (Arc a : tpArcs) if (a.equals(new Arc(t,p))) return a.getWeight();
        return 0;
    }

    public void removeArc(Place p, Transition t) {
        PlaceImpl pi = (PlaceImpl) p;
        TransitionImpl ti = (TransitionImpl) t;

        ptArcs.remove(new Arc(p,t));
        ti.removeIncoming(p);
        pi.removeOutgoing(t);
        changed();
    }

    public void removeArc(Transition t, Place p) {
        PlaceImpl pi = (PlaceImpl) p;
        TransitionImpl ti = (TransitionImpl) t;

        tpArcs.remove(new Arc(t,p));
        pi.removeIncoming(t);
        ti.removeOutgoing(p);
        changed();
    }

    public Initialisation findInitialisation(Place p) {
        for (Initialisation i : initialisations) if (i.getPlace().equals(p)) return i;
        return null;
    }

    public void addInitialArc(Place p, int w) throws IllegalArgumentException {
        if (p == null)           throw new IllegalArgumentException("Error adding Initial Arc: Null Place");
        if (w < 0)               throw new IllegalArgumentException("Error adding Initial Arc: \"" + w + "\" is not a valid Arc weight");
        if (!places.contains(p)) throw new IllegalArgumentException("Error adding Initial Arc: Place \"" + p + "\" does not exist");

        Initialisation i = new Initialisation(p, w);
        PlaceImpl pi = (PlaceImpl) p;
        if (initialisations.add(i)) {
            pi.setInitialArcWeight(w);
        } else {
            findInitialisation(p).addWeight(w);
            pi.addInitialArcWeight(w);
        }
        changed();
    }

    public void removeInitialArc(Place p) {
        initialisations.remove(findInitialisation(p));
        PlaceImpl pi = (PlaceImpl) p;
        pi.setInitialArcWeight(0);
        changed();
    }

    public void simInitialise() {
        for (Place p : this.places) {
            PlaceImpl pi = (PlaceImpl) p;
            pi.setMarking(pi.getInitialArcWeight());
        }
        changed();
    }

    private boolean isEnabled(Transition t) {
        for (Place p : t.preSet()) if (p.getMarking() < getArcWeight(p,t)) return false;
        return true;
    }

    public Set<Transition> simEnabledTransitions() {
        Set<Transition> enabled = new LinkedHashSet<Transition>();
        for (Transition t : transitions) if (isEnabled(t)) enabled.add(t);
        return enabled;
    }

    public void simFire(Transition t) throws IllegalArgumentException {
        // Ensure Transition is enabled
        if (t == null)                            throw new IllegalArgumentException("Error firing Transition: Null Transition");
        if (!simEnabledTransitions().contains(t)) throw new IllegalArgumentException("Error firing Transition: Transition \"" + t + "\" is not enabled");

        // Remove Markings from incoming Places
        for (Place p : t.preSet()) {
            PlaceImpl pi = (PlaceImpl) p;
            pi.setMarking(pi.getMarking() - getArcWeight(p,t));
        }

        // Add Markings to outgoing Places
        for (Place p : t.postSet()) {
            PlaceImpl pi = (PlaceImpl) p;
            pi.setMarking(pi.getMarking() + getArcWeight(t,p));
        }

        changed();
    }

    public void addListener(PetriNetListener pl) {
        listeners.add(pl);
    }

    public void removeListener(PetriNetListener pl) {
        listeners.remove(pl);
    }

    private void changed() {
        for (PetriNetListener pl : listeners) pl.petrinetHasChanged();
    }

    @Override
    public String toString() {
        String str = "";
        for (Place p : places)                   str +=      "PLACE " +   p + "\n";
        for (Transition t : transitions)         str += "TRANSITION " +   t + "\n";
        for (Arc pta : ptArcs)                   str +=      "ARCPT " + pta + "\n";
        for (Arc tpa : tpArcs)                   str +=      "ARCTP " + tpa + "\n";
        for (Initialisation i : initialisations) str +=    "INITIAL " +   i + "\n";
        return str;
    }

}
