package sofia.micro;

import sofia.graphics.DirectionalPad;
import android.view.KeyEvent;
import java.util.LinkedList;
import android.view.MotionEvent;
import android.graphics.RectF;
import sofia.graphics.ShapeField;
import java.util.Set;
import sofia.graphics.Shape;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

//-------------------------------------------------------------------------
/**
 * Represents a view containing a {@link World} and its {@link Actor}
 * objects.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class WorldView
    extends sofia.graphics.ShapeView
{
    //~ Fields ................................................................

    private World world;

    private LinkedList<MotionEvent> motionBuffer = new LinkedList<MotionEvent>();
    private LinkedList<Integer> actionBuffer = new LinkedList<Integer>();
    private LinkedList<KeyEvent> keyBuffer = new LinkedList<KeyEvent>();
    private LinkedList<Integer> keyCodeBuffer = new LinkedList<Integer>();

    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WorldView.
     *
     * @param context This view's context.
     */
    public WorldView(Context context)
    {
        super(context);
        getHolder().addCallback(new SurfaceHolder.Callback() {
            // ----------------------------------------------------------
            @Override
            public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
            {
//                System.out.println("WorldView: surface holder created.");
            }


            // ----------------------------------------------------------
            @Override
            public void surfaceChanged(
                SurfaceHolder holder, int format, int width, int height)
            {
//                System.out.println("WorldView: surface changed: "
//                    + width + " x " + height);
                World world = getWorld();
                if (world != null)
                {
                    world.resumeRunningIfNecessary();
                }
            }


            // ----------------------------------------------------------
            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
//                System.out.println("WorldView: surface holder destroyed.");
                World world = getWorld();
                if (world != null)
                {
                    world.temporarilyPauseRunning();
                }
            }
        });
    }


    // ----------------------------------------------------------
    /**
     * Creates a new WorldView.
     *
     * @param context This view's context.
     * @param attrs This view's attributes.
     */
    public WorldView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new WorldView.
     *
     * @param context   This view's context.
     * @param attrs     This view's attributes.
     * @param defStyle  This view's default style.
     */
    public WorldView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Add an Actor to this view.
     * @param actor The Actor to add.
     */
    public void add(Actor actor)
    {
        assert world != null : "No world defined";
        world.add(actor);
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
        assert world != null : "No world defined";
        world.add(actor, x, y);
    }

    /**
     * Adds a directional pad to this view. Note that a view should only contain
     * a single dpad.
     *
     * @param dpad directional pad to be added
     */
    public void addDirectionalPad(DirectionalPad dpad)
    {
        if (!getShapes().withClass(DirectionalPad.class).exist())
        {
            add(dpad);
        }
    }


    // ----------------------------------------------------------
    /**
     * Remove an Actor from this view.
     * @param actor The Actor to remove.
     */
    public void remove(Actor actor)
    {
        assert world != null : "No world defined";
        world.remove(actor);
    }


    // ----------------------------------------------------------
    /**
     * Removes all actors from this view's world.
     */
    public void clear()
    {
        ShapeField shapes = getShapeField();
        synchronized (shapes)
        {
            for (Shape shape : shapes.toArray())
            {
                if (shape instanceof Actor)
                {
                    remove((Actor)shape);
                }
                else
                {
                    remove(shape);
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Set the {@link World} associated with this view.
     * @param world The World to associate with this view.
     */
    public void setWorld(World world)
    {
        assert this.world == null : "Replacing world is not yet implemented";
        this.world = world;
        world.setWorldView(this);
    }


    // ----------------------------------------------------------
    /**
     * Get the {@link World} associated with this view.
     * @return This view's World.
     */
    public World getWorld()
    {
        return world;
    }

    /**
     * Returns the front object that intersect with the given actor.
     *
     * @param actor An Actor in the world.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return front object that intersects the given object.
     */
    /* package */ <MyActor extends Actor> MyActor getIntersectingShape(
        Actor actor, Class<MyActor> cls)
    {
        return getShapes().withClass(cls).intersecting(actor.getBounds()).front();
    }

    /**
     * Returns all the objects that intersect with the given actor.
     *
     * @param actor An Actor in the world.
     * @param cls Class of objects to look for (null or Object.class will find
     *            all classes).
     * @param <MyActor> The type of object to look for, as specified
     *                  in the cls parameter.
     * @return A set of objects that intersect the given object.
     */
    /* package */ <MyActor extends Actor> Set<MyActor> getIntersectingShapes(
        Actor actor, Class<MyActor> cls)
    {
        return getShapes().withClass(cls).intersecting(actor.getBounds()).all();
    }

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
     * @return An object at the given location, or null if none found.
     */
    /* package */ <MyActor extends Actor> MyActor getShapeAt(float x, float y,
        Class<MyActor> cls)
    {
        return getShapes().withClass(cls).locatedAt(x, y).front();
    }

    /**
     * Return all the objects that are located at the specified cell. Objects found
     * can be restricted to a specific class (and its subclasses) by supplying
     * the 'cls' parameter.
     *
     * @param x   X-coordinate.
     * @param y   Y-coordinate.
     * @param cls Class of objects to look for (passing 'null' will find all
     *            objects).
     * @return All objects at the given location, or null if none found.
     */
    /* package */ <MyActor extends Actor> Set<MyActor> getShapesAt(float x, float y,
        Class<MyActor> cls)
    {
        return getShapes().withClass(cls).locatedAt(x, y).all();
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

    /**
     * Handles catching any motion events for the world view, just pushes
     * them onto the event list so they can later be dispatched.
     *
     * @param e motion event to catch
     * @return true if the event was successfully added, false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return motionBuffer.add(e) && actionBuffer.add(e.getAction());
    }

    /**
     * Handles catching any key events, just pushes it on the key list so they
     * can be dispatched later.
     *
     * @param e key event to catch
     * @return true if the event was successfully added, false otherwise
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e)
    {
        return (keyCode == KeyEvent.KEYCODE_MENU) ? super.onKeyUp(keyCode, e) :
            keyBuffer.add(e) && keyCodeBuffer.add(keyCode);
    }

    /**
     * Handles catching any key events, just pushes it on the key list so they
     * can be dispatched later.
     *
     * @param e key event to catch
     * @return true if the event was successfully added, false otherwise
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)
    {
        return (keyCode == KeyEvent.KEYCODE_MENU) ? super.onKeyUp(keyCode, e) :
            keyBuffer.add(e) && keyCodeBuffer.add(keyCode);
    }

    /**
     * Returns the current event buffer.
     *
     * @return event buffer
     */
    public LinkedList<MotionEvent> getMotionBuffer()
    {
        return motionBuffer;
    }

    /**
     * Returns the current buffer that stores the actions corresponding to the
     * motion events.
     *
     * @return event buffer
     */
    public LinkedList<Integer> getActionBuffer()
    {
        return actionBuffer;
    }

    /**
     * Returns the current key buffer.
     *
     * @return event buffer
     */
    public LinkedList<KeyEvent> getKeyBuffer()
    {
        return keyBuffer;
    }

    /**
     * Returns the current buffer that stores the key codes corresponding to the
     * key events.
     *
     * @return event buffer
     */
    public LinkedList<Integer> getKeyCodeBuffer()
    {
        return keyCodeBuffer;
    }

    /**
     * Clears the motion and key event buffers.
     */
    public void clearBuffers()
    {
        motionBuffer.clear();
        keyBuffer.clear();
        actionBuffer.clear();
        keyCodeBuffer.clear();
    }

    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected void drawContents(RectF repaintBounds)
    {
        Canvas canvas = getCanvas();
        // Set up the grid-based coordinate transformation
        Matrix xform = null;
        if (world != null)
        {
            xform = world.getGridTransform();
        }
        if (xform != null)
        {
            canvas.save();
            canvas.concat(xform);
        }

        // Now draw!
        if (world != null)
        {
            world.draw(canvas);
        }
        super.drawContents(repaintBounds);

        // Undo grid-based transform
        if (xform != null)
        {
            canvas.restore();
        }
    }
}
