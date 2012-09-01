package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents Level 3 of a Light-Bot-style game.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Level3
    extends Level
{
    //~ Constructor ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new Level 3.
     */
    public Level3()
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
        for (int i = 0; i < getHeight(); i++)
        {
            add(new Tile(), 1, i);
            add(new Block(), 2, i);
            add(new Tile(), 3, i);
        }
        for (int i = 2; i < 5; i++)
        {
            add(new LightableTile(), 4, i);
        }
    }
}
