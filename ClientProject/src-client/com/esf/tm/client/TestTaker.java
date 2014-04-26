package com.esf.tm.client;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

/**
 * 
 * Client front-end.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r1
 * 
 */

public class TestTaker extends JFrame implements WindowListener, ActionListener
{
	private static final long serialVersionUID = -5547354766805951582L;

	private static final int WIDTH = 640, HEIGHT = 480;
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
			.getDefaultToolkit().getScreenSize().getHeight();

	/**
	 * 
	 * Creates a new test generator message panel to be used by the teacher.
	 * 
	 */

	public TestTaker()
	{
		super();

		// Formatting
		setSize(WIDTH, HEIGHT);
		setLocation(SCREEN_WIDTH / 2 - WIDTH / 2, SCREEN_HEIGHT / 2 - HEIGHT
				/ 2);
		setVisible(true);
		addWindowListener(this);
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

	@Override
	public void actionPerformed(ActionEvent e)
	{

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
		if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
			ErrorReporter.reportError(
					"Error occured while initiating graphics", "");
		new TestTaker();
	}
}
