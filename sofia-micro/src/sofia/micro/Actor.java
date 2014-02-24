package sofia.micro;

import java.util.Set;
import sofia.graphics.Anchor;
import sofia.graphics.ImageShape;
import sofia.graphics.Image;
import sofia.graphics.PointAndAnchor;
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
 * @version $Date: 2012/08/21 14:19 $
 */
public class Actor
    extends sofia.micro.internal.DelegatingShape
{
    //~ Fields ................................................................

    /** The world containing this actor. */
    private World world;
    private String nickName;
    private boolean scaleToCell;
    private boolean centerAnchorAfterScale = true;

    /** Error message to display when trying to use methods that requires
     * the actor be in a world.
     */
    private static final String ACTOR_NOT_IN_WORLD = "Actor not in world. "
        + "An attempt was made to use the actor's location while it is not "
        + "in the world. Either it has not yet been inserted, or it has been "
        + "removed.";


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Actor.  By default, this actor's image will be scaled
     * to the size of a single grid cell, preserving aspect ratio.
     */
    public Actor()
    {
        this(null, true);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Actor.  By default, this actor's image will be scaled
     * to the size of a single grid cell, preserving aspect ratio.
     * @param nickName The nickname for this actor.
     */
    public Actor(String nickName)
    {
        this(nickName, true);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Actor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public Actor(boolean scaleToCell)
    {
        this(null, scaleToCell);
    }

    // ----------------------------------------------------------
    /**
     * Create a new Actor.
     * @param nickName The nickname for this actor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public Actor(String nickName, boolean scaleToCell)
    {
        this.nickName = nickName;
        this.scaleToCell = scaleToCell;
        Image image = new Image(getClass());
        image.setScaleForDpi(false);
        RectF bounds = new RectF(-0.45f, -0.45f, 0.45f, 0.45f);
        setDelegate(new ImageShape(image, bounds));

        // This call is via super, to avoid the variable reset employed
        // when clients reset the position anchor
        super.setPositionAnchor(Anchor.CENTER);
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
     * Returns the image used to represent this actor, as an {@link Image}
     * object. This image can be modified to change the actor's appearance.
     *
     * @return The object's image.
     */
    public Image getImage()
    {
        Shape delegate = getDelegate();
        return (delegate instanceof ImageShape)
            ? ((ImageShape)delegate).getImage()
            : null;
    }


    // ----------------------------------------------------------
    /**
     * Returns the image used to represent this actor, as a {@link Shape}
     * object. This shape can be modified to change the actor's appearance.
     *
     * @return The object's image as a shape.
     */
    public Shape getShape()
    {
        return getDelegate();
    }


    // ----------------------------------------------------------
    /**
     * Set an image for this actor from an image file. The file may be in
     * jpeg, gif or png format.
     *
     * @param fileName The name of the image file.
     * @throws IllegalArgumentException If the image cannot be loaded.
     */
    public void setImage(String fileName)
        throws IllegalArgumentException
    {
        Image image = new Image(fileName);
        image.setScaleForDpi(false);
        setImage(image);
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
        if (world != null)
        {
            scaleImageForWorldIfNecessary();
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
     * @return A set of all neighbors found.
     */
    protected <MyActor extends Actor> Set<MyActor> getNeighbors(
        float distance, boolean diagonal, Class<MyActor> cls)
    {
        failIfNotInWorld();
        return world.getNeighbors(this, distance, diagonal, cls);
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
     * @return Set of objects at the given offset. The set will include this
     *         object, if the offset is zero.
     */
    protected <MyActor extends Actor> Set<MyActor> getObjectsAtOffset(
        float dx, float dy, Class<MyActor> cls)
    {
        failIfNotInWorld();
        return world.getObjectsAt(getX() + dx, getY() + dy, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return one object that is located at the specified cell (relative to
     * this object's location). Objects found can be restricted to a specific
     * class (and its subclasses) by supplying the 'cls' parameter. If more
     * than one object of the specified class resides at that location, one
     * of them will be chosen and returned.
     *
     * @param dx X-coordinate relative to this object's location.
     * @param dy Y-coordinate relative to this object's location.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @return An object at the given location, or null if none found.
     */
    protected <MyActor extends Actor> MyActor getOneObjectAtOffset(
        float dx, float dy, Class<MyActor> cls)
    {
        failIfNotInWorld();
        return world.getOneObjectAt(getX() + dx, getY() + dy, cls);
    }


    // ----------------------------------------------------------
    /**
     * Return all objects within range 'radius' around this object.
     * An object is within range if the distance between its centwr and this
     * object's center is less than or equal to 'radius'.
     *
     * @param radius Radius of the circle (in cells).
     * @param cls Class of objects to look for (passing 'null' will find
     *            all objects).
     * @param <MyActor> The type of actor to look for, as specified
     *                  in the cls parameter.
     * @return Set of objects within the specified range.
     */
    protected <MyActor extends Actor> Set<MyActor> getObjectsInRange(
        float radius, Class<MyActor> cls)
    {
        failIfNotInWorld();
        return world.getObjectsInRange(getX(), getY(), radius, cls);
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
     * @return Set of intersecting objects.
     */
    protected <MyActor extends Actor> Set<MyActor> getIntersectingObjects(
        Class<MyActor> cls)
    {
        failIfNotInWorld();
        return world.getIntersectingObjects(this, cls);
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
        failIfNotInWorld();
        return world.getOneIntersectingObject(this, cls);
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
        return Math.round(getX());
    }


    // ----------------------------------------------------------
    /**
     * Assign a new horizontal (x-axis) location for this actor. This moves
     * the actor to the specified x-axis location, without changing its y-axis
     * location. The location is specified as the x-coordinate of
     * a cell column in the world. For actors, gridX and x are essentially
     * the same.
     *
     * @param x The location to move to on the x-axis.
     *
     * @see #setGridLocation(int, int)
     */
    public void setGridX(int x)
    {
        setX(x);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(float x)
    {
        if (world != null)
        {
            x = limit(x, world.getWidth());
        }
        super.setX(x);
        updateBounds(x, getY());
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
        return Math.round(getY());
    }


    // ----------------------------------------------------------
    /**
     * Assign a new vertical (y-axis) location for this actor. This moves the
     * actor to the specified y-axis location, without changing its x-axis
     * location. The location is specified as the y-coordinate of
     * a cell row in the world. For actors, gridY and y are essentially
     * the same.
     *
     * @param y The location to move to on the y-axis.
     *
     * @see #setGridLocation(int, int)
     */
    public void setGridY(int y)
    {
        setY(y);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setY(float y)
    {
        if (world != null)
        {
            y = limit(y, world.getHeight());
        }
        super.setY(y);
        updateBounds(getX(), y);
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
        setPosition(new PointF(x, y));
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(PointF position)
    {
        if (world != null)
        {
            position.x = limit(position.x, world.getWidth());
            position.y = limit(position.y, world.getHeight());
        }
        super.setPosition(position);
        updateBounds(getX(), getY());
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(PointAndAnchor pointAndAnchor)
    {
        super.setPosition(pointAndAnchor);
        // Force limit checking
        setPosition(getPosition());
        updateBounds(getX(), getY());
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


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setBounds(RectF newBounds)
    {
        scaleToCell = false;
        super.setBounds(newBounds);
        // Force limit checking
        setPosition(getPosition());
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPositionAnchor(Anchor anchor)
    {
        centerAnchorAfterScale = false;
        super.setPositionAnchor(anchor);
        // Force limit checking
        setPosition(getPosition());
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPositionAnchor(PointF anchor)
    {
        centerAnchorAfterScale = false;
        super.setPositionAnchor(anchor);
        // Force limit checking
        setPosition(getPosition());
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void addOther(Shape newShape)
    {
        failIfNotInWorld();
        if (newShape instanceof Actor)
        {
            getWorld().add((Actor)newShape);
        }
        else
        {
            super.addOther(newShape);
        }
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove()
    {
        failIfNotInWorld();
        getWorld().remove(this);
    }


    // ----------------------------------------------------------
    /**
     * Get the "nickname" for this object--an assignable name that
     * is used in the toString() representation of this object.
     * @return This actor's nickname, or its class name if no
     *         nickname has been assigned.
     */
    public String getNickName()
    {
        return (nickName == null)
            ? getClass().getSimpleName()
            : nickName;
    }


    // ----------------------------------------------------------
    /**
     * Set the "nickname" for this object--an assignable name that
     * is used in the toString() representation of this object.
     * @param nickName The new nickname for this actor. If null, it
     *                 will "erase" any previously assigned name.
     */
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }


    // ----------------------------------------------------------
    /**
     * Returns a human-readable string representation of the actor.
     *
     * @return A human-readable string representation of the actor.
     */
    @Override
    public String toString()
    {
        return getNickName() + " at (" + getGridX() + ", " + getGridY() + ")";
    }


    //~ Infrastructure Methods ................................................

    // ----------------------------------------------------------
    /**
     * Sets the current world for the actor to the given world.
     *
     * @param world world for the actor to be contained in
     */
    /* package */ void setWorld(World world)
    {
        // This is not included as part of addedToWorld(), so that
        // subclasses overriding addedToWorld() don't have to call
        // super (or, more importantly, not calling super in an
        // overriding definition of addedToWorld() doesn't cause bugs).
        this.world = world;
        // limit checking is handled by setPosition() at end of this call:
        scaleImageForWorldIfNecessary();
    }


    // ----------------------------------------------------------
    /**
     * Updates the bounds for the image to match the changed x and y coordinate.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    private void updateBounds(float x, float y)
    {
        RectF bounds = getBounds();
        super.setBounds(new RectF(x - bounds.width() / 2, y - bounds.height() / 2,
            x + bounds.width() / 2, y + bounds.height() / 2));
    }

    // ----------------------------------------------------------
    private void scaleImageForWorldIfNecessary()
    {
        if (world == null)
        {
            return;
        }

        PointF anchor = null;
        if (!centerAnchorAfterScale)
        {
            // Preserve old position anchor as a proportional value
            anchor = getPositionAnchor();
            RectF bb = getBounds();
            anchor = new PointF(anchor.x / bb.width(), anchor.y / bb.height());
        }
        PointF position = getPosition();

        if (getDelegate() instanceof ImageShape)
        {
            Image image = ((ImageShape)getDelegate()).getImage();
            image.resolveAgainstContext(
                world.getWorldView().getContext());
            int width = image.getWidth();
            int height = image.getHeight();
            RectF bb = getBounds();
            if (scaleToCell)
            {
                if (width > height)
                {
                    float heightAddition = height / (float) width;
                    float adj = (heightAddition < bb.height()) ? heightAddition :
                        bb.height();

                    super.setBounds(new RectF(
                        bb.left, bb.top,
                        bb.left + bb.width(),
                        bb.top + adj));
                }
                else if (width < height)
                {
                    float widthAddition = width / (float) height;
                    float adj = (widthAddition < bb.height()) ? widthAddition :
                        bb.height();

                    super.setBounds(new RectF(
                        bb.left, bb.top,
                        bb.left + adj,
                        bb.top + bb.height()));
                }
                else
                {
                    super.setBounds(new RectF(
                        bb.left, bb.top, bb.left + bb.width(), bb.top + bb.height()));
                }
            }
            else
            {
                RectF newBounds = world.scaleRawPixels(width, height);
                newBounds.offsetTo(bb.left, bb.top);
                super.setBounds(newBounds);
            }
        }
        // otherwise, just use the existing bounding box, which starts
        // off as 1x1, which is already scaled to a single cell.

        if (centerAnchorAfterScale)
        {
            super.setPositionAnchor(Anchor.CENTER);
        }
        else
        {
            // Instead, scale and reuse old anchor
            RectF bb = getBounds();
            anchor.x *= bb.width();
            anchor.y *= bb.height();
            super.setPositionAnchor(anchor);
        }
        // Reset position, so that scaling happens "around" the anchor
        setPosition(position);
    }


    // ----------------------------------------------------------
    /**
     * Limits the value v to be less than limit and large or equal to zero.
     */
    private float limit(float v, float limit)
    {
        if (v < 0.0f)
        {
            return 0.0f;
        }
        if (v > limit - 1.0f)
        {
            return limit - 1.0f;
        }
        return v;
    }


    // ----------------------------------------------------------
    /**
     * Throws an exception if the actor is not in a world.
     *
     * @throws IllegalStateException If not in world.
     */
    private void failIfNotInWorld()
    {
        if (world == null)
        {
            throw new IllegalStateException(ACTOR_NOT_IN_WORLD);
        }
    }
}
