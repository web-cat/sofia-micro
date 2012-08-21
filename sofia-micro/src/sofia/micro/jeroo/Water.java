package sofia.micro.jeroo;

import sofia.micro.Actor;

//-------------------------------------------------------------------------
/**
 * Represents a patch of water on/around Santong Island.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Water
    extends Actor
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new patch of water.
     * @param x  The x-coordinate of the water's location.
     * @param y  The y-coordinate of the water's location.
     */
    public Water(int x, int y)
    {
        super();
        setGridLocation(x, y);
    }
}
