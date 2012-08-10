package sofia.micro;

//-------------------------------------------------------------------------
/**
 * Represents an actor script--that is, a sequence of actions on a
 * scriptable actor to control its behavior.
 *
 * @param <MyActor> The type of actor this script controls.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public interface Script<MyActor extends ScriptableActor>
{
    // ----------------------------------------------------------
    /**
     * Set the actor this script controls.
     * @param actor The actor this script controls
     */
    public void setActor(MyActor actor);


    // ----------------------------------------------------------
    /**
     * Get the actor this script controls.
     * @return The actor for this script.
     */
    public MyActor getActor();


    // ----------------------------------------------------------
    /**
     * Represents a sequence of actions for an actor to carry out.
     */
    public void script();
}
