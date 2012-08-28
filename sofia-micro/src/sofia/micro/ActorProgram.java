package sofia.micro;

//-------------------------------------------------------------------------
/**
 * Represents an actor program--that is, a sequence of actions on a
 * programmable actor to control its behavior.
 *
 * @param <MyActor> The type of actor this program controls.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public interface ActorProgram<MyActor extends ProgrammableActor>
    extends Program
{
    // ----------------------------------------------------------
    /**
     * Set the actor this program controls.
     * @param actor The actor this program controls
     */
    public void setActor(MyActor actor);


    // ----------------------------------------------------------
    /**
     * Get the actor this program controls.
     * @return The actor for this program.
     */
    public MyActor getActor();
}
