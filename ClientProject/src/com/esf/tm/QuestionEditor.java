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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * A specialized editor for a Question. The layout is described in
 * QuestionEditor.yml.
 * 
 * @author Jonathan Ni
 * @since 5/14/14
 * @version 0.0r1
 * 
 */

public class QuestionEditor extends JFrame implements ChangeListener,
		ListSelectionListener
{
	private static final long serialVersionUID = -8403774668066080746L;
	private BuildResult result;

	private CustomCardLayout customLayout;
	private JLabel questionLabel;
	private JPanel tquestionContainer, questionContainer;
	private JTextArea questionDescription;

	private JRadioButton rb1, rb2, rb3, rbTrue;

	private JCheckBox questionMixable, caseSensitive, whitespaceSensitive;
	private JSpinner blankSpaces;

	private JButton editC, addC, remC, moveUpC, moveDownC;
	private JButton editB, addB, remB, moveUpB, moveDownB;
	private JButton editCA, addCA, remCA, moveUpCA, moveDownCA;

	private JList mcChoices, fibBlankList;
	private JTable fibBlankCAChoicesList;
	private DefaultListModel mcCListModel = new DefaultListModel(),
			fibBlankListModel = new DefaultListModel();

	private final Question question;

	private ArrayList<Choice> mcQChoices = new ArrayList<Choice>();
	private int mcCorrectAnswer = -1, mcChoiceIndex = -1, mcChoiceCount;

	private ArrayList<ArrayList<String>> fibQcorrectAnswers = new ArrayList<ArrayList<String>>();
	private int fibBlankIndex = -1, fibBlankCount, fibCOIndex = -1, fibCOCount;

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

		questionDescription.setText(getQuestion().getMessage());
		(getQuestion() instanceof FIBQuestion ? rb3
				: (getQuestion() instanceof TFQuestion ? rb2 : rb1))
				.setSelected(true);
		blankSpaces.setModel(new SpinnerNumberModel(1, 1, 255, 1));
		blankSpaces.addChangeListener(this);

		// By default, the first is selected and is the "correct answer"
		addChoice();
		// ((CustomRadio) mcCListModel.get(0)).setSelected(true);

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

		mcChoices.setModel(mcCListModel);
		mcChoices.getSelectionModel().addListSelectionListener(this);

		fibBlankList.setModel(fibBlankListModel);
		fibBlankList.getSelectionModel().addListSelectionListener(this);

		fibBlankCAChoicesList.getSelectionModel()
				.addListSelectionListener(this);

		recBorder(this);

		setVisible(true);
		pack();
		pack();
	}

	private void recBorder(Component a)
	{
		Border line = BorderFactory.createLineBorder(Color.RED);

		if (a instanceof JPanel)
			((JPanel) a).setBorder(line);

		if (a instanceof Container)
			for (Component b : ((Container) a).getComponents())
				recBorder(b);
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

		if (fibBlankCount >= 2)
			remB.setEnabled(true);
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

		fibQcorrectAnswers.remove(fibBlankIndex);
		fibBlankListModel.remove(fibBlankIndex);
		fibBlankCount--;

		if (fibBlankIndex == fibBlankCount)
			fibBlankIndex--;

		if (fibBlankCount == 1)
			remB.setEnabled(false);
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
		int editedIndex = mcChoiceIndex;

		String msg = JOptionPane.showInputDialog(null,
				"Please input choice message", "Edit Choice",
				JOptionPane.QUESTION_MESSAGE);

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

	private void validateData()
	{

	}

	/**
	 * 
	 * Disposes of this window and reenables the TestGenerator.
	 * 
	 */

	private void exit()
	{
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
				for (String j : fibQcorrectAnswers.get(i))
					((FIBQuestion) newQuestion).addAnswer(i, j);

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

	@Override
	public void stateChanged(ChangeEvent event)
	{
		int bs = (Integer) blankSpaces.getValue();
		System.out.println(bs);
	}

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
		}
	}
}