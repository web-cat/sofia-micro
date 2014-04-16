package sofia.micro;

import android.content.pm.ActivityInfo;
import android.view.GestureDetector;
import java.util.List;
import java.util.ArrayList;
import android.view.KeyEvent;
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

    // We use 2 sets of 2 buffers, motion buffers store the touch
    // events along with the corresponding action, the key buffer and key code
    // store the key events and the corresponding key code. 2 sets of these buffers
    // are used so that while one buffer is being processed during the act() of the
    // world, the other buffer can store events that are fired
    private boolean onFirstBuffer = true;

    private ArrayList<MotionEventWrapper> motionBuffer1 = new ArrayList<MotionEventWrapper>();
    private ArrayList<MotionEventWrapper> motionBuffer2 = new ArrayList<MotionEventWrapper>();
    
    private ArrayList<KeyEventWrapper> keyBuffer2 = new ArrayList<KeyEventWrapper>();
    private ArrayList<KeyEventWrapper> keyBuffer1 = new ArrayList<KeyEventWrapper>();

    private GestureDetector gestureDetector;

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
        gestureDetector = new GestureDetector(context, new GestureListener());
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
        gestureDetector = new GestureDetector(context, new GestureListener());
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
        gestureDetector = new GestureDetector(context, new GestureListener());
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
        if (world != null && !world.isRunning())
        {
            return super.onTouchEvent(e);
        }

        // Adjusts the x/y coordinate to be within the World.
        float cellX = e.getX();
        float cellY = e.getY();
        if (world.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            cellX -= (getWidth() - (world.getWidth() * world.getCellSize())) / 2;
        }
        else if (world.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            cellY -= (getHeight() - (world.getHeight() * world.getCellSize())) / 2;
        }
        cellX /= world.getCellSize();
        cellY /= world.getCellSize();
        e.setLocation(cellX, cellY);

        gestureDetector.onTouchEvent(e);
        MotionEventWrapper wrapper = new MotionEventWrapper(e, e.getAction(), cellX, cellY);
        return (onFirstBuffer) ? motionBuffer1.add(wrapper) : motionBuffer2.add(wrapper);
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
        if (keyCode == KeyEvent.KEYCODE_MENU || (world != null && !world.isRunning()))
        {
            return super.onKeyUp(keyCode, e);
        }

        KeyEventWrapper wrapper = new KeyEventWrapper(e, keyCode);
        return (onFirstBuffer) ? keyBuffer1.add(wrapper) : keyBuffer2.add(wrapper);
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
        if (keyCode == KeyEvent.KEYCODE_MENU || (world != null && !world.isRunning()))
        {
            return super.onKeyDown(keyCode, e);
        }

        KeyEventWrapper wrapper = new KeyEventWrapper(e, keyCode);
        return (onFirstBuffer) ? keyBuffer1.add(wrapper) : keyBuffer2.add(wrapper);
    }

    /**
     * Returns the current event buffer.
     *
     * @return event buffer
     */
    protected List<MotionEventWrapper> getMotionBuffer()
    {
        return (onFirstBuffer) ? motionBuffer1 : motionBuffer2;
    }


    /**
     * Returns the current key buffer.
     *
     * @return event buffer
     */
    protected List<KeyEventWrapper> getKeyBuffer()
    {
        return (onFirstBuffer) ? keyBuffer1 : keyBuffer2;
    }

    /**
     * Clears the motion and key event buffers.
     */
    protected void clearBuffers()
    {
        if (!onFirstBuffer)
        {
            motionBuffer1.clear();
            keyBuffer1.clear();
        }
        else
        {
            motionBuffer2.clear();
            keyBuffer2.clear();
        }
    }

    /**
     * Swaps the buffers currently being used. This should be called once the
     * buffers currently being used have been retrieved using the getters.
     */
    protected void swapBuffers()
    {
        onFirstBuffer = !onFirstBuffer;
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

    /**
     *  Wrapper class for motion events along with their corresponding action
     *
     *  @author Brian Bowden
     *  @version March 5, 2014
     */
    protected class MotionEventWrapper
    {
        /** Motion event */
        public MotionEvent motionEvent;

        /** Corresponding action */
        public int action;

        /** Modified x coordinate for the corresponding grid cell */
        public float x;

        /** Modified x coordinate for the corresponding grid cell */
        public float y;

        /**
         * Comprehensive constructor.
         *
         * @param motionEvent motion event to store
         * @param action corresponding action
         * @param x x coordinate for the cell
         * @param y y coordinate for the cell
         */
        public MotionEventWrapper(MotionEvent motionEvent, int action, float x, float y)
        {
            this.motionEvent = motionEvent;
            this.action = action;
            this.x = (float) Math.floor(x);
            this.y = (float) Math.floor(y);
        }
    }

    /**
     *  Wrapper class for motion events along with their corresponding action
     *
     *  @author Brian Bowden
     *  @version March 5, 2014
     */
    protected class KeyEventWrapper
    {
        /** Key event */
        public KeyEvent keyEvent;

        /** Corresponding key code */
        public int keyCode;

        /**
         * Comprehensive constructor.
         *
         * @param keyEvent key event to store
         * @param keyCode corresponding key code
         */
        public KeyEventWrapper(KeyEvent keyEvent, int keyCode)
        {
            this.keyEvent = keyEvent;
            this.keyCode = keyCode;
        }
    }

    /**
     *  Inner class for the gesture detector, adds double taps to the buffer.
     *
     *  @author Brian Bowden
     *  @version March 5, 2014
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * Listener for double tap events.
         *
         * @param e motion event
         */
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            if (!world.isRunning())
            {
                return false;
            }

            // currently using -1 as the 'action' for double-tap, will probably
            // want to change later
            MotionEventWrapper wrapper = new MotionEventWrapper(
                e, e.getAction(), e.getX(), e.getY());
            return (onFirstBuffer) ? motionBuffer1.add(wrapper) : motionBuffer2.add(wrapper);
        }
    }
}
