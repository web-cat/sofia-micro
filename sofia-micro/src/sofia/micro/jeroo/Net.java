package sofia.micro.jeroo;

import sofia.micro.Actor;

//-------------------------------------------------------------------------
/**
 * Represents a net on Santong Island.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Net
    extends Actor
{
    //~ Constructor ...........................................................
    /**
     * Create a new net at coordinates (0, 0).
     */
    public Net()
    {
        this(0, 0);
    }

    // ----------------------------------------------------------
    /**
     * Create a new net.
     * @param x  The x-coordinate of the net's location.
     * @param y  The y-coordinate of the net's location.
     */
    public Net(int x, int y)
    {
        super();
        setGridLocation(x, y);
    }
}
