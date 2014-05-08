package com.esf.tm;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * Server front-end.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r2
 * 
 */

class TestGenerator extends JFrame implements ActionListener
{

    private static final long serialVersionUID = -6456791709807158899L;

    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
	    .getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
	    .getDefaultToolkit().getScreenSize().getHeight();
    private BuildResult result;

    private JButton btnPrevPanel;
    private JButton btnNextPanel;

    private JPanel mainPanel;

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    public TestGenerator()
    {
	result = SwingJavaBuilder.build(this);

	try
	{
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (ClassNotFoundException e)
	{
	    ErrorReporter.reportError(
		    "An error has occured while trying to open the window.",
		    Util.stackTraceToString(e));
	    e.printStackTrace();
	} catch (InstantiationException e)
	{
	    ErrorReporter.reportError(
		    "An error has occured while trying to open the window.",
		    Util.stackTraceToString(e));
	    e.printStackTrace();
	} catch (IllegalAccessException e)
	{
	    ErrorReporter.reportError(
		    "An error has occured while trying to open the window.",
		    Util.stackTraceToString(e));
	    e.printStackTrace();
	} catch (UnsupportedLookAndFeelException e)
	{
	    ErrorReporter.reportError(
		    "An error has occured while trying to open the window.",
		    Util.stackTraceToString(e));
	    e.printStackTrace();
	}

	btnPrevPanel.setVisible(false);
	btnPrevPanel.setEnabled(false);

	btnNextPanel.setVisible(false);
	btnNextPanel.setEnabled(false);

	setVisible(true);
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

    /**
     * 
     * Change to the next frame.
     * 
     */
    
    private void nextPanel()
    {
	((CardLayout) result.get("layout")).next(mainPanel);
    }

    /**
     * 
     * Change to the previous frame.
     * 
     */
    
    private void prevPanel()
    {
	((CardLayout) result.get("layout")).previous(mainPanel);
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

	SwingJavaBuilder.getConfig().prototype(
		"JButton(name=btnPrevPanel, text=Previous)");
	SwingJavaBuilder.getConfig().prototype(
		"JButton(name=btnNextPanel, text=Next)");

	new TestGenerator();
    }
}
