package sofia.micro.internal;

import sofia.micro.Program;

//-------------------------------------------------------------------------
/**
 * A thread for running a "program" associated with a programmable actor or
 * world.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/21 14:19 $
 */
public class ProgramThread
    extends Thread
{
    //~ Fields ................................................................

    private java.util.concurrent.Semaphore programGate =
        new java.util.concurrent.Semaphore(0);
    private volatile int depth = -1;
    private Program program;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create a new program thread.
     * @param threadName The name of this thread (for debugging).
     * @param program    The program this thread will run.
     */
    public ProgramThread(String threadName, Program program)
    {
        super(threadName);
        this.program = program;
    }


    // ----------------------------------------------------------
    /**
     * Create a new program thread.
     * @param namedAfter The object controlled by this program, which is
     *                   used to determining this thread's name (for
     *                   debugging).
     * @param program    The program this thread will run.
     */
    public ProgramThread(Object namedAfter, Program program)
    {
        this(
            "Program[" + namedAfter.getClass().getSimpleName() + "]", program);
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    @Override
    public void run()
    {
        try
        {
            // Immediately park this thread until we are resumed.
            pauseProgram();
            program.myProgram();
        }
        catch (ProgramTermination e)
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
     * Get the program associated with this thread.
     * @return This thread's program.
     */
    public Program getProgram()
    {
        return program;
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
        if (Thread.currentThread() instanceof ProgramThread)
        {
            ((ProgramThread)Thread.currentThread()).beginMyAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Should be called at the end of each atomic action sequence.
     */
    public void endMyAtomicAction()
    {
        depth--;
        pauseProgram();
    }


    // ----------------------------------------------------------
    /**
     * A convenience method for calling {@link #endMyAtomicAction()} on
     * the current thread, if necessary.
     */
    public static void endAtomicAction()
    {
        if (Thread.currentThread() instanceof ProgramThread)
        {
            ((ProgramThread)Thread.currentThread()).endMyAtomicAction();
        }
    }


    // ----------------------------------------------------------
    /**
     * Pause the program that is being executed by this thread, so that it
     * can be resumed again later.
     */
    private void pauseProgram()
    {
        if (currentThread() != this)
        {
            throw new IllegalStateException(
                "pauseProgram() called from outside this ScriptThread."
                + "  Caller = " + currentThread());
        }

        if (isInterrupted())
        {
            throw new ProgramTermination();
        }
        try
        {
            if (depth <= 0)
            {
                depth = -1;
                programGate.acquire();
            }
        }
        catch (InterruptedException e)
        {
            interrupt();
            throw new ProgramTermination();
        }
    }


    // ----------------------------------------------------------
    /**
     * Resume the program that is being executed by this thread.
     */
    public void resumeProgram()
    {
        programGate.release();
    }


    // ----------------------------------------------------------
    /**
     * Terminate the program that is being executed by this thread.
     */
    public void endProgram()
    {
        this.interrupt();
        if (currentThread() == this)
        {
            throw new ProgramTermination();
        }
    }


    // ----------------------------------------------------------
    private static class ProgramTermination
        extends RuntimeException
    {
        private static final long serialVersionUID = 2922423860480121589L;

        // Nothing else needed
    }
}
