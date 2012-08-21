package sofia.micro.internal;

import sofia.micro.Script;

//-------------------------------------------------------------------------
/**
 * A thread for running a "script" associated with a scriptable actor or
 * world.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ScriptThread
    extends Thread
{
    //~ Fields ................................................................

    private java.util.concurrent.Semaphore scriptGate =
        new java.util.concurrent.Semaphore(0);
    private volatile int depth = -1;
    private Script script;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new script thread.
     * @param threadName The name of this thread (for debugging).
     * @param script     The script this thread will run.
     */
    public ScriptThread(String threadName, Script script)
    {
        super(threadName);
        this.script = script;
    }


    // ----------------------------------------------------------
    /**
     * Create a new script thread.
     * @param namedAfter The object controlled by this script, which is
     *                   used to determining this thread's name (for
     *                   debugging).
     * @param script     The script this thread will run.
     */
    public ScriptThread(Object namedAfter, Script script)
    {
        this("Script[" + namedAfter.getClass().getSimpleName() + "]", script);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    @Override
    public void run()
    {
        try
        {
            // Immediately park this thread until we are resumed.
            pauseScript();
            script.script();
        }
        catch (ScriptTermination e)
        {
            // script stopped externally, so let method return
        }
        catch (Throwable e)
        {
            System.out.println("An exception has killed script thread " + this);
            e.printStackTrace();
        }
    }


    // ----------------------------------------------------------
    /**
     * Get the script associated with this thread.
     * @return This thread's script.
     */
    public Script getScript()
    {
        return script;
    }


    // ----------------------------------------------------------
    /**
     * Should be called at the beginning of each atomic action sequence.
     * Each call <b>must</b> have a corresponding call to
     * {@link #endMyAtomicAction()}.
     */
    public void beginMyAtomicAction()
    {
        depth++;
    }


    // ----------------------------------------------------------
    /**
     * A convenience method for calling {@link #beginMyAtomicAction()} on
     * the current thread, if necessary.
     */
    public static void beginAtomicAction()
    {
        if (Thread.currentThread() instanceof ScriptThread)
        {
            ((ScriptThread)Thread.currentThread()).beginMyAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Should be called at the end of each atomic action sequence.
     */
    public void endMyAtomicAction()
    {
        depth--;
        pauseScript();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method for calling {@link #endMyAtomicAction()} on
     * the current thread, if necessary.
     */
    public static void endAtomicAction()
    {
        if (Thread.currentThread() instanceof ScriptThread)
        {
            ((ScriptThread)Thread.currentThread()).endMyAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Pause the script that is being executed by this thread, so that it
     * can be resumed again later.
     */
    private void pauseScript()
    {
        if (currentThread() != this)
        {
            throw new IllegalStateException(
                "pauseThisScript() called from outside this ScriptThread."
                + "  Caller = " + currentThread());
        }

        if (isInterrupted())
        {
            throw new ScriptTermination();
        }
        try
        {
            if (depth <= 0)
            {
                depth = -1;
                scriptGate.acquire();
            }
        }
        catch (InterruptedException e)
        {
            interrupt();
            throw new ScriptTermination();
        }
    }


    // ----------------------------------------------------------
    /**
     * Resume the script that is being executed by this thread.
     */
    public void resumeScript()
    {
        scriptGate.release();
    }


    // ----------------------------------------------------------
    /**
     * Terminate the script that is being executed by this thread.
     */
    public void endScript()
    {
        this.interrupt();
        if (currentThread() == this)
        {
            throw new ScriptTermination();
        }
    }


    // ----------------------------------------------------------
    private static class ScriptTermination
        extends RuntimeException
    {
        private static final long serialVersionUID = 2922423860480121589L;

        // Nothing else needed
    }
}
