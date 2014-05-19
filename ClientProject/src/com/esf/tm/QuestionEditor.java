package com.esf.tm;

import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

public class QuestionEditor extends JFrame implements ChangeListener
{
	private static final long serialVersionUID = -8403774668066080746L;
	private BuildResult result;

	private CardLayout questionLayout;
	private JLabel questionLabel;
	private JPanel questionContainer;
	private JTextArea questionDescription;
	private JRadioButton rb1, rb2, rb3, rbTrue;
	private JCheckBox mixable, caseSensitive, whitespaceSensitive;
	private JSpinner blankSpaces;

	private final Question question;

	private ArrayList<Choice> mcQChoices = new ArrayList<Choice>();
	private int mcCorrectAnswer;

	private ArrayList<ArrayList<String>> fibQcorrectAnswers = new ArrayList<ArrayList<String>>();

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

		setVisible(true);
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

	private void addChoice()
	{
	}

	/**
	 * 
	 * Disposes of this window and reenables the TestGenerator.
	 * 
	 */

	private void exit()
	{
		Question newQuestion = rb1.isSelected() ? new MCQuestion(question)
				: (rb2.isSelected() ? new TFQuestion(question)
						: new FIBQuestion(question));

		newQuestion.setMessage(questionDescription.getText());

		if (newQuestion instanceof MCQuestion)
		{
			for (Choice i : mcQChoices)
				((MCQuestion) newQuestion).addChoice(i);

			((MCQuestion) newQuestion).setCorrectAnswer(mcCorrectAnswer);
			((MCQuestion) newQuestion).setMixable(mixable.isSelected());
		} else if (newQuestion instanceof TFQuestion)
			((TFQuestion) newQuestion).setCorrectAnswer(rbTrue.isSelected());
		else if (newQuestion instanceof FIBQuestion)
		{
			for (int i = 0; i < fibQcorrectAnswers.size(); i++)
				for (String j : fibQcorrectAnswers.get(i))
					((FIBQuestion) newQuestion).addAnswer(i, j);
			((FIBQuestion) newQuestion).setBlankSpaces((Integer) blankSpaces
					.getValue());
			((FIBQuestion) newQuestion).setCaseSensitive(caseSensitive
					.isSelected());
			((FIBQuestion) newQuestion)
					.setWhitespaceSensitive(whitespaceSensitive.isSelected());
		}

		TestGenerator.getInstance().updateQuestion(newQuestion);

		TestGenerator.getInstance().setVisible(true);
		setVisible(false);
		dispose();
	}

	private void doNothing()
	{
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

	@Override
	public void stateChanged(ChangeEvent event)
	{
		int bs = (Integer) blankSpaces.getValue();
		
	}
}
