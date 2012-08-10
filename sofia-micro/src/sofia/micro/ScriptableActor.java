package sofia.micro;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//-------------------------------------------------------------------------
/**
 * Represents an Actor that is controlled by a script--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a script for an actor: either define a
 * {@link #script()} method in a subclass (most common for beginner
 * programmers), or create a script as a separate object and pass
 * it into {@link #setScript(Script)} (more advanced, and more useful
 * if there may be multiple scripts that might be associated with a
 * given actor).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public class ScriptableActor
    extends Actor
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new scriptable actor.
     */
    public ScriptableActor()
    {
        super();
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
        super.move(distance);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void turn(double amount)
    {
        super.turn(amount);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnTowards(int x, int y)
    {
        super.turnTowards(x, y);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void turnTowards(Actor target)
    {
        super.turnTowards(target);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setRotation(double rotation)
    {
        super.setRotation(rotation);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridX(int x)
    {
        super.setGridX(x);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridY(int y)
    {
        super.setGridY(y);
        waitForScriptStep();
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridLocation(int x, int y)
    {
        super.setGridLocation(x, y);
        waitForScriptStep();
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
     * Associate a script with this actor by providing a {@link Script}
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
        Script<MyActor> script)
    {
        this.script = script;
        if (this.script != null)
        {
            @SuppressWarnings("unchecked")
            MyActor thisAsMyActor = (MyActor)this;
            script.setActor(thisAsMyActor);
            scriptGate = new java.util.concurrent.Semaphore(0);
            scriptThread = new ScriptThread(this);
        }
        else
        {
            if (scriptThread != null)
            {
                // Stop the thread before resetting the reference
                scriptThread.interrupt();
                scriptThread = null;
            }
            scriptGate = null;
        }
        // TODO: add some kind of "finishActing()" and "isFinished()"
        // pair of methods to allow actors to be "stopped" in general?
    }


    // ----------------------------------------------------------
    /**
     * Associate a script with this actor.  Actions in the script
     * will execute one move at a time as act() is called.  Passed a
     * value of null will remove any assigned script for this actor.
     *
     * @param script The script to activate.
     */
    public void setScript(Object script)
    {
        if (script instanceof ScriptableActor)
        {
            if (script == this)
            {
                setScript(new ActorScriptAdaptor());
            }
            else
            {
                throw new IllegalArgumentException("Cannot attach a "
                    + "different ScriptableActor as this actor's "
                    + "script.");
            }
        }
        else
        {
            setScript(new GeneralScriptAdaptor(script));
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the script associated with this actor.
     * @return This actor's script.
     */
    public Script<?> getScript()
    {
        return script;
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
            else if (scriptThread.getState() == Thread.State.TERMINATED)
            {
                scriptThread = null;
                scriptGate = null;
            }
            else
            {
                scriptGate.release();
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * Pauses the script thread to wait for the next step.
     */
    protected void waitForScriptStep()
    {
        if (scriptThread != null)
        {
            if (Thread.currentThread() == scriptThread)
            {
                if (Thread.interrupted())
                {
                    throw new RuntimeInterruptedException();
                }
                try
                {
                    scriptGate.acquire();
                }
                catch (InterruptedException e)
                {
                    scriptThread.interrupt();
                    throw new RuntimeInterruptedException();
                }
            }
        }
    }


    //~ Private Methods .......................................................

    // ----------------------------------------------------------
    private static class RuntimeInterruptedException
        extends RuntimeException
    {
        private static final long serialVersionUID = 2922423860480121589L;

        // Nothing else needed
    }


    // ----------------------------------------------------------
    private class ScriptThread
        extends Thread
    {
        public ScriptThread(ScriptableActor actor)
        {
            super("Script[" + actor.getClass().getSimpleName() + "]");
        }

        @Override
        public void run()
        {
            try
            {
                script.script();
            }
            catch (RuntimeInterruptedException e)
            {
                // script stopped externally, so let method returns
            }
        }
    }


    // ----------------------------------------------------------
    /**
     * An adaptor class to create a {@link Script} object from
     * a ScriptableActor's script() method.
     */
    private static class ActorScriptAdaptor
        implements Script<ScriptableActor>
    {
        //~ Fields ............................................................

        private ScriptableActor actor;


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        @Override
        public void setActor(ScriptableActor actor)
        {
            this.actor = actor;
        }


        // ----------------------------------------------------------
        @Override
        public ScriptableActor getActor()
        {
            return actor;
        }


        // ----------------------------------------------------------
        @Override
        public void script()
        {
            actor.script();
        }
    }


    // ----------------------------------------------------------
    /**
     * An adaptor class to create a {@link Script} object from
     * any object that has a {@code public void script()} method.
     */
    private static class GeneralScriptAdaptor
        implements Script<ScriptableActor>
    {
        //~ Fields ............................................................

        private Object script;
        private Method scriptMethod;


        //~ Constructor .......................................................

        // ----------------------------------------------------------
        /**
         * Create a new adaptor.  Throws an IllegalArgumentException if
         * the provided argument does not provide a script() method.
         * @param script An object that provides a script() method.
         */
        public GeneralScriptAdaptor(Object script)
        {
            this.script = script;
            try
            {
                scriptMethod = script.getClass().getMethod("script");
            }
            catch (SecurityException e)
            {
                throw new IllegalArgumentException(e);
            }
            catch (NoSuchMethodException e)
            {
                throw new IllegalArgumentException(e);
            }
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        @Override
        public void setActor(ScriptableActor actor)
        {
            try
            {
                Method setter = null;
                try
                {
                    setter = script.getClass().getMethod(
                        "setActor", Object.class);
                }
                catch (NoSuchMethodException e)
                {
                    try
                    {
                        setter = script.getClass().getMethod(
                            "setActor", ScriptableActor.class);
                    }
                    catch (NoSuchMethodException e1)
                    {
                        try
                        {
                            setter = script.getClass().getMethod(
                                "setActor", actor.getClass());
                        }
                        catch (NoSuchMethodException e2)
                        {
                            // Ignore, not setter found
                        }
                    }
                }
                if (setter != null)
                {
                    setter.invoke(script, actor);
                }
            }
            catch (Exception e)
            {
                throwException(e);
            }
        }


        // ----------------------------------------------------------
        @Override
        public ScriptableActor getActor()
        {
            try
            {
                Method getter = script.getClass().getMethod("getActor");
                Object result = getter.invoke(script);
                if (result instanceof ScriptableActor)
                {
                    return (ScriptableActor)result;
                }
                else
                {
                    return null;
                }
            }
            catch (Exception e)
            {
                throwException(e);
                return null; // Unreachable, but keeps compiler happy
            }
        }


        // ----------------------------------------------------------
        @Override
        public void script()
        {
            try
            {
                scriptMethod.invoke(script);
            }
            catch (Exception e)
            {
                throwException(e);
            }
        }


        // ----------------------------------------------------------
        private void throwException(Throwable e)
        {
            if (e instanceof RuntimeException)
            {
                throw (RuntimeException)e;
            }
            else if (e instanceof InvocationTargetException)
            {
                throwException(e.getCause());
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
    }


    //~ Fields ................................................................

    private Script<?> script;
    private ScriptThread scriptThread = null;
    private java.util.concurrent.Semaphore scriptGate = null;
}
