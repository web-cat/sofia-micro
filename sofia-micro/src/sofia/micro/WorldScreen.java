package sofia.micro;

import android.view.MenuInflater;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
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
     * Subclasses should override this method to set up the world
     * associated with this screen.  The default implementation here
     * does nothing.
     */
    public void initialize()
    {
        // Intentionally blank
    }


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
     * Called when the "Act" menu command is selected.
     */
    public void actClicked()
    {
        assert getWorld() != null : "No world defined";
        getWorld().runOneStep();
    }


    // ----------------------------------------------------------
    /**
     * Called with the "Run" menu command is selected.
     */
    public void runClicked()
    {
        assert getWorld() != null : "No world defined";
        getWorld().start();
    }


    // ----------------------------------------------------------
    /**
     * Called with the "Pause" menu command is selected.
     */
    public void pauseClicked()
    {
        assert getWorld() != null : "No world defined";
        getWorld().stop();
    }


    // ----------------------------------------------------------
    /**
     * Called with the "Reset" menu command is selected.
     */
    public void resetClicked()
    {
        if (getWorld() != null)
        {
            getWorld().stop();
        }
        if (getWorldView() != null)
        {
            getWorldView().clear();
        }
        initialize();
    }


    // ----------------------------------------------------------
    /**
     * Called with the "Speed" menu command is selected.
     */
    public void speedClicked()
    {
        if (getWorldView() == null)
        {
            return;
        }
        new ModalTask<Boolean>() {
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
                        getWorld().setSpeed(sb.getProgress());
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
                sb.setProgress(getWorld().getSpeed());
            }
        }.executeTask();
    }


    // ----------------------------------------------------------
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // Inflates the menu so menu.findItem(id) doesn't return null,
        // inflating the menu should only be done once so as to not have
        // the menu repeat itself
        if (menu.size() == 0)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.micro_options, menu);
        }

        World world = getWorld();
        if (world != null)
        {
            world.temporarilyPauseRunning();
            menu.findItem(R.id.act).setVisible(true)
                .setEnabled(!world.isRunning());
            menu.findItem(R.id.pause).setVisible(world.isRunning());
            menu.findItem(R.id.run).setVisible(!world.isRunning());
            menu.findItem(R.id.speed).setVisible(true).setEnabled(true);
        }
        else
        {
            menu.findItem(R.id.act).setVisible(false);
            menu.findItem(R.id.pause).setVisible(false);
            menu.findItem(R.id.run).setVisible(false);
            menu.findItem(R.id.speed).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    // ----------------------------------------------------------
    @Override
    public void onOptionsMenuClosed(Menu menu)
    {
        World world = getWorld();
        if (world != null)
        {
            world.resumeRunningIfNecessary();
        }
        super.onOptionsMenuClosed(menu);
    }


    //~ Protected Methods .....................................................

    // ----------------------------------------------------------
    @Override
    protected ShapeView createShapeView(ShapeScreen parent)
    {
        return new WorldView(parent);
    }
}
