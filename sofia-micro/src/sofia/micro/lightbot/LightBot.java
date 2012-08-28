package sofia.micro.lightbot;

import android.graphics.Point;
import sofia.graphics.Image;
import sofia.micro.ProgrammableActor;
import sofia.micro.World;
import sofia.micro.internal.ProgramThread;

//-------------------------------------------------------------------------
/**
 * Represents a Light-Bot.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class LightBot
    extends ProgrammableActor
{
    //~ Fields ................................................................

    private static enum CompassDirection {
        NORTH
        {
            public Point offset() { return new Point(0, -1); }
        },
        EAST
        {
            public Point offset() { return new Point(1, 0); }
        },
        SOUTH
        {
            public Point offset() { return new Point(0, 1); }
        },
        WEST
        {
            public Point offset() { return new Point(-1, 0); }
        };
        public abstract Point offset();
    };
    private CompassDirection direction = CompassDirection.EAST;

    private final Image imgLeft;
    private final Image imgRight;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Light-Bot at the origin (0, 0), facing east.
     */
    public LightBot()
    {
        this(0, 0);
    }


    // ----------------------------------------------------------
    /**
     * Create a new Light-Bot at the specified location, facing east.
     * @param x  The x-coordinate of the Light-Bot's location.
     * @param y  The y-coordinate of the Light-Bot's location.
     */
    public LightBot(int x, int y)
    {
        super();
        setGridLocation(x, y);
        this.direction = CompassDirection.EAST;
        imgRight = new Image("lightbot/android.png");
        imgLeft = new Image("lightbot/android_left.png");
        setImage(imgRight);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Move forward one space.
     */
    public void move()
    {
        try
        {
            ProgramThread.beginAtomicAction();

            Point offset = direction.offset();
            if (isInsideGrid(offset))
            {
                int myHeight = getHeightHere();
                int destHeight = getHeightAtOffset(offset);
                if (myHeight == destHeight)
                {
                    setGridLocation(
                        getGridX() + offset.x, getGridY() + offset.y);
                }
            }
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Turn the Light-Bot to its right.
     */
    public void turnRight()
    {
        switch (this.direction)
        {
            case NORTH:
                this.direction = CompassDirection.EAST;
                break;
            case EAST:
                this.direction = CompassDirection.SOUTH;
                break;
            case SOUTH:
                this.direction = CompassDirection.WEST;
                break;
            case WEST:
                this.direction = CompassDirection.NORTH;
                break;
        }

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
                setRotation(-90);
                break;

            case SOUTH:
                setRotation(90);
                break;
        }
    }


    // ----------------------------------------------------------
    /**
     * Turn the Light-Bot to its left.
     */
    public void turnLeft()
    {
        switch (this.direction)
        {
            case NORTH:
                this.direction = CompassDirection.WEST;
                break;
            case EAST:
                this.direction = CompassDirection.NORTH;
                break;
            case SOUTH:
                this.direction = CompassDirection.EAST;
                break;
            case WEST:
                this.direction = CompassDirection.SOUTH;
                break;
        }

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
                setRotation(90);
                break;

            case SOUTH:
                setRotation(-90);
                break;
        }
    }


    // ----------------------------------------------------------
    /**
     * Move forward one space while jumping up one block, or jumping
     * down one or more blocks.
     */
    public void jump()
    {
        try
        {
            ProgramThread.beginAtomicAction();

            Point offset = direction.offset();
            if (isInsideGrid(offset))
            {
                int myHeight = getHeightHere();
                int destHeight = getHeightAtOffset(offset);
                if (destHeight == myHeight + 1
                    || destHeight < myHeight)
                {
                    setGridLocation(
                        getGridX() + offset.x, getGridY() + offset.y);
                }
            }
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Turn on the blue tile the robot where the robot is standing.
     * This operation does nothing if the robot is not standing on a
     * blue tile.
     */
    public void turnLightOn()
    {
        LightableTile tile = getOneObjectAtOffset(0, 0, LightableTile.class);
        if (tile != null)
        {
            tile.turnLightOn();
        }
    }


    // ----------------------------------------------------------
    /**
     * Invoke method "f1" on this Light-Bot.  The default definition of
     * f1 does nothing.  Subclasses can override it to provide behavior.
     */
    public void f1()
    {
        // Nothing
    }


    // ----------------------------------------------------------
    /**
     * Invoke method "f2" on this Light-Bot.  The default definition of
     * f2 does nothing.  Subclasses can override it to provide behavior.
     */
    public void f2()
    {
        // Nothing
    }


    // ----------------------------------------------------------
    /**
     * Determine if the cell immediately in front of the robot is at the
     * same height as the robot--that is, can the robot move forward without
     * jumping.  Note that this method will always return false if the
     * robot is facing off the edge of the level.
     * @return True if the cell immediately in front of this robot is at
     * the same height as the cell where the robot is standing.
     */
    public boolean isGroundLevel()
    {
        Point offset = direction.offset();
        if (isInsideGrid(offset))
        {
            return getHeightHere() == getHeightAtOffset(offset);
        }
        else
        {
            return false;
        }
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    /**
     * Get the height of the stack of blocks the robot is currently
     * standing on (how high is the current grid cell?).
     * @return The number of blocks where the robot is standing.
     */
    public int getHeightHere()
    {
        return getHeightOfAdjacentCell(0, 0);
    }


    // ----------------------------------------------------------
    /**
     * Get the height of a stack of blocks next to the robot.
     * This operation only works for immediate neighbors (at most one
     * cell away in either the x or y direction).  The adjacent cell to
     * examine is specified by giving its x- and y-offsets relative to
     * the robot's current position.
     *
     * @param dx The x offset of the neighboring cell to examine (-1, 0, or 1).
     * @param dy The y offset of the neighboring cell to examine (-1, 0, or 1).
     * @return The number of blocks in the indicated adjacent cell.
     * @throws IllegalArgumentException if either dx or dy has a magnitude
     * greater than 1.
     */
    public int getHeightOfAdjacentCell(int dx, int dy)
    {
        if (Math.abs(dx) > 1)
        {
            throw new IllegalArgumentException("The specified dx value of "
                + dx + " is too far from the robot's current position.  Only "
                + "values of -1, 0, or 1 are allowed.");
        }
        if (Math.abs(dy) > 1)
        {
            throw new IllegalArgumentException("The specified dy value of "
                + dy + " is too far from the robot's current position.  Only "
                + "values of -1, 0, or 1 are allowed.");
        }
        return getHeightAtOffset(new Point(dx, dy));
    }


    // ----------------------------------------------------------
    private int getHeightAtOffset(Point offset)
    {
        if (isInsideGrid(offset))
        {
            return getObjectsAtOffset(offset.x, offset.y, Block.class).size();
        }
        else
        {
            return 0;
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
}
