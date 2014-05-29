package com.esf.tm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

import com.esf.tm.serializable.Choice;
import com.esf.tm.serializable.FIBQuestion;
import com.esf.tm.serializable.MCQuestion;
import com.esf.tm.serializable.Question;
import com.esf.tm.serializable.TFQuestion;

/**
 * 
 * A specialized editor for a Question. The layout is described in
 * QuestionEditor.yml.
 * 
 * @author Jonathan Ni
 * @since 5/14/14
 * @version 0.0r2
 * 
 */

public class QuestionEditor extends JFrame implements ChangeListener,
	ListSelectionListener, TableModelListener
{
    private static final long serialVersionUID = -8403774668066080746L;
    private BuildResult result;

    private CustomCardLayout customLayout;
    private JLabel questionLabel;
    private JPanel tquestionContainer, questionContainer;
    private JTextArea questionDescription;

    private JScrollPane blankCAScrollPanel;

    private JRadioButton rb1, rb2, rb3, rbTrue;

    private JCheckBox questionMixable, caseSensitive, whitespaceSensitive;
    private JSpinner blankSpaces;

    private JButton editC, addC, remC, moveUpC, moveDownC;
    private JButton editCA, addCA, remCA, moveUpCA, moveDownCA;

    private JList mcChoices, fibBlankList;
    private JTable fibBlankCAChoicesList;
    private DefaultListModel mcCListModel = new DefaultListModel(),
	    fibBlankListModel = new DefaultListModel();

    private final Question question;

    private ArrayList<Choice> mcQChoices = new ArrayList<Choice>();
    private int mcCorrectAnswer = -1, mcChoiceIndex = -1, mcChoiceCount;

    private ArrayList<ArrayList<String>> fibQcorrectAnswers = new ArrayList<ArrayList<String>>();
    private int fibBlankIndex = -1, fibBlankCount;
    private ArrayList<Integer> fibBlankCAIndices = new ArrayList<Integer>(),
	    fibBlankCACounts = new ArrayList<Integer>();

    private int blanks;

    private String invalidMessage;
    private boolean isBusy, isValid = true;

    private static final String BLANK_SPACE = "___.";

    /**
     * 
     * Creates a new QuestionEditor and binds a Question to it. This
     * QuestionEditor will change the contents of the question binded.
     * 
     * @param question
     *            the Question to bind
     */

    public QuestionEditor(Question question)
    {
	this.question = question;

	result = SwingJavaBuilder.build(this);

	questionDescription.setText(getQuestion().getMessage());
	(getQuestion() instanceof FIBQuestion ? rb3
		: (getQuestion() instanceof TFQuestion ? rb2 : rb1))
		.setSelected(true);

	blankSpaces.setModel(new SpinnerNumberModel(1, 1, 255, 1));
	blankSpaces
		.setValue(getQuestion() instanceof FIBQuestion ? ((FIBQuestion) getQuestion())
			.getBlankSpaces() : 1);
	blanks = (Integer) blankSpaces.getValue();
	blankSpaces.addChangeListener(this);

	// Create a new layout which packs to each JPanel size, create a new
	// panel with that layout, add all the components from the old to the
	// new, then add the new panel
	customLayout = new CustomCardLayout();
	questionContainer = new JPanel(customLayout);

	{
	    int j = 0;
	    for (Component i : tquestionContainer.getComponents())
		questionContainer.add((JPanel) i, "" + j++);
	}

	remove(tquestionContainer);
	add(questionContainer);

	// Some formatting
	mcChoices.setModel(mcCListModel);
	mcChoices.getSelectionModel().addListSelectionListener(this);

	fibBlankList.setModel(fibBlankListModel);
	fibBlankList.getSelectionModel().addListSelectionListener(this);

	fibBlankCAChoicesList.getSelectionModel()
		.addListSelectionListener(this);
	((DefaultTableModel) fibBlankCAChoicesList.getModel())
		.addColumn(new TableColumn());
	fibBlankCAChoicesList.setTableHeader(null);
	fibBlankCAChoicesList
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	((DefaultTableModel) fibBlankCAChoicesList.getModel())
		.addTableModelListener(this);

	blankCAScrollPanel.setColumnHeaderView(null);

	fibBlankCAChoicesList.getSelectionModel()
		.addListSelectionListener(this);

	// Load question data
	if (getQuestion() instanceof MCQuestion)
	{
	    MCQuestion q = (MCQuestion) getQuestion();

	    questionMixable.setSelected(q.isMixable());

	    // Add all the choices...
	    for (int i = 0; i < q.getChoiceCount(); i++)
	    {
		addChoice();
		editChoice(q.getChoice(i).getMessage());
	    }

	    // Set correct answer
	    mcChoiceIndex = q.getCorrectAnswer();
	    setChoice();

	    // Reset
	    mcChoiceIndex = 0;
	    mcChoices.setSelectedIndex(0);
	} else
	    // By default, the first is selected and is the "correct answer"
	    addChoice();

	isBusy = true;

	if (getQuestion() instanceof FIBQuestion)
	{
	    FIBQuestion q = (FIBQuestion) getQuestion();

	    caseSensitive.setSelected(q.isCaseSensitive());
	    whitespaceSensitive.setSelected(q.isWhitespaceSensitive());

	    // Add all the blanks...
	    for (ArrayList<String> i : q.getCorrectAnswers())
	    {
		addBlank();

		// Add all the correct answers...
		for (int j = 0; j < i.size(); j++)
		{
		    addCorrectAnswer();
		    editCorrectAnswer(i.get(j), j);
		}

		fibBlankCAIndices.set(fibBlankIndex, -1);
	    }

	    fibBlankIndex = 0;
	    fibBlankList.setSelectedIndex(0);
	} else
	    // By default, a blank is created
	    addBlank();

	isBusy = false;

	// recBorder(this);

	if (rb1.isSelected())
	{
	    chMCQPanel();
	    chMCQLabel();
	} else if (rb2.isSelected())
	{
	    chTFQPanel();
	    chTFQLabel();
	} else
	{
	    chFIBQPanel();
	    chFIBQLabel();
	}

	setVisible(true);
	pack();
	pack();
    }

    /**
     * 
     * Gets the question associated with this QuestionEditor.
     * 
     * @return the question
     */

    public Question getQuestion()
    {
	return question;
    }

    /**
     * 
     * Adds a new blank to the JList and to the backend. Increments the
     * fibBlankIndex to point to the new blank, and increment the fibBlankCount.
     * Updates the buttons as necessary.
     * 
     */

    private void addBlank()
    {
	fibQcorrectAnswers.add(++fibBlankIndex, new ArrayList<String>());
	fibBlankListModel.add(fibBlankIndex, "Blank " + fibBlankIndex);
	fibBlankCount++;

	fibBlankCAIndices.add(fibBlankIndex, -1);
	fibBlankCACounts.add(fibBlankIndex, 0);

	fibBlankList.setSelectedIndex(fibBlankIndex);

	updateTable();
    }

    /**
     * 
     * Removes a blank from the JList and the backend. Decrements the
     * fibBlankIndex if necessary, at the end of the list, and decrement the
     * fibBlankCount. Updates the buttons as necessary.
     * 
     */

    private void removeBlank()
    {
	if (fibBlankCount <= 1)
	    return;

	isBusy = true;

	fibQcorrectAnswers.remove(fibBlankIndex);
	fibBlankListModel.remove(fibBlankIndex);
	fibBlankCount--;

	fibBlankCAIndices.remove(fibBlankIndex);
	fibBlankCACounts.remove(fibBlankIndex);

	if (fibBlankIndex == fibBlankCount)
	    fibBlankIndex--;

	fibBlankList.setSelectedIndex(fibBlankIndex);

	isBusy = false;
    }

    /**
     * 
     * Edit the correct answer in a row of the JTable.
     * 
     * @param row
     */

    private void editCorrectAnswer(int row)
    {
	editCorrectAnswer(
		(String) ((DefaultTableModel) fibBlankCAChoicesList.getModel()).getValueAt(
			row, 0), row);
    }

    /**
     * 
     * Update the correct answer for the current blank in a specific row of the
     * JTable.
     * 
     * @param msg
     * @param row
     */

    private void editCorrectAnswer(String msg, int row)
    {
	fibQcorrectAnswers.get(fibBlankIndex).set(row, msg);
    }

    /**
     * 
     * Adds a new correct answer to the JTable and to the backend. Increments
     * the fibBlankCAIndex to point to the new choice, and increment the
     * fibBlankCACount. Updates the buttons as necessary.
     * 
     */
    private void addCorrectAnswer()
    {
	String correct;
	// Increment index
	fibBlankCAIndices.set(fibBlankIndex,
		fibBlankCAIndices.get(fibBlankIndex) + 1);

	// Get incremented index
	int blankCAIndex = fibBlankCAIndices.get(fibBlankIndex);

	// Insert into backend
	fibQcorrectAnswers.get(fibBlankIndex).add(blankCAIndex,
		correct = "Correct Answer " + blankCAIndex);

	// Insert into frontend
	((DefaultTableModel) fibBlankCAChoicesList.getModel()).insertRow(
		blankCAIndex, new Object[] { correct });

	// Increment count
	fibBlankCACounts.set(fibBlankIndex,
		fibBlankCACounts.get(fibBlankIndex) + 1);

	if (fibBlankCACounts.get(fibBlankIndex) >= 2)
	    remCA.setEnabled(true);

	// Select next item
	fibBlankCAChoicesList.changeSelection(
		fibBlankCAIndices.get(fibBlankIndex), 0, false, false);
    }

    /**
     * 
     * Removes a correct answer from the JTable and the backend. Decrements the
     * fibBlankCAIndex if necessary, at the end of the list, and decrement the
     * fibBlankCACount. Updates the buttons as necessary.
     * 
     */
    private void removeCorrectAnswer()
    {
	if (fibBlankCACounts.get(fibBlankIndex) <= 1)
	    return;

	// Get index
	int blankCAIndex = fibBlankCAIndices.get(fibBlankIndex);

	// Remove from backend
	fibQcorrectAnswers.get(fibBlankIndex).remove(blankCAIndex);

	isBusy = true;
	// Remove from frontend
	((DefaultTableModel) fibBlankCAChoicesList.getModel())
		.removeRow(blankCAIndex);
	isBusy = false;

	// Decrement count
	fibBlankCACounts.set(fibBlankIndex,
		fibBlankCACounts.get(fibBlankIndex) - 1);

	// If at end, decrement
	if (blankCAIndex == fibBlankCACounts.get(fibBlankIndex))
	    fibBlankCAIndices.set(fibBlankIndex, blankCAIndex - 1);

	if (fibBlankCACounts.get(fibBlankIndex) == 1)
	    remCA.setEnabled(false);

	// Select prev item
	fibBlankCAChoicesList.changeSelection(
		fibBlankCAIndices.get(fibBlankIndex), 0, false, false);
    }

    /**
     * 
     * Moves a correct answer up the JTable. Cannot move a correct answer up
     * that is at the beginning of the list.
     * 
     */
    private void moveUpCorrectAnswer()
    {
	int blankCAIndex = fibBlankCAIndices.get(fibBlankIndex);

	if (blankCAIndex < 1)
	    return;

	// Swap backend
	Collections.swap(fibQcorrectAnswers.get(fibBlankIndex),
		blankCAIndex - 1, blankCAIndex);

	isBusy = true;
	// Swap frontend
	((DefaultTableModel) fibBlankCAChoicesList.getModel()).moveRow(
		blankCAIndex, blankCAIndex, blankCAIndex - 1);
	isBusy = false;

	fibBlankCAIndices.set(fibBlankIndex, blankCAIndex - 1);

	// Select prev item
	fibBlankCAChoicesList.changeSelection(
		fibBlankCAIndices.get(fibBlankIndex), 0, false, false);
    }

    /**
     * 
     * Moves a correct answer down the JTable. Cannot move a correct answer down
     * that is at the end of the list.
     * 
     */
    private void moveDownCorrectAnswer()
    {
	int blankCAIndex = fibBlankCAIndices.get(fibBlankIndex);

	if (blankCAIndex == fibBlankCACounts.get(fibBlankIndex) - 1)
	    return;

	// Swap backend
	Collections.swap(fibQcorrectAnswers.get(fibBlankIndex), blankCAIndex,
		blankCAIndex + 1);

	isBusy = true;
	// Swap frontend
	((DefaultTableModel) fibBlankCAChoicesList.getModel()).moveRow(
		blankCAIndex, blankCAIndex, blankCAIndex + 1);
	isBusy = false;

	fibBlankCAIndices.set(fibBlankIndex, blankCAIndex + 1);

	// Select next item
	fibBlankCAChoicesList.changeSelection(
		fibBlankCAIndices.get(fibBlankIndex), 0, false, false);
    }

    /**
     * 
     * Sets the correct answer of the choice and marks it with a (*). Removes
     * the marking off of the old answer.
     * 
     */

    private void setChoice()
    {
	if (mcCorrectAnswer == mcChoiceIndex)
	    return;

	if (mcCorrectAnswer != -1)
	{
	    String old = (String) mcCListModel.get(mcCorrectAnswer);
	    if (old.startsWith("(*) "))
		mcCListModel.set(mcCorrectAnswer, old.substring(4));
	}

	String msg = (String) mcCListModel.get(mcChoiceIndex);
	mcCListModel.set(mcChoiceIndex, "(*) " + msg);

	mcCorrectAnswer = mcChoiceIndex;
    }

    /**
     * 
     * Edits the current choice pointed to by the JList if the index is valid.
     * Creates a new {@link JOptionPane} in order to achieve this.
     * 
     */

    private void editChoice()
    {
	String msg = JOptionPane.showInputDialog(null,
		"Please input choice message", "Edit Choice",
		JOptionPane.QUESTION_MESSAGE);

	editChoice(msg);
    }

    /**
     * 
     * Changes the message of the currently selected choice.
     * 
     * @param msg
     *            the message
     */

    private void editChoice(String msg)
    {
	int editedIndex = mcChoiceIndex;

	if (msg == null || msg.equals(""))
	    return;

	mcQChoices.get(editedIndex).setMessage(msg);
	mcCListModel.set(editedIndex, msg);
    }

    /**
     * 
     * Adds a new Choice to the JList and to the backend. Increments the
     * mcChoiceIndex to point to the new choice, and increment the
     * mcChoiceCount. Updates the buttons as necessary.
     * 
     */

    private void addChoice()
    {
	if (mcCorrectAnswer > mcChoiceIndex)
	    mcCorrectAnswer++;

	Choice choice;
	mcQChoices.add(++mcChoiceIndex, choice = new Choice("New Choice "
		+ mcChoiceIndex));
	mcCListModel.add(mcChoiceIndex, choice.getMessage());
	mcChoiceCount++;

	if (mcChoiceCount >= 2)
	    remC.setEnabled(true);

	mcChoices.setSelectedIndex(mcChoiceIndex);
    }

    /**
     * 
     * Removes a Choice from the JList and the backend. Decrements the
     * mcChoiceIndex if necessary, at the end of the list, and decrement the
     * mcChoiceCount. Updates the buttons as necessary.
     * 
     */

    private void removeChoice()
    {
	if (mcChoiceCount <= 1)
	    return;

	if (mcChoiceIndex == mcCorrectAnswer)
	    mcCorrectAnswer = -1;

	if (mcCorrectAnswer > mcChoiceIndex)
	    mcCorrectAnswer--;

	mcQChoices.remove(mcChoiceIndex);
	mcCListModel.remove(mcChoiceIndex);
	mcChoiceCount--;

	if (mcChoiceIndex == mcChoiceCount)
	    mcChoiceIndex--;

	if (mcChoiceCount == 1)
	    remC.setEnabled(false);

	mcChoices.setSelectedIndex(mcChoiceIndex);
    }

    /**
     * 
     * Moves a Choice up the JList. Cannot move a choice up that is at the
     * beginning of the list.
     * 
     */

    private void moveUpChoice()
    {
	if (mcChoiceIndex < 1)
	    return;

	if (mcChoiceIndex - 1 == mcCorrectAnswer)
	    mcCorrectAnswer++;

	Collections.swap(mcQChoices, mcChoiceIndex - 1, mcChoiceIndex);

	String temp = (String) mcCListModel.get(mcChoiceIndex);
	mcCListModel.set(mcChoiceIndex, mcCListModel.get(mcChoiceIndex - 1));
	mcCListModel.set(mcChoiceIndex - 1, temp);

	mcChoiceIndex--;
	mcChoices.setSelectedIndex(mcChoiceIndex);
    }

    /**
     * 
     * Moves a Choice down the JList. Cannot move a choice down that is at the
     * end of the list.
     * 
     */

    private void moveDownChoice()
    {
	if (mcChoiceIndex == mcChoiceCount - 1)
	    return;

	if (mcChoiceIndex + 1 == mcCorrectAnswer)
	    mcCorrectAnswer--;

	Collections.swap(mcQChoices, mcChoiceIndex, mcChoiceIndex + 1);

	String temp = (String) mcCListModel.get(mcChoiceIndex + 1);
	mcCListModel.set(mcChoiceIndex + 1, mcCListModel.get(mcChoiceIndex));
	mcCListModel.set(mcChoiceIndex, temp);

	mcChoiceIndex++;
	mcChoices.setSelectedIndex(mcChoiceIndex);
    }

    /**
     * 
     * Changes the correct answers table so that it points to a new blank.
     * Should be called after the blank index is changed.
     * 
     */

    private void updateTable()
    {
	int rows = ((DefaultTableModel) fibBlankCAChoicesList.getModel())
		.getRowCount();

	isBusy = true;
	// Remove everything
	for (int i = rows - 1; i >= 0; i--)
	    ((DefaultTableModel) fibBlankCAChoicesList.getModel()).removeRow(i);

	for (String i : fibQcorrectAnswers.get(fibBlankIndex))
	    ((DefaultTableModel) fibBlankCAChoicesList.getModel())
		    .addRow(new Object[] { i });
	isBusy = false;
    }

    /**
     * 
     * Validates data to make sure that data was entered correctly.
     * 
     * The FIB question must have the # of blanks match the number of "___."s.
     * 
     */

    private void validateData()
    {
	isValid = rb3.isSelected() ? Util.countSubstring(BLANK_SPACE,
		questionDescription.getText()) == blanks : true;
	invalidMessage = "The number of blanks does not match"
		+ " the blanks in the question description.";
    }

    /**
     * 
     * Disposes of this window and reenables the TestGenerator.
     * 
     */

    private void exit()
    {
	if (!isValid)
	{
	    JOptionPane.showMessageDialog(null, invalidMessage,
		    "Please re-enter data", JOptionPane.ERROR_MESSAGE);
	    invalidMessage = "";

	    isValid = true;
	    return;
	}

	// Change the type of question
	Question newQuestion = rb1.isSelected() ? new MCQuestion(question)
		: (rb2.isSelected() ? new TFQuestion(question)
			: new FIBQuestion(question));

	// Set message field
	newQuestion.setMessage(questionDescription.getText());

	if (newQuestion instanceof MCQuestion) // MC question attributes
	{
	    for (Choice i : mcQChoices)
		// Add all the choices
		((MCQuestion) newQuestion).addChoice(i);

	    // Set the correct answer and if the question is mixable
	    ((MCQuestion) newQuestion).setCorrectAnswer(mcCorrectAnswer);
	    ((MCQuestion) newQuestion).setMixable(questionMixable.isSelected());
	} else if (newQuestion instanceof TFQuestion) // TF question attributes
	    // Sets the correct answer
	    ((TFQuestion) newQuestion).setCorrectAnswer(rbTrue.isSelected());
	else if (newQuestion instanceof FIBQuestion) // FIB question attributes
	{
	    // Add all the correct answers for all blanks
	    for (int i = 0; i < fibQcorrectAnswers.size(); i++)
	    {
		((FIBQuestion) newQuestion).prepare(i);
		for (String j : fibQcorrectAnswers.get(i))
		    ((FIBQuestion) newQuestion).addAnswer(i, j);
	    }

	    // Set the number of blank spaces, if the question is case
	    // sensitive, and if the question is whitespace sensitive
	    ((FIBQuestion) newQuestion).setBlankSpaces((Integer) blankSpaces
		    .getValue());
	    ((FIBQuestion) newQuestion).setCaseSensitive(caseSensitive
		    .isSelected());
	    ((FIBQuestion) newQuestion)
		    .setWhitespaceSensitive(whitespaceSensitive.isSelected());
	}

	// Make sure to update the question at the current index
	TestGenerator.getInstance().updateQuestion(newQuestion);

	// Switch to the other window
	TestGenerator.getInstance().setVisible(true);
	setVisible(false);
	dispose();
    }

    /**
     * 
     * Change the question panel to the multiple choice question selection.
     * 
     */

    private void chMCQPanel()
    {
	customLayout.show(questionContainer, "0");
	pack();
    }

    /**
     * 
     * Change the question panel to the true/false question selection.
     * 
     */

    private void chTFQPanel()
    {
	customLayout.show(questionContainer, "1");
	pack();
    }

    /**
     * 
     * Change the question panel to the fill in the blank question selection.
     * 
     */

    private void chFIBQPanel()
    {
	customLayout.show(questionContainer, "2");
	pack();
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

    /**
     * 
     * Triggered when the JSpinner value changes.
     * 
     * When the value goes up, blanks are added. When the value goes down,
     * blanks are removed.
     * 
     */

    @Override
    public void stateChanged(ChangeEvent event)
    {
	int bs = (Integer) blankSpaces.getValue();

	if (bs < blanks)
	    removeBlank();
	else if (bs > blanks)
	    addBlank();

	blanks = bs;
    }

    /**
     * 
     * Triggered when the selection of the list is changed. Updates buttons and
     * indices.
     * 
     */

    @Override
    public void valueChanged(ListSelectionEvent event)
    {
	if (event.getSource() == mcChoices.getSelectionModel())
	{
	    if (!event.getValueIsAdjusting()
		    && mcChoices.getSelectedIndex() != -1)
		mcChoiceIndex = mcChoices.getSelectedIndex();

	    moveUpC.setEnabled(true);
	    moveDownC.setEnabled(true);

	    if (mcChoiceIndex == 0)
		moveUpC.setEnabled(false);
	    if (mcChoiceIndex == mcChoiceCount - 1)
		moveDownC.setEnabled(false);
	} else if (event.getSource() == fibBlankList.getSelectionModel())
	{
	    if (!event.getValueIsAdjusting()
		    && fibBlankList.getSelectedIndex() != -1)
		fibBlankIndex = fibBlankList.getSelectedIndex();

	    if (!isBusy)
		updateTable();
	} else if (event.getSource() == fibBlankCAChoicesList
		.getSelectionModel())
	{
	    if (!event.getValueIsAdjusting()
		    && fibBlankCAChoicesList.getSelectedRow() != -1 && !isBusy)
		fibBlankCAIndices.set(fibBlankIndex,
			fibBlankCAChoicesList.getSelectedRow());

	    moveUpCA.setEnabled(true);
	    moveDownCA.setEnabled(true);

	    if (fibBlankCAIndices.get(fibBlankIndex) == 0)
		moveUpCA.setEnabled(false);
	    if (fibBlankCAIndices.get(fibBlankIndex) == fibBlankCACounts
		    .get(fibBlankIndex) - 1)
		moveDownCA.setEnabled(false);
	}
    }

    /**
     * 
     * Triggered when the correct answer blanks are changed.
     * 
     * Edits the correct answer list.
     * 
     */

    @Override
    public void tableChanged(TableModelEvent event)
    {
	if (isBusy)
	    return;

	editCorrectAnswer(event.getFirstRow());
    }
}