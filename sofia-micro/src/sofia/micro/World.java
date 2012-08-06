package sofia.micro;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.Collection;
import java.util.List;
import sofia.graphics.Color;
import sofia.graphics.Image;
import sofia.graphics.Shape;

//-------------------------------------------------------------------------
/**
 * Represents a "microworld" containing Actors and rendered on a screen.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class World
{
    //~ Fields ................................................................

    private WorldView view;

    /** Size of the world */
    private int     width;
    private int     height;
    private int     scaledCellSize = 0;
    private boolean scaleToFit     = true;
    private int     orientation;
    private Color   backgroundColor;
    private Color   gridColor;
    private Image   background;
    private boolean backgroundIsForCell;

    private static final int DEFAULT_WIDTH  = 20;
    private static final int DEFAULT_HEIGHT = 12;

    private float  pixelsPerCell;
    private RectF  grid;            // In grid coords
    private RectF  gridArea;        // In pixels
    private RectF  backgroundRect;  // BB for background image in grid coords
    private Matrix gridTransform;
    private List<Actor> deferredAdds;


    //~ Constructor ...........................................................

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
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
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
        this(width, height, 0, true);
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
        this(width, height, scaledCellSize, true);
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
        this(width, height, scaledCellSize, scaleToFit, true);
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
    public World(int width, int height, int scaledCellSize, boolean scaleToFit,
        boolean backgroundIsForCell)
    {
        this.width = width;
        this.height = height;
        this.backgroundIsForCell = backgroundIsForCell;
        orientation = (width >= height)
            ? android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        background = new Image(getClass());
        background.setUseDefaultIfNotFound(false);
        grid = new RectF(0, 0, width, height);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Get all the actors in this world.
     * @return a collection of all the actors in this world.
     */
    public Collection<Actor> getActors()
    {
        throw new UnsupportedOperationException("Not yet implemented!");
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to the world.
     * @param actor The Actor to add.
     */
    public void add(Actor actor)
    {
        if (view == null)
        {
            if (deferredAdds == null)
            {
                deferredAdds = new java.util.ArrayList<Actor>();
            }
            deferredAdds.add(actor);
        }
        else
        {
            // TODO: implement appropriate add semantics
            actor.setWorld(this);
            view.add((Shape)actor);
            actor.addedToWorld(this);
            // throw new UnsupportedOperationException("Not yet implemented!");
        }
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to the world at a specified location.  This is a
     * convenience method that is equivalent to calling {@code add()} on
     * an actor, and then calling {@code setGridLocation()} on the actor
     * to specify its position.
     *
     * @param actor The Actor to add.
     * @param x The x coordinate of the location where the actor is added.
     * @param y The y coordinate of the location where the actor is added.
     */
    public void add(Actor actor, int x, int y)
    {
        actor.setGridLocation(x, y);
        add(actor);
    }


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
     * @param actor The Actor to remove.
     */
    public void remove(Actor actor)
    {
        if (actor.getWorld() == this)
        {
            // TODO: implement appropriate remove semantics
            actor.setWorld(null);
            view.remove((Shape)actor);
            // actor.removedToWorld(this);
            // throw new UnsupportedOperationException("Not yet implemented!");
        }
        else
        {
            throw new IllegalArgumentException(
                "The specified Actor is not in this World.");
        }
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
     * Act method for world. The act method is called by the system
     * at each action step in the environment. The world's act method is
     * called before the act method of any objects in the world.
     *
     * <p>This method does nothing. It should be overridden in subclasses to
     * implement an world's action.</p>
     */
    public void act()
    {
        // by default, do nothing
    }


    // ----------------------------------------------------------
    /**
     * This method is called by the system when the execution has started.
     * This method can be overridden to implement custom behavior when
     * the execution is started.
     * <p>
     * This default implementation is empty.</p>
     */
    public void started()
    {
        // by default, do nothing
    }


    // ----------------------------------------------------------
    /**
     * This method is called by the system when the execution has stopped.
     * This method can be overridden to implement custom behavior when
     * the execution is stopped.
     * <p>
     * This default implementation is empty.</p>
     */
    public void stopped()
    {
        // by default, do nothing
    }


    // ----------------------------------------------------------
    /**
     * Ask the world to draw visible grid lines of a specified color.  Use
     * a parameter of null to request that no grid lines be shown (the
     * default).
     *
     * @param gridColor The color to use for grid lines around each cell,
     *                  or null to suppress grid lines (the default).
     */
    public void setGridColor(Color gridColor)
    {
        this.gridColor = gridColor;
    }


    // ----------------------------------------------------------
    /**
     * Set the color of the world's background, if you prefer a solid
     * color background instead of an Image.
     *
     * @param backgroundColor The color to use for the background of the world.
     */
    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for each Cell.  The given image will be
     * scaled to fill exactly one cell, and tiled across the entire
     * world's grid.  This will replace any world background image.
     *
     * @param background The background image to use for each cell.
     */
    public void setCellBackground(String background)
    {
        setCellBackground(new Image(background));
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for each Cell.  The given image will be
     * scaled to fill exactly one cell, and tiled across the entire
     * world's grid.  This will replace any world background image.
     *
     * @param background The background image to use for each cell.
     */
    public void setCellBackground(Image background)
    {
        this.background = background;
        backgroundRect = new RectF(-0.5f, -0.5f, 0.5f, 0.5f);
        backgroundIsForCell = true;
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for the entire world.  The given image
     * will be scaled to fill the world's entire grid.  This will replace
     * any tiled cell background image.
     *
     * @param background The background image to use for the entire world.
     */
    public void setWorldBackground(String background)
    {
        setWorldBackground(new Image(background));
    }


    // ----------------------------------------------------------
    /**
     * Set a background image to use for the entire world.  The given image
     * will be scaled to fill the world's entire grid.  This will replace
     * any tiled cell background image.
     *
     * @param background The background image to use for the entire world.
     */
    public void setWorldBackground(Image background)
    {
        this.background = background;
        backgroundRect = new RectF(-0.5f, -0.5f, width - 0.5f, height - 0.5f);
        backgroundIsForCell = false;
    }


    // ----------------------------------------------------------
    /**
     * Return the world's background image.
     *
     * @return The background image.
     */
    public Image getBackground()
    {
        return background;
    }


    // ----------------------------------------------------------
    /**
     * Return whether the background image is scaled to each cell and
     * repeated, or stretched over the entire grid.
     *
     * @return True if the background is applied separately to each cell,
     * or false if the background is fit to the entire world grid.
     */
    public boolean backgroundIsForCells()
    {
        return backgroundIsForCell;
    }


    // ----------------------------------------------------------
    /**
     * Determine the preferred orientation of this World when displayed on the
     * device.
     *
     * @param orientation The orientation to use, specified as one of
     *                    the screen orientation constants defined in
     *                    {@link android.content.pm.ActivityInfo}; typically,
     *                    either
     * {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE}
     * (locks the display in landscape orientation),
     * {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}
     * (locks the display in portrait orientation),
     * or
     * {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_SENSOR}
     * (the orientation is controlled by the device's built-in
     * orientation sensor).
     */
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
        if (view != null && view.getContext() instanceof Activity)
        {
            ((Activity)view.getContext()).setRequestedOrientation(orientation);
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the preferred orientation of this world.
     * @return An integer constant representing the orientation.
     * @see #setOrientation(int)
     */
    public int getOrientation()
    {
        return orientation;
    }


    // ----------------------------------------------------------
    /**
     * Set the effective cell size of this world (for bitmaps) and
     * whether this world (and its actors) should be automatically scaled
     * up (zoomed) if the Android device resolution permits it.
     *
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
    public void setScaledCellSize(int scaledCellSize, boolean scaleToFit)
    {
        this.scaledCellSize = scaledCellSize;
        this.scaleToFit = scaleToFit;

        if (view != null)
        {
//            System.out.println("scaledCellSize = " + scaledCellSize
//                + ", scaleToFit = " + scaleToFit);
            int vWidth = view.getWidth();
            int vHeight = view.getHeight();
            pixelsPerCell = scaledCellSize;
            if (scaledCellSize == 0)
            {
                this.scaleToFit = true;
            }

            if (this.scaleToFit)
            {
                // Account for 1 extra pixel on right and bottom if
                // grid lines are desired
                int offset = (gridColor == null) ? 0 : 1;
                float xScale = (vWidth - offset)  / (float)width;
                float yScale = (vHeight - offset) / (float)height;
                pixelsPerCell = Math.min(xScale, yScale);
            }

//            System.out.println("pixelsPerCell = " + pixelsPerCell);
            gridArea = new RectF(0, 0,
                (pixelsPerCell * width),
                (pixelsPerCell * height));
            float xOffset = (vWidth - gridArea.right)/2.0f;
            gridArea.left += xOffset;
            gridArea.right += xOffset;
            float yOffset = (vHeight - gridArea.bottom)/2.0f;
            gridArea.top += yOffset;
            gridArea.bottom += yOffset;

            gridTransform = new Matrix();
            gridTransform.postTranslate(0.5f, 0.5f);
            gridTransform.postScale(pixelsPerCell, pixelsPerCell);
            gridTransform.postTranslate(gridArea.left, gridArea.top);
        }
    }


    // ----------------------------------------------------------
    /**
     * Draws the world (as a background) on the canvas.  Does not draw
     * the world's actors, which are drawn by the enclosing WorldView.
     *
     * @param canvas the Canvas on which to draw the world.
     */
    public void draw(Canvas canvas)
    {
//        {
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.FILL_AND_STROKE);
//            paint.setColor(android.graphics.Color.MAGENTA);
//            canvas.drawRect(new android.graphics.Rect(0, 0, 200, 200), paint);
//        }

        if (backgroundColor != null)
        {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(backgroundColor.toRawColor());
            canvas.drawRect(grid, paint);
        }

        if (background != null)
        {
            Bitmap bm = background.asBitmap();
            if (bm == null)
            {
                background.resolveAgainstContext(view.getContext());
                bm = background.asBitmap();
                if (bm == null)
                {
                    background = null;
                }
            }

            if (bm != null)
            {
                if (backgroundIsForCell)
                {
                    RectF dest = new RectF(backgroundRect);

                    for (int x = 0; x < width; x++)
                    {
                        dest.top = backgroundRect.top;
                        dest.bottom = backgroundRect.bottom;
                        for (int y = 0; y < height; y++)
                        {
                            canvas.drawBitmap(bm, null, dest, null);
                            dest.top++;
                            dest.bottom++;
                        }
                        dest.left++;
                        dest.right++;
                    }
                }
                else
                {
                    canvas.drawBitmap(bm, null, backgroundRect, null);
                }
            }
        }

        if (gridColor != null)
        {
            Paint paint = new Paint();
            paint.setColor(gridColor.toRawColor());
            paint.setStrokeWidth(0);
            float limit = height - 0.5f;
            for (float x = -0.5f; x < width; x++)
            {
                canvas.drawLine(x, -0.5f, x, limit, paint);
            }
            limit = width - 0.5f;
            for (float y = -0.5f; y < height; y++)
            {
                canvas.drawLine(-0.5f, y, limit, y, paint);
            }
        }
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /* package */ void setWorldView(WorldView view)
    {
        this.view = view;

        // force setOrientation() to take effect here
        setOrientation(orientation);

        // force setScaledCellSize() to take effect here
        setScaledCellSize(scaledCellSize, scaleToFit);

        if (deferredAdds != null)
        {
            for (Actor actor : deferredAdds)
            {
                add(actor);
            }
            deferredAdds = null;
        }
    }


    // ----------------------------------------------------------
    /* package */ WorldView getWorldView()
    {
        return view;
    }


    // ----------------------------------------------------------
    /* package */ Matrix getGridTransform()
    {
        return gridTransform;
    }


    // ----------------------------------------------------------
    /* package */ RectF scaleRawPixels(int width, int height)
    {
        float scaleFactor = pixelsPerCell;
        if (scaledCellSize != 0)
        {
            scaleFactor = scaledCellSize;
        }
        return new RectF(0.0f, 0.0f,
            width / scaleFactor,
            height / scaleFactor);
    }
}
