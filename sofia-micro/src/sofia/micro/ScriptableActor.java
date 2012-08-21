package sofia.micro;

import sofia.micro.internal.ScriptThread;

//-------------------------------------------------------------------------
/**
 * Represents an Actor that is controlled by a script--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a script for an actor: either override the
 * {@link #script()} method in a subclass (most common for beginner
 * programmers), or create a script as a separate object and pass
 * it into {@link #setScript(Script)} (more advanced, and more useful
 * if there may be multiple scripts that might be associated with a
 * given actor).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ScriptableActor
    extends Actor
    implements Script
{
    //~ Fields ................................................................

    private ScriptThread scriptThread = null;
    private Script       futureScript = null;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new scriptable actor. By default, this actor's image will
     * be scaled to the size of a single grid cell, preserving aspect ratio.
     */
    public ScriptableActor()
    {
        super();
        setScript(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new scriptable actor.  By default, this actor's image will
     * be scaled to the size of a single grid cell, preserving aspect ratio.
     * @param nickName The nickname for this actor.
     */
    public ScriptableActor(String nickName)
    {
        super(nickName);
        setScript(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new scriptable ctor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public ScriptableActor(boolean scaleToCell)
    {
        super(scaleToCell);
        setScript(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new scriptable actor.
     * @param nickName The nickname for this actor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public ScriptableActor(String nickName, boolean scaleToCell)
    {
        super(nickName, scaleToCell);
        setScript(this);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * This implementation of the act method executes one action in
     * this actor's script.  Normally, scriptable actors do not
     * directly call or override this method--if you want to provide act(),
     * more often than not you want to use {@link Actor} as your base
     * class.
     *
     * <p>If, on the other hand, you want to combine the features of
     * a scriptable actor with a custom act() method, you can do that by
     * inheriting from this class and overriding this method.  If you
     * override this method, be sure to call <code>super.act()</code> or
     * your actor will no longer obey its assigned script.</p>
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
    public void move(int distance)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.move(distance);
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
    public void turn(double amount)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.turn(amount);
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
    public void turnTowards(int x, int y)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.turnTowards(x, y);
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
    public void turnTowards(Actor target)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.turnTowards(target);
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
    public void setRotation(double rotation)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.setRotation(rotation);
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
    public void setGridX(int x)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.setGridX(x);
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
    public void setGridY(int y)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.setGridY(y);
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
    public void setGridLocation(int x, int y)
    {
        try
        {
            ScriptThread.beginAtomicAction();
            super.setGridLocation(x, y);
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
     * Associate a script with this actor by providing an {@link ActorScript}
     * object.  Actions in the script will execute one move at a time as
     * act() is called.  A script value of null will remove any assigned
     * script for this actor.
     *
     * @param script The script to activate.
     * @param <MyActor>     The type of actor this script is written
     *                      for, which should be the same as this actor's
     *                      type (or one of its supertypes).
     */
    public <MyActor extends ScriptableActor> void setScript(
        ActorScript<MyActor> script)
    {
        if (script != null)
        {
            @SuppressWarnings("unchecked")
            MyActor thisAsMyActor = (MyActor)this;
            script.setActor(thisAsMyActor);
        }
        setScript((Script)script);
    }


    // ----------------------------------------------------------
    /**
     * Associate a script with this actor by providing a {@link Script}
     * object.  Actions in the script will execute one move at a time as
     * act() is called.  A script value of null will remove any assigned
     * script for this actor.
     *
     * @param script The script to activate.
     */
    public void setScript(Script script)
    {
        if (getWorld() == null)
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
    /* package */ void setWorld(World world)
    {
        super.setWorld(world);
        if (futureScript != null)
        {
            setScript(futureScript);
            futureScript = null;
        }
    }
}
