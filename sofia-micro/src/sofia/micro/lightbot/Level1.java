package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents Level 1 of a Light-Bot-style game.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Level1
    extends Level
{
    //~ Constructor ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new Level 1.
     */
    public Level1()
    {
        super();
    }


    //~ Constructor ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates the tiles for this level.
     */
    @Override
    protected void populate()
    {
        add(new LightableTile(6, 5));
    }
}
