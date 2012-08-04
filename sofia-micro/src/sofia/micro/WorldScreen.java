package sofia.micro;

import sofia.app.ShapeScreen;
import sofia.graphics.ShapeView;

//-------------------------------------------------------------------------
/**
 * Represents a screen containing a single {@link WorldView}, to be used
 * as the parent for "microworld" applications.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public class WorldScreen
    extends ShapeScreen
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WorldScreen.
     */
    public WorldScreen()
    {
        super();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the {@link WorldView} that holds all of the shapes on this screen.
     * @return This screen's WorldView
     */
    public WorldView getWorldView()
    {
        return (WorldView)getShapeView();
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link World} for this screen.
     * @return This screen's World
     */
    public World getWorld()
    {
        return getWorldView().getWorld();
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to this screen.
     * @param actor The Actor to add.
     */
    public void add(Actor actor)
    {
        getWorldView().add(actor);
    }


    // ----------------------------------------------------------
    /**
     * Remove an Actor from this screen.
     * @param actor The Actor to remove.
     */
    public void remove(Actor actor)
    {
        getWorldView().remove(actor);
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected ShapeView createShapeView(ShapeScreen parent)
    {
        return new WorldView(parent);
    }
}
