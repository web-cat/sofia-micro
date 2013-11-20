package sofia.micro.jeroo;

import sofia.micro.Actor;

//-------------------------------------------------------------------------
/**
 * Represents a winsum flower, the Jeroo's primary food source.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Flower
    extends Actor
{
    //~ Constructor ...........................................................
    /**
     * Create a new flower at coordinates (0, 0).
     */
    public Flower()
    {
        this(0, 0);
    }

    // ----------------------------------------------------------
    /**
     * Create a new flower.
     * @param x  The x-coordinate of the flower's location.
     * @param y  The y-coordinate of the flower's location.
     */
    public Flower(int x, int y)
    {
        super();
        setGridLocation(x, y);
    }

}
