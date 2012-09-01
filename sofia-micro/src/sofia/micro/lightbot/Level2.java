package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents Level 2 of a Light-Bot-style game.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Level2
    extends Level
{
    //~ Constructor ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new Level 2.
     */
    public Level2()
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
        for (int i = 0; i < getWidth(); i++)
        {
            add(new Block(), i, 0);
            add(new Tile(), i, 1);
            if (i < 2 || i > getWidth() - 3)
            {
                add(new Block(), i, 0);
                add(new Block(), i, getHeight() - 1);
                if (i < 1 || i > getWidth() - 2)
                {
                    add(new Block(), i, 0);
                    add(new Block(), i, getHeight() - 1);
                }
            }

            add(new Tile(), i, getHeight() - 2);
            add(new Block(), i, getHeight() - 1);
        }

        add(new Tile(), 3, 2);
        add(new Tile(), 4, 2);
        add(new Tile(), 5, 2);
        add(new Tile(), 3, 3);
        add(new Block(), 4, 3);
        add(new Block(), 4, 3);
        add(new Tile(), 5, 3);
        add(new Tile(), 3, 4);
        add(new Tile(), 4, 4);
        add(new Tile(), 5, 4);

        add(new LightableTile(), 6, 3);
    }
}
