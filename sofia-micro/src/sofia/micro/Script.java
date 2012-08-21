package sofia.micro;

//-------------------------------------------------------------------------
/**
 * Represents a script--that is, a sequence of actions on a
 * scriptable object to control its behavior.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public interface Script
{
    // ----------------------------------------------------------
    /**
     * Represents a sequence of actions to carry out, one turn at a time.
     */
    public void script();
}
