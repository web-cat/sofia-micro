package sofia.micro.lightbot;

//-------------------------------------------------------------------------
/**
 * Represents a tile (possibly on top of a block) that can be lighted
 * by a Light-Bot.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class LightableTile
    extends Tile
{
    //~ Fields ................................................................

    private boolean isLit = false;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new lightable tile.
     */
    public LightableTile()
    {
        super();
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Turn this lightable tile "on" (it starts unlit).
     */
    public void turnLightOn()
    {
        isLit = true;
        determineImage();
    }


    // ----------------------------------------------------------
    /**
     * Determine if this tile's light is on.
     * @return True if this tile is lit up.
     */
    public boolean isOn()
    {
        return isLit;
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    protected String imageBaseName()
    {
        return isLit ? "lightedtile" : "bluetile";
    }
}
