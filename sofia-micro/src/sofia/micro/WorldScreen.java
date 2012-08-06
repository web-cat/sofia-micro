package sofia.micro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import sofia.app.ShapeScreen;
import sofia.graphics.ShapeView;
import sofia.internal.ModalTask;

//-------------------------------------------------------------------------
/**
 * Represents a screen containing a single {@link WorldView}, to be used
 * as the parent for "microworld" applications.
 *
 * @author  Stephen Edwards
 * @author  Last changed by $Author: edwards $
 * @version $Date: 2012/08/06 11:13 $
 */
// @OptionsMenu(R.menu.micro_options)  // <- can't, if this is a library
public class WorldScreen
    extends ShapeScreen
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new WorldScreen.
     */
    public WorldScreen()
    {
        super();
    }


    //~ Public Methods ........................................................

    // ----------------------------------------------------------
    /**
     * Gets the {@link WorldView} that holds all of the shapes on this screen.
     * @return This screen's WorldView
     */
    public WorldView getWorldView()
    {
        return (WorldView)getShapeView();
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link World} for this screen.
     * @return This screen's World
     */
    public World getWorld()
    {
        assert getWorldView() != null : "No view defined";
        return getWorldView().getWorld();
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to this screen.
     * @param actor The Actor to add.
     */
    public void add(Actor actor)
    {
        assert getWorldView() != null : "No view defined";
        getWorldView().add(actor);
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to the world at a specified location.  This is a
     * convenience method that is equivalent to calling {@code add()} on
     * an actor, and then calling {@code setGridLocation()} on the actor
     * to specify its position.
     *
     * @param actor The Actor to add.
     * @param x The x coordinate of the location where the actor is added.
     * @param y The y coordinate of the location where the actor is added.
     */
    public void add(Actor actor, int x, int y)
    {
        assert getWorldView() != null : "No view defined";
        getWorldView().add(actor, x, y);
    }


    // ----------------------------------------------------------
    /**
     * Add an Actor to the world at a specified location.
     *
     * <p>This method is identical to {@code add()}, but is provided for
     * Greenfoot compatibility.</p>
     *
     * @param actor The Actor to add.
     * @param x The x coordinate of the location where the actor is added.
     * @param y The y coordinate of the location where the actor is added.
     */
    public void addObject(Actor actor, int x, int y)
    {
        assert getWorldView() != null : "No view defined";
        getWorldView().addObject(actor, x, y);
    }


    // ----------------------------------------------------------
    /**
     * Remove an Actor from this screen.
     * @param actor The Actor to remove.
     */
    public void remove(Actor actor)
    {
        assert getWorldView() != null : "No view defined";
        getWorldView().remove(actor);
    }


    // ----------------------------------------------------------
    /**
     * Remove an Actor from this screen.
     *
     * <p>This method is identical to {@code remove()}, but is provided
     * for Greenfoot compatibility.</p>
     *
     * @param actor The Actor to remove.
     */
    public void removeObject(Actor actor)
    {
        assert getWorldView() != null : "No view defined";
        getWorldView().remove(actor);
    }


    // ----------------------------------------------------------
    public void actSelected()
    {
        System.out.println("WorldScreen.actSelected()");
    }


    // ----------------------------------------------------------
    public void runSelected()
    {
        isRunning = true;
        System.out.println("WorldScreen.runSelected()");
    }


    // ----------------------------------------------------------
    public void pauseSelected()
    {
        isRunning = false;
        System.out.println("WorldScreen.pauseSelected()");
    }


    // ----------------------------------------------------------
    public void resetSelected()
    {
        System.out.println("WorldScreen.resetSelected()");
    }


    // ----------------------------------------------------------
    public void speedSelected()
    {
        System.out.println("WorldScreen.speedSelected()");
        if (getWorldView() == null)
        {
            return;
        }
        ModalTask<Boolean> modal = new ModalTask<Boolean>() {
            @Override
            protected void run()
            {
                final View layout = getLayoutInflater().inflate(
                    R.layout.speed_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    WorldScreen.this);

                builder.setTitle("Simulation Speed");
                builder.setView(layout);

                builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SeekBar sb =
                            (SeekBar)layout.findViewById(R.id.speedBar);
                        // FIXME: change speed here
                        speed = sb.getProgress();
                        System.out.println("new speed = " + sb.getProgress());
                        endModal(true);
                    }
                });

                builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        endModal(false);
                    }
                });

                builder.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog)
                    {
                        endModal(false);
                    }
                });

                builder.show();
                SeekBar sb =
                    (SeekBar)layout.findViewById(R.id.speedBar);
                // FIXME: set initial speed
                sb.setProgress(speed);
            }
        };

        if (modal.executeTask())
        {
            System.out.println("speed changed!");
        }
    }


    // ----------------------------------------------------------
    private boolean isRunning = false;
    private int speed = 24;
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        System.out.println("WorldScreen.onPrepareOptionsMenu()");
        // TODO: enable/disable as required
        menu.findItem(R.id.act).setEnabled(!isRunning);
        menu.findItem(R.id.pause).setVisible(isRunning);
        menu.findItem(R.id.run).setVisible(!isRunning);
        // TODO: temporarily pause simulation thread
        return super.onPrepareOptionsMenu(menu);
    }


    // ----------------------------------------------------------
    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
        System.out.println("WorldScreen.onOptionsMenuClosed()");
        // TODO: resume simulation thread if it was temporarily paused
        super.onOptionsMenuClosed(menu);
    }


    // ----------------------------------------------------------
    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected ShapeView createShapeView(ShapeScreen parent)
    {
        return new WorldView(parent);
    }
}
