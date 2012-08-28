package sofia.micro;

import sofia.graphics.Color;
import sofia.graphics.Image;
import sofia.micro.internal.ProgramThread;

//-------------------------------------------------------------------------
/**
 * Represents a World that is controlled by a program--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a program: either override the {@link #myProgram()}
 * method in a subclass (most common for beginner programmers), or create a
 * program as a separate object and pass it into {@link #setProgram(Program)}
 * (more advanced, and more useful if there may be multiple program that
 * might be associated with a given world).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ProgrammableWorld
    extends World
    implements Program
{
    //~ Fields ................................................................

    private ProgramThread programThread = null;
    private Program       futureProgram = null;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Construct a new world with a default size of 20 x 12.  This default
     * size is based on a 320x480 (Android's HVGA resolution, which is
     * a mid-level phone resolution) in landscape orientation, leaving some
     * room for a notification bar and other decorations.  This would
     * result in 24x24 pixel cells, or 16x16 cells on a 240x320 phone.
     * This world (and its actors) will be automatically scaled up (zoomed)
     * if the Android device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     */
    public ProgrammableWorld()
    {
        super();
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified. This world (and its actors) will be automatically
     * scaled up (zoomed) if the Android device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width  The width of the world (in cells).
     * @param height The height of the world (in cells).
     */
    public ProgrammableWorld(int width, int height)
    {
        super(width, height);
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.  This constructor also sets the effective cell
     * size of this world (for bitmaps). This world (and its actors) will
     * be automatically scaled up (zoomed) if the Android device resolution
     * permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     */
    public ProgrammableWorld(int width, int height, int scaledCellSize)
    {
        super(width, height, scaledCellSize);
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.  This constructor also sets the effective cell
     * size of this world (for bitmaps) and whether this world (and its
     * actors) should be automatically scaled up (zoomed) if the Android
     * device resolution permits it.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background for each cell.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     * @param scaleToFit     If true, cells will be scaled larger or smaller
     *                       so that the grid is as large as possible on
     *                       the physical device, with bitmaps
     *                       scaled proportionately.  If false, the grid
     *                       will be rendered so that each cell is exactly
     *                       scaledCellSize pixels in size, no more and no
     *                       less, even if this means some of the grid will
     *                       be clipped by the screen boundaries.
     */
    public ProgrammableWorld(
        int width, int height, int scaledCellSize, boolean scaleToFit)
    {
        super(width, height, scaledCellSize, scaleToFit);
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Construct a new world. The size of the world (in number of cells)
     * must be specified.
     *
     * <p>If an image based on the world's class name exists, it will be
     * used as the background image.</p>
     *
     * @param width          The width of the world (in cells).
     * @param height         The height of the world (in cells).
     * @param scaledCellSize For rendering bitmaps, treat each cell as if
     *                       it were a square of this many pixels on each side.
     * @param scaleToFit     If true, cells will be scaled larger or smaller
     *                       so that the grid is as large as possible on
     *                       the physical device, with bitmaps
     *                       scaled proportionately.  If false, the grid
     *                       will be rendered so that each cell is exactly
     *                       scaledCellSize pixels in size, no more and no
     *                       less, even if this means some of the grid will
     *                       be clipped by the screen boundaries.
     * @param backgroundIsForCell Indicates whether any existing background
     *                       image based on the world's class name should be
     *                       used as the background for each cell (if true),
     *                       or stretched to fit the entire world grid (if
     *                       false).
     */
    public ProgrammableWorld(
        int width,
        int height,
        int scaledCellSize,
        boolean scaleToFit,
        boolean backgroundIsForCell)
    {
        super(width, height, scaledCellSize, scaleToFit, backgroundIsForCell);
        setProgram(this);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Actor actor)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.add(actor);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Actor actor, int x, int y)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.add(actor, x, y);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    @Override
    public void remove(Actor actor)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.remove(actor);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * This implementation of the act method executes one action in
     * this world's program.  Normally, programmable worlds do not
     * directly call or override this method--if you want to provide act(),
     * more often than not you want to use {@link World} as your base
     * class.
     *
     * <p>If, on the other hand, you want to combine the features of
     * a programmable world with a custom act() method, you can do that by
     * inheriting from this class and overriding this method.  If you
     * override this method, be sure to call <code>super.act()</code> or
     * your world will no longer obey its assigned program.</p>
     */
    @Override
    public void act()
    {
        super.act();
        programStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridColor(Color gridColor)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setGridColor(gridColor);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackgroundColor(Color backgroundColor)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setBackgroundColor(backgroundColor);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellBackground(Image background)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setCellBackground(background);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorldBackground(Image background)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setWorldBackground(background);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Subclasses can override this method to provide the "program"
     * for the world to follow.  The default implementation does
     * nothing (i.e., no program by default).
     */
    public void myProgram()
    {
        // Intentionally empty
    }


    // ----------------------------------------------------------
    /**
     * Associate a program with this world by providing a {@link Program}
     * object.  Actions in the program will execute one move at a time as
     * act() is called.  A program value of null will remove any assigned
     * program for this world.
     *
     * @param program The program to activate.
     */
    public void setProgram(Program program)
    {
        if (getWorldView() == null)
        {
            futureProgram = program;
            return;
        }

        if (programThread != null)
        {
            // Stop the thread before resetting the reference
            programThread.endProgram();
            programThread = null;
        }

        if (program != null)
        {
            programThread = new ProgramThread(this, program);
            programThread.start();
        }
        // TODO: add some kind of "finishActing()" and "isFinished()"
        // pair of methods to allow actors to be "stopped" in general?
    }


    // ----------------------------------------------------------
    /**
     * Get the program associated with this world.
     * @return This world's program.
     */
    public Program getProgram()
    {
        return programThread == null
            ? null
            : programThread.getProgram();
    }


    // ----------------------------------------------------------
    /**
     * Stop any currently executing program associated with this world.
     */
    public void stopProgram()
    {
        setProgram(null);
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Triggers one action in this world's program.
     */
    protected void programStep()
    {
        if (programThread != null)
        {
            if (programThread.getState() == Thread.State.NEW)
            {
                programThread.start();
            }

            if (programThread.getState() == Thread.State.TERMINATED)
            {
                programThread = null;
            }
            else
            {
                programThread.resumeProgram();
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    /* package */ void setWorldView(WorldView view)
    {
        super.setWorldView(view);
        if (futureProgram != null)
        {
            setProgram(futureProgram);
            futureProgram = null;
        }
    }
}
