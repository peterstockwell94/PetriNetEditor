public class Initialisation {
    private Place place;
    private int weight;

    public Initialisation(Place p) {
        this.place = p;
        this.weight = 0;
    }

    public Initialisation(Place p, int w) {
        this.place = p;
        this.weight = w;
    }

    public Place getPlace() {
        return this.place;
    }

    public int getWeight() {
        return this.weight;
    }

    public void addWeight(int w) { this.weight += w; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Initialisation)) return false;
        if (obj == this) return true;

        Initialisation i = (Initialisation) obj;
        return this.place.equals(i.getPlace());
    }

    @Override
    public int hashCode() {
        return place.getName().hashCode();
    }

    @Override
    public String toString() {
        return place.getName() + "(" + weight + ")";
    }
}
