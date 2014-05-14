package com.esf.tm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
 * @version 0.0r4
 * 
 */

class TestGenerator extends JFrame
{

    private static final long serialVersionUID = -6456791709807158899L;

    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
	    .getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
	    .getDefaultToolkit().getScreenSize().getHeight();
    private BuildResult result;

    private JButton prevPanel, nextPanel, addQ, remQ;
    private JPanel mainPanel, tmainPanel, npPanel, qPanel, questionContainer;
    private CustomCardLayout customLayout;
    private CardLayout layout, questionLayout;
    private JLabel questionLabel;
    private JTextField testTitleField, testDescriptField;
    private JTextArea questionDescription;

    private Logger logger = new Logger();

    private ArrayList<Integer> panelStops = new ArrayList<Integer>();
    private ArrayList<Integer> panelStarts = new ArrayList<Integer>();

    private int cardIndex, frameCount, questionIndex, questionCount;

    private static final int TITLE_PANEL_INDEX = 0,
	    IMPORT_TEST_PANEL_INDEX = 1, CREATE_TEST_PANEL_INDEX = 2,
	    CREATE_QUESTION_PANEL_INDEX = 3;

    private boolean isValid = true;
    private String invalidMessage;

    private Test currentTest = new Test();

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

	// Add in where the buttons should be disabled
	panelStarts.add(CREATE_TEST_PANEL_INDEX);
	panelStarts.add(IMPORT_TEST_PANEL_INDEX);
	panelStops.add(frameCount = tmainPanel.getComponentCount() - 1);

	// Create a new layout which packs to each JPanel size, create a new
	// panel with that layout, add all the components from the old to the
	// new, then add the new panel
	customLayout = new CustomCardLayout();
	mainPanel = new JPanel(customLayout);

	{
	    int j = 0;
	    for (Component i : tmainPanel.getComponents())
		mainPanel.add((JPanel) i, String.valueOf(j++));
	}

	remove(tmainPanel);
	remove(npPanel);
	remove(qPanel);

	add(mainPanel);
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
     * Change to the next frame. This requires testing to see if the button is
     * enabled, returning if it is not, reenabling the back button if it was
     * previously disabled, adding the Prev/Next panel if it is not the title
     * screen, flipping the page, packing, and updating the buttons to
     * enable/disable based on starts and stops.
     * 
     */

    private void nextPanel()
    {
	if (!isValid)
	{
	    JOptionPane.showMessageDialog(null, invalidMessage,
		    "Please re-enter data", JOptionPane.ERROR_MESSAGE);
	    invalidMessage = "";

	    isValid = true;
	    return;
	}

	if (!nextPanel.isEnabled())
	    return;

	if (cardIndex == CREATE_TEST_PANEL_INDEX)
	{
	    add(qPanel, BorderLayout.SOUTH);
	    pack();
	}

	if (!prevPanel.isEnabled())
	    prevPanel.setEnabled(true);

	if (cardIndex == 0)
	{
	    add(npPanel, BorderLayout.SOUTH);
	    pack();
	}

	customLayout.next(mainPanel);
	pack();

	cardIndex++;
	checkButtons();
    }

    /**
     * 
     * Change to the previous frame. This requires testing to see if the button
     * is enabled, returning if it is not, reenabling the next button if it was
     * previously disabled, flipping the page, packing, and updating the buttons
     * to enable/disable based on starts and stops.
     * 
     */

    private void prevPanel()
    {
	if (!prevPanel.isEnabled())
	    return;

	if (cardIndex == CREATE_QUESTION_PANEL_INDEX && questionIndex == 0)
	{
	    remove(qPanel);
	    pack();
	}

	if (!nextPanel.isEnabled())
	    nextPanel.setEnabled(true);

	customLayout.previous(mainPanel);
	pack();

	cardIndex--;
	checkButtons();
    }

    /**
     * 
     * Change to an arbitrary frame. This requires testing to see if the index
     * to change to is different than the current one, returning if it is not,
     * adding the Prev/Next panel if it is not the title screen, flipping the
     * page, packing, and updating the buttons to enable/disable based on starts
     * and stops.
     * 
     * This method performs checks to see if the index is within bounds.
     * 
     * @param index
     *            the index to change to
     */

    private void changePanel(int index)
    {
	if (!isValid)
	{
	    JOptionPane.showMessageDialog(null, invalidMessage,
		    "Please re-enter data", JOptionPane.ERROR_MESSAGE);
	    invalidMessage = "";

	    isValid = true;
	    return;
	}

	if (cardIndex == index)
	    return;

	if (index < TITLE_PANEL_INDEX || index >= frameCount)
	    return;

	if (cardIndex == TITLE_PANEL_INDEX)
	{
	    add(npPanel, BorderLayout.SOUTH);
	    pack();
	}

	cardIndex = index;
	checkButtons();

	customLayout.show(mainPanel, String.valueOf(cardIndex));
	pack();
    }

    /**
     * 
     * Updates the status of the buttons. If the current index is at a start,
     * disable the previous button. If the current index is at a start, disable
     * the next button. An index can be both at a start and a stop.
     * 
     */

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

    /**
     * 
     * Changes the panel to the create test frame.
     * 
     */

    private void changePanelCreateTest()
    {
	changePanel(CREATE_TEST_PANEL_INDEX);
    }

    /**
     * 
     * Validate the data contained in each panel.
     * 
     */

    private void validateData()
    {
	switch (cardIndex)
	{
	    case CREATE_TEST_PANEL_INDEX:
		isValid = testTitleField.getText().length() != 0;
		invalidMessage = "The Test Title Field is Mandatory";
		break;
	    case CREATE_QUESTION_PANEL_INDEX:
		isValid = questionDescription.getText().length() != 0;
		invalidMessage = "The Question Description Field is Mandatory";
		break;
	}
    }

    private void addQuestion()
    {
	currentTest.addQuestionAtIndex(questionIndex + 1, new Question("", 0));
	questionCount++;

	if (questionCount >= 2)
	    remQ.setEnabled(true);
    }

    private void removeQuestion()
    {
	if (questionCount <= 1)
	    return;

	currentTest.removeQuestion(questionIndex);
	questionCount--;

	if (questionCount == 1)
	    remQ.setEnabled(false);
    }

    /**
     * 
     * Change the question panel to the multiple choice question selection.
     * 
     */

    private void chMCQPanel()
    {
	questionLayout.show(questionContainer, "MCQuestionContainer");
    }

    /**
     * 
     * Change the question panel to the true/false question selection.
     * 
     */

    private void chTFQPanel()
    {
	questionLayout.show(questionContainer, "TFQuestionContainer");
    }

    /**
     * 
     * Change the question panel to the fill in the blank question selection.
     * 
     */

    private void chFIBQPanel()
    {
	questionLayout.show(questionContainer, "FIBQuestionContainer");
    }

    /**
     * 
     * Change the question label to the multiple choice question selection.
     * 
     */

    private void chMCQLabel()
    {
	questionLabel.setText("Create New Multiple Choice Question");
    }

    /**
     * 
     * Change the question label to the true/false question selection.
     * 
     */

    private void chTFQLabel()
    {
	questionLabel.setText("Create New True/False Question");
    }

    /**
     * 
     * Change the question label to the fill in the blank question selection.
     * 
     */

    private void chFIBQLabel()
    {
	questionLabel.setText("Create New Fill in the Blank Question");
    }

    public static void main(String[] args)
    {
	if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
	    ErrorReporter.reportError(
		    "Error occured while initiating graphics", "");

	new TestGenerator();
    }
}
