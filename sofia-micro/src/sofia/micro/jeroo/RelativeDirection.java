package sofia.micro.jeroo;

//-------------------------------------------------------------------------
/**
 * Represents the four relative directions for turning or looking.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public enum RelativeDirection
{
    /** To the left of a Jeroo's current location or direction. */
    LEFT,

    /** To the right of a Jeroo's current location or direction. */
    RIGHT,

    /** Straight in front of the Jeroo, in the direction it is currently
     * facing.
     */
    AHEAD,

    /** The location where the Jeroo is currently standing. */
    HERE
}
