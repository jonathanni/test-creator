package com.esf.tm;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

public class QuestionEditor extends JFrame
{
	private static final long serialVersionUID = -8403774668066080746L;
	private BuildResult result;
	private final Question question;

	private CardLayout questionLayout;
	private JLabel questionLabel;
	private JPanel questionContainer;

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

	/**
	 * 
	 * Disposes of this window and reenables the TestGenerator.
	 * 
	 */

	private void exit()
	{
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
}
