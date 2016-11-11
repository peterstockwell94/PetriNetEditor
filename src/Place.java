import java.util.Set;

public interface Place
{
    /**
     * Return a set containing all Transitions that lead to this Place
     * @return A set containing all Transitions that lead to this Place
     */
    public Set<Transition> preSet();


    /**
     * Return a set containing all Transitions reachable from this Place
     * @return A set containing all Transitions reachable from this Place
     */
    public Set<Transition> postSet();


    /**
     * Return the weight of the Initial Arc of this Place
     * @return The weight of the Initial Arc of this Place or 0 if no Initial Arc exists
     */
    public int getInitialArcWeight();


    /**
     * Move the position of this Place by (dx,dy) from its current position
     * @param dx The amount to move the x position of this Place by
     * @param dy The amount to move the y position of this Place by
     */
    public void moveBy(int dx, int dy);


    /**
     * Return the name of this Place
     * @return The name of this Place
     */
    public String getName();


    /**
     * Return the x position of this Place
     * @return The x position of this Place
     */
    public int getX();


    /**
     * Return the y position of this Place
     * @return The y position of this Place
     */
    public int getY();


    /**
     * Return the current Marking of this Place
     * @return The current Marking of this Place
     */
    public int getMarking();


    /**
     * Return a string containing information about this Place in the form: placeName(xPos,yPos)
     * @return A string representing this Place
     */
    public String toString();


    /**
     * Add a PlaceListener to this Place
     * @param pl The PlaceListener to add to this Place
     */
    public void addListener(PlaceListener pl);


    /**
     * Remove a PlaceListener from this Place
     * @param pl The PlaceListener to remove from this Place
     */
    public void removeListener(PlaceListener pl);
}
