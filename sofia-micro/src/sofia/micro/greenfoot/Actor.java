package sofia.micro.greenfoot;

import java.util.Set;
import sofia.graphics.Image;
import sofia.graphics.ImageShape;
import sofia.graphics.Shape;

//-------------------------------------------------------------------------
/**
 * A small "adaptor" class that provides Greenfoot-style methods
 * for some Actor features.  It is intended to provide for source-level
 * compatibility of some Greenfoot examples.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Actor
    extends sofia.micro.Actor
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Actor.  By default, this actor's image will be scaled
     * to the size of a single grid cell, preserving aspect ratio.
     */
    public Actor()
    {
        super(false);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Actor.  By default, this actor's image will be scaled
     * to the size of a single grid cell, preserving aspect ratio.
     * @param nickName The nickname for this actor.
     */
    public Actor(String nickName)
    {
        super(nickName, false);
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
        super(scaleToCell);
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
        super(nickName, scaleToCell);
    }


    //~ Public Methods (Greenfoot style) ......................................

    // ----------------------------------------------------------
    @Override
    public World getWorld()
    {
        if (super.getWorld() instanceof World)
        {
            return (World)super.getWorld();
        }
        else
        {
            throw new IllegalStateException(
                "getWorld() in sofia.micro.greenfoot.Actor assumes that the "
                + "actor is in a sofia.micro.greenfoot.World, but this "
                + "actor is in " + super.getWorld() + ", which is a "
                + "sofia.micro.World instead.");
        }
    }


    // ----------------------------------------------------------
    /**
     * Assign a new location for this actor. This moves the actor to the
     * specified location. The location is specified as the coordinates of a
     * cell in the world.
     *
     * @param x Location index on the x-axis.
     * @param y Location index on the y-axis.
     *
     * @see #move(int)
     */
    public void setLocation(int x, int y)
    {
        setGridLocation(x, y);
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
    protected <MyActor extends Actor> Set<MyActor> getNeighbours(
        float distance, boolean diagonal, Class<MyActor> cls)
    {
        return super.getNeighbors(distance, diagonal, cls);
    }


    // ----------------------------------------------------------
    /**
     * Returns the image used to represent this actor, as a
     * {@link GreenfootImage} object. This image can be modified to
     * change the actor's appearance.
     *
     * @return The object's image.
     */
    public GreenfootImage getGreenfootImage()
    {
        Image image = getImage();
        if (image == null)
        {
            throw new IllegalStateException("getGreenfootImage() called on "
                + this + ", when this actor's image is not a bitmap.  It is: "
                + getShape());
        }
        else if (image instanceof GreenfootImage)
        {
            return (GreenfootImage)image;
        }
        else
        {
            return new GreenfootImage(image);
        }
    }
}
