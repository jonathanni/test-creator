package com.esf.tm;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * Server front-end.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r1
 * 
 */

class TestGenerator extends JFrame implements ActionListener
{

	private static final long serialVersionUID = -6456791709807158899L;
	
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
			.getDefaultToolkit().getScreenSize().getHeight();
	private BuildResult result;

	/**
	 * 
	 * Creates a new test generator message panel to be used by the teacher.
	 * 
	 */

	public TestGenerator()
	{
		result = SwingJavaBuilder.build(this);
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


	public void windowDestroy()
	{
		destroy(new Callback());
	}

	@Override
	public void actionPerformed(ActionEvent e)
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
