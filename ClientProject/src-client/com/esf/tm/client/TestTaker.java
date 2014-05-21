package com.esf.tm.client;

import javax.swing.JFrame;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * Client front-end.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r2
 * 
 */

public class TestTaker extends JFrame
{
    private static final long serialVersionUID = -5547354766805951582L;

    private BuildResult result;

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    public TestTaker()
    {
	super();

	result = SwingJavaBuilder.build(this);

	pack();
	setVisible(true);
	pack();
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

    public static void main(String[] args)
    {
	new TestTaker();
    }
}
