package sofia.micro.jeroo;

import sofia.micro.ScriptableWorld;

//-------------------------------------------------------------------------
/**
 * Represents an island where Jeroos live (e.g., Santong Island).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Island
    extends ScriptableWorld
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new island with a default size of 20 x 12 cells.
     */
    public Island()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.
     *
     * @param width  The width of the world (in cells).
     * @param height The height of the world (in cells).
     */
    public Island(int width, int height)
    {
        super(width, height);
    }
}
