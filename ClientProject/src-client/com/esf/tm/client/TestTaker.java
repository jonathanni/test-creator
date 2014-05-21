package com.esf.tm.client;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

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
    private JList ips;
    private DefaultListModel ipListModel = new DefaultListModel();
    
    public static final int PORT = 3353;

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    public TestTaker()
    {
	super();

	result = SwingJavaBuilder.build(this);

	ips.setModel(ipListModel);

	pack();
	setVisible(true);
	pack();

	new Thread(new NodeScanner()).start();
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

    /**
     * 
     * Function necessary for javabuilders to call in order to destroy the
     * window.
     * 
     */

    private void windowDestroy()
    {
	destroy(new Callback());
    }

    public static void main(String[] args)
    {
	new TestTaker();
    }
}
