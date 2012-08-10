package sofia.micro.greenfoot;

import sofia.graphics.Image;
import sofia.micro.Actor;

//-------------------------------------------------------------------------
/**
 * A small "adaptor" class that provides Greenfoot-style methods
 * for some World features.  It is intended to provide for source-level
 * compatibility of some Greenfoot examples.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class World
    extends sofia.micro.World
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new world with a default size of 20 x 12.  This default
     * size is based on a 320x480 (Android's HVGA resolution, which is
     * a mid-level phone resolution) in landscape orientation, leaving some
     * room for a notification bar and other decorations.  This would
     * result in 24x24 pixel cells, or 16x16 cells on a 240x320 phone.
     * This world (and its actors) will be automatically scaled up (zoomed)
     * if the Android device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     */
    public World()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified. This world (and its actors) will be automatically
     * scaled up (zoomed) if the Android device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width  The width of the world (in cells).
     * @param height The height of the world (in cells).
     */
    public World(int width, int height)
    {
        super(width, height);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.  This constructor also sets the effective cell
     * size of this world (for bitmaps). This world (and its actors) will
     * be automatically scaled up (zoomed) if the Android device resolution
     * permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     */
    public World(int width, int height, int scaledCellSize)
    {
        super(width, height, scaledCellSize);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.  This constructor also sets the effective cell
     * size of this world (for bitmaps) and whether this world (and its
     * actors) should be automatically scaled up (zoomed) if the Android
     * device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     * @param scaleToFit     If true, cells will be scaled larger or smaller
     *                       so that the grid is as large as possible on
     *                       the physical device, with bitmaps
     *                       scaled proportionately.  If false, the grid
     *                       will be rendered so that each cell is exactly
     *                       scaledCellSize pixels in size, no more and no
     *                       less, even if this means some of the grid will
     *                       be clipped by the screen boundaries.
     */
    public World(int width, int height, int scaledCellSize, boolean scaleToFit)
    {
        super(width, height, scaledCellSize, scaleToFit);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background image.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     * @param scaleToFit     If true, cells will be scaled larger or smaller
     *                       so that the grid is as large as possible on
     *                       the physical device, with bitmaps
     *                       scaled proportionately.  If false, the grid
     *                       will be rendered so that each cell is exactly
     *                       scaledCellSize pixels in size, no more and no
     *                       less, even if this means some of the grid will
     *                       be clipped by the screen boundaries.
     * @param backgroundIsForCell Indicates whether any existing background
     *                       image based on the world's class name should be
     *                       used as the background for each cell (if true),
     *                       or stretched to fit the entire world grid (if
     *                       false).
     */
    public World(
        int width,
        int height,
        int scaledCellSize,
        boolean scaleToFit,
        boolean backgroundIsForCell)
    {
        super(width, height, scaledCellSize, scaleToFit, backgroundIsForCell);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Add an Actor to the world at a specified location.
     *
     * <p>This method is identical to {@code add()}, but is provided for
     * Greenfoot compatibility.</p>
     *
     * @param actor The Actor to add.
     * @param x The x coordinate of the location where the actor is added.
     * @param y The y coordinate of the location where the actor is added.
     */
    public void addObject(Actor actor, int x, int y)
    {
        add(actor, x, y);
    }


    // ----------------------------------------------------------
    /**
     * Remove an Actor from the world.
     *
     * <p>This method is identical to {@code remove()}, but is provided
     * for Greenfoot compatibility.</p>
     *
     * @param actor The Actor to remove.
     */
    public void removeObject(Actor actor)
    {
        remove(actor);
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for each Cell.  The given image will be
     * scaled to fill exactly one cell, and tiled across the entire
     * world's grid.  This will replace any world background image.
     *
     * @param background The background image to use for each cell.
     */
    public void setBackground(Image background)
    {
        super.setCellBackground(background);
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for each Cell.  The given image will be
     * scaled to fill exactly one cell, and tiled across the entire
     * world's grid.  This will replace any world background image.
     *
     * @param background The background image to use for each cell.
     */
    public void setBackground(String background)
    {
        super.setCellBackground(background);
    }
}
