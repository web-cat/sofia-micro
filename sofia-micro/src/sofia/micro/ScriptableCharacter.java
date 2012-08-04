package sofia.micro;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//-------------------------------------------------------------------------
/**
 * Represents an Actor that is controlled by a script--that is, a
 * predefined sequence of behavior played out over time.  There are
 * two ways to provide a script for a character: either define a
 * {@link #script()} method in a subclass (most common for beginner
 * programmers), or create a script as a separate object and pass
 * it into {@link #setScript(Script)} (more advanced, and more useful
 * if there may be multiple scripts that might be associated with a
 * given class of character).
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/04 16:40 $
 */
public class ScriptableCharacter
    extends Actor
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Create a new Character.
     */
    public ScriptableCharacter()
    {
        super();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * This implementation of the act method executes one action in
     * this character's script.  Normally, scriptable characters do not
     * directly call or override this method--if you want to provide act(),
     * more often than not you want to use {@link Actor} as your base
     * class.
     *
     * <p>If, on the other hand, you want to combine the features of
     * a scriptable character with an actor, you can do that by
     * inheriting from this class and overriding this method.  If you
     * override this method, be sure to call <code>super.act()</code> or
     * your character will no longer obey its assigned script.</p>
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
     * for the character to follow.  The default implementation does
     * nothing (i.e., no script by default).
     */
    public void script()
    {
        // Intentionally empty
    }


    // ----------------------------------------------------------
    /**
     * Associate a script with this character by providing a {@link Script}
     * object.  Actions in the script will execute one move at a time as
     * act() is called.  A script value of null will remove any assigned
     * script for this character.
     *
     * @param script The script to activate.
     * @param <MyCharacter> The type of character this script is written
     *                      for, which should be the same as this character's
     *                      type (or one of its supertypes).
     */
    public <MyCharacter extends ScriptableCharacter> void setScript(
        Script<MyCharacter> script)
    {
        this.script = script;
        if (this.script != null)
        {
            @SuppressWarnings("unchecked")
            MyCharacter thisAsMyCharacter = (MyCharacter)this;
            script.setCharacter(thisAsMyCharacter);
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
     * Associate a script with this character.  Actions in the script
     * will execute one move at a time as act() is called.  Passed a
     * value of null will remove any assigned script for this character.
     *
     * @param script The script to activate.
     */
    public void setScript(Object script)
    {
        if (script instanceof ScriptableCharacter)
        {
            if (script == this)
            {
                setScript(new CharacterScriptAdaptor());
            }
            else
            {
                throw new IllegalArgumentException("Cannot attach a "
                    + "different ScriptableCharacter as this character's "
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
     * Get the script associated with this character.
     * @return This character's script.
     */
    public Script<?> getScript()
    {
        return script;
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    /**
     * Triggers one action in this character's script.
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
        public ScriptThread(ScriptableCharacter character)
        {
            super("Script[" + character.getClass().getSimpleName() + "]");
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
     * a ScriptableCharacter's script() method.
     */
    private static class CharacterScriptAdaptor
        implements Script<ScriptableCharacter>
    {
        //~ Fields ............................................................

        private ScriptableCharacter character;


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        @Override
        public void setCharacter(ScriptableCharacter character)
        {
            this.character = character;
        }


        // ----------------------------------------------------------
        @Override
        public ScriptableCharacter getCharacter()
        {
            return character;
        }


        // ----------------------------------------------------------
        @Override
        public void script()
        {
            character.script();
        }
    }


    // ----------------------------------------------------------
    /**
     * An adaptor class to create a {@link Script} object from
     * any object that has a {@code public void script()} method.
     */
    private static class GeneralScriptAdaptor
        implements Script<ScriptableCharacter>
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
        public void setCharacter(ScriptableCharacter character)
        {
            try
            {
                Method setter = null;
                try
                {
                    setter = script.getClass().getMethod(
                        "setCharacter", Object.class);
                }
                catch (NoSuchMethodException e)
                {
                    try
                    {
                        setter = script.getClass().getMethod(
                            "setCharacter", ScriptableCharacter.class);
                    }
                    catch (NoSuchMethodException e1)
                    {
                        try
                        {
                            setter = script.getClass().getMethod(
                                "setCharacter", character.getClass());
                        }
                        catch (NoSuchMethodException e2)
                        {
                            // Ignore, not setter found
                        }
                    }
                }
                if (setter != null)
                {
                    setter.invoke(script, character);
                }
            }
            catch (Exception e)
            {
                throwException(e);
            }
        }


        // ----------------------------------------------------------
        @Override
        public ScriptableCharacter getCharacter()
        {
            try
            {
                Method getter = script.getClass().getMethod("getCharacter");
                Object result = getter.invoke(script);
                if (result instanceof ScriptableCharacter)
                {
                    return (ScriptableCharacter)result;
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
