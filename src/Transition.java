import java.util.Set;

public interface Transition
{
    /**
     * Return a set containing all Places that lead to this Transition
     * @return A set containing all Places that lead to this Transition
     */
    public Set<Place> preSet();


    /**
     * Return a set containing all Places reachable from this Transition
     * @return A set containing all Places reachable from this Transition
     */
    public Set<Place> postSet();


    /**
     * Move the position of this Transition by (dx,dy) from its current position
     * @param dx The amount to move the x position of this Transition by
     * @param dy The amount to move the y position of this Transition by
     */
    public void moveBy(int dx, int dy);


    /**
     * Return the name of this Transition
     * @return The name of this Transition
     */
    public String getName();


    /**
     * Return the x position of this Transition
     * @return The x position of this Transition
     */
    public int getX();


    /**
     * Return the y position of this Transition
     * @return The y position of this Transition
     */
    public int getY();


    //Return a string containing information about this Transition 
    //in the form: transitionName(xPos,yPos)
    /**
     * Return a string containing information about this Transition in the form: placeName(xPos,yPos)
     * @return A string representing this Transition
     */
    public String toString();


    /**
     * Add a TransitionListener to this Place
     * @param tl The TransitionListener to add to this Place
     */
    public void addListener(TransitionListener tl);


    /**
     * Remove a TransitionListener from this Transition
     * @param tl The TransitionListener to remove from this Transition
     */
    public void removeListener(TransitionListener tl);
}
