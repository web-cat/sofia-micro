package sofia.micro.greenfoot;

//-------------------------------------------------------------------------
/**
 * A small "adaptor" class that provides Greenfoot-style static methods
 * for some Sofia features.  It is intended to provide for source-level
 * compatibility of some Greenfoot examples.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class Greenfoot
{
    //~ Fields ................................................................

    private static sofia.micro.World world;

    /** Error message to display when trying to use methods that requires
     * the actor be in a world.
     */
    private static final String NO_WORLD = "Not world to control. "
        + "An attempt was made to use Greenfoot methods before a world was "
        + "created and attached to a view.";


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * This is a static utility class that should never be instantiated.
     */
    private Greenfoot()
    {
        // Intentionally blank
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Set the speed of the execution.
     *
     * @param speed  The new speed. the value must be in the range (1..100)
     */
    public static void setSpeed(int speed)
    {
        failIfNoWorld();
        world.setSpeed(speed);
    }


    // ----------------------------------------------------------
    /**
     * Pause the execution.
     */
    public static void stop()
    {
        failIfNoWorld();
        world.stop();
    }


    // ----------------------------------------------------------
    /**
     * Run (or resume) the execution.
     */
    public static void start()
    {
        failIfNoWorld();
        world.start();
    }


    // ----------------------------------------------------------
    /**
     * Return a random number between 0 (inclusive) and limit (exclusive).
     * @param limit The upper limit of the generated number--the generated
     *              number will be strictly less than this limit.
     * @return A random number in the specified range.
     */
    public static int getRandomNumber(int limit)
    {
        return sofia.util.Random.generator().nextInt(limit);
    }


    // ----------------------------------------------------------
    /**
     * Throws an exception if there is no world set.
     *
     * @throws IllegalStateException If no world is set.
     */
    private static void failIfNoWorld()
    {
        if (world == null)
        {
            throw new IllegalStateException(NO_WORLD);
        }
    }


    //~ Internal Methods ......................................................

    // ----------------------------------------------------------
    /**
     * Sets the World to run to the one given.
     * This World will now be the main World that Greenfoot runs with on the
     * next act.
     *
     * @param world The World to switch running to, cannot be null.
     */
    /* package */ static void setWorld(World world)
    {
        if ( world == null )
        {
            throw new NullPointerException("The given world cannot be null.");
        }

        Greenfoot.world = world;
    }

}
