package sofia.micro;

import java.util.Collection;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

//-------------------------------------------------------------------------
/**
 * Represents a view containing a {@link World} and its {@link Actor}
 * objects.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
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
     * Get all the actors on this view (i.e., in this view's world).
     * @return a collection of all the actors in this view's world.
     */
    public Collection<Actor> getActors()
    {
        assert world != null : "No world defined";
        return world.getActors();
    }


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
