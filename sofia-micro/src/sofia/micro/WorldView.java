package sofia.micro;

import sofia.graphics.Shape;
import sofia.graphics.ShapeSet;
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
        assert world != null : "No world defined";
        world.addObject(actor, x, y);
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
     * Remove an Actor from this view.
     *
     * <p>This method is identical to {@code remove()}, but is provided
     * for Greenfoot compatibility.</p>
     *
     * @param actor The Actor to remove.
     */
    public void removeObject(Actor actor)
    {
        assert world != null : "No world defined";
        world.removeObject(actor);
    }


    // ----------------------------------------------------------
    /**
     * Removes all actors from this view's world.
     */
    public void clear()
    {
        ShapeSet shapes = (ShapeSet)getShapes();
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


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected void drawContents(Canvas canvas)
    {
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
        super.drawContents(canvas);

        // Undo grid-based transform
        if (xform != null)
        {
            canvas.restore();
        }
    }
}
