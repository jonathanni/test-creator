package com.esf.tm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * Server front-end.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r6
 * 
 */

class TestGenerator extends JFrame implements ListSelectionListener
{

    private static final long serialVersionUID = -6456791709807158899L;

    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
	    .getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
	    .getDefaultToolkit().getScreenSize().getHeight();

    private static TestGenerator instance;
    private BuildResult result;

    // GUI objects. These are initialized using Javabuilders in the
    // TestGenerator.yml document, but they can be referenced here.
    private JButton prevPanel, nextPanel, addQ, remQ;
    private JPanel mainPanel, tmainPanel, npPanel, qPanel;
    private CustomCardLayout customLayout;
    private JTextField testTitleField, testDescriptField;
    private JTextArea questionDescription;
    private JList qList;

    // For debug purposes only
    private Logger logger = new Logger();

    private ArrayList<Integer> panelStops = new ArrayList<Integer>();
    private ArrayList<Integer> panelStarts = new ArrayList<Integer>();

    // Controller indices and counters for the number of frames/questions
    private int cardIndex, frameCount, questionIndex = -1, questionCount;

    // Constants for each frame ID
    private static final int TITLE_PANEL_INDEX = 0,
	    IMPORT_TEST_PANEL_INDEX = 1, CREATE_TEST_PANEL_INDEX = 2,
	    CREATE_QUESTION_PANEL_INDEX = 3;

    // Used by the validator
    private boolean isValid = true;
    private String invalidMessage;

    // The Test associated with this object
    private Test currentTest = new Test();

    private ArrayList<Question> testQuestions = new ArrayList<Question>();
    private DefaultListModel qListModel = new DefaultListModel();

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    private TestGenerator()
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
		mainPanel.add((JPanel) i, "" + j++);
	}

	// Bind the JList to the list model
	qList.setModel(qListModel);
	qList.getSelectionModel().addListSelectionListener(this);

	remove(tmainPanel);
	remove(npPanel);

	add(mainPanel);

	setResizable(false);

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
	// Prevent going to the next page due to validation failing
	if (!isValid)
	{
	    JOptionPane.showMessageDialog(null, invalidMessage,
		    "Please re-enter data", JOptionPane.ERROR_MESSAGE);
	    invalidMessage = "";

	    isValid = true;
	    return;
	}

	// Prevent going to the next page due to the button being disabled
	if (!nextPanel.isEnabled())
	    return;

	if (cardIndex == CREATE_TEST_PANEL_INDEX)
	{
	    add(qPanel, BorderLayout.SOUTH);
	    pack();
	}

	// If the prev button is disabled, enable it, since the user can now go
	// back
	if (!prevPanel.isEnabled())
	    prevPanel.setEnabled(true);

	// If the user is going away from the first page, add the prev/next
	// buttons
	if (cardIndex == TITLE_PANEL_INDEX)
	{
	    add(npPanel, BorderLayout.SOUTH);
	    pack();
	}

	cardIndex++;
	checkButtons();

	customLayout.next(mainPanel);
	pack();
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
	// Prevent going to the prev page due to the button being disabled
	if (!prevPanel.isEnabled())
	    return;

	// If the panel is flipped before the first question creation panel,
	// remove the add/remove question panel
	if (cardIndex == CREATE_QUESTION_PANEL_INDEX)
	{
	    remove(qPanel);
	    pack();
	}

	// If the next button is disabled, enable it, since the user can now go
	// forward
	if (!nextPanel.isEnabled())
	    nextPanel.setEnabled(true);

	cardIndex--;
	checkButtons();

	customLayout.previous(mainPanel);
	pack();
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
	// Prevent going to the page due to validation failing
	if (!isValid)
	{
	    JOptionPane.showMessageDialog(null, invalidMessage,
		    "Please re-enter data", JOptionPane.ERROR_MESSAGE);
	    invalidMessage = "";

	    isValid = true;
	    return;
	}

	// Going to the same index has no effect
	if (cardIndex == index)
	    return;

	// Going to an out of bounds index has no effect
	if (index < TITLE_PANEL_INDEX || index >= frameCount)
	    return;

	// Exiting the title panel will add the prev/next buttons
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

    /**
     * 
     * Edits the current question pointed to by the JList if the index is valid.
     * Creates a new {@link QuestionEditor} in order to achieve this.
     * 
     */

    private void editQuestion()
    {
	if (questionIndex < 0 || questionIndex >= questionCount)
	    return;

	QuestionEditor editor = new QuestionEditor(
		testQuestions.get(questionIndex));
	setVisible(false);
    }

    /**
     * 
     * Adds a new Question to the JList and to the backend. Increments the
     * questionIndex to point to the new question, and increment the
     * questionCount. Updates the buttons as necessary.
     * 
     */

    private void addQuestion()
    {
	Question question;
	testQuestions.add(++questionIndex, question = new Question(
		"New Question " + questionIndex, 0));
	qListModel.add(questionIndex, question.getMessage());
	questionCount++;

	if (questionCount >= 2)
	    remQ.setEnabled(true);
    }

    /**
     * 
     * Removes a Question from the JList and the backend. Decrements the
     * questionIndex if necessary, at the end of the list, and decrement the
     * questionCount. Updates the buttons as necessary.
     * 
     */

    private void removeQuestion()
    {
	if (questionCount <= 1)
	    return;

	testQuestions.remove(questionIndex);
	qListModel.remove(questionIndex);
	questionCount--;

	if (questionIndex == questionCount)
	    questionIndex--;

	if (questionCount == 1)
	    remQ.setEnabled(false);
    }

    /**
     * 
     * Updates the question at the current index. This question was modified by
     * the QuestionEditor. Make sure to update the type of question as well by
     * creating a new one with the old properties.
     * 
     * @param newQ
     *            the new question to add in the place of the old one
     */

    void updateQuestion(Question newQ)
    {
	if (questionCount == 0)
	    return;

	testQuestions.set(questionIndex, newQ);
	qListModel.set(questionIndex, testQuestions.get(questionIndex)
		.getMessage());
    }

    @Override
    public void valueChanged(ListSelectionEvent event)
    {
	if (!event.getValueIsAdjusting() && qList.getSelectedIndex() != -1)
	    questionIndex = qList.getSelectedIndex();
    }

    /**
     * 
     * Gets the only instance of the TestGenerator that should be created.
     * 
     * @return the instance
     */

    public static TestGenerator getInstance()
    {
	return instance;
    }

    public static void main(String[] args)
    {
	if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
	    ErrorReporter.reportError(
		    "Error occured while initiating graphics", "");

	instance = new TestGenerator();
    }
}
