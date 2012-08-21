package sofia.micro.jeroo;

import android.graphics.Point;

//-------------------------------------------------------------------------
/**
 * Represents the four cardinal directions of the compass.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public enum CompassDirection
{
    // ----------------------------------------------------------
    /**
     * Facing right.
     */
    EAST
    {
        // ----------------------------------------------------------
        public CompassDirection turn(RelativeDirection where)
        {
            switch (where)
            {
                case LEFT:
                    return NORTH;

                case RIGHT:
                    return SOUTH;

                default:
                    return this;
            }
        }


        // ----------------------------------------------------------
        public Point offset()
        {
            return new Point(1, 0);
        }
    },


    // ----------------------------------------------------------
    /**
     * Facing down.
     */
    SOUTH
    {
        // ----------------------------------------------------------
        public CompassDirection turn(RelativeDirection where)
        {
            switch (where)
            {
                case LEFT:
                    return EAST;

                case RIGHT:
                    return WEST;

                default:
                    return this;
            }
        }


        // ----------------------------------------------------------
        public Point offset()
        {
            return new Point(0, 1);
        }
    },


    // ----------------------------------------------------------
    /**
     * Facing left.
     */
    WEST
    {
        // ----------------------------------------------------------
        public CompassDirection turn(RelativeDirection where)
        {
            switch (where)
            {
                case LEFT:
                    return SOUTH;

                case RIGHT:
                    return NORTH;

                default:
                    return this;
            }
        }


        // ----------------------------------------------------------
        public Point offset()
        {
            return new Point(-1, 0);
        }
    },


    // ----------------------------------------------------------
    /**
     * Facing up.
     */
    NORTH
    {
        // ----------------------------------------------------------
        public CompassDirection turn(RelativeDirection where)
        {
            switch (where)
            {
                case LEFT:
                    return WEST;

                case RIGHT:
                    return EAST;

                default:
                    return this;
            }
        }


        // ----------------------------------------------------------
        public Point offset()
        {
            return new Point(0, -1);
        }
    };


    // ----------------------------------------------------------
    /**
     * Get the new compass direction by turning a relative amount from
     * the current compass direction.  If the specified direction is
     * either AHEAD or HERE, this method returns the current direction
     * unchanged.
     * @param where The direction to turn (LEFT or RIGHT).
     * @return The compass direction that is either LEFT or RIGHT of this
     *         compass direction, depending on {@code where} you are turning.
     */
    public abstract CompassDirection turn(RelativeDirection where);


    // ----------------------------------------------------------
    /**
     * Get the offset (in terms of a change in x-coordinate and y-coordinate)
     * of the cell one cell away in this direction.  The x and y coordinates
     * are returned as a {@link Point}, where each coordinate is either 1, 0,
     * or -1.
     * @return The offset needed to move one cell in this direction from an
     *         existing location.
     */
    public abstract Point offset();
}
