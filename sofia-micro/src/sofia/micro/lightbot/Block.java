package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents a block, which will block a Light-Bot unless the robot
 * jumps on it.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Block
    extends Tile
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new block.
     * @param x  The x-coordinate of the block's location.
     * @param y  The y-coordinate of the block's location.
     */
    public Block(int x, int y)
    {
        super(x, y);
    }
}
