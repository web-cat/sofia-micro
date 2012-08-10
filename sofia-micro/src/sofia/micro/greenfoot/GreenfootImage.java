package sofia.micro.greenfoot;

import android.graphics.Bitmap;
import sofia.graphics.Image;

//-------------------------------------------------------------------------
/**
 * A small "adaptor" class that allows client code to use the class name
 * GreenfootImage instead of just Image.  It is intended to provide for
 * source-level compatibility of some Greenfoot examples.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
public class GreenfootImage
    extends Image
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Create an image from a bitmap.
     * @param bitmap The bitmap forming this image's contents.
     */
    public GreenfootImage(Bitmap bitmap)
    {
        super(bitmap);
        setScaleForDpi(false);
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a bitmap by specifying a resource id.
     * @param bitmapId The id of the bitmap resource for this image.
     */
    public GreenfootImage(int bitmapId)
    {
        super(bitmapId);
        setScaleForDpi(false);
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a class.  The image used will be found
     * based on the name of the class.
     *
     * @param klass The Java class after which the file is named.
     */
    public GreenfootImage(Class<?> klass)
    {
        super(klass);
        setScaleForDpi(false);
    }


    // ----------------------------------------------------------
    /**
     * Create an image from a file.  The image will be found by
     * searching for an appropriate match.
     *
     * @param fileName The name of the image file.
     */
    public GreenfootImage(String fileName)
    {
        super(fileName);
        setScaleForDpi(false);
    }


    // ----------------------------------------------------------
    /**
     * Create an image that is a duplicate of another image
     * (a copy constructor).
     * @param other The image to copy.
     */
    public GreenfootImage(Image other)
    {
        super(other);
    }


    //~ Public Methods ........................................................
}
