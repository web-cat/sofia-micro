package sofia.micro;

import sofia.micro.WorldView.KeyEventWrapper;
import sofia.micro.WorldView.MotionEventWrapper;
import java.util.ArrayList;
import sofia.graphics.DirectionalPad;
import sofia.internal.events.TouchDispatcher;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.List;
import java.util.Set;
import sofia.graphics.Color;
import sofia.graphics.Image;
import sofia.graphics.Shape;
import sofia.graphics.ShapeField;
import sofia.graphics.ZIndexComparator;

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
    private Set<Actor> deferredRemoves;
    private Set<Actor> actSet;
    private Object actorSetLock = new Object();
    private DirectionalPad dpad;

    private Engine engine;
    private static final int MAX_SPEED = 100;
    private static World mostRecentlyCreated = null;

    /** Error message to display when trying to use methods that requires
     * the actor be in a world.
     */
    private static final String WORLD_NOT_IN_VIEW = "World not in view. "
        + "An attempt was made to use the world's features while it is not "
        + "attached to a view. Either it has not yet been inserted, or it "
        + "has been removed.";

    private static final org.slf4j.Logger log =
        org.slf4j.LoggerFactory.getLogger(World.class);


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
        mostRecentlyCreated = this;
        this.width = width;
        this.height = height;
        this.backgroundIsForCell = backgroundIsForCell;
        orientation = (width >= height)
            ? android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        background = new Image(getClass());
        background.setUseDefaultIfNotFound(false);
        grid = new RectF(0, 0, width, height);
        setScaledCellSize(scaledCellSize, scaleToFit);
        deferredAdds = new java.util.ArrayList<Actor>();
        dpad = null;
        //dpad = new DirectionalPad(new RectF(-0.45f, -0.45f, 0.45f, 0.45f));
        engine = new Engine();
        engine.start();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Return the width of the world (in number of cells).
     * @return The width of the world (in number of cells).
     */
    public int getWidth()
    {
        return width;
    }


    // ----------------------------------------------------------
    /**
     * Return the height of the world (in number of cells).
     * @return The height of the world (in number of cells).
     */
    public int getHeight()
    {
        return height;
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to the world.
     * @param actor The Actor to add.
     */
    public void add(Actor actor)
    {
        synchronized (actorSetLock)
        {
            if (view == null)
            {
                synchronized (deferredAdds)
                {
                    deferredAdds.add(actor);
                }
            }
            else
            {
                // TODO: implement appropriate add semantics
                actor.setWorld(this);
                view.add((Shape)actor);
                if (actSet != null)
                {
                    if (isRunning())
                    {
                        synchronized (deferredAdds)
                        {
                            deferredAdds.add(actor);
                        }
                    }
                    else
                    {
                        actSet.add(actor);
                    }
                }
            }
        }
        actor.addedToWorld(this);
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

    /**
     * Adds a directional pad to the world view. Note that a view should only contain
     * a single dpad.
     *
     * @param bounds bounds for the directional pad to be set
     */
    public void setDirectionalPadBounds(RectF bounds)
    {
        dpad = new DirectionalPad(bounds);
        if (!view.getShapes().withClass(DirectionalPad.class).exist())
        {
            view.add(dpad);
        }
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
            synchronized (actorSetLock)
            {
                actor.setWorld(null);
                if (actSet != null)
                {
                    if (isRunning())
                    {
                        synchronized (deferredRemoves)
                        {
                            deferredRemoves.add(actor);
                        }
                    }
                    else
                    {
                        actSet.remove(actor);
                    }
                }
                view.remove((Shape)actor);
            }
        }
        // Otherwise, attempt to remove it from deferredAdds, if appropriate
        else if (actor.getWorld() != null)
        {
            throw new IllegalArgumentException(
                "The specified Actor is not in this World.");
        }
        else
        {
            synchronized (deferredAdds)
            {
                if (!deferredAdds.remove(actor))
                {
                    throw new IllegalArgumentException(
                    "The specified Actor is not in this World.");
                }
            }
        }
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
     * Set the paint order of objects in the world. Paint order is specified
     * by class: objects of one class will always be painted on top of objects
     * of some other class. The order of objects of the same class cannot be
     * specified.
     *
     * <p>Objects of classes listed first in the parameter list will
     * appear on top of all objects of classes listed later.</p>
     * <p>Objects of a class not explicitly specified inherit the
     * paint order from their superclass.</p>
     * <p>Objects of classes not listed will appear below the objects whose
     * classes have been specified.</p>
     *
     * @param classes  The classes in desired paint order.
     */
    public void setPaintOrder(Class<? extends Actor> ... classes)
    {
        failIfNotInView();
        view.getShapeField().setDrawingOrder(
            new ZClassComparator(view.getShapeField(), true, classes));
    }


    // ----------------------------------------------------------
    /**
     * Set the act order of objects in the world. Act order is specified
     * by class: objects of one class will always act before objects
     * of some other class. The order of objects of the same class cannot be
     * specified.
     *
     * <p>Objects of classes listed first in the parameter list will
     * act before any objects of classes listed later.</p>
     * <p>Objects of a class not explicitly specified inherit the act
     * order from their superclass.</p>
     * <p>Objects of classes not listed will act after all objects whose
     * classes have been specified.</p>
     *
     * @param classes The classes in desired act order.
     */
    public void setActOrder(Class<? extends Actor> ... classes)
    {
        failIfNotInView();
        // FIXME: This will totally break if called by an Actor from act()!
        actSet = new java.util.TreeSet<Actor>(
            new ZClassComparator(view.getShapeField(), false, classes));
        actSet.addAll(view.getShapes(Actor.class));
    }


    // ----------------------------------------------------------
    /**
     * Get all the actors in the world.
     * @return A set of all the actors in this world.
     */
    public Set<Actor> getObjects()
    {
        return view.getShapes(Actor.class);
    }


    // ----------------------------------------------------------
    /**
     * Get all the actors of the specified type in this world.
     *
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return Set of all the actors of the specified type (or any of its
     *         subtypes) in the world.
     */
    public <MyActor extends Actor> Set<MyActor> getObjects(
        Class<MyActor> cls)
    {
        failIfNotInView();
        if (cls == null)
        {
            @SuppressWarnings("unchecked")
            Set<MyActor> result = (Set<MyActor>)view.getShapes(Actor.class);
            return result;
        }
        else
        {
            return view.getShapes(cls);
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the number of actors currently in the world.
     *
     * @return The number of actors in the world.
     */
    public int numberOfObjects()
    {
        failIfNotInView();
        return view.getShapes().count();
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
     * Return all objects of the specified type at a given cell.
     * <p>
     * An object is defined to be at that cell if its graphical representation
     * overlaps the center of the cell.</p>
     *
     * @param x X-coordinate of the cell to be checked.
     * @param y Y-coordinate of the cell to be checked.
     * @param cls Class of objects to find ('null' will return all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                      in the cls parameter.
     * @return A set of objects at the specified location.
     */
    public <MyActor extends Actor> Set<MyActor> getObjectsAt(
        float x, float y, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getShapesAt(x, y, cls);
    }


    //~ Android-oriented Methods ..............................................

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
     * Set the speed of this world, which determines the delay between
     * successive steps where act() is called on all Actors.  The speed
     * is a value between 0-100, where 100 is maximum speed (least delay)
     * and 0 is the slowest speed.
     * @param speed The new speed (0-100)
     */
    public void setSpeed(int speed)
    {
        if (speed < 0)
        {
            speed = 0;
        }
        else if (speed > 100)
        {
            speed = 100;
        }
        engine.setSpeed(speed);
    }


    // ----------------------------------------------------------
    /**
     * Get the speed of this world, which determines the delay between
     * successive steps where act() is called on all Actors.  The speed
     * is a value between 0-100, where 100 is maximum speed (least delay)
     * and 0 is the slowest speed.
     * @return The current speed (0-100)
     */
    public int getSpeed()
    {
        return engine.getSpeed();
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
    /**
     * Return all the objects that intersect the given object. This takes the
     * graphical extent of objects into consideration.
     *
     * @param actor An Actor in the world.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A set of objects that intersect the given object.
     */
    /* package */ <MyActor extends Actor> Set<MyActor> getIntersectingObjects(
        Actor actor, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getIntersectingShapes(actor, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return one object that intersects the given object. This takes the
     * graphical extent of objects into consideration.  Objects found can be
     * restricted to a specific class (and its subclasses) by supplying the
     * 'cls' parameter If more than one object of the specified class
     * intersects the given actor, one of them will be chosen and returned.
     *
     * @param actor An Actor in the world.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A set of objects that intersect the given object.
     */
    /* package */ <MyActor extends Actor> MyActor getOneIntersectingObject(
        Actor actor, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getIntersectingShape(actor, cls);
    }


    // ----------------------------------------------------------
    /**
     * Returns all objects with the logical location within the specified
     * circle. In other words an object A is within the range of an object B if
     * the distance between the centre of the two objects is less than r.
     *
     * @param x Center of the circle.
     * @param y Center of the circle.
     * @param r Radius of the circle.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A set of objects that lie within the given circle.
     */
    /* package */ <MyActor extends Actor> Set<MyActor> getObjectsInRange(
        float x, float y, float r, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getShapesInRange(x, y, r, cls);
    }


    // ----------------------------------------------------------
    /**
     * Returns the neighbors to the given location. This method only looks at
     * the logical location and not the extent of objects. Hence it is most
     * useful in scenarios where objects only span one cell.
     *
     * @param actor    The actor whose neighbors will be located.
     * @param distance Distance in which to look for other objects.
     * @param diag     Is the distance also diagonal?
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A collection of all neighbors found.
     */
    /* package */ <MyActor extends Actor> Set<MyActor> getNeighbors(
        Actor actor, float distance, boolean diag, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getNeighbors(actor, distance, diag, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return all objects that intersect a straight line from the location at
     * a specified angle. The angle is clockwise.
     *
     * @param x x-coordinate.
     * @param y y-coordinate.
     * @param angle The angle relative to current rotation of the object.
     *            (0-359).
     * @param length How far we want to look (in cells).
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyShape> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A collection of all objects found.
     */
    /* package */ <MyShape extends Shape> Set<MyShape> getObjectsInDirection(
        float x, float y, float angle, float length, Class<MyShape> cls)
    {
        failIfNotInView();
        return view.getShapesInDirection(x, y, angle, length, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return one object that is located at the specified cell. Objects found
     * can be restricted to a specific class (and its subclasses) by supplying
     * the 'cls' parameter.  If more than one object of the specified class
     * resides at that location, one of them will be chosen and returned.
     *
     * @param x   X-coordinate.
     * @param y   Y-coordinate.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return An object at the given location, or null if none found.
     */
    /* package */ <MyActor extends Actor> MyActor getOneObjectAt(
        float x, float y, Class<MyActor> cls)
    {
        failIfNotInView();
        return view.getShapeAt(x, y, cls);
    }


    // ----------------------------------------------------------
    /**
     * Sets the world view to the given view. Also adds and removes any deferred
     * actors to the world.
     *
     * @param view view to be added to this world
     */
    /* package */ void setWorldView(WorldView view)
    {
        this.view = view;

        // force setOrientation() to take effect here
        setOrientation(orientation);

        // force setScaledCellSize() to take effect here
        setScaledCellSize(scaledCellSize, scaleToFit);
        if (deferredRemoves == null)
        {
            deferredRemoves = new java.util.TreeSet<Actor>(
                new ZClassComparator(view.getShapeField(), false));
        }

        if (view != null && !isRunning())
        {
            synchronized (deferredAdds)
            {
                for (Actor actor : deferredAdds)
                {
                    add(actor);
                }
                deferredAdds.clear();
            }
            synchronized (deferredRemoves)
            {
                for (Actor actor : deferredRemoves)
                {
                    remove(actor);
                }
                deferredRemoves.clear();
            }
            if (dpad != null)
            {
                view.add(dpad);
                dpad.setY(height - 1);
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the (Android) view that is displaying this world.
     * @return The view displaying this world.
     */
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


    // ----------------------------------------------------------
    /**
     * Returns whether the engine is currently running.
     *
     * @return true if the engine is running, false otherwise.
     */
    /* package */ boolean isRunning()
    {
        return engine.isRunning();
    }


    // ----------------------------------------------------------
    /**
     * Start (or run, or resume) the world.  This requires that the world
     * has already been attached to a view.
     */
    public void start()
    {
        engine.startRunning();
    }


    // ----------------------------------------------------------
    /* package */ void runOneStep()
    {
        engine.requestOneStep();
    }


    // ----------------------------------------------------------
    /**
     * Stop (or pause running of) the world.  This requires that the world
     * has already been attached to a view.
     */
    public void stop()
    {
        engine.stopRunning();
    }


    // ----------------------------------------------------------
    /* package */ void temporarilyPauseRunning()
    {
        engine.temporarilyPauseRunning();
    }


    // ----------------------------------------------------------
    /* package */ void resumeRunningIfNecessary()
    {
        engine.resumeRunningIfNecessary();
    }


    // ----------------------------------------------------------
    /* package */ World getMostRecentlyCreated()
    {
        return mostRecentlyCreated;
    }


    // ----------------------------------------------------------
    /**
     * Throws an exception if the world is not associated with a view.
     *
     * @throws IllegalStateException If not in view.
     */
    private void failIfNotInView()
    {
        if (view == null)
        {
            throw new IllegalStateException(WORLD_NOT_IN_VIEW);
        }
    }


    // ----------------------------------------------------------
    private static class ZClassComparator
        extends ZIndexComparator
    {
        private java.util.Map<Class<? extends Shape>, Integer> order;
        private int last = 0;

        // ----------------------------------------------------------
        /**
         * Create a comparator that orders objects by class first, and
         * then by z-index.
         * @param parent  The shape set to use for determining relative
         *                insertion times.
         * @param reverse If false, the classes should be ordered exactly
         *                as they appear in the parameter list; if true,
         *                the classes should be ordered in the reverse of
         *                the parameter list order.
         * @param classes The class order to use.  Any objects that are not
         *                listed will appear <i>after</i> any of those listed.
         */
        public ZClassComparator(ShapeField parent, boolean reverse,
            Class<? extends Shape> ... classes)
        {
            //super(parent);
            super();
            if (classes != null && classes.length > 0)
            {
                order =
                    new java.util.TreeMap<Class<? extends Shape>, Integer>();
                if (reverse)
                {
                    last = classes.length;
                }
                for (Class<? extends Shape> cls : classes)
                {
                    order.put(cls, last);
                    if (reverse)
                    {
                        last--;
                    }
                    else
                    {
                        last++;
                    }
                }
            }
        }


        // ----------------------------------------------------------
        private int getClassOrder(Class<? extends Shape> cls)
        {
            if (order == null || cls == null)
            {
                return 0;
            }
            else
            {
                Integer result = order.get(cls);
                if (result == null)
                {
                    Class<?> superClass = cls.getSuperclass();
                    while (superClass != null && result == null)
                    {
                        result = order.get(superClass);
                    }
                    if (result == null)
                    {
                        result = last;
                    }
                    order.put(cls, result);
                }
                return result;
            }
        }


        // ----------------------------------------------------------
        @Override
        public int compare(Shape shape1, Shape shape2)
        {
            int order1 = getClassOrder(shape1.getClass());
            int order2 = getClassOrder(shape2.getClass());
            if (order1 == order2)
            {
                return super.compare(shape1, shape2);
            }
            else
            {
                return order1 - order2;
            }
        }
    }


    // ----------------------------------------------------------
    private class Engine
        extends Thread
    {
        private volatile boolean isRunning            = false;
        private volatile boolean oneStep              = false;
        private volatile boolean isTemporarilyPaused  = false;
        private volatile boolean willStop             = false;
        private volatile boolean willTemporarilyPause = false;
        private volatile boolean signalStart          = false;
        private volatile boolean signalStop           = false;
        private volatile int     speed;
        private volatile long    delay;


        // ----------------------------------------------------------
        public Engine()
        {
            setSpeed(30);
        }


        // ----------------------------------------------------------
        public synchronized int getSpeed()
        {
            return speed;
        }


        // ----------------------------------------------------------
        public synchronized void setSpeed(int speed)
        {
            log.debug("setSpeed({})", speed);

            this.speed = speed;
            // Make the speed into a delay
            long rawDelay = MAX_SPEED - speed;

            long min = 1L;     // Delay at MAX_SIMULATION_SPEED - 1
            long max = 5000L;  // Delay at slowest speed

            double a = Math.pow(max / (double) min, 1D / (MAX_SPEED - 1));
            if (rawDelay > 0)
            {
                delay = (long) (Math.pow(a, rawDelay - 1) * min);
            }
            else
            {
                delay = 0;
            }
        }


        // ----------------------------------------------------------
        public synchronized boolean isRunning()
        {
            return isRunning;
        }


        // ----------------------------------------------------------
        public synchronized void startRunning()
        {
            log.debug("startRunning()");
            if (!isRunning)
            {
                isRunning = true;
                signalStart = true;
                notify();
            }
        }


        // ----------------------------------------------------------
        public synchronized void requestOneStep()
        {
            log.debug("requestOneStep()");
            if (!isRunning)
            {
                oneStep   = true;
                isRunning = true;
                notify();
            }
        }


        // ----------------------------------------------------------
        public synchronized void stopRunning()
        {
            log.debug("stopRunning()");
            if (isRunning)
            {
                willStop = true;
                signalStop = true;
            }
        }


        // ----------------------------------------------------------
        public synchronized void temporarilyPauseRunning()
        {
            log.debug("temporarilyPauseRunning()");
            if (isRunning && !isTemporarilyPaused)
            {
                willTemporarilyPause = true;
            }
        }


        // ----------------------------------------------------------
        public synchronized void resumeRunningIfNecessary()
        {
            log.debug("resumeRunningIfNecessary()");
            willTemporarilyPause = false;
            if (isRunning && isTemporarilyPaused)
            {
                isTemporarilyPaused = false;
                notify();
            }
        }


        // ----------------------------------------------------------
        @Override
        public void run()
        {
            while (true)
            {
                boolean needToWait = false;
                synchronized (this)
                {
                    if (willStop)
                    {
                        isRunning = false;
                    }

                    if (!isRunning)
                    {
                        log.debug("engine stopping");
                        willStop = false;
                        willTemporarilyPause = false;
                        isTemporarilyPaused = false;
                        needToWait = true;
                    }
                    else if (willTemporarilyPause)
                    {
                        log.debug("engine will pause");
                        isTemporarilyPaused = true;
                        willTemporarilyPause = false;
                        needToWait = true;
                    }
                }

                // If necessary, wait for a signal to start up
                if (needToWait)
                {
                    if (signalStop)
                    {
                        notifyOfStop();
                        signalStop = false;
                    }
                    try
                    {
                        log.debug("No longer running.  Waiting for request.");
                        synchronized (this)
                        {
                            wait();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        // Ignore and resume
                    }


                    if (signalStart)
                    {
                        notifyOfStart();
                        signalStart = false;
                    }
                }
                // Otherwise, insert a delay between steps
                else
                {
                    try
                    {
                        log.debug("engine: sleep({})", delay);
                        sleep(delay);
                    }
                    catch (InterruptedException e)
                    {
                        // Ignore and resume
                    }
                }

                step();

                synchronized (this)
                {
                    if (oneStep)
                    {
                        log.debug("oneStep request detected, will stop.");
                        oneStep = false;
                        willStop = true;
                    }
                }
            }
        }


        // ----------------------------------------------------------
        /**
         * Run a single step of the world, which includes calling act()
         * on all actors, the world, and the view.
         */
        private void step()
        {
            List<MotionEventWrapper> motionBuffer = view.getMotionBuffer();
            List<KeyEventWrapper> keyBuffer = view.getKeyBuffer();
            //LinkedList<KeyEvent> keyBuffer = view.getKeyBuffer();
            //LinkedList<Integer> keyCodeBuffer = view.getKeyCodeBuffer();
            view.swapBuffers();

            log.debug("beginning step");

            // act for view
            try
            {
                view.act();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + view.getClass().getSimpleName() + ".act()", e);
            }

            // dispatch events to the dpad before we call the act() method
            // for the world or the actors
            try
            {
                for (MotionEventWrapper e : motionBuffer)
                {
                    if (dpad != null)
                    {
                        dpad.onTouchDown(e.motionEvent);
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in dispatching events to dpad", e);
            }

            // act for world
            try
            {
                // if the world has any listeners, dispatch motion events to it
                if (!motionBuffer.isEmpty() && TouchDispatcher.hasTouchListeners(World.this))
                {
                    for (MotionEventWrapper e : motionBuffer)
                    {
                        TouchDispatcher.dispatchTo(World.this,
                            World.this.scaledCellSize, e.motionEvent, e.action);
                    }
                }
                //for (KeyEventWrapper e : keyBuffer)
                //{
                    // dispatch key events
                //}
                act();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + World.this.getClass().getSimpleName() + ".act()", e);
            }

            // act for all actors
            if (actSet == null)
            {
                actSet = new java.util.TreeSet<Actor>(
                    new ZClassComparator(view.getShapeField(), false));
                actSet.addAll(getObjects(Actor.class));
            }
            for (Actor actor : actSet)
            {
                if (!deferredRemoves.contains(actor))
                {
                    try
                    {
                        // if the actor has any listeners, dispatch motion events to it
                        if (!motionBuffer.isEmpty() && TouchDispatcher.hasTouchListeners(actor))
                        {
                            for (MotionEventWrapper e : motionBuffer)
                            {
                                TouchDispatcher.dispatchTo(actor, World.this.scaledCellSize,
                                    e.motionEvent, e.action);
                            }
                        }
                        //for (KeyEventWrapper e : keyBuffer)
                        //{
                            // dispatch key events
                        //}
                        actor.act();
                    }
                    catch (Exception e)
                    {
                        log.error("Unexpected exception in "
                            + actor.getClass().getSimpleName() + ".act()", e);
                    }
                }
            }
            view.clearBuffers();

            handleDeferredActions();
            log.debug("ending step");
            view.repaint();
        }


        // ----------------------------------------------------------
        private void notifyOfStart()
        {
            log.debug("notifying started()");

            // started for view
            try
            {
                view.started();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + view.getClass().getSimpleName() + ".started()", e);
            }

            // started for world
            try
            {
                started();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + World.this.getClass().getSimpleName() + ".started()", e);
            }

            handleDeferredActions();
            view.repaint();
        }


        // ----------------------------------------------------------
        private void notifyOfStop()
        {
            log.debug("notifying stopped()");

            // stopped for view
            try
            {
                view.stopped();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + view.getClass().getSimpleName() + ".stopped()", e);
            }

            // stopped for world
            try
            {
                stopped();
            }
            catch (Exception e)
            {
                log.error("Unexpected exception in "
                    + World.this.getClass() + ".stopped()", e);
            }

            handleDeferredActions();
            view.repaint();
        }


        // ----------------------------------------------------------
        private void handleDeferredActions()
        {
            synchronized (deferredAdds)
            {
                for (Actor actor : deferredAdds)
                {
                    actSet.add(actor);
                }
                deferredAdds.clear();
            }
            synchronized (deferredRemoves)
            {
                for (Actor actor : deferredRemoves)
                {
                    actSet.remove(actor);
                }
                deferredRemoves.clear();
            }
        }
    }
}
