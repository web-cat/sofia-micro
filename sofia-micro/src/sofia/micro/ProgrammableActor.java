package sofia.micro;

import sofia.micro.internal.ProgramThread;

//-------------------------------------------------------------------------
/**
 * Represents an Actor that is controlled by its own program--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a program for an actor: either override the
 * {@link #myProgram()} method in a subclass (most common for beginner
 * programmers), or create a script as a separate object and pass
 * it into {@link #setProgram(Program)} (more advanced, and more useful
 * if there may be multiple programs that might be associated with a
 * given actor).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ProgrammableActor
    extends Actor
    implements Program
{
    //~ Fields ................................................................

    private ProgramThread programThread = null;
    private Program       futureProgram = null;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new programmable actor. By default, this actor's image will
     * be scaled to the size of a single grid cell, preserving aspect ratio.
     */
    public ProgrammableActor()
    {
        super();
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new programmable actor.  By default, this actor's image will
     * be scaled to the size of a single grid cell, preserving aspect ratio.
     * @param nickName The nickname for this actor.
     */
    public ProgrammableActor(String nickName)
    {
        super(nickName);
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new programmable ctor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public ProgrammableActor(boolean scaleToCell)
    {
        super(scaleToCell);
        setProgram(this);
    }


    // ----------------------------------------------------------
    /**
     * Create a new programmable actor.
     * @param nickName The nickname for this actor.
     * @param scaleToCell If true, the Actor's image will be scaled to
     *                    the dimensions of a single World grid cell, while
     *                    preserving aspect ratio.  If false, the image
     *                    will be sized relative to the underlying bitmap
     *                    or shape.
     */
    public ProgrammableActor(String nickName, boolean scaleToCell)
    {
        super(nickName, scaleToCell);
        setProgram(this);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * This implementation of the act method executes one action in
     * this actor's program.  Normally, programmable actors do not
     * directly call or override this method--if you want to provide act(),
     * more often than not you want to use {@link Actor} as your base
     * class.
     *
     * <p>If, on the other hand, you want to combine the features of
     * a programmable actor with a custom act() method, you can do that by
     * inheriting from this class and overriding this method.  If you
     * override this method, be sure to call <code>super.act()</code> or
     * your actor will no longer obey its assigned program.</p>
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
    public void move(int distance)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.move(distance);
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
    public void turn(double amount)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.turn(amount);
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
    public void turnTowards(int x, int y)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.turnTowards(x, y);
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
    public void turnTowards(Actor target)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.turnTowards(target);
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
    public void setRotation(double rotation)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setRotation(rotation);
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
    public void setGridX(int x)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setGridX(x);
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
    public void setGridY(int y)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setGridY(y);
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
    public void setGridLocation(int x, int y)
    {
        try
        {
            ProgramThread.beginAtomicAction();
            super.setGridLocation(x, y);
        }
        finally
        {
            ProgramThread.endAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Subclasses can override this method to provide the "program"
     * for the actor to follow.  The default implementation does
     * nothing (i.e., no program by default).
     */
    public void myProgram()
    {
        // Intentionally empty
    }


    // ----------------------------------------------------------
    /**
     * Associate a program with this actor by providing an {@link ActorProgram}
     * object.  Actions in the program will execute one move at a time as
     * act() is called.  A program value of null will remove any assigned
     * program for this actor.
     *
     * @param program       The program to activate.
     * @param <MyActor>     The type of actor this program is written
     *                      for, which should be the same as this actor's
     *                      type (or one of its supertypes).
     */
    public <MyActor extends ProgrammableActor> void setProgram(
        ActorProgram<MyActor> program)
    {
        if (program != null)
        {
            @SuppressWarnings("unchecked")
            MyActor thisAsMyActor = (MyActor)this;
            program.setActor(thisAsMyActor);
        }
        setProgram((Program)program);
    }


    // ----------------------------------------------------------
    /**
     * Associate a program with this actor by providing a {@link Program}
     * object.  Actions in the program will execute one move at a time as
     * act() is called.  A program value of null will remove any assigned
     * program for this actor.
     *
     * @param program The program to activate.
     */
    public void setProgram(Program program)
    {
        if (getWorld() == null)
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
     * Get the program associated with this actor.
     * @return This actor's program.
     */
    public Program getProgram()
    {
        return programThread == null
            ? null
            : programThread.getProgram();
    }


    // ----------------------------------------------------------
    /**
     * Stop any currently executing program associated with this actor.
     */
    public void stopProgram()
    {
        setProgram(null);
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Triggers one action in this actor's program.
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
    /* package */ void setWorld(World world)
    {
        super.setWorld(world);
        if (futureProgram != null)
        {
            setProgram(futureProgram);
            futureProgram = null;
        }
    }
}
