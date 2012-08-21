package sofia.micro;

import sofia.graphics.Color;
import sofia.graphics.Image;
import sofia.micro.internal.ScriptThread;

//-------------------------------------------------------------------------
/**
 * Represents a World that is controlled by a script--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a script: either override the {@link #script()}
 * method in a subclass (most common for beginner programmers), or create a
 * script as a separate object and pass it into {@link #setScript(Script)}
 * (more advanced, and more useful if there may be multiple scripts that
 * might be associated with a given world).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ScriptableWorld
    extends World
    implements Script
{
    //~ Fields ................................................................

    private ScriptThread scriptThread = null;
    private Script       futureScript = null;


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
    public ScriptableWorld()
    {
        super();
        setScript(this);
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
    public ScriptableWorld(int width, int height)
    {
        super(width, height);
        setScript(this);
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
    public ScriptableWorld(int width, int height, int scaledCellSize)
    {
        super(width, height, scaledCellSize);
        setScript(this);
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
    public ScriptableWorld(
        int width, int height, int scaledCellSize, boolean scaleToFit)
    {
        super(width, height, scaledCellSize, scaleToFit);
        setScript(this);
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
    public ScriptableWorld(
        int width,
        int height,
        int scaledCellSize,
        boolean scaleToFit,
        boolean backgroundIsForCell)
    {
        super(width, height, scaledCellSize, scaleToFit, backgroundIsForCell);
        setScript(this);
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
            ScriptThread.beginAtomicAction();
            super.add(actor);
        }
        finally
        {
            ScriptThread.endAtomicAction();
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
            ScriptThread.beginAtomicAction();
            super.add(actor, x, y);
        }
        finally
        {
            ScriptThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    @Override
    public void remove(Actor actor)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.remove(actor);
        }
        finally
        {
            ScriptThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * This implementation of the act method executes one action in
     * this world's script.  Normally, scriptable worlds do not
     * directly call or override this method--if you want to provide act(),
     * more often than not you want to use {@link World} as your base
     * class.
     *
     * <p>If, on the other hand, you want to combine the features of
     * a scriptable world with a custom act() method, you can do that by
     * inheriting from this class and overriding this method.  If you
     * override this method, be sure to call <code>super.act()</code> or
     * your world will no longer obey its assigned script.</p>
     */
    @Override
    public void act()
    {
        super.act();
        scriptStep();
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
            ScriptThread.beginAtomicAction();
            super.setGridColor(gridColor);
        }
        finally
        {
            ScriptThread.endAtomicAction();
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
            ScriptThread.beginAtomicAction();
            super.setBackgroundColor(backgroundColor);
        }
        finally
        {
            ScriptThread.endAtomicAction();
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
            ScriptThread.beginAtomicAction();
            super.setCellBackground(background);
        }
        finally
        {
            ScriptThread.endAtomicAction();
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
            ScriptThread.beginAtomicAction();
            super.setWorldBackground(background);
        }
        finally
        {
            ScriptThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Subclasses can override this method to provide the "script"
     * for the actor to follow.  The default implementation does
     * nothing (i.e., no script by default).
     */
    public void script()
    {
        // Intentionally empty
    }


    // ----------------------------------------------------------
    /**
     * Associate a script with this world by providing a {@link Script}
     * object.  Actions in the script will execute one move at a time as
     * act() is called.  A script value of null will remove any assigned
     * script for this world.
     *
     * @param script The script to activate.
     */
    public void setScript(Script script)
    {
        if (getWorldView() == null)
        {
            futureScript = script;
            return;
        }

        if (scriptThread != null)
        {
            // Stop the thread before resetting the reference
            scriptThread.endScript();
            scriptThread = null;
        }

        if (script != null)
        {
            scriptThread = new ScriptThread(this, script);
            scriptThread.start();
        }
        // TODO: add some kind of "finishActing()" and "isFinished()"
        // pair of methods to allow actors to be "stopped" in general?
    }


    // ----------------------------------------------------------
    /**
     * Get the script associated with this actor.
     * @return This actor's script.
     */
    public Script getScript()
    {
        return scriptThread == null
            ? null
            : scriptThread.getScript();
    }


    // ----------------------------------------------------------
    /**
     * Stop any currently executing script associated with this actor.
     */
    public void stopScript()
    {
        setScript(null);
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Triggers one action in this actor's script.
     */
    protected void scriptStep()
    {
        if (scriptThread != null)
        {
            if (scriptThread.getState() == Thread.State.NEW)
            {
                scriptThread.start();
            }

            if (scriptThread.getState() == Thread.State.TERMINATED)
            {
                scriptThread = null;
            }
            else
            {
                scriptThread.resumeScript();
            }
        }
    }


    // ----------------------------------------------------------
    @Override
    /* package */ void setWorldView(WorldView view)
    {
        super.setWorldView(view);
        if (futureScript != null)
        {
            setScript(futureScript);
            futureScript = null;
        }
    }
}
