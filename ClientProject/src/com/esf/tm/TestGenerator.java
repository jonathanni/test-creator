package com.esf.tm;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * 
 * Client front-end.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r1
 * 
 */
public class TestGenerator extends JFrame implements WindowListener
{

    private static final long serialVersionUID = -6456791709807158899L;

    private static final int WIDTH = 640, HEIGHT = 480;
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
	    .getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
	    .getDefaultToolkit().getScreenSize().getHeight();

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    public TestGenerator()
    {
	super();

	// Formatting
	setSize(WIDTH, HEIGHT);
	setLocation(SCREEN_WIDTH / 2 - WIDTH / 2, SCREEN_HEIGHT / 2 - HEIGHT
		/ 2);
	setVisible(true);
	addWindowListener(this);

	new Choice(-1, "a");
    }

    /**
     * 
     * Destroys the program.
     * 
     * @param callback
     *            the "function" to call before exiting.
     */
    static void destroy(Callback callback)
    {
	callback.cbFunction();
	System.exit(0);
    }

    @Override
    public void windowActivated(WindowEvent we)
    {

    }

    @Override
    public void windowClosed(WindowEvent we)
    {

    }

    @Override
    public void windowClosing(WindowEvent we)
    {
	destroy(new Callback());
    }

    @Override
    public void windowDeactivated(WindowEvent we)
    {

    }

    @Override
    public void windowDeiconified(WindowEvent we)
    {

    }

    @Override
    public void windowIconified(WindowEvent we)
    {

    }

    @Override
    public void windowOpened(WindowEvent we)
    {

    }

    public static void main(String[] args)
    {
	if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
	    ErrorReporter.reportError(
		    "Error occured while initiating graphics", "");
	new TestGenerator();
    }
}
