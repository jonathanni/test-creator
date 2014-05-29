package com.esf.tm.client;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

import com.esf.tm.serializable.FIBQuestion;
import com.esf.tm.serializable.MCQuestion;
import com.esf.tm.serializable.Message;
import com.esf.tm.serializable.Question;
import com.esf.tm.serializable.TFQuestion;
import com.esf.tm.serializable.Test;
import com.esf.tm.serializable.TestAnswer;

/**
 * 
 * Client front-end.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r3
 * 
 */

public class TestTaker extends JFrame implements MouseListener
{
	private static final long serialVersionUID = -5547354766805951582L;

	private static TestTaker instance;

	private BuildResult result;

	private JPanel mainPanel, questionPanel, npPanel, scorePanel;
	private JScrollPane ipPanel;
	private JList ips;
	private JButton next, prev;
	private JLabel scoreText;

	private CustomCardLayout layout = new CustomCardLayout();

	private DefaultListModel ipListModel = new DefaultListModel();

	private NodeScanner nScanner;
	private ServerCommunicator communicator;

	private Test currentTest;

	private int currentQuestion;
	private ArrayList<Object> testAnswers = new ArrayList<Object>();

	private ArrayList<ArrayList<Component>> testComponents = new ArrayList<ArrayList<Component>>();

	private TestAnswer answers;

	public static final int PORT = 3353;

	/**
	 * 
	 * Creates a new test generator message panel to be used by the teacher.
	 * 
	 */

	public TestTaker()
	{
		super();

		result = SwingJavaBuilder.build(this);

		ips.setModel(ipListModel);
		ips.addMouseListener(this);

		mainPanel.remove(scorePanel);
		mainPanel.remove(npPanel);

		pack();
		setVisible(true);
		pack();

		new Thread(nScanner = new NodeScanner()).start();
	}

	public int getCurrentQuestion()
	{
		return currentQuestion;
	}

	public void setCurrentQuestion(int currentQuestion)
	{
		this.currentQuestion = currentQuestion;
	}

	public ServerCommunicator getCommunicator()
	{
		return communicator;
	}

	void clearList()
	{
		ipListModel.removeAllElements();
	}

	void appendIP(String ip)
	{
		ipListModel.addElement(ip);
	}

	private ArrayList<String> getFIBData()
	{
		ArrayList<String> list = new ArrayList<String>();

		for (Component i : testComponents.get(currentQuestion))
			list.add(((JTextField) i).getText());

		return list;
	}

	private void nextPanel()
	{
		if (currentTest.getQuestion(currentQuestion) instanceof FIBQuestion)
			answers.getAnswers().set(currentQuestion, getFIBData());

		if (currentQuestion == currentTest.getQuestionCount() - 1)
			try
			{
				finish();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		if (currentQuestion == currentTest.getQuestionCount() - 2)
			next.setText("Finish");

		currentQuestion++;

		layout.next(questionPanel);
		pack();
	}

	private void prevPanel()
	{
		checkButtons();
		if (!prev.isEnabled())
			return;

		if (currentTest.getQuestion(currentQuestion) instanceof FIBQuestion)
			answers.getAnswers().set(currentQuestion, getFIBData());

		if (currentQuestion == currentTest.getQuestionCount() - 1)
			next.setText("Next");

		currentQuestion--;

		layout.previous(questionPanel);
		pack();
	}

	private void checkButtons()
	{
		if (currentQuestion == 0)
			prev.setEnabled(false);
		else
			prev.setEnabled(true);
	}

	private void finish() throws InterruptedException
	{
		// sendTest
		communicator.getWriter().getQueue()
				.add(new Message("sendTest", answers));

		// receiveScore
		Integer score = (Integer) communicator.getReader().getQueue().take()
				.getPayload();

		// exit
		communicator.getWriter().getQueue().add(new Message("exit", null));

		// acknowledge exit
		communicator.getReader().getQueue().take();

		scoreText.setText(score + "/" + currentTest.getPointWorth());

		mainPanel.remove(questionPanel);
		remove(npPanel);

		mainPanel.add(scorePanel);

		communicator.getReader().isRunning = false;
		communicator.getWriter().isRunning = false;
	}

	private void requestLogin(String ip) throws InterruptedException
	{
		System.out.println("Host is up");

		// Retire node scanner
		nScanner.isRunning = false;

		communicator = new ServerCommunicator(ip);

		// requestLogin
		communicator.getWriter().getQueue()
				.add(new Message("requestLogin", null));

		System.out.println("Added message");

		// receive requestPassword
		Message m = communicator.getReader().getQueue().take();

		System.out.println(m);

		boolean flag = true;

		while (flag)
		{
			JPanel panel = new JPanel();

			JTextField userField = new JTextField(10);
			JTextField passField = new JTextField(10);

			panel.add(new JLabel("User: "));
			panel.add(userField);
			panel.add(Box.createVerticalStrut(5));
			panel.add(new JLabel("Password: "));
			panel.add(passField);

			JOptionPane.showConfirmDialog(null, panel, "Enter the password",
					JOptionPane.OK_OPTION);

			String user = userField.getText();
			String password = passField.getText();

			// login
			communicator.getWriter().getQueue()
					.add(new Message("login", new String[] { user, password }));

			Message recv = communicator.getReader().getQueue().take();

			if (recv.getHeader().equals("loginSuccess"))
				flag = false;
			else if (recv.getHeader().equals("loginFailure"))
				// receive requestPassword
				communicator.getReader().getQueue().take();
		}

		System.out.println("GETTING TEST");

		// receiveTest
		currentTest = (Test) communicator.getReader().getQueue().take()
				.getPayload();

		System.out.println("GOT TEST");

		mainPanel.remove(ipPanel);

		questionPanel = new JPanel(layout);

		for (int i = 0; i < currentTest.getQuestionCount(); i++)
		{
			JPanel qPanel = new JPanel();
			Question j = currentTest.getQuestion(i);

			testComponents.add(new ArrayList<Component>());

			if (j instanceof MCQuestion)
			{
				JLabel title = new JLabel(i + ". "
						+ j.getMessage().replace("\n", "<br />") + "<br />");

				qPanel.add(title);

				ButtonGroup group = new ButtonGroup();

				for (int k = 0; k < ((MCQuestion) j).getChoiceCount(); k++)
				{
					JRadioButton btn = new JRadioButton(((MCQuestion) j)
							.getChoice(k).getMessage());

					btn.addMouseListener(this);

					group.add(btn);
					qPanel.add(btn);

					testComponents.get(i).add(btn);
				}
			} else if (j instanceof TFQuestion)
			{
				JLabel title = new JLabel(i + ". "
						+ j.getMessage().replace("\n", "<br />") + "<br />");

				qPanel.add(title);

				ButtonGroup group = new ButtonGroup();

				JRadioButton btnTrue = new JRadioButton("True");
				JRadioButton btnFalse = new JRadioButton("False");

				btnTrue.addMouseListener(this);
				btnFalse.addMouseListener(this);

				group.add(btnTrue);
				group.add(btnFalse);

				qPanel.add(btnTrue);
				qPanel.add(btnFalse);

				testComponents.get(i).add(btnTrue);
				testComponents.get(i).add(btnFalse);
			} else
			{
				JLabel title = new JLabel(i + ". ");

				qPanel.add(title);

				String[] pieces = j.getMessage().replace("\n", "<br />")
						.split("___.");
				for (String k : pieces)
				{
					JLabel piece = new JLabel(k);

					qPanel.add(piece);

					if (!k.equals(pieces[pieces.length - 1]))
					{
						JTextField field = new JTextField();

						qPanel.add(piece);

						testComponents.get(i).add(field);
					}
				}
			}

			questionPanel.add(qPanel, "" + i);
		}
		
		System.out.println("Finished layout");

		mainPanel.add(questionPanel);
		mainPanel.add(npPanel);

		answers = new TestAnswer(currentTest.getQuestionCount());
		
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

	public static TestTaker getInstance()
	{
		return instance;
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		System.setProperty("javax.net.ssl.keyStore", "testgen.key");
		System.setProperty("javax.net.ssl.keyStorePassword",
				"2436230468901920356");

		instance = new TestTaker();
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
		if (event.getSource() == ips)
		{
			JList list = (JList) event.getSource();

			if (event.getClickCount() == 2)
			{
				String ip = (String) ipListModel.get(list.getSelectedIndex());
				try
				{
					requestLogin(ip);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		} else if (event.getSource() instanceof JRadioButton)
		{
			JRadioButton btn = (JRadioButton) event.getSource();

			Question question = currentTest.getQuestion(currentQuestion);

			if (question instanceof MCQuestion)
				testComponents.get(currentQuestion).indexOf(btn);
			else if (question instanceof TFQuestion)
				answers.getAnswers().set(currentQuestion,
						testComponents.get(currentQuestion).indexOf(btn) == 0);
		}
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{

	}

	@Override
	public void mouseExited(MouseEvent event)
	{

	}

	@Override
	public void mousePressed(MouseEvent event)
	{

	}

	@Override
	public void mouseReleased(MouseEvent event)
	{

	}

}
