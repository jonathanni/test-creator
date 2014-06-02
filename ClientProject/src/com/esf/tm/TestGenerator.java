package com.esf.tm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

import com.esf.tm.serializable.FIBQuestion;
import com.esf.tm.serializable.MCQuestion;
import com.esf.tm.serializable.Question;
import com.esf.tm.serializable.TFQuestion;
import com.esf.tm.serializable.Test;

/**
 * 
 * Server front-end.
 * 
 * @author Jonathan Ni
 * @since 4/22/14
 * @version 0.0r7
 * 
 */

class TestGenerator extends JFrame implements ListSelectionListener
{

	private static final long serialVersionUID = -6456791709807158899L;

	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth(), SCREEN_HEIGHT = (int) Toolkit
			.getDefaultToolkit().getScreenSize().getHeight();

	public static final int PORT = 3353;

	private static TestGenerator instance;
	private BuildResult result;

	// GUI objects. These are initialized using Javabuilders in the
	// TestGenerator.yml document, but they can be referenced here.
	private JButton prevPanel, nextPanel, addQ, remQ, moveUpQ, moveDownQ,
			startNetwork;
	private JPanel mainPanel, tmainPanel, npPanel, qPanel, networkTestScreen;
	private JLabel ipAddress, password;
	private CustomCardLayout customLayout;
	private JTextField testTitleField, testDescriptField;
	private JList qList, clientList;
	private JScrollPane qListScrollPanel;
	private JSpinner printCount;

	// For debug purposes only
	private Logger logger = new Logger();

	private ArrayList<Integer> panelStops = new ArrayList<Integer>();
	private ArrayList<Integer> panelStarts = new ArrayList<Integer>();

	// Controller indices and counters for the number of frames/questions
	private int cardIndex, frameCount, questionIndex = -1, questionCount;

	// Constants for each frame ID
	private static final int TITLE_PANEL_INDEX = 0,
			IMPORT_TEST_PANEL_INDEX = 1, CREATE_TEST_PANEL_INDEX = 2,
			CREATE_QUESTION_PANEL_INDEX = 3, CHOOSE_TEST_PANEL_INDEX = 4,
			PRINT_TEST_PANEL_INDEX = 5, NETWORK_TEST_PANEL_INDEX = 6;

	// Used by the validator
	private boolean isValid = true;
	private String invalidMessage;

	// The Test associated with this object
	private Test currentTest = new Test();

	private ArrayList<Question> testQuestions = new ArrayList<Question>();
	private DefaultListModel qListModel = new DefaultListModel(),
			clientListModel = new DefaultListModel();

	private int testForms;

	private ArrayList<ClientCommunicator> clients = new ArrayList<ClientCommunicator>();
	private ClientProcessor processor;
	private ClientListener listener;

	private HashMap<String, Integer[]> scores = new HashMap<String, Integer[]>();

	public static final String PASSWORD = generateString(new Random(),
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
			10);

	/**
	 * 
	 * Creates a new test generator message panel to be used by the teacher.
	 * 
	 */

	private TestGenerator()
	{
		result = SwingJavaBuilder.build(this);

		// Add in where the buttons should be disabled
		panelStarts.add(CREATE_TEST_PANEL_INDEX);
		panelStarts.add(IMPORT_TEST_PANEL_INDEX);
		panelStarts.add(CHOOSE_TEST_PANEL_INDEX);
		panelStarts.add(PRINT_TEST_PANEL_INDEX);
		panelStarts.add(NETWORK_TEST_PANEL_INDEX);

		panelStops.add(CHOOSE_TEST_PANEL_INDEX);
		panelStops.add(PRINT_TEST_PANEL_INDEX);
		panelStops.add((frameCount = tmainPanel.getComponentCount()) - 1);

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

		// Set print spinner
		printCount.setModel(new SpinnerNumberModel(1, 1, 255, 1));

		// Bind the JList to the list model
		qList.setModel(qListModel);
		qList.getSelectionModel().addListSelectionListener(this);

		qListScrollPanel
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		qListScrollPanel
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		remove(tmainPanel);
		remove(npPanel);

		add(mainPanel);

		// recBorder(this);

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
			currentTest.setTestTitle(testTitleField.getText());
			currentTest.setTestDescription(testDescriptField.getText());

			add(qPanel, BorderLayout.SOUTH);
			pack();
		} else if (cardIndex == CREATE_QUESTION_PANEL_INDEX)
		{
			for (Question i : testQuestions)
				currentTest.addQuestion(i);

			currentTest.assignIDs();

			remove(qPanel);
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
	 * Imports a test and changes the panel to question creation.
	 * 
	 */

	private void importTest()
	{
		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
		// In response to a button click:
		int returnVal = fc.showOpenDialog(getParent());

		if (returnVal == JFileChooser.CANCEL_OPTION)
			return;

		if (fc.getSelectedFile() == null || !fc.getSelectedFile().exists())
			return;

		String pathName = fc.getSelectedFile().getAbsolutePath();
		File file = new File(pathName);
		Import myImport = new Import();
		Test importTest = myImport.importNew(file);

		int numQuestions = importTest.getQuestionCount();
		for (int i = 0; i < numQuestions; i++)
		{
			getInstance().addQuestion();
			getInstance().updateQuestion(importTest.getQuestion(i));
		}

		currentTest.setTestTitle(importTest.getTestTitle());
		currentTest.setTestDescription(importTest.getTestDescription());

		changePanel(CREATE_QUESTION_PANEL_INDEX);
	}

	/**
	 * 
	 * Prints a test out.
	 * 
	 */

	private void printTest()
	{
		TestPrinter printer = new TestPrinter(currentTest, -1);

		try
		{
			printer.printTest();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Changes the panel to the print test frame.
	 * 
	 */

	private void changePanelPrintTest()
	{
		testForms = (Integer) printCount.getValue();
		changePanel(PRINT_TEST_PANEL_INDEX);

		for (int i = 0; i < testForms; i++)
		{
			Test other = currentTest.mix();
			TestPrinter printer = new TestPrinter(other, i);

			try
			{
				printer.printTest();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Changes the panel to the network test frame.
	 * 
	 */

	/**
	 * 
	 */
	private void changePanelNetworkTest()
	{
		try
		{
			ipAddress.setText("IP Address: "
					+ InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}

		password.setText(PASSWORD);

		changePanel(NETWORK_TEST_PANEL_INDEX);

		new Thread(listener = new ClientListener()).start();
		new Thread(processor = new ClientProcessor()).start();

		clientList.setModel(clientListModel);
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
			Pattern restricted = Pattern.compile(Util.RESTRICTED_CHARACTERS);
			isValid = testTitleField.getText().length() != 0
					&& !restricted.matcher(testTitleField.getText()).find();
			invalidMessage = "The test title field is mandatory, and it must not contain any of: <>:\"/\\|?*";
			break;
		case CREATE_QUESTION_PANEL_INDEX:
			isValid = true;
			for (Question i : testQuestions)
				if (!(i instanceof MCQuestion || i instanceof TFQuestion || i instanceof FIBQuestion))
					isValid = false;

			invalidMessage = "Make sure you select the type of all questions.";
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
		qList.setSelectedIndex(questionIndex);
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
		qList.setSelectedIndex(questionIndex);

		if (questionCount == 1)
			remQ.setEnabled(false);
	}

	/**
	 * 
	 * Moves a Question up the JList. Cannot move a question up that is at the
	 * beginning of the list.
	 * 
	 */

	private void moveUpQuestion()
	{
		if (questionIndex < 1)
			return;

		Collections.swap(testQuestions, questionIndex - 1, questionIndex);

		String temp = (String) qListModel.get(questionIndex);
		qListModel.set(questionIndex, qListModel.get(questionIndex - 1));
		qListModel.set(questionIndex - 1, temp);

		questionIndex--;
		qList.setSelectedIndex(questionIndex);
	}

	/**
	 * 
	 * Moves a Question down the JList. Cannot move a question down that is at
	 * the end of the list.
	 * 
	 */

	private void moveDownQuestion()
	{
		if (questionIndex == questionCount - 1)
			return;

		Collections.swap(testQuestions, questionIndex, questionIndex + 1);

		String temp = (String) qListModel.get(questionIndex + 1);
		qListModel.set(questionIndex + 1, qListModel.get(questionIndex));
		qListModel.set(questionIndex, temp);

		questionIndex++;
		qList.setSelectedIndex(questionIndex);
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

	/**
	 * 
	 * Gets the clients associated with the test generator.
	 * 
	 * @return the clients
	 */

	public ArrayList<ClientCommunicator> getClients()
	{
		return clients;
	}

	/**
	 * 
	 * Adds a new client to the GUI.
	 * 
	 */

	void addClientNode()
	{
		if (!startNetwork.isEnabled())
			startNetwork.setEnabled(true);

		clientListModel.addElement("Name: [not connected] Progress: [N/A]");
	}

	/**
	 * 
	 * Sends tests to all the clients.
	 * 
	 */

	private void distributeTests()
	{
		if (!startNetwork.isEnabled())
			return;

		startNetwork.setText("Distributing...");
		processor.sendTest();
		startNetwork.setText("Distributed");
		startNetwork.setEnabled(false);

		listener.isRunning = false;
	}

	void exportResults() throws IOException
	{
		File resultFolder = new File("results/");

		if (!resultFolder.exists())
			resultFolder.mkdir();

		File resultFile = new File("results/"
				+ Util.encodeURI(currentTest.getTestTitle()) + ".result"
				+ ".txt");

		BufferedWriter resultOut = new BufferedWriter(
				new FileWriter(resultFile));

		// Headers

		resultOut.write("Student,");

		for (int i = 0; i < currentTest.getQuestionCount(); i++)
			resultOut.write("\"" + i + ". "
					+ currentTest.getQuestion(i).getMessage() + "\",");

		resultOut.write("Total");

		resultOut.write("\n");

		// Question pointage

		resultOut.write("Pointage,");

		for (int i = 0; i < currentTest.getQuestionCount(); i++)
		{
			Question q = currentTest.getQuestion(i);
			if (q instanceof MCQuestion || q instanceof TFQuestion)
				resultOut.write("1,");
			else
				resultOut.write(((FIBQuestion) q).getBlankSpaces() + ",");
		}

		resultOut.write(currentTest.getPointWorth() + "");

		resultOut.write("\n");

		// Student

		HashMap<String, Integer> studentTotals = new HashMap<String, Integer>();
		double[] means = new double[currentTest.getQuestionCount() + 1];

		for (Entry<String, Integer[]> i : scores.entrySet())
		{
			studentTotals.put(i.getKey(), 0);

			resultOut.write(i.getKey() + ",");

			for (int j = 0; j < i.getValue().length; j++)
			{
				resultOut.write(i.getValue()[j] + ",");
				studentTotals.put(i.getKey(),
						studentTotals.get(i.getKey()) + i.getValue()[j]);
			}

			resultOut.write(studentTotals.get(i.getKey()) + "");

			resultOut.write("\n");
		}

		// Mean

		resultOut.write("Average,");

		for (int i = 0; i < currentTest.getQuestionCount(); i++)
		{
			for (Entry<String, Integer[]> j : scores.entrySet())
				means[i] += j.getValue()[i];
			means[i] /= scores.size();

			resultOut.write(means[i] + ",");
		}

		for (Entry<String, Integer> i : studentTotals.entrySet())
			means[currentTest.getQuestionCount()] += i.getValue();
		means[currentTest.getQuestionCount()] /= scores.size();

		resultOut.write(means[currentTest.getQuestionCount()] + "");

		resultOut.write("\n");

		// Median

		resultOut.write("Median,");

		for (int i = 0; i < currentTest.getQuestionCount(); i++)
		{
			int[] points = new int[scores.size()];

			int k = 0;
			for (Entry<String, Integer[]> j : scores.entrySet())
				points[k++] = j.getValue()[i];

			resultOut.write(Util.median(points) + ",");
		}

		{
			int[] points = new int[scores.size()];

			int k = 0;
			for (Entry<String, Integer> i : studentTotals.entrySet())
				points[k++] = i.getValue();

			resultOut.write(Util.median(points) + "");
		}

		resultOut.write("\n");

		resultOut.close();
	}

	/**
	 * 
	 * Triggered when the question list selection changes. Updates the GUI.
	 * 
	 */

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (!event.getValueIsAdjusting() && qList.getSelectedIndex() != -1)
			questionIndex = qList.getSelectedIndex();

		moveUpQ.setEnabled(true);
		moveDownQ.setEnabled(true);

		if (questionIndex == 0)
			moveUpQ.setEnabled(false);
		if (questionIndex == questionCount - 1)
			moveDownQ.setEnabled(false);
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

	/**
	 * 
	 * Gets the current test associated with the generator.
	 * 
	 * @return
	 */

	Test getTest()
	{
		return currentTest;
	}

	/**
	 * 
	 * Gets the scores HashMap.
	 * 
	 * @return
	 */

	HashMap<String, Integer[]> getScores()
	{
		return scores;
	}

	/**
	 * 
	 * Gets the client list model.
	 * 
	 * @return the client list model
	 */

	DefaultListModel getClientListModel()
	{
		return clientListModel;
	}

	/**
	 * 
	 * Generates a new random string.
	 * 
	 * @param rng
	 *            a {@link java.util.Random}
	 * @param characters
	 *            the alphabet of the string
	 * @param length
	 *            the length of the string
	 * @return a random string
	 */

	private static String generateString(Random rng, String characters,
			int length)
	{
		char[] text = new char[length];
		for (int i = 0; i < length; i++)
		{
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}

	/**
	 * 
	 * Entry point.
	 * 
	 * @param args
	 *            the command line arguments
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 */

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		if (SCREEN_WIDTH < 0 || SCREEN_HEIGHT < 0)
			ErrorReporter.reportError(
					"Error occured while initiating graphics", "");

		instance = new TestGenerator();
	}
}
