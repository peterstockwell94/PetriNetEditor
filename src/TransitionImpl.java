import java.util.Set;
import java.util.LinkedHashSet;

public class TransitionImpl implements Transition {
    private String name;
    private int xPos;
    private int yPos;
    private Set<PlaceImpl> incoming;
    private Set<PlaceImpl> outgoing;
    private Set<TransitionListener> listeners;

    public TransitionImpl(String name, int x, int y) {
        this.name = name;
        this.xPos = x;
        this.yPos = y;
        this.incoming = new LinkedHashSet<PlaceImpl>();
        this.outgoing = new LinkedHashSet<PlaceImpl>();
        this.listeners = new LinkedHashSet<TransitionListener>();
    }

    public Set<Place> preSet() {
        return new LinkedHashSet<Place>(this.incoming);
    }

    public Set<Place> postSet() {
        return new LinkedHashSet<Place>(this.outgoing);
    }

    public void addIncoming(Place p) {
        this.incoming.add((PlaceImpl) p);
        this.hasChanged();
    }

    public void addOutgoing(Place p) {
        this.outgoing.add((PlaceImpl) p);
        this.hasChanged();
    }

    public void removeIncoming(Place p) {
        this.incoming.remove((PlaceImpl) p);
        this.hasChanged();
    }

    public void removeOutgoing(Place p) {
        this.outgoing.remove((PlaceImpl) p);
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

    public void addListener(TransitionListener tl) {
        this.listeners.add(tl);
    }

    public void removeListener(TransitionListener tl) {
        this.listeners.remove(tl);
    }

    private void hasChanged() {
        for (TransitionListener tl : listeners) tl.transitionHasChanged();
    }

    @Override
    public String toString() {
        return this.name + "(" + this.xPos + "," + this.yPos + ")";
    }

}
