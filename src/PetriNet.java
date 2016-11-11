import java.util.Set;

public interface PetriNet
{
    /**
     * Create a new Place and add it to this Petri Net
     * @param name The name of the Place to create
     * @param x    The x position of the Place to create
     * @param y    The y position of the Place to create
     * @return     The newly created Place
     * @throws IllegalArgumentException If the name is not valid
     * @throws IllegalArgumentException If the name is the same as an existing Place
     */
    public Place newPlace(String name, int x, int y) throws IllegalArgumentException;


    /**
     * Remove a Place from the Petri Net
     * If the Place does not exist, returns without error
     * @param p The Place object to remove
     */
    public void removePlace(Place p);


    /**
     * Find and return the Place with the given name
     * @param placeName The name of the Place to find
     * @return          The found Place or NULL if no place exists with the given name
     */
    public Place findPlace(String placeName);


    /**
     * Returns a set containing all of the places in this Petri Net
     * @return A set containing all of the places in this Petri Net
     */
    public Set<Place> getPlaces();


    /**
     * Create a new Transition and add it to this Petri Net
     * @param name The name of the Transition to create
     * @param x    The x position of the Transition to create
     * @param y    The y position of the Transition to create
     * @return     The new Transition
     * @throws IllegalArgumentException If the name is not valid
     * @throws IllegalArgumentException If the name is the same as an existing transition
     */
    public Transition newTransition(String name, int x, int y) throws IllegalArgumentException;


    /**
     * Removes a Transition from the Petri Net
     * If the Transition does not exist, returns without error
     * @param t The Transition object to remove
     */
    public void removeTransition(Transition t);


    /**
     * Find and return the Transition with the given name
     * If no transition exists with given name, return NULL
     * @param name The name of the Transition to find
     * @return     The found Transition or NULL if no Transition exists with the given name
     */
    public Transition findTransition(String name);


    /**
     * Returns a set containing all the transitions in this Petri Net
     * @return A set containing all the transitions in this Petri Net
     */
    public Set<Transition> getTransitions();


    /**
     * Add a new Arc from a Place to a Transition
     * @param p The Place to add the Arc from
     * @param t The Transition to add the Arc to
     * @param w The weight of the Arc
     * @throws IllegalArgumentException If the given Place does not exist
     * @throws IllegalArgumentException If the given Transition does not exist
     * @throws IllegalArgumentException If the given weight is not a valid value
     */
    public void addArc(Place p, Transition t, int w) throws IllegalArgumentException;


    /**
     * Add a new Arc from a Transition to a Place
     * @param t The Transition to add the Arc from
     * @param p The Place to add the Arc to
     * @param w The weight of the Arc
     * @throws IllegalArgumentException If the given Transition does not exist
     * @throws IllegalArgumentException If the given Place does not exist
     * @throws IllegalArgumentException If the given weight is not a valid value
     */
      public void addArc(Transition t, Place p, int w) throws IllegalArgumentException;


    /**
     * Returns the weight of an Arc from a Place to a Transition
     * @param p The Place at which the Arc begins
     * @param t The Transition at which the Arc ends
     * @return  The weight of the Arc between p and t or 0 if no Arc exists
     */
    public int getArcWeight(Place p, Transition t);


    /**
     * Returns the weight of an Arc from a Transition to a Place
     * @param t The Transition at which the Arc begins
     * @param p The Place at which the Arc ends
     * @return  The weight of the Arc between t and p or 0 if no Arc exists
     */
    public int getArcWeight(Transition t, Place p);


    /**
     * Removes the arc from a Transition to a Place
     * If the Arc does not exist, returns without error
     * @param p The Place at which the Arc begins
     * @param t The Transition at which the Arc ends
     */
    public void removeArc(Place p, Transition t);


    /**
     * Removes the arc from a Transition to a Place
     * If the Arc does not exist, returns without error
     * @param t The Transition at which the Arc begins
     * @param p The Place at which the Arc ends
     */
    public void removeArc(Transition t, Place p);


    /**
     * Add an initial arc to a Place with a given weight
     * @param p The Place to add the Initial Arc to
     * @param w The weight of the Initial Arc to add
     * @throws IllegalArgumentException if the Place does not exist
     * @throws IllegalArgumentException if the the weight is not a valid value
     */
    public void addInitialArc(Place p, int w) throws IllegalArgumentException;


    /**
     * Removes the Initial Arc from a given Place
     * If the place does not exist, returns without error
     * If there was no initial arc, returns without error
     * @param p The Place from which to remove the Initial Arc
     */
    public void removeInitialArc(Place p);


    /**
     * Initialise the simulation of this Petri Net to its Initial Marking
     */
    public void simInitialise();


    /**
     * Returns a set containing all the Transitions that are currently enabled
     * @return A set containing all the Transitions that are currently enabled
     */
    public Set<Transition> simEnabledTransitions();


    /**
     * Update the simulation by firing a given Transition
     * @param t The Transition to fire
     * @throws IllegalArgumentException If the Transition is not enabled
     */
    public void simFire(Transition t) throws IllegalArgumentException;


    /**
     * Returns a string describing this Petri Net that contains (in this order):
     *   for each Place in the Petri Net, a line (terminated by \n)
     *     PLACE followed by the toString result for that Place
     *   for each Transition in the Petri Net, a line (terminated by \n)
     *     TRANSITION followed by the toString result for that Transition
     *   for each Arc from a place to a a transition, a line (terminated by \n)
     *     ARCPT nameOfPlace(weight)nameOfTransition
     *   for each Arc from a place to a a transition, a line (terminated by \n)
     *     ARCTP nameOfTransition(weight)nameOfPlace
     *   for each Initial Arc in the Petri Net, a line (terminated by \n)
     *     INITIAL nameOfPlace(weight)
     * @return A string describing this Petri Net
     */
    public String toString();


     /**
     * Add a PetriNetListener to this class
     * @param pl The PetriNetListener to add to this class
     */
    public void addListener(PetriNetListener pl);


    /**
     * Remove a PetriNetListener from this class
     * @param pl The PetriNetListener to remove from this class
     */
    public void removeListener(PetriNetListener pl);
}
