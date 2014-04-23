package sofia.micro.jeroo;

import sofia.micro.Actor;
import java.util.Set;
import android.graphics.Point;
import sofia.graphics.Image;
import sofia.micro.ProgrammableActor;
import sofia.micro.World;
import sofia.micro.internal.ProgramThread;

//-------------------------------------------------------------------------
/**
 * Represents a Jeroo on Santong Island.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class Jeroo
    extends ProgrammableActor
{
    //~ Fields ................................................................

    private int flowers = 0;
    private CompassDirection direction = EAST;

    private final Image imgLeft;
    private final Image imgRight;

    // While static imports would be better, here we do this for simpler
    // beginner programs
    /** A constant that means facing east, or to the right. */
    public static final CompassDirection EAST  = CompassDirection.EAST;
    /** A constant that means facing south, or down. */
    public static final CompassDirection SOUTH = CompassDirection.SOUTH;
    /** A constant that means facing west, or to the left. */
    public static final CompassDirection WEST  = CompassDirection.WEST;
    /** A constant that means facing north, or up. */
    public static final CompassDirection NORTH = CompassDirection.NORTH;

    /** A constant that represents the direction to the left of a Jeroo's
     * current location or direction.
     */
    public static final RelativeDirection LEFT = RelativeDirection.LEFT;

    /** A constant that represents the direction to the right of a Jeroo's
     * current location or direction.
     */
    public static final RelativeDirection RIGHT = RelativeDirection.RIGHT;

    /** A constant that represents the direction straight in front of the
     * Jeroo, in the direction it is currently facing.
     */
    public static final RelativeDirection AHEAD = RelativeDirection.AHEAD;

    /** A constant that represents the location where the Jeroo is currently
     * standing.
     */
    public static final RelativeDirection HERE = RelativeDirection.HERE;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Jeroo at the origin (0, 0), facing east, with no flowers.
     */
    public Jeroo()
    {
        this(0, 0);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Jeroo at the origin (0, 0), facing east.
     * @param flowers The number of flowers the Jeroo is holding.
     */
    public Jeroo(int flowers)
    {
        this(0, 0, flowers);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Jeroo, facing east, with no flowers.
     * @param x  The x-coordinate of the Jeroo's location.
     * @param y  The y-coordinate of the Jeroo's location.
     */
    public Jeroo(int x, int y)
    {
        this(x, y, 0);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Jeroo with no flowers.
     * @param x         The x-coordinate of the Jeroo's location.
     * @param y         The y-coordinate of the Jeroo's location.
     * @param direction The direction the Jeroo is facing.
     */
    public Jeroo(int x, int y, CompassDirection direction)
    {
        this(x, y, direction, 0);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Jeroo facing east.
     * @param x         The x-coordinate of the Jeroo's location.
     * @param y         The y-coordinate of the Jeroo's location.
     * @param flowers   The number of flowers the Jeroo is holding.
     */
    public Jeroo(int x, int y, int flowers)
    {
        this(x, y, EAST, flowers);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Jeroo.
     * @param x         The x-coordinate of the Jeroo's location.
     * @param y         The y-coordinate of the Jeroo's location.
     * @param direction The direction the Jeroo is facing.
     * @param flowers   The number of flowers the Jeroo is holding.
     */
    public Jeroo(int x, int y, CompassDirection direction, int flowers)
    {
        super();
        setGridLocation(x, y);
        this.direction = direction;
        this.flowers = flowers;
        imgRight = new Image("jeroo");
        imgLeft = new Image("jeroo_left");
        if (direction == WEST)
        {
            setImage(imgLeft);
        }
        else
        {
            setImage(imgRight);
        }
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Move forward one space. It is an error if the hopping Jeroo lands in
     * the water, lands on another Jeroo, or hops onto a net.  A Jeroo can
     * hop onto a flower.
     */
    public void hop()
    {
        try
        {
            ProgramThread.beginAtomicAction();

            Point offset = direction.offset();
            if (isInsideGrid(offset))
            {
                setGridLocation(getGridX() + offset.x, getGridY() + offset.y);
                Net net = getOneObjectAtOffset(0, 0, Net.class);
                if (net != null)
                {
                    incapacitate("is now trapped in a net.");
                }
                else
                {
                    Water water = getOneObjectAtOffset(0, 0, Water.class);
                    if (water != null)
                    {
                        incapacitate("is now stuck in the water.");
                    }
                    else
                    {
                        Set<Jeroo> others =
                            getObjectsAtOffset(0, 0, Jeroo.class);
                        if (others.size() > 1)
                        {
                            for (Jeroo jeroo : others)
                            {
                                jeroo.incapacitate("bumped into "
                                    + others.size() + " other Jeroo"
                                    + (others.size() == 1 ? "" : "s") + ".");
                            }
                        }
                    }
                }
            }
            else
            {
                incapacitate("attempted to move out of bounds and failed.");
            }
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Hop <i>number</i> times in a row, where <i>number</i> is a positive
     * integer.
     * @param number The number of spaces to move (greater than zero).
     */
    public void hop(int number)
    {
        for (int i = 0; i < number; i++)
        {
            hop();
        }
    }


    // ----------------------------------------------------------
    /**
     * Pick a flower from the current location.  Nothing happens if there is
     * no flower at the current location.
     */
    public void pick()
    {
        Flower flower = getOneObjectAtOffset(0, 0, Flower.class);
        if (flower != null)
        {
            flowers++;
            flower.remove();
        }
    }


    // ----------------------------------------------------------
    /**
     * Plant a flower at the current location. Nothing happens if the Jeroo
     * does not have a flower to plant.
     */
    public void plant()
    {
        if (flowers > 0)
        {
            flowers--;
            getWorld().add(new Flower(getGridX(), getGridY()));
        }
    }


    // ----------------------------------------------------------
    /**
     * Toss a flower one space ahead.  The tossed flower is lost forever.
     * If the flower lands on a net, the net is disabled.
     */
    public void toss()
    {
        if (flowers > 0)
        {
            flowers--;
            Point offset = direction.offset();
            if (isInsideGrid(offset))
            {
                Net net = getOneObjectAtOffset(offset.x, offset.y, Net.class);
                if (net != null)
                {
                    net.remove();
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Give a flower to a Jeroo in a neighboring cell in the indicated
     * direction. Nothing happens if the giving Jeroo has no flowers or if
     * there is no Jeroo in the indicated direction.
     * ({@code give(HERE);} is meaningless.)
     * @param direction The direction to give (LEFT, RIGHT, or AHEAD).
     */
    public void give(RelativeDirection direction)
    {
        if (flowers > 0)
        {
            Point offset = this.direction.turn(direction).offset();
            if (isInsideGrid(offset))
            {
                Jeroo buddy =
                    getOneObjectAtOffset(offset.x, offset.y, Jeroo.class);
                if (buddy != null)
                {
                    flowers--;
                    buddy.flowers++;
                }
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Turn in the indicated direction, but stay in the same location.
     * ({@code turn(AHEAD);} and {@code turn(HERE);} are meaningless.)
     * @param direction The direction to turn (LEFT or RIGHT).
     */
    public void turn(RelativeDirection direction)
    {
        if (direction == AHEAD || direction == HERE)
        {
            return;
        }

        this.direction = this.direction.turn(direction);
        switch (this.direction)
        {
            case WEST:
                setImage(imgLeft);
                setRotation(0);
                break;

            case EAST:
                setImage(imgRight);
                setRotation(0);
                break;

            case NORTH:
                setRotation(direction == LEFT ? 90 : -90);
                break;

            case SOUTH:
                setRotation(direction == LEFT ? -90 : 90);
                break;
        }
    }


    // ----------------------------------------------------------
    /**
     * Does this Jeroo have any flowers in its pouch?
     * @return True if this Jeroo has at least one flower.
     */
    public boolean hasFlower()
    {
        return flowers > 0;
    }


    // ----------------------------------------------------------
    /**
     * Is the Jeroo facing the indicated direction?
     * @param direction The direction to check (NORTH, SOUTH, EAST, or WEST).
     * @return True if this Jeroo is facing the specified direction.
     */
    public boolean isFacing(CompassDirection direction)
    {
        return this.direction == direction;
    }


    // ----------------------------------------------------------
    /**
     * Is there a flower in the indicated direction?
     * @param direction The direction to check.
     * @return True if there is a flower in the specified direction.
     */
    public boolean seesFlower(RelativeDirection direction)
    {
        return seesObject(direction, Flower.class);
    }


    // ----------------------------------------------------------
    /**
     * Is there a Jeroo in the indicated direction?
     * @param direction The direction to check.
     * @return True if there is a flower in the specified direction.
     */
    public boolean seesJeroo(RelativeDirection direction)
    {
        return seesObject(direction, Jeroo.class);
    }


    // ----------------------------------------------------------
    /**
     * Is there a net in the indicated direction?
     * @param direction The direction to check.
     * @return True if there is a net in the specified direction.
     */
    public boolean seesNet(RelativeDirection direction)
    {
        return seesObject(direction, Net.class);
    }


    // ----------------------------------------------------------
    /**
     * Is there water in the indicated direction?
     * @param direction The direction to check.
     * @return True if there is water in the specified direction.
     */
    public boolean seesWater(RelativeDirection direction)
    {
        return seesObject(direction, Water.class);
    }


    // ----------------------------------------------------------
    /**
     * Are there no obstacles (no net, no flower, no Jeroo, and no water) in
     * the indicated direction?
     * @param direction The direction to check.
     * @return True if there are no obstacles in the specified direction.
     */
    public boolean seesClear(RelativeDirection direction)
    {
        return seesObject(direction, null);
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Called under various error conditions to indicate that this
     * Jeroo is out of commission and cannot carry out any more actions.
     * @param message A log message to print about the event.
     */
    protected void incapacitate(String message)
    {
        if (getProgram() != null)
        {
            log(message);
            setImage(imgRight);
            setRotation(180);
            stopProgram();
        }
    }


    // ----------------------------------------------------------
    private boolean isInsideGrid(Point offset)
    {
        World world = getWorld();
        int x = getGridX() + offset.x;
        int y = getGridY() + offset.y;
        return x >= 0 && x < world.getWidth()
            && y >= 0 && y < world.getHeight();
    }


    // ----------------------------------------------------------
    private void log(String message)
    {
        System.out.println(this + " " + message);
    }

    /**
     * Returns true if the given cls is at the direction.
     * @param direction The direction to check.
     * @param cls class of object to check for
     * @return true if there are no obstacles in the specified direction.
     */
    private boolean seesObject(RelativeDirection direction, Class<? extends Actor> cls)
    {
        Point offset = new Point(0, 0);
        if (direction != RelativeDirection.HERE)
        {
            offset = this.direction.turn(direction).offset();
        }
        return isInsideGrid(offset)
            && getOneObjectAtOffset(offset.x, offset.y, cls) != null;
    }
}
