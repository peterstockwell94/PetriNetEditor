import java.util.Set;
import java.util.LinkedHashSet;

public class PlaceImpl implements Place {
    private String name;
    private int xPos;
    private int yPos;
    private int markings;
    private int initialWeight;
    private Set<TransitionImpl> incoming;
    private Set<TransitionImpl> outgoing;
    private Set<PlaceListener> listeners;

    PlaceImpl(String name, int x, int y) {
        this.name = name;
        this.xPos = x;
        this.yPos = y;
        this.markings = 0;
        this.initialWeight = 0;
        this.incoming = new LinkedHashSet<TransitionImpl>();
        this.outgoing = new LinkedHashSet<TransitionImpl>();
        this.listeners = new LinkedHashSet<PlaceListener>();
    }

    public Set<Transition> preSet() {
        return new LinkedHashSet<Transition>(this.incoming);
    }

    public Set<Transition> postSet() {
        return new LinkedHashSet<Transition>(this.outgoing);
    }

    public void addIncoming(Transition t) {
        this.incoming.add((TransitionImpl) t);
        this.hasChanged();
    }

    public void addOutgoing(Transition t) {
        this.outgoing.add((TransitionImpl) t);
        this.hasChanged();
    }

    public void removeIncoming(Transition t) {
        this.incoming.remove((TransitionImpl) t);
        this.hasChanged();
    }

    public void removeOutgoing(Transition t) {
        this.outgoing.remove((TransitionImpl) t);
        this.hasChanged();
    }

    public int getInitialArcWeight() {
        return this.initialWeight;
    }

    public void setInitialArcWeight(int weight) {
        this.initialWeight = weight;
        this.hasChanged();
    }

    public void addInitialArcWeight(int weight) {
        this.initialWeight += weight;
        this.hasChanged();
    }

    public void moveBy(int dx, int dy) {
        this.xPos += dx;
        this.yPos += dy;
        this.hasChanged();
    }

    public String getName() {
        return this.name;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public int getMarking() {
        return this.markings;
    }

    public void setMarking(int markings) {
        this.markings = markings;
        this.hasChanged();
    }

    public void addListener(PlaceListener pl) {
        this.listeners.add(pl);
    }

    public void removeListener(PlaceListener pl) {
        this.listeners.remove(pl);
    }

    private void hasChanged() {
        for (PlaceListener pl : listeners) pl.placeHasChanged();
    }

    @Override
    public String toString() {
        return this.name + "(" + this.xPos + "," + this.yPos + ")";
    }

    public String debug() {
        return this.name + "(" + this.xPos + "," + this.yPos + ")" + " " + this.initialWeight + " " + this.markings;
    }

}
