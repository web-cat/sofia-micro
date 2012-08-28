package sofia.micro.lightbot;

import sofia.micro.ProgrammableWorld;

//-------------------------------------------------------------------------
/**
 * Represents a level of a Light-Bot-style game.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public abstract class Level
    extends ProgrammableWorld
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new level with a default size of 12 x 12 cells.
     */
    public Level()
    {
        this(12, 12);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new level. The size of the level (in number of cells)
     * must be specified.
     *
     * @param width  The width of the level (in cells).
     * @param height The height of the level (in cells).
     */
    public Level(int width, int height)
    {
        super(width, height);
        populate();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * This method populates the level with all of its blocks and tiles.
     * Subclasses should define this appropriately.
     */
    protected abstract void populate();
}
