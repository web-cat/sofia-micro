package sofia.micro.lightbot;

import java.util.HashSet;
import java.util.Set;
import sofia.graphics.Image;
import sofia.micro.Actor;
import sofia.micro.World;

//-------------------------------------------------------------------------
/**
 * Represents a block or lightable tile in the Light-Bot world.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public abstract class Tile
    extends Actor
{
    //~ Fields ................................................................

    private static final Set<Tile> emptySet = new HashSet<Tile>();
    private static enum Direction { NORTH, EAST, SOUTH, WEST };


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new tile.
     * @param x  The x-coordinate of the block's location.
     * @param y  The y-coordinate of the block's location.
     */
    public Tile(int x, int y)
    {
        super();
        setGridLocation(x, y);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * This method is called when the block is added to the world.  It
     * takes care of setting the image used for the block based on how
     * high it is stacked and how high its neighbors are.
     */
    @Override
    public void addedToWorld(World world)
    {
        super.addedToWorld(world);

        for (int xOffset = -1; xOffset <= 1; xOffset++)
        {
            for (int yOffset = -1; yOffset <= 1; yOffset++)
            {
                for (Tile neighbor : tilesAt(xOffset, yOffset))
                {
                    neighbor.determineImage();
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Determine the set of images to use for this kind of tile.
     * The default is simply "tile", but can be overridden by
     * subclasses.
     * @return The name of the family of images to use for rendering this tile.
     */
    protected String imageBaseName()
    {
        return "tile";
    }


    // ----------------------------------------------------------
    private Set<Tile> tilesAt(int dx, int dy)
    {
        return tilesAt(dx, dy, Tile.class);
    }


    // ----------------------------------------------------------
    private Set<Block> blocksAt(int dx, int dy)
    {
        return tilesAt(dx, dy, Block.class);
    }


    // ----------------------------------------------------------
    private <T extends Tile> Set<T> tilesAt(int dx, int dy, Class<T> cls)
    {
        int x = getGridX();
        int y = getGridY();

        if (x + dx >= 0
            && x + dx < getWorld().getWidth()
            && y + dy >= 0
            && y + dy < getWorld().getHeight())
        {
            return getObjectsAtOffset(dx, dy, cls);
        }
        else
        {
            @SuppressWarnings("unchecked")
            Set<T> result = (Set<T>)emptySet;
            return result;
        }
    }


    // ----------------------------------------------------------
    private int depthTo(Direction direction)
    {
        switch (direction)
        {
            case NORTH:
                return blocksAt(0, -1).size();
            case EAST:
                return blocksAt(1, 0).size();
            case SOUTH:
                return blocksAt(0, 1).size();
            case WEST:
                return blocksAt(-1, 0).size();
        }
        // unreachable
        return 0;
    }


    // ----------------------------------------------------------
    private String tallerTo(Direction direction)
    {
        int myDepth = blocksAt(0, 0).size();
        int neighborDepth = depthTo(direction);
        return (myDepth < neighborDepth) ? "1" : "0";
    }


    // ----------------------------------------------------------
    /**
     * Automatically sets the image used to render this object based
     * on the depth of neighbors and the kind of object this is.
     */
    protected void determineImage()
    {
        setImage(new Image("lightbot/" + imageBaseName() + "_"
            + tallerTo(Direction.NORTH)
            + tallerTo(Direction.EAST)
            + tallerTo(Direction.SOUTH)
            + tallerTo(Direction.WEST)));
    }
}
