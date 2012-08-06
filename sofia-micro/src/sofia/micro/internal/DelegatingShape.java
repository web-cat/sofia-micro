package sofia.micro.internal;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import sofia.graphics.Anchor;
import sofia.graphics.Color;
import sofia.graphics.PointAndAnchor;
import sofia.graphics.Shape;

// -------------------------------------------------------------------------
/**
 * A shape that holds another shape internally, and delegates all behavior
 * to that "inner" shape.  This class is intended to provide "shape wrapping"
 * behavior for creating other classes that wrap or decorate a shape
 * with additional behaviors, but that delegate all drawing and visual
 * representation to some other (more primitive) shape class.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class DelegatingShape
    extends Shape
{
    //~ Fields ................................................................

    private Shape delegate;


    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new delegating shape with no delegate.  Be sure to call
     * setDelegate() before using any other methods.
     * @param bounds The (initial) bounding box for this shape.
     */
    public DelegatingShape(RectF bounds)
    {
        super(bounds);
    }


    // ----------------------------------------------------------
    /**
     * Creates a new shape wrapping the specified delegate.
     *
     * @param delegate The inner shape.
     */
    public DelegatingShape(Shape delegate)
    {
        this(delegate.getBounds());
        this.delegate = delegate;
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Set the shape used to represent this delegating shape.
     * @param delegate The "inner" shape to delegate to.
     */
    public void setDelegate(Shape delegate)
    {
        this.delegate = delegate;
        super.setBounds(delegate.getBounds());
    }


    // ----------------------------------------------------------
    /**
     * Get the "inner" shape wrapped by this delegating shape.
     * @return The "inner" shape (the delegate).
     */
    public Shape getDelegate()
    {
        return delegate;
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setBounds(RectF newBounds)
    {
        super.setBounds(newBounds);
        delegate.setBounds(newBounds);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setX(float x)
    {
        super.setX(x);
        delegate.setX(x);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setY(float y)
    {
        super.setY(y);
        delegate.setY(y);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setPosition(PointF position)
    {
        super.setPosition(position);
        delegate.setPosition(position);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setPosition(PointAndAnchor pointAndAnchor)
    {
        super.setPosition(pointAndAnchor);
        delegate.setPosition(pointAndAnchor);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPositionAnchor(PointF anchor)
    {
        super.setPositionAnchor(anchor);
        delegate.setPositionAnchor(anchor);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    @Override
    public void setPositionAnchor(Anchor anchor)
    {
        super.setPositionAnchor(anchor);
        delegate.setPositionAnchor(anchor);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void move(float dx, float dy)
    {
        super.move(dx, dy);
        delegate.move(dx, dy);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public boolean contains(float x, float y)
    {
        float[] point = inverseTransformPoint(x, y);
        return delegate.contains(point[0], point[1]);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setZIndex(int newZIndex)
    {
        super.setZIndex(newZIndex);
        delegate.setZIndex(newZIndex);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setColor(Color newColor)
    {
        delegate.setColor(newColor);
        super.setColor(newColor);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean newVisible)
    {
        delegate.setVisible(newVisible);
        super.setVisible(newVisible);
    }


    // ----------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void draw(Canvas canvas)
    {
        delegate.draw(canvas);
    }


    // ----------------------------------------------------------
    /**
     * Returns a human-readable string representation of the shape.
     *
     * @return a human-readable string representation of the shape
     */
    @Override
    public String toString()
    {
        return "(" + getClass().getSimpleName() + ")" + delegate;
    }
}
