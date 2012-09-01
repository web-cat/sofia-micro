package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents Level 3 of a Light-Bot-style game.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Level6
    extends Level
{
    //~ Constructor ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new Level 3.
     */
    public Level6()
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
            if (i != 3)
            {
                add(new Tile(), 4, i);
            }
            if (i < getHeight() - 1)
            {
                add(new Block(), 5, i);
                add(new Block(), 5, i);
            }
            else
            {
                add(new Tile(), 5, i);
            }
            add(new Tile(), 6, i);
        }
        add(new LightableTile(), 5, 0);
        add(new LightableTile(), 5, getHeight() - 2);

        add(new Block(), 4, 3);
        add(new Block(), 4, 3);
        add(new Block(), 4, 3);
        add(new Block(), 4, 3);
        add(new LightableTile(), 4, 3);

        add(new Tile(), 3, 2);
        add(new Block(), 3, 3);
        add(new Block(), 3, 3);
        add(new Block(), 3, 3);
        add(new Tile(), 3, 4);

        add(new Tile(), 1, 2);
        add(new Tile(), 2, 2);
        add(new Block(), 2, 3);
        add(new Block(), 2, 3);
        add(new Tile(), 1, 3);

        add(new Tile(), 1, 4);
        add(new Tile(), 1, 5);
        add(new Block(), 2, 4);
        add(new Tile(), 2, 5);
        add(new Tile(), 3, 5);
        add(new Tile(), 3, 4);
    }
}
