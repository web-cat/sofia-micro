package sofia.micro;

import java.util.List;
import sofia.graphics.ImageShape;
import sofia.graphics.Image;
import sofia.graphics.Shape;
import android.graphics.PointF;
import android.graphics.RectF;

//-------------------------------------------------------------------------
/**
 * An Actor is an object (or character) that exists in a "microworld".
 * Every Actor has a location in the world, and an appearance (that is:
 * an icon, image, or shape).
 *
 * <p>An Actor is not normally instantiated directly, but instead used as a
 * superclass.  Create a subclass to represent more specific objects in the
 * world. Every object that is intended to appear in the world must extend
 * Actor. Subclasses can then define their own appearance and behavior.</p>
 *
 * <p>One of the most important aspects of this class is the 'act' method.
 * This method is called when the 'Act' or 'Run' buttons are activated in
 * the interface. The method here is empty, and subclasses normally provide
 * their own implementations.</p>
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public class Actor
    extends sofia.micro.internal.DelegatingShape
{
    //~ Fields ................................................................

    /** The world containing this actor. */
    private World world;
    private boolean bitmapScaledForWorld = false;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Actor.
     */
    public Actor()
    {
        super(new RectF(0.0f, 0.0f, 1.0f, 1.0f));
        setDelegate(new ImageShape(
            new Image(getClass()), new RectF(0.0f, 0.0f, 1.0f, 1.0f)));
    }


    //~ Public Methods (Greenfoot style) ......................................

    // ----------------------------------------------------------
    /**
     * The act method is called by the system to give actors a chance to
     * perform some action. At each action step in the environment,
     * each object's act method is invoked, in unspecified order.
     *
     * <p>The default implementation does nothing. This method should be
     * overridden in subclasses to implement an actor's behavior.</p>
     */
    public void act()
    {
        // Intentionally empty
    }


    // ----------------------------------------------------------
    /**
     * Move this actor the specified distance in the direction it is
     * currently facing.
     *
     * <p>The direction can be set using the {@link #setRotation(double)}
     * method.</p>
     *
     * @param distance  The distance to move (in cell-size units). A
     *                  negative value will move backwards.
     *
     * @see #setGridLocation(int, int)
     */
    public void move(int distance)
    {
        double radians = Math.toRadians(getRotation());

        // We round to the nearest integer, to allow moving one unit at an
        // angle to actually move.
        int dx = (int) Math.round(Math.cos(radians) * distance);
        int dy = (int) Math.round(Math.sin(radians) * distance);
        setGridLocation(getGridX() + dx, getGridY() + dy);
    }


    // ----------------------------------------------------------
    /**
     * Turn this actor by the specified amount (in degrees).
     *
     * @param amount  The number of degrees to turn. Positive values turn
     *                clockwise.
     *
     * @see #setRotation(double)
     */
    public void turn(double amount)
    {
        setRotation(getRotation() + amount);
    }


    // ----------------------------------------------------------
    /**
     * Turn this actor to face towards a certain location.
     *
     * @param x  The x-coordinate of the cell to turn towards.
     * @param y  The y-coordinate of the cell to turn towards.
     */
    public void turnTowards(int x, int y)
    {
        double a = Math.atan2(y - getGridY(), x - getGridX());
        setRotation(Math.toDegrees(a));
    }


    // ----------------------------------------------------------
    /**
     * Turn this actor to face towards another actor (in the same world).
     *
     * @param target  The actor to turn towards.
     */
    public void turnTowards(Actor target)
    {
        turnTowards(target.getGridX(), target.getGridY());
    }


    // ----------------------------------------------------------
    /**
     * Return the world that this actor lives in.
     *
     * @return The world.
     */
    public World getWorld()
    {
        return world;
    }


    // ----------------------------------------------------------
    /**
     * Returns the image used to represent this actor, as a {@link Shape}
     * object. This image can be modified to change the actor's appearance.
     *
     * @return The object's image/shape.
     */
    public Shape getImage()
    {
        Shape image = getDelegate();
        if (image == null)
        {
            setImage(new Image(getClass()));
        }
        scaleBitmapForWorldIfNecessary();
        return image;
    }


    // ----------------------------------------------------------
    /**
     * Set an image for this actor from an image file. The file may be in
     * jpeg, gif or png format.
     *
     * @param fileName The name of the image file.
     * @throws IllegalArgumentException If the image can not be loaded.
     */
    public void setImage(String fileName)
        throws IllegalArgumentException
    {
        setImage(new Image(fileName));
    }


    // ----------------------------------------------------------
    /**
     * Set a bitmap as the image for this actor.
     *
     * @param image The bitmap to use as this actor's image.
     */
    public void setImage(Image image)
    {
        setImage(new ImageShape(image, getBounds()));
    }


    // ----------------------------------------------------------
    /**
     * Set a {@link Shape} as the image for this actor.
     *
     * @param image The shape to use as this actor's image.
     */
    public void setImage(Shape image)
    {
        setDelegate(image);
        bitmapScaledForWorld = false;
        if (world != null)
        {
            scaleBitmapForWorldIfNecessary();
        }
    }


    // ----------------------------------------------------------
    /**
     * Return the neighbors to this object within a given distance. This
     * method considers only logical location, ignoring extent of the image.
     * Thus, it is most useful in scenarios where objects are contained in a
     * single cell.
     *
     * <p>All cells that can be reached in the number of steps given in
     * 'distance' from this object are considered. Steps may be only in the
     * four main directions, or may include diagonal steps, depending on the
     * 'diagonal' parameter. Thus, a distance/diagonal specification of
     * (1,false) will inspect four cells, (1,true) will inspect eight cells.
     * </p>
     *
     * @param distance Distance (in cells) in which to look for other objects.
     * @param diagonal If true, include diagonal steps.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return A list of all neighbors found.
     */
    protected <MyActor extends Actor> List<MyActor> getNeighbors(
        int distance, boolean diagonal, Class<MyActor> cls)
    {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }


    // ----------------------------------------------------------
    /**
     * Return all objects that intersect the center of the given location
     * (relative to this object's location).
     *
     * @param dx X-coordinate relative to this object's location.
     * @param dy Y-coordinate relative to this object's location.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return List of objects at the given offset. The list will include this
     *         object, if the offset is zero.
     */
    protected <MyActor extends Actor> List<MyActor> getObjectsAtOffset(
        int dx, int dy, Class<MyActor> cls)
    {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }


    // ----------------------------------------------------------
    /**
     * Return one object that is located at the specified cell (relative to
     * this objects location). Objects found can be restricted to a specific
     * class (and its subclasses) by supplying the 'cls' parameter. If more
     * than one object of the specified class resides at that location, one
     * of them will be chosen and returned.
     *
     * @param dx X-coordinate relative to this object's location.
     * @param dy Y-coordinate relative to this object's location.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return An object at the given location, or null if none found.
     */
    protected <MyActor extends Actor> MyActor getOneObjectAtOffset(
        int dx, int dy, Class<MyActor> cls)
    {
        List<MyActor> characters = getObjectsAtOffset(dx, dy, cls);
        if (characters.size() == 0)
        {
            return null;
        }
        else
        {
            return characters.get(0);
        }
    }


    // ----------------------------------------------------------
    /**
     * Return all objects within range 'radius' around this object.
     * An object is within range if the distance between its centre and this
     * object's centre is less than or equal to 'radius'.
     *
     * @param radius Radius of the circle (in cells).
     * @param cls Class of objects to look for (passing 'null' will find
     *            all objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return List of objects within the specified range.
     */
    protected <MyActor extends Actor> List<MyActor> getObjectsInRange(
        int radius, Class<MyActor> cls)
    {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }


    // ----------------------------------------------------------
    /**
     * Return all the objects that intersect this object. This takes the
     * graphical extent of objects into consideration.
     *
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return List of intersecting objects.
     */
    protected <MyActor extends Actor> List<MyActor> getIntersectingObjects(
        Class<MyActor> cls)
    {
        // TODO: implement
        throw new UnsupportedOperationException("Not yet implemented!");
    }


    // ----------------------------------------------------------
    /**
     * Return an object that intersects this object. This takes the
     * graphical extent of objects into consideration.
     *
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return An arbitrary intersecting object of the specified type, if
     * one exists; null otherwise.
     */
    protected <MyActor extends Actor> MyActor getOneIntersectingObject(
        Class<MyActor> cls)
    {
        List<MyActor> characters = getIntersectingObjects(cls);
        if (characters.size() == 0)
        {
            return null;
        }
        else
        {
            return characters.get(0);
        }
    }


    // ----------------------------------------------------------
    /**
     * This method is called by the system when this actor has been inserted
     * into the world. This method can be overridden to implement
     * custom behavior when the actor is inserted into the world.
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param world The world the object was added to.
     */
    public void addedToWorld(World world)
    {
        // Intentionally empty
    }


    //~ Public Methods (World support) ........................................

    // ----------------------------------------------------------
    /**
     * Return the x-coordinate of the actor's current location. The value
     * returned is the horizontal index of the actor's cell in the world.
     *
     * @return The x-coordinate of the object's current location.
     * @throws IllegalStateException If the actor has not been added into
     * a world.
     */
    public int getGridX()
    {
        // Truncate, not round
        return (int)getX();
    }


    // ----------------------------------------------------------
    /**
     * Assign a new horizontal (x-axis) location for this actor. This moves
     * the actor to the specified x-axis location, without changing its y-axis
     * location. The location is specified as the x-coordinate of
     * a cell column in the world.
     *
     * @param x The location to move to on the x-axis.
     *
     * @see #setGridLocation(int, int)
     */
    public void setGridX(int x)
    {
        float xOffset = getWidth() / 2.0f - 0.5f;
        setX(x - xOffset);
    }


    // ----------------------------------------------------------
    /**
     * Return the y-coordinate of the object's current location. The value
     * returned is the vertical index of the actor's cell in the world.
     *
     * @return The y-coordinate of the actor's current location
     * @throws IllegalStateException If the actor has not been added into
     * a world.
     */
    public int getGridY()
    {
        // Truncate, not round
        return (int)getY();
    }


    // ----------------------------------------------------------
    /**
     * Assign a new vertical (y-axis) location for this actor. This moves the
     * actor to the specified y-axis location, without changing its x-axis
     * location. The location is specified as the y-coordinate of
     * a cell row in the world.
     *
     * @param y The location to move to on the y-axis.
     *
     * @see #setGridLocation(int, int)
     */
    public void setGridY(int y)
    {
        float yOffset = getHeight() / 2.0f - 0.5f;
        setY(y - yOffset);
    }


    // ----------------------------------------------------------
    /**
     * Assign a new location for this actor. This moves the actor to the
     * specified location. The location is specified as the coordinates of
     * a cell in the world.
     *
     * <p>If this method is overridden it is important to call this method as
     * {@code super.setLocation(x,y)} from the overriding method, to avoid
     * infinite recursion.</p>
     *
     * @param x The location to move to on the x-axis.
     * @param y The location to move to on the y-axis.
     *
     * @see #move(int)
     */
    public void setGridLocation(int x, int y)
    {
        float xOffset = getWidth() / 2.0f - 0.5f;
        float yOffset = getHeight() / 2.0f - 0.5f;
        setPosition(new PointF(x - xOffset, y - yOffset));
    }


    //~ Public Methods (Shape style)...........................................

    // ----------------------------------------------------------
    /**
     * Set the rotation of this actor. Rotation is expressed as a degree
     * value, range (0..359). Zero degrees is to the east (right-hand side
     * of the world), and the angle increases clockwise.
     * @param angleInDegrees The new rotation amount, expressed as an
     * absolute angle in degrees.
     */
    public void setRotation(double angleInDegrees)
    {
        super.setRotation((float)angleInDegrees);
    }


    //~ Infrastructure Methods ................................................

    // ----------------------------------------------------------
    /* package */ void setWorld(World world)
    {
        // This is not included as part of addedToWorld(), so that
        // subclasses overriding addedToWorld() don't have to call
        // super (or, more importantly, not calling super in an
        // overriding definition of addedToWorld() doesn't cause bugs).
        this.world = world;
        bitmapScaledForWorld = false;
        scaleBitmapForWorldIfNecessary();
    }


    // ----------------------------------------------------------
    private void scaleBitmapForWorldIfNecessary()
    {
        if (!bitmapScaledForWorld && world != null)
        {
            if (getDelegate() instanceof ImageShape)
            {
                ((ImageShape)getDelegate()).getImage().resolveAgainstContext(
                    world.getWorldView().getContext());
                // TODO: scale bitmap
            }
            bitmapScaledForWorld = true;
        }
    }
}
