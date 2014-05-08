package com.esf.tm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

class TestGenerator extends JFrame
{

    private static final long serialVersionUID = -6456791709807158899L;

    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
	    .getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
	    .getDefaultToolkit().getScreenSize().getHeight();
    private BuildResult result;

    private JButton prevPanel;
    private JButton nextPanel;

    private JPanel mainPanel;
    private JPanel npPanel;

    private CardLayout layout;

    private Logger logger = new Logger();

    private ArrayList<Integer> panelStops = new ArrayList<Integer>();
    private ArrayList<Integer> panelStarts = new ArrayList<Integer>();

    private int cardIndex;

    private static final int CREATE_TEST_PANEL_INDEX = 2,
	    IMPORT_TEST_PANEL_INDEX = 1;

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

	panelStarts.add(CREATE_TEST_PANEL_INDEX);
	panelStarts.add(IMPORT_TEST_PANEL_INDEX);
	panelStops.add(mainPanel.getComponentCount() - 1);

	remove(npPanel);
	pack();

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
	if (!nextPanel.isEnabled())
	    return;

	if (!prevPanel.isEnabled())
	    prevPanel.setEnabled(true);

	if (cardIndex == 0)
	{
	    add(npPanel, BorderLayout.SOUTH);
	    pack();
	}

	layout.next(mainPanel);

	cardIndex++;
	checkButtons();
    }

    /**
     * 
     * Change to the previous frame.
     * 
     */

    private void prevPanel()
    {
	if (!prevPanel.isEnabled())
	    return;

	if (!nextPanel.isEnabled())
	    nextPanel.setEnabled(true);

	layout.previous(mainPanel);

	cardIndex--;
	checkButtons();
    }

    private void changePanel(int index)
    {
	if (cardIndex == index)
	    return;

	if (cardIndex == 0)
	{
	    add(npPanel, BorderLayout.SOUTH);
	    pack();
	}

	cardIndex = index;
	checkButtons();

	layout.show(mainPanel, mainPanel.getComponent(cardIndex).getName());
    }

    private void checkButtons()
    {
	{
	    boolean flag = false;

	    for (int i : panelStops)
		if (cardIndex == i)
		{
		    flag = true;
		    break;
		}

	    if (flag)
		nextPanel.setEnabled(false);
	}

	{
	    boolean flag = false;

	    for (int i : panelStarts)
		if (cardIndex == i)
		{
		    flag = true;
		    break;
		}

	    if (flag)
		prevPanel.setEnabled(false);
	}
    }

    private void changePanelCreateTest()
    {
	changePanel(CREATE_TEST_PANEL_INDEX);
    }

    public static void main(String[] args)
    {
	if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
	    ErrorReporter.reportError(
		    "Error occured while initiating graphics", "");

	new TestGenerator();
    }
}
