public class Arc {
    private static final int PT = 0;
    private static final int TP = 1;
    private Place place;
    private Transition transition;
    private int weight;
    private int direction;

    public Arc(Place p, Transition t) {
        this.place = p;
        this.transition = t;
        this.weight = 0;
        this.direction = PT;
    }

    public Arc(Transition t, Place p) {
        this.transition = t;
        this.place = p;
        this.weight = 0;
        this.direction = TP;
    }

    public Arc(Place p, Transition t, int w) {
        this.place = p;
        this.transition = t;
        this.weight = w;
        this.direction = PT;
    }

    public Arc(Transition t, Place p, int w) {
        this.transition = t;
        this.place = p;
        this.weight = w;
        this.direction = TP;
    }

    public Place getPlace() {
        return this.place;
    }

    public Transition getTransition() {
        return this.transition;
    }

    public int getWeight() {
        return this.weight;
    }

    public int getDirection() {
        return this.direction;
    }

    public void addWeight(int w) {
        this.weight += w;
    }

    @Override
    public String toString() {
        if      (direction == PT) return place.getName() + "(" + weight + ")" + transition.getName();
        else if (direction == TP) return transition.getName() + "(" + weight + ")" + place.getName();
        else throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    @Override
    public int hashCode() {
        return place.getName().charAt(0) + transition.getName().charAt(0) + direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Arc)) return false;
        if (obj == this) return true;

        Arc a = (Arc) obj;
        return this.place.equals(a.getPlace()) && this.transition.equals(a.getTransition()) && this.direction == a.getDirection();
    }
}
