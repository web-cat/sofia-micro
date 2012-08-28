package sofia.micro;

//-------------------------------------------------------------------------
/**
 *  This class provides some customized behavior beyond the features of
 *  {@link student.TestCase} to support testing of sofia micro-world
 *  applications.  In most cases, it can be used as a completely transparent
 *  drop-in replacement for its parent class.
 *
 *  @author  Stephen Edwards
 *  @author Last changed by $Author$
 *  @version $Date$
 */
public class TestCase
    extends student.TestCase
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new TestCase object.
     */
    public TestCase()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Creates a new TestCase object.
     * @param name The name of this test case
     */
    public TestCase(String name)
    {
        super(name);
    }


    //~ Methods ...............................................................
}
