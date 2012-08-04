package sofia.micro;

//-------------------------------------------------------------------------
/**
 * Represents a character script--that is, a sequence of actions on a
 * scriptable character to control its behavior.
 *
 * @param <MyCharacter> The type of character this script controls.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public interface Script<MyCharacter extends ScriptableCharacter>
{
    // ----------------------------------------------------------
    /**
     * Set the character this script controls.
     * @param character The character this script controls
     */
    public void setCharacter(MyCharacter character);


    // ----------------------------------------------------------
    /**
     * Get the character this script controls.
     * @return The character for this script.
     */
    public MyCharacter getCharacter();


    // ----------------------------------------------------------
    /**
     * Represents a sequence of actions for a character to carry out.
     */
    public void script();
}
