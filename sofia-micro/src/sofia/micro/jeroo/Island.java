package sofia.micro.jeroo;

import sofia.micro.ProgrammableWorld;

//-------------------------------------------------------------------------
/**
 * Represents an island where Jeroos live (e.g., Santong Island).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Island
    extends ProgrammableWorld
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new island with a default size of 20 x 12 cells.
     */
    public Island()
    {
        this(20, 12);
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
        setCellBackground("ground.png");
        addWater();
    }

    private void addWater()
    {
        for (int i = 0; i < getWidth(); i++)
        {
            add(new Water(i, 0));
            add(new Water(i, getHeight() - 1));
        }

        for (int j = 0; j < getWidth(); j++)
        {
            add(new Water(0, j));
            add(new Water(getWidth() - 1, j));
        }
    }
}
